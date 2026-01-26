package sawfowl.regionguard.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldArchetype;
import org.spongepowered.api.world.server.WorldArchetypeType;
import org.spongepowered.api.world.server.storage.ServerWorldProperties.LoadOptions;
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
		if(region == null || !region.getWorld().isPresent() || !region.getWorld().get().isLoaded() || region.getCuboid() == null) return false;
		return Sponge.server().worldManager().loadWorld(
				ResourceKey.of("regionguard", "tempworld_" + region.getWorldKey().value()),
				LoadOptions
					.builder()
					.create(
						WorldArchetype.of(
							WorldArchetypeType
							.builder()
							.worldType(region.getWorld().get().worldType())
							.chunkGenerator(
								region.getWorld().get().generator()
							)
							.build()
						)
					)
				.createCallback(p -> p.offer(Keys.SEED, region.getWorld().get().seed()))
				.build()
			).thenAccept(optWorld -> {
				optWorld.ifPresent(tempWorld -> {
					lockWorld(region, tempWorld.key());
					for(ChunkNumber chunkNumber : region.getChunkNumbers()) if(!tempWorld.isChunkLoaded(chunkNumber.chunkPosition(), true)) tempWorld.loadChunk(chunkNumber.chunkPosition(), true);
					ServerWorld world = region.getWorld().get();
					for(Vector3i vector3i : region.getCuboid().getAllPositions()) {
						world.setBlock(vector3i, tempWorld.block(vector3i));
					}
					unlockWorld(region, tempWorld.key());
				});
			})
		.thenRun(() -> removeWorld(ResourceKey.of("regionguard", "tempworld_" + region.getWorldKey().value())))
		.isDone();
	}

	public boolean regenAsync(Region region, int delay) {
		if(region == null || !region.getWorld().isPresent() || !region.getWorld().get().isLoaded() || region.getCuboid() == null) return false;
		Sponge.server().worldManager().loadWorld(
				ResourceKey.of("regionguard", "tempworld_" + region.getWorldKey().value()),
				LoadOptions
					.builder()
					.create(
						WorldArchetype.of(
							WorldArchetypeType
							.builder()
							.worldType(region.getWorld().get().worldType())
							.chunkGenerator(
								region.getWorld().get().generator()
							)
							.build()
						)
					)
				.createCallback(p -> p.offer(Keys.SEED, region.getWorld().get().seed()))
				.build()
			).thenAcceptAsync(optWorld -> {
				optWorld.ifPresent(tempWorld -> {
					lockWorld(region, tempWorld.key());
					for(ChunkNumber chunkNumber : region.getChunkNumbers()) if(!tempWorld.isChunkLoaded(chunkNumber.chunkPosition(), true)) tempWorld.loadChunk(chunkNumber.chunkPosition(), true);
					ServerWorld world = region.getWorld().get();
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
						} else Sponge.server().scheduler().submit(Task.builder().plugin(plugin.getPluginContainer()).delay(delay, TimeUnit.SECONDS).execute(() -> {
							blocks.forEach((vector, block) -> world.setBlock(vector, block));
							unlockWorld(region, tempWorld.key());
						}).build());
					}
				});
			})
		.thenRun(() -> removeWorld(ResourceKey.of("regionguard", "tempworld_" + region.getWorldKey().value())))
		.isDone();
		return true;
	}

	private void lockWorld(Region region, ResourceKey world) {
		if(!lockedWorlds.containsKey(world)) lockedWorlds.put(world, new ArrayList<Region>());
		lockedWorlds.get(world).add(region);
	}

	private void unlockWorld(Region region, ResourceKey world) {
		if(lockedWorlds.containsKey(world) && lockedWorlds.get(world).contains(region)) lockedWorlds.get(world).remove(region);
		if(lockedWorlds.get(world).isEmpty()) lockedWorlds.remove(world);
	}

	private void removeWorld(ResourceKey world) {
		if(lockedWorlds.containsKey(world) && lockedWorlds.get(world).isEmpty()) Sponge.server().worldManager().unloadWorld(world).thenRun(() -> Sponge.server().worldManager().deleteWorld(world));
	}

}
