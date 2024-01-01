package sawfowl.regionguard.listeners;

import java.util.Optional;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.blockray.RayTrace;
import org.spongepowered.api.util.blockray.RayTraceResult;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.server.ServerWorld;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionInteractItemEvent;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ListenerUtils;

public class InteractItemListener{

	private final RegionGuard plugin;
	private Cause cause;
	public InteractItemListener(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onInteract(InteractItemEvent.Secondary event, @Root Entity entity) {
		DataContainer container = event.itemStack().toContainer();
		if(container.get(DataQuery.of("UnsafeData")).isPresent() && container.get(DataQuery.of("UnsafeData")).get().toString().contains("WandItem")) return;
		ResourceKey worldKey = ((ServerWorld) entity.world()).key();
		Optional<ServerPlayer> optPlayer = event.cause().first(ServerPlayer.class);
		Optional<RayTraceResult<LocatableBlock>> blockRay = Optional.empty();
		String itemid = ListenerUtils.itemId(event.itemStack().createStack());
		if(optPlayer.isPresent() && plugin.getConfig().getTankItems().contains(itemid)) {
			blockRay = RayTrace.block()
					.world(optPlayer.get().world())
					.sourceEyePosition(optPlayer.get())
					.limit(7)
					.direction(optPlayer.get())
					.select(RayTrace.nonAir())
					.execute();
		}
		boolean liquidInteract = isLiquidInteract(blockRay);
		Region region = plugin.getAPI().findRegion(worldKey, liquidInteract ? blockRay.get().hitPosition().toInt() : entity.blockPosition());
		boolean isAllow = !liquidInteract ? isAllowInteractItem(region, entity,  event.itemStack().createStack()) : isAllowInteractItem(region, entity, event.itemStack().createStack()) && isAllowInteractBlockSecondary(region, entity, blockRay.get().selectedObject().blockState(), true) && isAllowBreak(region, entity, blockRay.get().selectedObject().blockState(), true) ;
		class InteractEvent implements RegionInteractItemEvent {

			boolean cancelled;
			Component message;
			@Override
			public Entity getSource() {
				return entity;
			}

			@Override
			public ItemStack getItemStack() {
				return event.itemStack().createStack();
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
			public InteractItemEvent.Secondary spongeEvent() {
				return event;
			}
			
		}
		RegionInteractItemEvent rgEvent = new InteractEvent();
		rgEvent.setCancelled(!isAllow);
		if(optPlayer.isPresent()) rgEvent.setMessage(liquidInteract ? plugin.getLocales().getComponent(optPlayer.get().locale(), LocalesPaths.CANCEL_BREAK) : plugin.getLocales().getComponent(optPlayer.get().locale(), LocalesPaths.INTERACT_ITEM));
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			if(rgEvent.getPlayer().isPresent() && rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		}
	}

	private boolean isAllowInteractItem(Region region, Entity entity, ItemStack itemStack) {
		if(region.isTrusted(entity.uniqueId())) return true;
		if(entity instanceof ServerPlayer && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.INTERACT_ITEM))) return true;
		for(String entityId : ListenerUtils.flagEntityArgs(entity)) {
			for(String itemid : ListenerUtils.flagItemArgs(itemStack)) {
				Tristate flagResult = region.getFlagResult(Flags.INTERACT_ITEM, entityId, itemid);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowInteractItem(plugin.getAPI().getGlobalRegion(region.getWorldKey()), entity, itemStack);
	}

	boolean isLiquidInteract(Optional<RayTraceResult<LocatableBlock>> blockRay) {
		return blockRay.isPresent() && blockRay.get().selectedObject().toContainer().toString().contains("level=");
	}

	private boolean isAllowInteractBlockSecondary(Region region, Entity entity, BlockState block, boolean first) {
		if(first && (region.isCurrentTrustType(entity.uniqueId(), TrustTypes.OWNER) || region.isCurrentTrustType(entity.uniqueId(), TrustTypes.BUILDER) || region.isCurrentTrustType(entity.uniqueId(), TrustTypes.MANAGER) || region.isCurrentTrustType(entity.uniqueId(), TrustTypes.USER))) return true;
		if(entity instanceof ServerPlayer && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.INTERACT_BLOCK_SECONDARY))) return true;
		for(String entityId : ListenerUtils.flagEntityArgs(entity)) {
			for(String blockid : ListenerUtils.flagBlockArgs(block)) {
				Tristate flagResult = region.getFlagResult(Flags.INTERACT_BLOCK_SECONDARY, entityId, blockid);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowInteractBlockSecondary(plugin.getAPI().getGlobalRegion(region.getWorldKey()), entity, block, false);
	}

	private boolean isAllowBreak(Region region, Entity entity, BlockState block, boolean first) {
		if(first && (entity != null && (region.isCurrentTrustType(entity.uniqueId(), TrustTypes.OWNER) || region.isCurrentTrustType(entity.uniqueId(), TrustTypes.BUILDER) || region.isCurrentTrustType(entity.uniqueId(), TrustTypes.MANAGER) || region.isCurrentTrustType(entity.uniqueId(), TrustTypes.USER)))) return true;
		if(entity != null && (entity instanceof ServerPlayer) && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.BLOCK_BREAK))) return true;
		for(String entityId : ListenerUtils.flagEntityArgs(entity)) {
			for(String blockid : ListenerUtils.flagBlockArgs(block)) {
				Tristate flagResult = region.getFlagResult(Flags.BLOCK_BREAK, entityId, blockid);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowBreak(plugin.getAPI().getGlobalRegion(region.getWorldKey()), entity, block, false);
	}

}
