package sawfowl.regionguard.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.generation.config.WorldGenerationConfig;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldTemplate;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.ChunkNumber;
import sawfowl.regionguard.api.data.Region;

public class RegenUtil {

	private final RegionGuard plugin;
	private Map<ResourceKey, List<Region>> lockedWorlds = new HashMap<ResourceKey, List<Region>>();
	public RegenUtil(RegionGuard plugin) {
		this.plugin = plugin;
	}

	public boolean regenSync(Region region) {
		if(region == null || !region.getServerWorld().isPresent() || !region.getServerWorld().get().isLoaded() || region.getCuboid() == null) return false;
		WorldTemplate template = createWorldTemplate(region);
		return Sponge.server().worldManager().loadWorld(template).thenRun(() -> {
			ServerWorld tempWorld = Sponge.server().worldManager().world(template.key()).get();
			for(ChunkNumber chunkNumber : region.getChunkNumbers()) if(!tempWorld.isChunkLoaded(chunkNumber.chunkPosition(), true)) tempWorld.loadChunk(chunkNumber.chunkPosition(), true);
			ServerWorld world = region.getServerWorld().get();
			for(Vector3i vector3i : region.getCuboid().getAllPositions()) {
				world.setBlock(vector3i, tempWorld.block(vector3i));
			}
		}).thenRun(() -> {
			removeWorld(region, template.key());
		}).isDone();
	}

	public boolean regenAsync(Region region, int delay) {
		if(region == null || !region.getServerWorld().isPresent() || !region.getServerWorld().get().isLoaded() || region.getCuboid() == null) return false;
		WorldTemplate template = createWorldTemplate(region);
		Sponge.server().worldManager().loadWorld(template).thenRunAsync(() -> {
			ServerWorld tempWorld = Sponge.server().worldManager().world(template.key()).get();
			for(ChunkNumber chunkNumber : region.getChunkNumbers()) if(!tempWorld.isChunkLoaded(chunkNumber.chunkPosition(), true)) tempWorld.loadChunk(chunkNumber.chunkPosition(), true);
			ServerWorld world = region.getServerWorld().get();
			Map<Vector3i, BlockState> blocks = new HashMap<Vector3i, BlockState>();
			for(Vector3i vector3i : region.getCuboid().getAllPositions()) if(tempWorld.block(vector3i).type() != world.block(vector3i).type()) blocks.put(vector3i, tempWorld.block(vector3i));
			if(!blocks.isEmpty()) {
				if(delay <= 0) {
					blocks.forEach((vector, block) -> {
						Sponge.server().scheduler().submit(Task.builder().plugin(plugin.getPluginContainer()).delay(delay, TimeUnit.SECONDS).execute(() -> {
							world.setBlock(vector, block);
						}).build());
					});
					blocks.clear();
				} else blocks.forEach((vector, block) -> {
					Sponge.server().scheduler().submit(Task.builder().plugin(plugin.getPluginContainer()).delay(delay, TimeUnit.SECONDS).execute(() -> {
						world.setBlock(vector, block);
					}).build());
				});
			}
		}).thenRun(() -> {
			removeWorld(region, template.key());
		});
		return true;
	}

	private void lockWorld(Region region, ResourceKey world) {
		if(!lockedWorlds.containsKey(world)) lockedWorlds.put(world, new ArrayList<Region>());
		lockedWorlds.get(world).add(region);
	}

	private WorldTemplate createWorldTemplate(Region region) {
		ServerWorld world = region.getServerWorld().get();
		WorldGenerationConfig baseConfig = world.asTemplate().generationConfig();
		WorldTemplate tempWorldProperties = world.asTemplate().asBuilder()
			.key(ResourceKey.of("regionguard", "tempworld_" + world.key().value()))
			.loadOnStartup(true)
			.serializationBehavior(SerializationBehavior.NONE)
			.generationConfig(baseConfig)
			.build();
		lockWorld(region, tempWorldProperties.key());
		return tempWorldProperties;
	}

	private void removeWorld(Region region, ResourceKey world) {
		if(lockedWorlds.containsKey(world)) {
			lockedWorlds.get(world).remove(region);
			if(lockedWorlds.get(world).isEmpty()) Sponge.server().worldManager().unloadWorld(world).thenRun(() -> Sponge.server().worldManager().deleteWorld(world));
		}
	}

}
