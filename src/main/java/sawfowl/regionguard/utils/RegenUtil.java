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
	private Map<ResourceKey, List<Region>> blockedWorlds = new HashMap<ResourceKey, List<Region>>();
	public RegenUtil(RegionGuard plugin) {
		this.plugin = plugin;
	}

	public boolean regenSync(Region region) {
		WorldTemplate template = getWorldTemplate(region);
		writeMap(region, template);
		return Sponge.server().worldManager().loadWorld(template).thenRun(() -> {
			ServerWorld tempWorld = Sponge.server().worldManager().world(template.key()).get();
			for(ChunkNumber chunkNumber : region.getChunkNumbers()) if(!tempWorld.isChunkLoaded(chunkNumber.chunkPosition(), true)) tempWorld.loadChunk(chunkNumber.chunkPosition(), true);
			ServerWorld world = region.getServerWorld().get();
			for(Vector3i vector3i : region.getCuboid().getAllPositions()) {
				world.setBlock(vector3i, tempWorld.block(vector3i));
			}
		}).thenRun(() -> {
			removeWorldAndBlock(region, template.key());
		}).isDone();
	}

	public boolean regenAsync(Region region, int delay) {
		WorldTemplate template = getWorldTemplate(region);
		writeMap(region, template);
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
			removeWorldAndBlock(region, template.key());
		});
		return true;
	}

	private void removeWorldAndBlock(Region region, ResourceKey world) {
		if(blockedWorlds.containsKey(world)) blockedWorlds.get(world).remove(region);
		if(!blockedWorlds.containsKey(world) || blockedWorlds.get(world).size() == 0) Sponge.server().worldManager().unloadWorld(world).thenRun(() -> Sponge.server().worldManager().deleteWorld(world));
	}

	private void writeMap(Region region, WorldTemplate template) {
		if(blockedWorlds.isEmpty() || !blockedWorlds.containsKey(template.key())) {
			List<Region> regions = new ArrayList<Region>();
			regions.add(region);
			blockedWorlds.put(template.key(), regions);
		} else {
			blockedWorlds.get(template.key()).add(region);
		}
	}

	private WorldTemplate getWorldTemplate(Region region) {
		ServerWorld world = region.getServerWorld().get();
		final String id = "tempworld_" + world.key().value();

		WorldGenerationConfig baseConfig = world.asTemplate().generationConfig();

		WorldTemplate tempWorldProperties = world.asTemplate().asBuilder()
			.key(ResourceKey.of("regionguard", id))
			.loadOnStartup(true)
			.serializationBehavior(SerializationBehavior.NONE)
			.generationConfig(baseConfig)
			.build();
		return tempWorldProperties;
	}

}
