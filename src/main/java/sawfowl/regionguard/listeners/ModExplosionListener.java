package sawfowl.regionguard.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.commandpack.api.events.ModExplosionEvent;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.world.RegionExplosionEvent.SurfaceMod;
import sawfowl.regionguard.utils.ListenerUtils;

public class ModExplosionListener {

	private final RegionGuard plugin;
	public ModExplosionListener(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Listener
	public void onExplosion(ModExplosionEvent event) {
		if(event.isCancelled()) return;
		Map<Region, Map<Vector3i, BlockState>> regions = new HashMap<>();
		Map<Region, Map<Vector3i, Boolean>> allowBlockDestruction = new HashMap<>();
		List<String> sources = new ArrayList<String>();
		if(event.getDirectSourceEntity() != null) sources.addAll(ListenerUtils.flagEntityArgs(event.getDirectSourceEntity()));
		if(event.getIndirectSourceEntity() != null) sources.addAll(ListenerUtils.flagEntityArgs(event.getIndirectSourceEntity()));
		boolean allowExplosion = false;
		for(Vector3i position : event.getBlockPositionsAffected()) {
			Region region = plugin.getAPI().findRegion(event.getWorld(), position);
			if(!regions.containsKey(region)) regions.put(region, new HashMap<>());
			BlockState block = event.getWorld().block(position);
			regions.get(region).put(position, block);
			if(!allowBlockDestruction.containsKey(region)) allowBlockDestruction.put(region, new HashMap<>());
			boolean allow = isAllowExplosion(region, sources, block);
			allowExplosion |= allow;
			allowBlockDestruction.get(region).put(position, allow);
		}
		boolean finalAllow = allowExplosion;
		Region region = plugin.getAPI().findRegion(event.getWorld(), event.getExplosion().blockPosition());
		SurfaceMod regionEvent = new SurfaceMod() {
			
			@Override
			public void setCancelled(boolean cancel) {
				event.setCancelled(cancel);
			}
			
			@Override
			public boolean isCancelled() {
				return event.isCancelled();
			}
			
			@Override
			public Cause cause() {
				return event.cause();
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public <T extends Event> T getSpongeEvent() {
				return (T) event;
			}
			
			@Override
			public void setExplosion(Explosion explosion) {}
			
			@Override
			public boolean isAllowExplosion() {
				return finalAllow;
			}
			
			@Override
			public Optional<Explosive> getExplosive() {
				return event.getExplosion().sourceExplosive();
			}
			
			@Override
			public Explosion getExplosion() {
				return event.getExplosion();
			}
			
			@Override
			public ServerWorld getWorld() {
				return event.getWorld();
			}
			
			@Override
			public Map<Region, Map<Vector3i, BlockState>> getRegionsAffected() {
				return regions;
			}
			
			@Override
			public Region getRegion() {
				return region;
			}
			
			@Override
			public Living getIndirectSourceEntity() {
				return event.getIndirectSourceEntity();
			}
			
			@Override
			public Entity getDirectSourceEntity() {
				return event.getDirectSourceEntity();
			}
			
			@Override
			public Collection<Vector3i> getBlockPositionsAffected() {
				return event.getBlockPositionsAffected();
			}
			
			@Override
			public Map<Region, Map<Vector3i, Boolean>> getAllowBlockDestruction() {
				return allowBlockDestruction;
			}

			@Override
			public void removeBlock(Vector3i vector3i) {
				event.removeBlock(vector3i);
				regions.forEach((r, m) -> m.remove(vector3i));
				allowBlockDestruction.forEach((r, m) -> m.remove(vector3i));
			}

			@Override
			public void removeBlocks(Collection<Vector3i> vectors3i) {
				vectors3i.forEach(vector3i -> removeBlock(vector3i));
			}
		};
		Sponge.eventManager().post(regionEvent);
		if(regionEvent.isCancelled()) return;
		regionEvent.getAllowBlockDestruction().forEach((r, m) -> m.forEach((p, v) -> {
			if(!v) event.removeBlock(p);
		}));
	}

	private boolean isAllowExplosion(Region region, List<String> sources, BlockState blockState) {
		if(!sources.isEmpty()) {
			for(String source : sources) {
				for(String target : Arrays.asList(ListenerUtils.blockID(blockState), "all")) {
					Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, source, target);
					if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
				}
			}
		} else {
			Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, null, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowExplosion(plugin.getAPI().getGlobalRegion(region.getWorldKey()), sources, blockState);
	}

}
