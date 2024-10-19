package sawfowl.regionguard.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.server.ServerWorld;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.world.RegionInteractEntityEvent;
import sawfowl.regionguard.utils.ListenerUtils;

public class InteractEntityListener extends ManagementEvents {

	public InteractEntityListener(RegionGuard plugin) {
		super(plugin);
	}

	Map<UUID, Long> lastTime = new HashMap<UUID, Long>();

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onPrimary(InteractEntityEvent.Primary event, @Root ServerPlayer player) {
		Region region = plugin.getAPI().findRegion(player.world(), event.entity().blockPosition());
		boolean isAllow = isAllowPrimary(region, player, event.entity());
		RegionInteractEntityEvent.Primary rgEvent = new RegionInteractEntityEvent.Primary() {

			boolean canceled;
			Component message;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public void setMessage(Component message) {
				this.message = message;
			}

			@Override
			public Optional<Component> getMessage() {
				return Optional.ofNullable(message);
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public boolean isCancelled() {
				return canceled;
			}

			@Override
			public void setCancelled(boolean cancel) {
				canceled = cancel;
			}

			@Override
			public Entity getEntity() {
				return event.entity();
			}

			@SuppressWarnings("unchecked")
			@Override
			public ServerPlayer getPlayer() {
				return player;
			}

			@Override
			public boolean isAllowInteract() {
				return isAllow;
			}

			@Override
			public ServerWorld getWorld() {
				return player.world();
			}

			@SuppressWarnings("unchecked")
			@Override
			public InteractEntityEvent.Primary getSpongeEvent() {
				return event;
			}
		};
		rgEvent.setCancelled(!isAllow);
		rgEvent.setMessage(getEvents(player).getEntity().getInteract().getPrimary());
		ListenerUtils.postEvent(rgEvent);
		if(!rgEvent.isCancelled()) return;
		event.setCancelled(true);
		if(rgEvent.getMessage().isPresent()) player.sendMessage(rgEvent.getMessage().get());
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onSecondary(InteractEntityEvent.Secondary event, @Root ServerPlayer player) {
		long time = System.currentTimeMillis();
		if(lastTime.containsKey(player.uniqueId()) && time - lastTime.get(player.uniqueId()) < 200) return;
		if(lastTime.containsKey(player.uniqueId())) lastTime.remove(player.uniqueId());
		lastTime.put(player.uniqueId(), time);
		Region region = plugin.getAPI().findRegion(player.world(), event.entity().blockPosition());
		boolean isAllow = isAllowSecondary(region, player, event.entity());
		RegionInteractEntityEvent rgEvent = new RegionInteractEntityEvent.Secondary() {

			boolean canceled;
			Component message;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public void setMessage(Component message) {
				this.message = message;
			}

			@Override
			public Optional<Component> getMessage() {
				return Optional.ofNullable(message);
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public boolean isCancelled() {
				return canceled;
			}

			@Override
			public void setCancelled(boolean cancel) {
				canceled = cancel;
			}

			@Override
			public Entity getEntity() {
				return event.entity();
			}

			@SuppressWarnings("unchecked")
			@Override
			public ServerPlayer getPlayer() {
				return player;
			}

			@Override
			public boolean isAllowInteract() {
				return isAllow;
			}

			@Override
			public ServerWorld getWorld() {
				return player.world();
			}

			@SuppressWarnings("unchecked")
			@Override
			public InteractEntityEvent.Secondary getSpongeEvent() {
				return event;
			}
		};
		rgEvent.setCancelled(!isAllow);
		rgEvent.setMessage(getEvents(player).getEntity().getInteract().getSecondary());
		ListenerUtils.postEvent(rgEvent);
		if(!rgEvent.isCancelled()) return;
		event.setCancelled(true);
		if(rgEvent.getMessage().isPresent()) player.sendMessage(rgEvent.getMessage().get());
	}

	private boolean isAllowPrimary(Region region, ServerPlayer player, Entity entity) {
		if((region.isCurrentTrustType(player, TrustTypes.HUNTER) && (ListenerUtils.entityCategory(entity).contains("monster") || ListenerUtils.entityCategory(entity).contains("hostile"))) || region.isCurrentTrustType(player, TrustTypes.USER) || region.isCurrentTrustType(player, TrustTypes.MANAGER) || region.isCurrentTrustType(player, TrustTypes.OWNER)) return true;
		for(String entityId : ListenerUtils.flagEntityArgs(player)) {
			for(String blockId : ListenerUtils.flagEntityArgs(entity)) {
				Tristate flagResult = region.getFlagResult(Flags.INTERACT_ENTITY_PRIMARY, entityId, blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowPrimary(plugin.getAPI().getGlobalRegion(region.getWorldKey()), player, entity);
	}

	private boolean isAllowSecondary(Region region, ServerPlayer player, Entity entity) {
		if((region.isCurrentTrustType(player, TrustTypes.HUNTER) && (ListenerUtils.entityCategory(entity).contains("monster") || ListenerUtils.entityCategory(entity).contains("hostile"))) || region.isCurrentTrustType(player, TrustTypes.USER) || region.isCurrentTrustType(player, TrustTypes.MANAGER) || region.isCurrentTrustType(player, TrustTypes.OWNER)) return true;
		for(String entityId : ListenerUtils.flagEntityArgs(player)) {
			for(String blockId : ListenerUtils.flagEntityArgs(entity)) {
				Tristate flagResult = region.getFlagResult(Flags.INTERACT_ENTITY_SECONDARY, entityId, blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowSecondary(plugin.getAPI().getGlobalRegion(region.getWorldKey()), player, entity);
	}

}
