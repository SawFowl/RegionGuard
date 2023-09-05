package sawfowl.regionguard.listeners;

import java.util.Optional;

import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.server.ServerWorld;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionChangeBlockEvent;
import sawfowl.regionguard.utils.ListenerUtils;

public class ExplosionListener {

	private final RegionGuard plugin;
	private Cause cause;
	public ExplosionListener(RegionGuard plugin) {
		this.plugin = plugin;
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onExplosion(ExplosionEvent.Pre event) {
		Explosion explosion = event.explosion();
		Region region = plugin.getAPI().findRegion(event.world(), explosion.blockPosition());
		boolean allow = isAllowExplosion(region, explosion);
		class RegionExplosionEvent implements RegionChangeBlockEvent.Explode {
			
			Explosion explosion;
			boolean cencelled;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public Cause spongeCause() {
				return event.cause();
			}

			@Override
			public Object source() {
				return event.source();
			}

			@Override
			public ServerWorld getWorld() {
				return event.world();
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public boolean isCancelled() {
				return cencelled;
			}

			@Override
			public void setCancelled(boolean cancel) {
				cencelled = cancel;
			}

			@Override
			public org.spongepowered.api.world.explosion.Explosion getExplosion() {
				return explosion;
			}

			@Override
			public void setExplosion(org.spongepowered.api.world.explosion.Explosion explosion) {
				this.explosion = explosion;
			}

			@Override
			public Optional<Explosive> getExplosive() {
				return event.explosion().sourceExplosive();
			}

			@Override
			public boolean isAllowExplosion() {
				return allow;
			}
			
		}
		RegionChangeBlockEvent.Explode rgEvent = new RegionExplosionEvent();
		rgEvent.setExplosion(explosion);
		rgEvent.setCancelled(!allow);
		ListenerUtils.postEvent(rgEvent);
		event.setCancelled(rgEvent.isCancelled());
	}

	boolean isAllowExplosion(Region region, Explosion explosion) {
		if(explosion.sourceExplosive().isPresent()) {
			for(String entityId : ListenerUtils.flagEntityArgs(explosion.sourceExplosive().get())) {
				Tristate flagResult = region.getFlagResult(Flags.EXPLOSION, entityId, null);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		} else {
			Tristate flagResult = region.getFlagResult(Flags.EXPLOSION, null, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowExplosion(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), explosion);
	}

}
