package sawfowl.regionguard.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.util.Tristate;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionChangeInventoryEvent;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ListenerUtils;

public class PickupDropItemListener {

	private final RegionGuard plugin;
	private Cause cause;
	public PickupDropItemListener(RegionGuard plugin) {
		this.plugin = plugin;
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onPickup(ChangeInventoryEvent.Pickup event) {
		if(!event.cause().first(Entity.class).isPresent()) return;
		Entity entity = event.cause().first(Entity.class).get();
		if(entity.get(Keys.HEALTH).isPresent() && entity.get(Keys.HEALTH).get() <= 0) return;
		ResourceKey worldKey = entity.createSnapshot().world();
		Region region = plugin.getAPI().findRegion(worldKey, entity.blockPosition());
		List<ItemStackSnapshot> items = new ArrayList<ItemStackSnapshot>();
		for(SlotTransaction slotTransaction : event.transactions()) {
			items.add(slotTransaction.original());
		}
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
			public ChangeInventoryEvent.Pickup spongeEvent() {
				return event;
			}
			
		}
		RegionChangeInventoryEvent rgEvent = new DropEvent();
		rgEvent.setCancelled(!allowPickup);
		if(optPlayer.isPresent() && rgEvent.isCancelled()) rgEvent.setMessage(plugin.getLocales().getText(optPlayer.get().locale(), LocalesPaths.ITEM_PICKUP));
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			if(rgEvent.getPlayer().isPresent()) {
				if(rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
			}
		}
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onDrop(ChangeInventoryEvent.Drop event) {
		if(!event.cause().first(Entity.class).isPresent()) return;
		Entity entity = event.cause().first(Entity.class).get();
		if(entity.get(Keys.HEALTH).isPresent() && entity.get(Keys.HEALTH).get() <= 0) return;
		ResourceKey worldKey = entity.createSnapshot().world();
		Region region = plugin.getAPI().findRegion(worldKey, entity.blockPosition());
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
			
		}
		RegionChangeInventoryEvent rgEvent = new DropEvent();
		rgEvent.setCancelled(!allowDrop);
		if(optPlayer.isPresent() && rgEvent.isCancelled()) rgEvent.setMessage(plugin.getLocales().getText(optPlayer.get().locale(), LocalesPaths.ITEM_DROP));
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
		return region.isGlobal() ? true : isAllowItemPickup(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), source, snapshots);
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
		return region.isGlobal() ? true : isAllowItemDrop(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), source, snapshots);
	}

}
