package sawfowl.regionguard.listeners;

import java.util.Optional;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.util.Tristate;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionCollideEntityEvent;
import sawfowl.regionguard.utils.ListenerUtils;

public class CollideEntityListener {

	private final RegionGuard plugin;
	private Cause cause;
	public CollideEntityListener(RegionGuard plugin) {
		this.plugin = plugin;
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onCollide(CollideEntityEvent event) {
		Optional<Entity> optEntitySource = event.cause().first(Entity.class);
		if(!optEntitySource.isPresent()) return;
		Entity entitySource = optEntitySource.get();
		ResourceKey worldKey = ResourceKey.resolve(entitySource.world().context().getValue());
		Region region = plugin.getAPI().findRegion(worldKey, entitySource.blockPosition());
		boolean isAllow = true;
		Entity entityTarget = null;
		for(Entity target : event.entities()) {
			if(!isAllowCollideEntity(null, entitySource, target)) {
				isAllow = false;
				entityTarget = target;
				break;
			}
		}
		Entity finalTarget = entityTarget;
		boolean finalAllow = isAllow;
		class CollideEvent extends AbstractEvent implements RegionCollideEntityEvent {

			boolean cencelled;
			@Override
			public Cause cause() {
				return cause;
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
			public Region getRegion() {
				return region;
			}

			@Override
			public CollideEntityEvent spongeEvent() {
				return event;
			}

			@Override
			public boolean isAllow() {
				return finalAllow;
			}

			@Override
			public Entity getSource() {
				return entitySource;
			}

			@Override
			public Entity getTarget() {
				return finalTarget;
			}
			
		}
		RegionCollideEntityEvent rgEvent = new CollideEvent();
		rgEvent.setCancelled(!finalAllow);
		ListenerUtils.postEvent(rgEvent);
		event.setCancelled(rgEvent.isCancelled());
	}

	private boolean isAllowCollideEntity(Region region, Entity source, Entity target) {
		if(source instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) source;
			if(player.hasPermission(Permissions.bypassFlag(Flags.COLLIDE_ENTITY))) return true;
		}
		Tristate finalFlagResult = Tristate.UNDEFINED;
		for(String sourceid : ListenerUtils.flagEntityArgs(source)) {
			for(String targetid : ListenerUtils.flagEntityArgs(target)) {
				Tristate flagResult = region.getFlagResult(Flags.COLLIDE_ENTITY, sourceid, targetid);
				if(flagResult != Tristate.UNDEFINED) {
					finalFlagResult = flagResult;
					break;
				}
			}
		}
		return region.isGlobal() ? (finalFlagResult == Tristate.UNDEFINED ? true : finalFlagResult.asBoolean()) : isAllowCollideEntity(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), source, target);
	}
}
