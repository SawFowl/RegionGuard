package sawfowl.regionguard.listeners;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.MovementType;
import org.spongepowered.api.event.cause.entity.MovementTypes;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.world.RegionEntityChangeWorldEvent;
import sawfowl.regionguard.api.events.world.RegionMoveEntityEvent;
import sawfowl.regionguard.utils.ListenerUtils;

public class EntityMoveListener extends ManagementEvents {

	public EntityMoveListener(RegionGuard plugin) {
		super(plugin);
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onMove(MoveEntityEvent event) {
		double distance = distanceSquared(event.originalPosition(), event.originalDestinationPosition());
		boolean isRiding = event.entity().vehicle().isPresent();
		Entity ridingEntity = isRiding ? event.entity().vehicle().get().get() : null;
		ServerPlayer player = event.entity() instanceof ServerPlayer ? (ServerPlayer) event.entity() : null;
		if(player != null && plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).getLastWandLocation() != null) plugin.getAPI().getWorldEditCUIAPI().sendVisualDrag(player, player.blockPosition());
		if(!isRiding && distance <= 0.000015) return;
		Optional<MovementType> movementType = event.context().get(EventContextKeys.MOVEMENT_TYPE);
		boolean command = movementType.isPresent() && movementType.get() == MovementTypes.COMMAND.get();
		boolean enderPeal = movementType.isPresent() && movementType.get() == MovementTypes.ENDER_PEARL.get();
		boolean portal = movementType.isPresent() && movementType.get() == MovementTypes.PORTAL.get();
		boolean teleport = command || portal || enderPeal || movementType.isPresent() && (movementType.get() == MovementTypes.ENTITY_TELEPORT.get() || movementType.get() == MovementTypes.END_GATEWAY.get() || movementType.get() == MovementTypes.PORTAL.get());
		ServerWorld world = event.entity().serverLocation().world();
		Region from = plugin.getAPI().findRegion(world, event.originalPosition().toInt());
		Region destination = plugin.getAPI().findRegion(world, event.destinationPosition().toInt());
		boolean isAllowRiding = isRiding ? isAllowRidingEntity(from, event.entity(), ridingEntity) && (from.getUniqueId().equals(destination.getUniqueId()) ? true : isAllowRidingEntity(destination, event.entity(), ridingEntity)) : true;
		boolean isAllowPortalUse = portal ? isAllowPortalUse(event.entity(), from) : true;
		RegionMoveEntityEvent moveEvent = new RegionMoveEntityEvent() {

			boolean cancelled = false;
			boolean allowFly = true;
			boolean allowRiding = true;
			Component stopFly;
			Component stopRiding;
			@Override
			public Cause cause() {
				return cause;
			}

			@SuppressWarnings("unchecked")
			@Override
			public MoveEntityEvent getSpongeEvent() {
				return event;
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
			public Entity getEntity() {
				return event.entity();
			}

			@Override
			public Optional<ServerPlayer> getPlayer() {
				return Optional.ofNullable(player);
			}

			@Override
			public boolean isAllowFly() {
				return allowFly;
			}

			@Override
			public void setAllowFly(boolean allow) {
				allowFly = allow;
			}

			@Override
			public void setDestinationPosition(Vector3d vector3d) {
				event.setDestinationPosition(vector3d);
			}

			@Override
			public double getDistanceSquared() {
				return distance;
			}

			@Override
			public Region fromRegion() {
				return from;
			}

			@Override
			public Optional<Component> getStopFlyingMessage() {
				return Optional.ofNullable(stopFly);
			}

			@Override
			public void setStopFlyingMessage(Component component) {
				stopFly = component;
			}

			@Override
			public boolean isRiding() {
				return isRiding;
			}

			@Override
			public boolean isAllowRiding() {
				return allowRiding;
			}

			@Override
			public void setAllowRiding(boolean allow) {
				allowRiding = allow;
			}

			@Override
			public Optional<Entity> getRidingEntity() {
				return Optional.ofNullable(ridingEntity);
			}

			@Override
			public Optional<Component> getStopRidingMessage() {
				return Optional.ofNullable(stopRiding);
			}

			@Override
			public void setStopRidingMessage(Component component) {
				stopRiding = component;
			}

			@Override
			public boolean isPortal() {
				return portal;
			}

			@Override
			public ServerWorld getWorld() {
				return world;
			}
			
		};
		moveEvent.setAllowRiding(isAllowRiding);
		if(isRiding && !isAllowRiding && player != null) moveEvent.setStopRidingMessage(getEvents(player).getEntity().getRiding());
		if(player != null && !isAllowPlayerFly(player, from) && player.get(Keys.CAN_FLY).isPresent() && player.get(Keys.CAN_FLY).get()) {
			moveEvent.setAllowFly(false);
			moveEvent.setStopFlyingMessage(getEvents(player).getFly().getDisable());
		}
		ListenerUtils.postEvent(moveEvent);
		if(!moveEvent.isAllowRiding() && moveEvent.isRiding() && moveEvent.getRidingEntity().isPresent()) {
			event.setCancelled(true);
			Entity toRespawn = moveEvent.getRidingEntity().get().copy();
			moveEvent.getRidingEntity().get().remove();
			if(toRespawn != null && !ListenerUtils.entityId(toRespawn).equals("") && !ListenerUtils.entityId(toRespawn).contains(" ")) {
				moveEvent.fromRegion().setTempFlag(Flags.ENTITY_SPAWN, true, null, ListenerUtils.entityId(toRespawn));
				world.spawnEntity(toRespawn);
				moveEvent.fromRegion().removeTempFlag(Flags.ENTITY_SPAWN, null, ListenerUtils.entityId(toRespawn));
			}
			if(moveEvent.getPlayer().isPresent() && moveEvent.getStopRidingMessage().isPresent()) moveEvent.getPlayer().get().sendMessage(moveEvent.getStopRidingMessage().get());
		}
		if(!moveEvent.isAllowFly()) {
			moveEvent.getEntity().offer(Keys.CAN_FLY, false);
			moveEvent.getEntity().offer(Keys.IS_FLYING, false);
			if(moveEvent.getPlayer().isPresent() && moveEvent.getStopFlyingMessage().isPresent()) moveEvent.getPlayer().get().sendMessage(moveEvent.getStopFlyingMessage().get());
		}
		if(moveEvent.isCancelled()) event.setCancelled(true);
		if(from.equals(destination)) return;
		boolean allowFrom = true;
		boolean allowTo = true;
		boolean allowFly = true;
		Component message = null;
		if(teleport) {
			allowFrom = ((!from.containsFlag(Flags.ENTITY_TELEPORT_FROM) || !destination.containsFlag(Flags.ENTITY_TELEPORT_FROM)) && (from.getAllChilds().contains(destination) || destination.getAllChilds().contains(from))) || isAllowTeleportFrom(event.entity(), from);
			allowTo = ((!from.containsFlag(Flags.ENTITY_TELEPORT_FROM) || !destination.containsFlag(Flags.ENTITY_TELEPORT_FROM)) && (from.getAllChilds().contains(destination) || destination.getAllChilds().contains(from))) || isAllowTeleportTo(event.entity(), destination);
			if(player != null) {
				allowFly = isAllowPlayerFly(player, destination);
				if(command && event.cause().first(ServerPlayer.class).isPresent())  {
					ServerPlayer commandPlayer = event.cause().first(ServerPlayer.class).get();
					if(!allowTo && !commandPlayer.hasPermission(Permissions.bypassFlag(Flags.ENTITY_TELEPORT_TO)) && !destination.isTrusted(player.uniqueId())) {
						commandPlayer.sendMessage(getEvents(commandPlayer).getTeleport().getToRegion().getOther());
						event.setCancelled(true);
						return;
					} else if(!allowFrom && !commandPlayer.hasPermission(Permissions.bypassFlag(Flags.ENTITY_TELEPORT_FROM)) && !from.isTrusted(player.uniqueId())) {
						commandPlayer.sendMessage(getEvents(commandPlayer).getTeleport().getFromRegion().getOther());
						event.setCancelled(true);
						return;
					}
				}
				if(portal) {
					message = getEvents(player).getTeleport().getPortalUse();
				} else {
					if(!allowFrom) message = enderPeal ? getEvents(player).getTeleport().getFromRegion().getEnderpearl() : getEvents(player).getTeleport().getFromRegion().getSelf();
					if(!allowTo) message = enderPeal ? getEvents(player).getTeleport().getToRegion().getEnderpearl() : getEvents(player).getTeleport().getToRegion().getSelf();
				}
			}
		} else if(player != null) {
			allowFly = isAllowPlayerFly(player, destination);
			allowFrom = ((!destination.containsFlag(Flags.EXIT_CLAIM) || !from.containsFlag(Flags.EXIT_CLAIM)) && (from.getChilds().contains(destination) || destination.getChilds().contains(from))) || isAllowPlayerExit((ServerPlayer) event.entity(), from);
			allowTo = ((!destination.containsFlag(Flags.EXIT_CLAIM) || !from.containsFlag(Flags.EXIT_CLAIM)) && (from.getChilds().contains(destination) || destination.getChilds().contains(from))) || isAllowPlayerJoin((ServerPlayer) event.entity(), destination);
		}
		boolean finalAllowTo = allowTo;
		boolean finalAllowFrom = allowFrom;
		RegionMoveEntityEvent.ChangeRegion rgEvent = new RegionMoveEntityEvent.ChangeRegion() {

			boolean fly;
			boolean cancelled;
			boolean allowRiding = true;
			Component message;
			Component stopFlyingMessage;
			Component stopStopRidingMessage;
			@Override
			public Cause cause() {
				return cause;
			}

			@SuppressWarnings("unchecked")
			@Override
			public MoveEntityEvent getSpongeEvent() {
				return event;
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
			public Entity getEntity() {
				return event.entity();
			}

			@SuppressWarnings("unchecked")
			@Override
			public Optional<ServerPlayer> getPlayer() {
				return Optional.ofNullable(player);
			}

			@Override
			public boolean isAllowFrom() {
				return finalAllowFrom;
			}

			@Override
			public boolean isAllowTo() {
				return finalAllowTo;
			}

			@Override
			public boolean isAllowFly() {
				return fly;
			}

			@Override
			public void setAllowFly(boolean allow) {
				fly = allow;
			}

			@Override
			public void setDestinationPosition(Vector3d vector3d) {
				event.setDestinationPosition(vector3d);
			}

			@Override
			public double getDistanceSquared() {
				return distance;
			}

			@Override
			public Region fromRegion() {
				return from;
			}

			@Override
			public Region toRegion() {
				return destination;
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
			public Optional<Component> getStopFlyingMessage() {
				return Optional.ofNullable(stopFlyingMessage);
			}

			@Override
			public void setStopFlyingMessage(Component component) {
				stopFlyingMessage = component;
			}

			@Override
			public boolean isRiding() {
				return isRiding;
			}

			@Override
			public boolean isAllowRiding() {
				return allowRiding;
			}

			@Override
			public void setAllowRiding(boolean allow) {
				allowRiding = allow;
			}

			@Override
			public Optional<Entity> getRidingEntity() {
				return Optional.ofNullable(ridingEntity);
			}

			@Override
			public Optional<Component> getStopRidingMessage() {
				return Optional.ofNullable(stopStopRidingMessage);
			}

			@Override
			public void setStopRidingMessage(Component component) {
				stopStopRidingMessage = component;
			}

			@Override
			public boolean isPortal() {
				return portal;
			}

			@Override
			public ServerWorld getWorld() {
				return world;
			}
		};
		rgEvent.setMessage(message);
		if(!allowTo || !allowFrom || !isAllowPortalUse) {
			rgEvent.setCancelled(true);
			if(!portal) {
				if(!allowTo && player != null) rgEvent.setMessage(getEvents(player).getMove().getJoin());
				if(!allowFrom && player != null) rgEvent.setMessage(getEvents(player).getMove().getExit());
			}
		}
		if(!allowFly && player != null) rgEvent.setStopFlyingMessage(getEvents(player).getFly().getDisableOnJoin());
		if(isRiding && !isAllowRiding && player != null) rgEvent.setStopRidingMessage(getEvents(player).getEntity().getRiding());
		rgEvent.setDestinationPosition(event.destinationPosition());
		rgEvent.setAllowFly(allowFly);
		rgEvent.setAllowRiding(isAllowRiding);
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			if(rgEvent.getPlayer().isPresent()) {
				if(enderPeal) pearlBack(rgEvent.getPlayer().get(), event.originalPosition());
				if(rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
			}
		} else {
			if(rgEvent.getPlayer().isPresent()) {
				if(rgEvent.fromRegion().getExitMessage(rgEvent.getPlayer().get().locale()).isPresent()) rgEvent.getPlayer().get().showTitle(Title.title(rgEvent.fromRegion().getPlainName(rgEvent.getPlayer().get().locale()).isPresent() ? rgEvent.fromRegion().getName(rgEvent.getPlayer().get().locale()) : rgEvent.fromRegion().getOwnerData().asComponent(rgEvent.getPlayer().get()), rgEvent.fromRegion().getExitMessage(rgEvent.getPlayer().get().locale()).get()));
				if(rgEvent.toRegion().getJoinMessage(rgEvent.getPlayer().get().locale()).isPresent()) rgEvent.getPlayer().get().showTitle(Title.title(rgEvent.toRegion().getPlainName(rgEvent.getPlayer().get().locale()).isPresent() ? rgEvent.toRegion().getName(rgEvent.getPlayer().get().locale()) : rgEvent.toRegion().getOwnerData().asComponent(rgEvent.getPlayer().get()), rgEvent.toRegion().getJoinMessage(rgEvent.getPlayer().get().locale()).get()));
			}
		}
		if(rgEvent.getPlayer().isPresent() && !rgEvent.isAllowFly() && rgEvent.getPlayer().get().get(Keys.CAN_FLY).isPresent() && rgEvent.getPlayer().get().get(Keys.CAN_FLY).get()) {
			rgEvent.getPlayer().get().offer(Keys.CAN_FLY, false);
			rgEvent.getPlayer().get().offer(Keys.IS_FLYING, false);
			if(rgEvent.getStopFlyingMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getStopFlyingMessage().get());
		}
		if(!rgEvent.isAllowRiding() && rgEvent.isRiding() && rgEvent.getRidingEntity().isPresent()) {
			event.setCancelled(true);
			Entity toRespawn = rgEvent.getRidingEntity().get().copy();
			rgEvent.getRidingEntity().get().remove();
			if(toRespawn != null && !ListenerUtils.entityId(toRespawn).equals("") && !ListenerUtils.entityId(toRespawn).contains(" ")) {
				rgEvent.fromRegion().setTempFlag(Flags.ENTITY_SPAWN, true, null, ListenerUtils.entityId(toRespawn));
				rgEvent.toRegion().setTempFlag(Flags.ENTITY_SPAWN, true, null, ListenerUtils.entityId(toRespawn));
				world.spawnEntity(toRespawn);
				rgEvent.fromRegion().removeTempFlag(Flags.ENTITY_SPAWN, null, ListenerUtils.entityId(toRespawn));
				rgEvent.toRegion().removeTempFlag(Flags.ENTITY_SPAWN, null, ListenerUtils.entityId(toRespawn));
			}
			if(rgEvent.getPlayer().isPresent() && rgEvent.getStopRidingMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getStopRidingMessage().get());
		}
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onWorldChange(ChangeEntityWorldEvent.Reposition event) {
		Entity entity = event.entity();
		Optional<ServerPlayer> optPlayer = event.cause().first(ServerPlayer.class);
		Optional<ServerPlayer> optSourcePlayer = entity instanceof ServerPlayer ? Optional.ofNullable((ServerPlayer) entity) : Optional.empty();
		Region from = plugin.getAPI().findRegion(event.originalWorld(), event.originalPosition().toInt());
		Region to = plugin.getAPI().findRegion(event.destinationWorld(), event.destinationPosition().toInt());
		boolean isAllowFrom = optPlayer.isPresent() && !optPlayer.get().uniqueId().equals(entity.uniqueId()) ? isAllowTeleportFrom(optPlayer.get(), from) : isAllowTeleportFrom(entity, from);
		boolean isAllowTo = optPlayer.isPresent() && !optPlayer.get().uniqueId().equals(entity.uniqueId()) ? isAllowTeleportTo(optPlayer.get(), from) : isAllowTeleportTo(entity, to);
		boolean isAllowFly = optPlayer.isPresent() && !optPlayer.get().uniqueId().equals(entity.uniqueId()) ? isAllowPlayerFly(optPlayer.get(), from) : (optSourcePlayer.isPresent() ? isAllowPlayerFly(optSourcePlayer.get(), to) : true);
		if(optPlayer.isPresent() && optSourcePlayer.isPresent() && !optPlayer.get().uniqueId().equals(optSourcePlayer.get().uniqueId())) {
			if(!isAllowFrom) {
				optPlayer.get().sendMessage(getEvents(optPlayer.get()).getTeleport().getFromRegion().getOther());
				event.setCancelled(true);
				return;
			}
			if(!isAllowTo) {
				optPlayer.get().sendMessage(getEvents(optPlayer.get()).getTeleport().getToRegion().getOther());
				event.setCancelled(true);
				return;
			}
		}
		RegionEntityChangeWorldEvent rgEvent = new RegionEntityChangeWorldEvent() {
			Component message;
			Component stopFly;
			boolean canceled;
			boolean allowFly;
			@Override
			public void setMessage(Component message) {
				this.message = message;
			}

			@Override
			public Optional<Component> getMessage() {
				return Optional.ofNullable(message);
			}

			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public boolean isAllowFrom() {
				return isAllowFrom;
			}

			@Override
			public boolean isAllowTo() {
				return isAllowTo;
			}

			@Override
			public Region fromRegion() {
				return from;
			}

			@Override
			public Region toRegion() {
				return to;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Optional<ServerPlayer> getPlayer() {
				return optPlayer;
			}

			@Override
			public boolean isAllowFly() {
				return allowFly;
			}

			@Override
			public void setAllowFly(boolean allow) {
				allowFly = allow;
			}

			@Override
			public Optional<Component> getStopFlyingMessage() {
				return Optional.ofNullable(stopFly);
			}

			@Override
			public void setStopFlyingMessage(Component component) {
				stopFly = component;
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
			public Object source() {
				return null;
			}

			@Override
			public ServerWorld getWorld() {
				return event.originalWorld();
			}

			@SuppressWarnings("unchecked")
			@Override
			public ChangeEntityWorldEvent.Reposition getSpongeEvent() {
				return event;
			}

			@Override
			public ServerWorld getTargetWorld() {
				return event.destinationWorld();
			}
			
		};
		rgEvent.setCancelled(!isAllowFrom || !isAllowTo);
		rgEvent.setAllowFly(isAllowFly);
		if(optPlayer.isPresent() && !isAllowFly) rgEvent.setStopFlyingMessage(getEvents(optPlayer.get()).getFly().getDisable());
		if(rgEvent.isCancelled() && optPlayer.isPresent()) rgEvent.setMessage(!isAllowFrom ? getEvents(optPlayer.get()).getTeleport().getFromRegion().getSelf() : getEvents(optPlayer.get()).getTeleport().getToRegion().getSelf());
		ListenerUtils.postEvent(rgEvent);
		if(!rgEvent.isAllowFly() && rgEvent.getPlayer().isPresent()) {
			rgEvent.getPlayer().get().offer(Keys.CAN_FLY, false);
			rgEvent.getPlayer().get().offer(Keys.IS_FLYING, false);
			if(rgEvent.getStopFlyingMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getStopFlyingMessage().get());
		}
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			if(rgEvent.getPlayer().isPresent() && rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		} else {
			if(rgEvent.getPlayer().isPresent()) {
				Optional<Component> exit = from.getExitMessage(rgEvent.getPlayer().get().locale());
				Optional<Component> join = from.getJoinMessage(rgEvent.getPlayer().get().locale());
				if(exit.isPresent()) rgEvent.getPlayer().get().sendMessage(exit.get());
				if(join.isPresent()) rgEvent.getPlayer().get().sendMessage(join.get());
			}
		}
	}

	private void pearlBack(ServerPlayer player, Vector3d vector3d) {
		Sponge.server().scheduler().submit(Task.builder().plugin(plugin.getPluginContainer()).execute(() -> {
			player.setPosition(vector3d);
		}).build());
		if(player.gameMode().get() != GameModes.CREATIVE.get()) player.inventory().offer(ItemStack.of(ItemTypes.ENDER_PEARL.get()));
	}

	private double distanceSquared(Vector3d original, Vector3d destination) {
		return original.distanceSquared(destination);
	}

	private boolean isAllowTeleportFrom(Entity entity, Region region) {
		if(region.isTrusted(entity.uniqueId())) return true;
		if((entity instanceof ServerPlayer) && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.ENTITY_TELEPORT_FROM))) return true;
		for(String source : ListenerUtils.flagEntityArgs(entity)) {
			Tristate flagResult = region.getFlagResult(Flags.ENTITY_TELEPORT_FROM, source, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowTeleportFrom(entity, plugin.getAPI().getGlobalRegion(region.getWorldKey()));
	}

	private boolean isAllowPortalUse(Entity entity, Region region) {
		if(region.isTrusted(entity.uniqueId())) return true;
		if((entity instanceof ServerPlayer) && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.PORTAL_USE))) return true;
		for(String targetid : ListenerUtils.flagEntityArgs(entity)) {
			Tristate flagResult = region.getFlagResult(Flags.PORTAL_USE, targetid, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowPortalUse(entity, plugin.getAPI().getGlobalRegion(region.getWorldKey()));
	}

	private boolean isAllowTeleportTo(Entity entity, Region region) {
		if(region.isTrusted(entity.uniqueId())) return true;
		if((entity instanceof ServerPlayer) && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.ENTITY_TELEPORT_TO))) return true;
		for(String source : ListenerUtils.flagEntityArgs(entity)) {
			Tristate flagResult = region.getFlagResult(Flags.ENTITY_TELEPORT_TO, source, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowTeleportTo(entity, plugin.getAPI().getGlobalRegion(region.getWorldKey()));
	}

	private boolean isAllowPlayerJoin(ServerPlayer player, Region region) {
		if(region.isTrusted(player)) return true;
		if(player.hasPermission(Permissions.bypassFlag(Flags.ENTER_CLAIM))) return true;
		for(String targetid : ListenerUtils.flagEntityArgs(player)) {
			Tristate flagResult = region.getFlagResultWhithoutParrents(Flags.ENTER_CLAIM, targetid, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowPlayerJoin(player, plugin.getAPI().getGlobalRegion(region.getWorldKey()));
	}

	private boolean isAllowPlayerExit(ServerPlayer player, Region region) {
		if(region.isTrusted(player)) return true;
		if(player.hasPermission(Permissions.bypassFlag(Flags.EXIT_CLAIM))) return true;
		for(String targetid : ListenerUtils.flagEntityArgs(player)) {
			Tristate flagResult = region.getFlagResultWhithoutParrents(Flags.EXIT_CLAIM, targetid, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowPlayerExit(player, plugin.getAPI().getGlobalRegion(region.getWorldKey()));
	}

	private boolean isAllowPlayerFly(ServerPlayer player, Region region) {
		if(region.isTrusted(player)) return true;
		if(player.hasPermission(Permissions.bypassFlag(Flags.ALLOW_FLY))) return true;
		for(String targetid : ListenerUtils.flagEntityArgs(player)) {
			Tristate flagResult = region.getFlagResult(Flags.ALLOW_FLY, targetid, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowPlayerFly(player, plugin.getAPI().getGlobalRegion(region.getWorldKey()));
	}

	private boolean isAllowRidingEntity(Region region, Entity source, Entity vehicle) {
		if(source instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) source;
			if(player.hasPermission(Permissions.bypassFlag(Flags.ETITY_RIDING))) return true;
		}
		for(String sourceid : ListenerUtils.flagEntityArgs(source)) {
			for(String targetid : ListenerUtils.flagEntityArgs(vehicle)) {
				Tristate flagResult = region.getFlagResult(Flags.ETITY_RIDING, sourceid, targetid);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowRidingEntity(plugin.getAPI().getGlobalRegion(region.getWorldKey()), source, vehicle);
	}

}
