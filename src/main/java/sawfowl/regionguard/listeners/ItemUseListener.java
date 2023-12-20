package sawfowl.regionguard.listeners;

import java.util.Optional;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.server.ServerWorld;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionUseItemStackEvent;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ListenerUtils;

public class ItemUseListener {

	private final RegionGuard plugin;
	private Cause cause;
	public ItemUseListener(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onUse(UseItemStackEvent.Start event, @Root Entity entity) {
		DataContainer container = event.itemStackInUse().toContainer();
		if(container.get(DataQuery.of("UnsafeData")).isPresent() && container.get(DataQuery.of("UnsafeData")).get().toString().contains("WandItem")) return;
		ResourceKey worldKey = ((ServerWorld) entity.world()).key();
		Region region = plugin.getAPI().findRegion(worldKey, entity.blockPosition());
		boolean isAllow = isAllowUse(region, entity, event.itemStackInUse().createStack());
		Optional<ServerPlayer> optPlayer = event.cause().first(ServerPlayer.class);
		class UseEvent implements RegionUseItemStackEvent {

			boolean cancelled;
			Component message;
			@Override
			public Entity getEntity() {
				return entity;
			}

			@Override
			public ItemStack getItemStack() {
				return event.itemStackInUse().createStack();
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public boolean isAllow() {
				return isAllow;
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
			public UseItemStackEvent.Start spongeEvent() {
				return event;
			}
			
		}
		RegionUseItemStackEvent rgEvent = new UseEvent();
		rgEvent.setCancelled(!isAllow);
		if(optPlayer.isPresent()) rgEvent.setMessage(plugin.getLocales().getText(optPlayer.get().locale(), LocalesPaths.ITEM_USE));
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			if(rgEvent.getPlayer().isPresent() && rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		}
	}

	private boolean isAllowUse(Region region, Entity entity, ItemStack itemStack) {
		if(region.isTrusted(entity.uniqueId())) return true;
		if(entity instanceof ServerPlayer && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.ITEM_USE))) return true;
		for(String entityId : ListenerUtils.flagEntityArgs(entity)) {
			for(String itemid : ListenerUtils.flagItemArgs(itemStack)) {
				Tristate flagResult = region.getFlagResult(Flags.ITEM_USE, entityId, itemid);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowUse(plugin.getAPI().getGlobalRegion(region.getWorldKey()), entity, itemStack);
	}

}
