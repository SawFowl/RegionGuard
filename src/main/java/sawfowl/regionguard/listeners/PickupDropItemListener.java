package sawfowl.regionguard.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent.Pickup.Pre;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.server.ServerWorld;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.world.RegionChangeInventoryEvent;
import sawfowl.regionguard.utils.ListenerUtils;

public class PickupDropItemListener extends ManagementEvents {

	public PickupDropItemListener(RegionGuard plugin) {
		super(plugin);
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onPickup(ChangeInventoryEvent.Pickup.Pre event, @First Entity entity) {
		if(entity.get(Keys.HEALTH).isPresent() && entity.get(Keys.HEALTH).get() <= 0) return;
		ServerWorld world = entity.serverLocation().world();
		Region region = plugin.getAPI().findRegion(world, entity.blockPosition());
		List<ItemStackSnapshot> items = event.finalStacks();
		boolean allowPickup = isAllowItemPickup(region, entity, items);
		Optional<ServerPlayer> optPlayer = entity instanceof ServerPlayer ? Optional.ofNullable((ServerPlayer) entity) : Optional.empty();
		class DropEvent implements RegionChangeInventoryEvent.Pickup {

			boolean cancelled;
			Component message;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public boolean isCancelled() {
				return cancelled;
			}

			@Override
			public void setCancelled(boolean cancel) {
				cancelled = cancel;
			}

			@Override
			public Entity getSource() {
				return entity;
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public boolean isAllow() {
				return allowPickup;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Optional<ServerPlayer> getPlayer() {
				return optPlayer;
			}

			@Override
			public Optional<Component> getMessage() {
				return Optional.ofNullable(message);
			}

			@Override
			public void setMessage(Component component) {
				message = component;
			}

			@Override
			public ChangeInventoryEvent.Pickup.Pre spongeEvent() {
				return event;
			}

			@Override
			public Object source() {
				return entity;
			}

			@Override
			public ServerWorld getWorld() {
				return world;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Pre getSpongeEvent() {
				return event;
			}

		}
		RegionChangeInventoryEvent rgEvent = new DropEvent();
		rgEvent.setCancelled(!allowPickup);
		if(optPlayer.isPresent() && rgEvent.isCancelled()) rgEvent.setMessage(getEvents(optPlayer.get().locale()).getItem().getPickup());
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			if(rgEvent.getPlayer().isPresent()) {
				if(rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
			}
		}
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onDrop(ChangeInventoryEvent.Drop event, @First Entity entity) {
		if(entity.get(Keys.HEALTH).isPresent() && entity.get(Keys.HEALTH).get() <= 0) return;
		ServerWorld world = entity.serverLocation().world();
		Region region = plugin.getAPI().findRegion(world, entity.blockPosition());
		List<ItemStackSnapshot> items = new ArrayList<ItemStackSnapshot>();
		for(SlotTransaction slotTransaction : event.transactions()) {
			items.add(slotTransaction.original());
		}
		boolean allowDrop = isAllowItemDrop(region, entity, items);
		Optional<ServerPlayer> optPlayer = entity instanceof ServerPlayer ? Optional.ofNullable((ServerPlayer) entity) : Optional.empty();
		class DropEvent implements RegionChangeInventoryEvent.Drop {

			boolean cancelled;
			Component message;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public boolean isCancelled() {
				return cancelled;
			}

			@Override
			public void setCancelled(boolean cancel) {
				cancelled = cancel;
			}

			@Override
			public Entity getSource() {
				return entity;
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public boolean isAllow() {
				return allowDrop;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Optional<ServerPlayer> getPlayer() {
				return optPlayer;
			}

			@Override
			public Optional<Component> getMessage() {
				return Optional.ofNullable(message);
			}

			@Override
			public void setMessage(Component component) {
				message = component;
			}

			@Override
			public ChangeInventoryEvent.Drop spongeEvent() {
				return event;
			}

			@Override
			public Object source() {
				return null;
			}

			@Override
			public ServerWorld getWorld() {
				return world;
			}

			@SuppressWarnings("unchecked")
			@Override
			public ChangeInventoryEvent.Drop getSpongeEvent() {
				return event;
			}
			
		}
		RegionChangeInventoryEvent rgEvent = new DropEvent();
		rgEvent.setCancelled(!allowDrop);
		if(optPlayer.isPresent() && rgEvent.isCancelled()) rgEvent.setMessage(getEvents(optPlayer.get().locale()).getItem().getDrop());
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			if(rgEvent.getPlayer().isPresent()) {
				if(rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
			}
		}
	}

	private boolean isAllowItemPickup(Region region, Entity source, List<ItemStackSnapshot> snapshots) {
		if(source instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) source;
			if(player.hasPermission(Permissions.bypassFlag(Flags.ITEM_PICKUP)) || region.isCurrentTrustType(player, TrustTypes.USER) || region.isCurrentTrustType(player, TrustTypes.MANAGER) || region.isCurrentTrustType(player, TrustTypes.OWNER)) return true;
		}
		for(String entityId : ListenerUtils.flagEntityArgs(source)) {
			for(String itemid : ListenerUtils.flagItemsSnapshotsArgs(snapshots)) {
				Tristate flagResult = region.getFlagResult(Flags.ITEM_PICKUP, entityId, itemid);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowItemPickup(plugin.getAPI().getGlobalRegion(region.getWorldKey()), source, snapshots);
	}

	private boolean isAllowItemDrop(Region region, Entity source, List<ItemStackSnapshot> snapshots) {
		if(source instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) source;
			if(player.hasPermission(Permissions.bypassFlag(Flags.ITEM_DROP)) || region.isCurrentTrustType(player, TrustTypes.USER) || region.isCurrentTrustType(player, TrustTypes.MANAGER) || region.isCurrentTrustType(player, TrustTypes.OWNER)) return true;
		}
		for(String entityId : ListenerUtils.flagEntityArgs(source)) {
			for(String itemid : ListenerUtils.flagItemsSnapshotsArgs(snapshots)) {
				Tristate flagResult = region.getFlagResult(Flags.ITEM_DROP, entityId, itemid);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowItemDrop(plugin.getAPI().getGlobalRegion(region.getWorldKey()), source, snapshots);
	}

}
