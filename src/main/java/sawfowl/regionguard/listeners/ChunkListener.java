package sawfowl.regionguard.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.chunk.ChunkEvent;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.data.ChunkNumber;
import sawfowl.regionguard.api.data.Region;

public class ChunkListener {

	private final RegionGuard plugin;
	public ChunkListener(RegionGuard instance) {
		plugin = instance;
	}

	@Listener
	public void chunkLoad(ChunkEvent.Load event) {
		if(!Sponge.server().worldManager().world(event.worldKey()).isPresent()) return;
		ChunkNumber chunkNumber = new ChunkNumber(event.chunkPosition());
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			for(Region region : plugin.getAPI().getRegions()) {
				if(event.worldKey().equals(region.getServerWorldKey()) && region.getChunkNumbers().contains(chunkNumber)) {
					if(plugin.getAPI().getRegionsPerWorld().containsKey(event.worldKey())) {
						if(!plugin.getAPI().getRegionsPerWorld().get(event.worldKey()).containsKey(chunkNumber)) {
							ArrayList<Region> regions = new ArrayList<>();
							regions.add(region);
							plugin.getAPI().getRegionsPerWorld().get(event.worldKey()).put(chunkNumber, regions);
						} else {
							if(!plugin.getAPI().getRegionsPerWorld().get(event.worldKey()).get(chunkNumber).contains(region)) plugin.getAPI().getRegionsPerWorld().get(event.worldKey()).get(chunkNumber).add(region);
						}
					} else {
						Map<ChunkNumber, ArrayList<Region>> regionsPerChunk = new HashMap<ChunkNumber, ArrayList<Region>>();
						ArrayList<Region> regions = new ArrayList<>();
						regions.add(region);
						regionsPerChunk.put(chunkNumber, regions);
						plugin.getAPI().getRegionsPerWorld().put(event.worldKey(), regionsPerChunk);
					}
				}
			}
		});
	}

	@Listener
	public void chunkUnLoad(ChunkEvent.Unload.Pre event) {
		if(!Sponge.server().worldManager().world(event.worldKey()).isPresent()) return;
		ChunkNumber chunkNumber = new ChunkNumber(event.chunkPosition());
		if(plugin.getAPI().getRegionsPerWorld().containsKey(event.worldKey()) && plugin.getAPI().getRegionsPerWorld().get(event.worldKey()).containsKey(chunkNumber)) {
			Map<ChunkNumber, ArrayList<Region>> map = new HashMap<ChunkNumber, ArrayList<Region>>();
			map.putAll(plugin.getAPI().getRegionsPerWorld().get(event.worldKey()));
			for(Region region : map.get(chunkNumber)) {
				if(!region.getType().equals(RegionTypes.ADMIN)) {
					plugin.getAPI().getRegionsPerWorld().get(event.worldKey()).remove(chunkNumber);
				}
			}
			map.clear();
			map = null;
		}
	}

}
