package sawfowl.regionguard.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.FallingBlock;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent.All;
import org.spongepowered.api.event.block.ChangeBlockEvent.Pre;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.common.world.server.SpongeLocatableBlock;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionCreateEvent;
import sawfowl.regionguard.api.events.RegionResizeEvent;
import sawfowl.regionguard.api.events.world.RegionChangeBlockEvent;
import sawfowl.regionguard.api.events.world.RegionExplosionEvent;
import sawfowl.regionguard.api.events.world.RegionInteractBlockEvent;
import sawfowl.regionguard.api.events.world.RegionPistonEvent;
import sawfowl.regionguard.utils.ListenerUtils;

public class BlockAndWorldChangeListener extends ManagementEvents {

	public BlockAndWorldChangeListener(RegionGuard plugin) {
		super(plugin);
	}

	
	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onPrimary(InteractBlockEvent.Primary.Start event, @Root Entity entity) {
		ServerPlayer player = event.source() instanceof ServerPlayer ? (ServerPlayer) event.source() : null;
		Region region = plugin.getAPI().findRegion(entity.serverLocation().world(), event.block().position());
		if(player != null && resizeOrCreateRegion(player, event.block().position(), region)) {
			event.setCancelled(true);
			return;
		}
		boolean allow = isAllowInteractBlockPrimary(entity, region, event.block(), true);
		RegionInteractBlockEvent rgEvent = new RegionInteractBlockEvent() {

			Component message;
			boolean cancelled;
			@Override
			public Cause cause() {
				return cause;
			}

			@SuppressWarnings("unchecked")
			@Override
			public InteractBlockEvent getSpongeEvent() {
				return event;
			}

			@Override
			public Entity getEntity() {
				return entity;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Optional<ServerPlayer> getPlayer() {
				return Optional.ofNullable(player);
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public boolean isPrimary() {
				return true;
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
			public boolean isAllowInteract() {
				return allow;
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
			public ServerWorld getWorld() {
				return entity.serverLocation().world();
			}
			
		};
		rgEvent.setCancelled(!allow);
		if(player != null) rgEvent.setMessage(getEvents(player).getBlock().getInteract().getPrimary());
		ListenerUtils.postEvent(rgEvent);
		event.setCancelled(rgEvent.isCancelled());
		if(rgEvent.isCancelled() && player != null && rgEvent.getMessage().isPresent()) {
			player.sendMessage(rgEvent.getMessage().get());
		}
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onSecondary(InteractBlockEvent.Secondary event, @Root Entity entity) {
		ServerPlayer player = event.source() instanceof ServerPlayer ? (ServerPlayer) event.source() : null;
		Region region = plugin.getAPI().findRegion(entity.serverLocation().world(), event.block().position());
		if(player != null && getRegionInfo(player, region)) {
			event.setCancelled(true);
			return;
		}
		boolean allow = isAllowInteractBlockSecondary(entity, region, event.block(), true);
		RegionInteractBlockEvent rgEvent = new RegionInteractBlockEvent() {

			Component message;
			boolean cancelled;
			@Override
			public Cause cause() {
				return cause;
			}

			@SuppressWarnings("unchecked")
			@Override
			public InteractBlockEvent getSpongeEvent() {
				return event;
			}

			@Override
			public Entity getEntity() {
				return entity;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Optional<ServerPlayer> getPlayer() {
				return Optional.ofNullable(player);
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public boolean isPrimary() {
				return false;
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
			public boolean isAllowInteract() {
				return allow;
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
			public ServerWorld getWorld() {
				return entity.serverLocation().world();
			}

		};
		rgEvent.setCancelled(!allow);
		if(player != null) rgEvent.setMessage(getEvents(player).getBlock().getInteract().getSecondary());
		ListenerUtils.postEvent(rgEvent);
		event.setCancelled(rgEvent.isCancelled());
		if(rgEvent.isCancelled() && player != null && rgEvent.getMessage().isPresent()) {
			player.sendMessage(rgEvent.getMessage().get());
		}
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onPreChange(ChangeBlockEvent.Pre event) {
		if(event.locations().size() == 1) return;
		if(event.source() instanceof SpongeLocatableBlock && ListenerUtils.isPiston(((SpongeLocatableBlock) event.source()).location().createSnapshot())) {
			onPistonMove(event, ((SpongeLocatableBlock) event.source()).location().createSnapshot());
		} else {
			event.locations().stream().filter(l -> (ListenerUtils.isPiston(l.createSnapshot()))).findFirst().ifPresent(location -> {
				onPistonMove(event, location.createSnapshot());
			});
		}
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onBlockChange(ChangeBlockEvent.All event) {
		if(event.isCancelled()) return;
		boolean isPlayer = event.source() instanceof ServerPlayer;
		ServerPlayer player = isPlayer ? (ServerPlayer) event.source() : null;
		if(isPlayer) {
			DataContainer container = player.itemInHand(HandTypes.MAIN_HAND.get()).toContainer();
			if(container.get(DataQuery.of("UnsafeData")).isPresent() && container.get(DataQuery.of("UnsafeData")).get().toString().contains("WandItem")) {
				event.setCancelled(true);
				return;
			}
		}
		if(ListenerUtils.isDecay(event.transactions())) {
			BlockTransaction blockTransaction = ListenerUtils.getTransaction(event.transactions(), Operations.DECAY.get());
			Region region = plugin.getAPI().findRegion(event.world(), blockTransaction.defaultReplacement().position());
			if(!isAllowDecay(region, blockTransaction)) event.setCancelled(true);
			return;
		}
		if(ListenerUtils.isGrowth(event.transactions())) {
			BlockTransaction blockTransaction = ListenerUtils.getTransaction(event.transactions(), Operations.GROWTH.get());
			Region region = plugin.getAPI().findRegion(event.world(), blockTransaction.defaultReplacement().position());
			if(!isAllowGrowth(region, blockTransaction, event.source(), true)) event.setCancelled(true);
			if(event.isCancelled() && isPlayer) player.sendMessage(getEvents(player).getBlock().getGrowth());
			return;
		}
		if(ListenerUtils.isExplosion(event.source())) {
			Explosion explosion = ((Explosion) event.source());
			Region region = plugin.getAPI().findRegion(event.world(), explosion.blockPosition());
			event.transactions().forEach(transaction -> {
				if(!isAllowExplosion(plugin.getAPI().findRegion(event.world(), transaction.original().position()), explosion, transaction)) transaction.setValid(false);
			});
			boolean allow = isAllowExplosion(region, explosion, event.world().block(explosion.blockPosition()));
			RegionExplosionEvent.Surface rgEvent = new RegionExplosionEvent.Surface() {

				Explosion explosion;
				boolean cancellded;
				@Override
				public Cause cause() {
					return cause;
				}

				@Override
				public Explosion getExplosion() {
					return explosion;
				}

				@Override
				public void setExplosion(Explosion explosion) {
					this.explosion = explosion;
					
				}

				@Override
				public Optional<Explosive> getExplosive() {
					return explosion.sourceExplosive();
				}

				@Override
				public boolean isAllowExplosion() {
					return allow;
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
					return cancellded;
				}

				@Override
				public void setCancelled(boolean cancel) {
					this.cancellded = cancel;
				}

				@Override
				public List<BlockTransaction> getTransactions() {
					return event.transactions();
				}

				@Override
				public BlockTransaction getDefaultTransaction() {
					return ListenerUtils.getTransaction(event.transactions(), Operations.BREAK.get());
				}

				@Override
				public BlockSnapshot afterTransaction() {
					return getDefaultTransaction().finalReplacement();
				}

				@Override
				public BlockSnapshot beforeTransaction() {
					return getDefaultTransaction().original();
				}

				@SuppressWarnings("unchecked")
				@Override
				public ChangeBlockEvent.All getSpongeEvent() {
					return event;
				}

			};
			rgEvent.setCancelled(!allow);
			rgEvent.setExplosion(explosion);
			ListenerUtils.postEvent(rgEvent);
			event.setCancelled(rgEvent.isCancelled());
			return;
		}
		if(ListenerUtils.isLiquidFlow(event.transactions())) {
			BlockTransaction blockTransaction = ListenerUtils.getTransaction(event.transactions(), Operations.LIQUID_SPREAD.get());
			Region region = plugin.getAPI().findRegion(event.world(), blockTransaction.defaultReplacement().position());
			boolean allow = isAllowLiquidFlow(region, blockTransaction);
			RegionChangeBlockEvent.LiquidFlow rgEvent = new RegionChangeBlockEvent.LiquidFlow() {

				boolean cancelled;
				@Override
				public Cause cause() {
					return cause;
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
				public List<BlockTransaction> getTransactions() {
					return event.transactions();
				}

				@Override
				public BlockTransaction getDefaultTransaction() {
					return blockTransaction;
				}

				@Override
				public BlockSnapshot afterTransaction() {
					return getDefaultTransaction().defaultReplacement();
				}

				@Override
				public BlockSnapshot beforeTransaction() {
					return getDefaultTransaction().original();
				}

				@Override
				public Region getRegion() {
					return region;
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
				public boolean isAllowFlow() {
					return allow;
				}

				@Override
				public int getFlowLevel() {
					return ListenerUtils.getLiquidFlowLevel(blockTransaction);
				}

				@SuppressWarnings("unchecked")
				@Override
				public All getSpongeEvent() {
					return event;
				}
				
			};
			rgEvent.setCancelled(!allow);
			ListenerUtils.postEvent(rgEvent);
			event.setCancelled(rgEvent.isCancelled());
			return;
		}
		if(ListenerUtils.isModify(event.transactions()) && ListenerUtils.blockID(ListenerUtils.getTransaction(event.transactions(), Operations.MODIFY.get()).defaultReplacement()).equals("minecraft:fire")) {
			BlockTransaction blockTransaction = ListenerUtils.getTransaction(event.transactions(), Operations.MODIFY.get());
			Region region = plugin.getAPI().findRegion(event.world(), blockTransaction.defaultReplacement().position());
			if(!isAllowFireSpread(region, blockTransaction)) event.setCancelled(true);
			return;
		}
		if(ListenerUtils.isPlaceBlock(event.transactions()) && event.source() instanceof Entity) {
			BlockTransaction blockTransaction = ListenerUtils.getTransaction(event.transactions(), Operations.PLACE.get());
			Region region = plugin.getAPI().findRegion(event.world(), blockTransaction.defaultReplacement().position());
			Entity entity = (Entity) event.source();
			boolean allow = isAllowPlace(region, blockTransaction, entity, true);
			RegionChangeBlockEvent.Place rgEvent = new RegionChangeBlockEvent.Place() {

				Component component;
				boolean cancelled;
				@Override
				public Cause cause() {
					return cause;
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
				public List<BlockTransaction> getTransactions() {
					return event.transactions();
				}

				@Override
				public BlockTransaction getDefaultTransaction() {
					return blockTransaction;
				}

				@Override
				public BlockSnapshot afterTransaction() {
					return getDefaultTransaction().defaultReplacement();
				}

				@Override
				public BlockSnapshot beforeTransaction() {
					return getDefaultTransaction().original();
				}

				@Override
				public Region getRegion() {
					return region;
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
				public boolean isAllowPlace() {
					return allow;
				}

				@Override
				public Entity getEntity() {
					return entity;
				}

				@SuppressWarnings("unchecked")
				@Override
				public Optional<ServerPlayer> getPlayer() {
					return Optional.ofNullable(player);
				}

				@Override
				public void setMessage(Component message) {
					component = message;
				}

				@Override
				public Optional<Component> getMessage() {
					return Optional.ofNullable(component);
				}

				@SuppressWarnings("unchecked")
				@Override
				public All getSpongeEvent() {
					return event;
				}
				
			};
			rgEvent.setCancelled(!allow);
			if(isPlayer) rgEvent.setMessage(getEvents(player).getBlock().getPlace());
			ListenerUtils.postEvent(rgEvent);
			event.setCancelled(rgEvent.isCancelled());
			if(rgEvent.isCancelled() && rgEvent.getMessage().isPresent() && isPlayer) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
			return;
		}
		if(ListenerUtils.isDestructBlock(event.transactions()) && event.source() instanceof Entity) {
			BlockTransaction blockTransaction = ListenerUtils.getTransaction(event.transactions(), Operations.BREAK.get());
			Region region = plugin.getAPI().findRegion(event.world(), blockTransaction.defaultReplacement().position());
			Entity entity = event.source() instanceof Entity ? (Entity) event.source() : null;
			boolean allow = isAllowBreak(region, blockTransaction, entity, true);
			RegionChangeBlockEvent.Break rgEvent = new RegionChangeBlockEvent.Break() {

				Component component;
				boolean cancelled;
				@Override
				public Cause cause() {
					return cause;
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
				public List<BlockTransaction> getTransactions() {
					return event.transactions();
				}

				@Override
				public BlockTransaction getDefaultTransaction() {
					return blockTransaction;
				}

				@Override
				public BlockSnapshot afterTransaction() {
					return getDefaultTransaction().defaultReplacement();
				}

				@Override
				public BlockSnapshot beforeTransaction() {
					return getDefaultTransaction().original();
				}

				@Override
				public Region getRegion() {
					return region;
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
					return entity;
				}

				@SuppressWarnings("unchecked")
				@Override
				public Optional<ServerPlayer> getPlayer() {
					return Optional.ofNullable(player);
				}

				@Override
				public void setMessage(Component message) {
					component = message;
				}

				@Override
				public Optional<Component> getMessage() {
					return Optional.ofNullable(component);
				}

				@Override
				public boolean isAllowBreak() {
					return allow;
				}

				@SuppressWarnings("unchecked")
				@Override
				public All getSpongeEvent() {
					return event;
				}
				
			};
			rgEvent.setCancelled(!allow);
			if(isPlayer) rgEvent.setMessage(getEvents(player).getBlock().getBreak());
			ListenerUtils.postEvent(rgEvent);
			event.setCancelled(rgEvent.isCancelled());
			if(rgEvent.isCancelled() && rgEvent.getMessage().isPresent() && isPlayer) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
			return;
		}
	}

	@Listener
	public void worldChange(ChangeEntityWorldEvent.Pre event, @Root ServerPlayer player) {
		plugin.removePlayerPositions(player);
	}

	private void onPistonMove(ChangeBlockEvent.Pre event, BlockSnapshot snapshot) {
		Direction direction = getDirect(snapshot);
		if(direction == null) return;
		Region region = plugin.getAPI().findRegion(event.world(), snapshot.position());
		Optional<Entity> optEntity = event.context().get(EventContextKeys.NOTIFIER).isPresent() ? event.world().entity(event.context().get(EventContextKeys.NOTIFIER).get()) : (event.context().get(EventContextKeys.CREATOR).isPresent() ? event.world().entity(event.context().get(EventContextKeys.CREATOR).get()) : Optional.empty());
		List<String> sources = optEntity.isPresent() ? ListenerUtils.flagEntityArgs(optEntity.get()) : Arrays.asList("all");
		List<String> targets = ListenerUtils.flagBlocksArgs(event.locations().stream().filter(l -> (!l.blockPosition().equals(snapshot.position()))).map(l -> (l.createSnapshot())).collect(Collectors.toList()));
		HashSet<Region> affectedRegions = getOtherRegions(event.world(), event.locations().stream().map(ServerLocation::blockPosition).collect(Collectors.toList()), direction, region, sources, targets);
		if(affectedRegions.isEmpty()) {
			boolean isAllow = isAllowPistonMove(region, sources, targets);
			RegionPistonEvent.OneRegion rgEvent = new RegionPistonEvent.OneRegion() {

				Component text;
				boolean cancelled;
				@SuppressWarnings("unchecked")
				@Override
				public Pre getSpongeEvent() {
					return event;
				}

				@Override
				public Region getRegion() {
					return region;
				}

				@Override
				public List<ServerLocation> getPistonMovedBlocks() {
					return event.locations().stream().filter(l -> (!l.blockPosition().equals(snapshot.position()))).collect(Collectors.toList());
				}

				@Override
				public Cause cause() {
					return cause;
				}

				@Override
				public boolean isAllowMove() {
					return isAllow;
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
				public Direction getDirection() {
					return direction;
				}

				@SuppressWarnings("unchecked")
				@Override
				public Optional<ServerPlayer> getPlayer() {
					return optEntity.isPresent() && optEntity.get() instanceof ServerPlayer ? Optional.ofNullable((ServerPlayer) optEntity.get()) : Optional.empty();
				}

				@Override
				public Optional<Entity> getEntity() {
					return optEntity;
				}

				@Override
				public void setMessage(Component message) {
					text = message;
				}

				@Override
				public Optional<Component> getMessage() {
					return Optional.ofNullable(text);
				}

				@Override
				public ServerWorld getWorld() {
					return event.world();
				}
				
			};
			rgEvent.setCancelled(!isAllow);
			if(!isAllow && rgEvent.getPlayer().isPresent()) rgEvent.setMessage(getEvents(rgEvent.getPlayer().get()).getPiston().getUse());
			ListenerUtils.postEvent(rgEvent);
			if(rgEvent.isCancelled()) {
				event.setCancelled(true);
			}
			if(rgEvent.getMessage().isPresent() && rgEvent.getPlayer().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		} else {
			boolean isAllow = isAllowPistonGrief(region, affectedRegions, sources, targets) && isAllowPistonMove(affectedRegions, sources, targets);
			RegionPistonEvent.Grief rgEvent = new RegionPistonEvent.Grief() {

				Component text;
				boolean cancelled;
				@SuppressWarnings("unchecked")
				@Override
				public Pre getSpongeEvent() {
					return event;
				}

				@Override
				public Region getRegion() {
					return region;
				}

				@Override
				public List<Region> getAffectedRegions() {
					return new ArrayList<Region>(affectedRegions);
				}

				@Override
				public List<ServerLocation> getPistonMovedBlocks() {
					return event.locations().stream().filter(l -> (!l.blockPosition().equals(snapshot.position()))).collect(Collectors.toList());
				}

				@Override
				public Cause cause() {
					return cause;
				}

				@Override
				public boolean isAllowGrief() {
					return isAllow;
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
				public Direction getDirection() {
					return direction;
				}

				@SuppressWarnings("unchecked")
				@Override
				public Optional<ServerPlayer> getPlayer() {
					return optEntity.isPresent() && optEntity.get() instanceof ServerPlayer ? Optional.ofNullable((ServerPlayer) optEntity.get()) : Optional.empty();
				}

				@Override
				public Optional<Entity> getEntity() {
					return optEntity;
				}

				@Override
				public void setMessage(Component message) {
					text = message;
				}

				@Override
				public Optional<Component> getMessage() {
					return Optional.ofNullable(text);
				}

				@Override
				public ServerWorld getWorld() {
					return event.world();
				}
				
			};
			rgEvent.setCancelled(!isAllow);
			if(!isAllow && rgEvent.getPlayer().isPresent()) rgEvent.setMessage(getEvents(rgEvent.getPlayer().get()).getPiston().getGrief());
			ListenerUtils.postEvent(rgEvent);
			if(rgEvent.isCancelled()) {
				event.setCancelled(true);
			}
			if(rgEvent.getMessage().isPresent() && rgEvent.getPlayer().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		}
	
	}

	private PlayerPositions getPositions(ServerPlayer player) {
		return plugin.getPlayerPositions(player).orElse(null);
	}

	private boolean resizeOrCreateRegion(ServerPlayer player, Vector3i blockPosition, Region region) {
		if(!plugin.playerPositionsExist(player)) plugin.addPlayerPositions(player, new PlayerPositions());
		if(!player.itemInHand(HandTypes.MAIN_HAND.get()).toContainer().get(DataQuery.of("components")).filter(data -> data.toString().contains("WandItem")).isPresent()) return false;
		//if(!ItemTypes.registry().valueKey(player.itemInHand(HandTypes.MAIN_HAND.get()).type()).toString().equals(plugin.getConfig().getWanditem().getItemTypeAsString())) return false;
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			if(region.isAdmin() && !player.hasPermission(Permissions.STAFF_ADMINCLAIM)) player.sendMessage(getEvents(player).getRegion().getCreate().getNoAdminPerm());
			if(!region.isGlobal() && !region.getOwnerUUID().equals(player.uniqueId()) && !player.hasPermission(Permissions.STAFF_RESIZE)) player.sendMessage(getEvents(player).getRegion().getCreate().getPositionLocked());
			if(!resizeRegion(player, region, blockPosition) && !getPositions(player).resize) createRegion(player, blockPosition, region);
		});
		return true;
	}

	private void createRegion(ServerPlayer player, Vector3i blockPosition, Region region) {
		if(getPositions(player).pos1 == null) {
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setDrag(true);
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setLastWandLocation(blockPosition);
			plugin.getAPI().getWorldEditCUIAPI().sendVisualDrag(player, blockPosition);
			getPositions(player).pos1 = blockPosition;
			player.sendMessage(getEvents(player).getRegion().getCreate().getSetPos(1, blockPosition));
			return;
		}
		if(getPositions(player).pos2 == null) {
			plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(player);
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setLastWandLocation(null);
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setDrag(false);
			getPositions(player).pos2 = blockPosition;
			player.sendMessage(getEvents(player).getRegion().getCreate().getSetPos(2, blockPosition));
		}
		if(getPositions(player).pos1.x() == blockPosition.x() || (getPositions(player).pos1.y() == blockPosition.y() && plugin.getAPI().getSelectorType(player.uniqueId()).equals(SelectorTypes.CUBOID)) || getPositions(player).pos1.z() == blockPosition.z()) {
			player.sendMessage(getEvents(player).getRegion().getCreate().getIncorrectCords());
			getPositions(player).clear();
			return;
		}
		if(getPositions(player).pos1 != null && getPositions(player).pos2 != null) {
			if(!region.isGlobal() && (getPositions(player).pos1.x() == blockPosition.x() || getPositions(player).pos1.y() == blockPosition.y() || getPositions(player).pos1.z() == blockPosition.z())) {
				player.sendMessage(getEvents(player).getRegion().getCreate().getIncorrectCords());
				getPositions(player).clear();
				return;
			}
			if(!region.isGlobal() && region.getCuboid().getAABB().intersects(AABB.of(getPositions(player).pos1, getPositions(player).pos2))) {
				getPositions(player).tempRegion = Region.builder().setType(plugin.getAPI().getSelectRegionType(player)).setOwner(player).setWorld(player.world()).setCuboid(Cuboid.of(plugin.getAPI().getSelectorType(player.uniqueId()), getPositions(player).pos1, getPositions(player).pos2)).build();
			} else {
				getPositions(player).tempRegion = Region.builder().setType(plugin.getAPI().getSelectRegionType(player)).setOwner(player).setWorld(player.world()).setCuboid(Cuboid.of(plugin.getAPI().getSelectorType(player.uniqueId()), getPositions(player).pos1, getPositions(player).pos2)).build();
			}
		} else return;
		if(region.isGlobal()) {
			if(!player.hasPermission(Permissions.UNLIMIT_CLAIMS) && plugin.getAPI().getLimitClaims(player) - plugin.getAPI().getClaimedRegions(player) <= 0) {
				player.sendMessage(getEvents(player).getRegion().getCreate().getLimitRegions(plugin.getAPI().getClaimedRegions(player), plugin.getAPI().getLimitClaims(player)));
				return;
			}
			if(!player.hasPermission(Permissions.UNLIMIT_BLOCKS)) {
				long limit = plugin.getAPI().getLimitBlocks(player) - plugin.getAPI().getClaimedBlocks(player);
				limit = limit >= 0 ? limit : 0;
				if(limit - getPositions(player).tempRegion.getCuboid().getSize() <= 0) {
					player.sendMessage(getEvents(player).getRegion().getCreate().getLimitBlocks(getPositions(player).tempRegion.getCuboid().getSize(), limit));
					getPositions(player).clear();
					return;
				}
			}
			Region find = plugin.getAPI().findIntersectsRegion(getPositions(player).tempRegion);
			if(!find.equals(getPositions(player).tempRegion)) {
				plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(player);
				plugin.getAPI().getWorldEditCUIAPI().revertVisuals(player, getPositions(player).tempRegion.getUniqueId());
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(find, player, false, false);
				player.sendMessage(getEvents(player).getRegion().getCreate().getIntersect());
				getPositions(player).clear();
				return;
			}
			RegionCreateEvent createRegionEvent = createRegion(player, getPositions(player).tempRegion);
			if(!createRegionEvent.isCancelled()) {
				if(createRegionEvent.getRegion().getCuboid().getSize() < plugin.getAPI().getMinimalRegionSize(createRegionEvent.getRegion().getCuboid().getSelectorType())) {
					player.sendMessage(getEvents(player).getRegion().getCreate().getSmallVolume(plugin.getAPI().getMinimalRegionSize(createRegionEvent.getRegion().getCuboid().getSelectorType())));
					getPositions(player).clear();
					return;
				}
				plugin.getAPI().addTempRegion(player.uniqueId(), createRegionEvent.getRegion());
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(createRegionEvent.getRegion(), player, true, true);
				if(createRegionEvent.getMessage().isPresent()) player.sendMessage(createRegionEvent.getMessage().get());
			} else {
				player.sendMessage(getEvents(player).getRegion().getCreate().getCancel());
				if(createRegionEvent.getMessage().isPresent()) player.sendMessage(createRegionEvent.getMessage().get());
			}
		} else {
			if(!player.hasPermission(Permissions.UNLIMIT_SUBDIVISIONS)) {
				long limit = plugin.getAPI().getLimitSubdivisions(player);
				limit = limit >= 0 ? limit : 0;
				if(region.getAllChilds().size() >= limit) {
					player.sendMessage(getEvents(player).getRegion().getCreate().getLimitSubdivisions(region.getAllChilds().size(), limit));
					return;
				}
			}
			if(region.getCuboid().getAABB().contains(getPositions(player).tempRegion.getCuboid().getMin()) && region.getCuboid().getAABB().contains(getPositions(player).tempRegion.getCuboid().getMax())) {
				if(region.isAdmin()) getPositions(player).tempRegion.setTrustType(new UUID(0, 0), TrustTypes.OWNER);
				RegionCreateEvent createSubdivisionEvent = createSubdivision(player, getPositions(player).tempRegion, region);
				if(!createSubdivisionEvent.isCancelled()) {
					createSubdivisionEvent.getRegion().setParrent(region);
					createSubdivisionEvent.getRegion().setRegionType(RegionTypes.SUBDIVISION);
					plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(createSubdivisionEvent.getRegion(), player, true, true);
					region.addChild(createSubdivisionEvent.getRegion());
					if(createSubdivisionEvent.getMessage().isPresent()) player.sendMessage(createSubdivisionEvent.getMessage().get());
					plugin.getAPI().saveRegion(region.getPrimaryParent());
					plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(createSubdivisionEvent.getRegion(), player, true, false);
				} else {
					player.sendMessage(getEvents(player).getRegion().getCreate().getCancel());
					if(createSubdivisionEvent.getMessage().isPresent()) player.sendMessage(createSubdivisionEvent.getMessage().get());
				}
			} else {
				player.sendMessage(getEvents(player).getRegion().getCreate().getWrongSubdivisionPositions());
				getPositions(player).clear();
			}
			return;
		}
		return;
	}

	private boolean resizeRegion(ServerPlayer player, Region region, Vector3i blockPosition) {
		if(getPositions(player).tempRegion != null && getPositions(player).oppositeCorner != null) {
			boolean out =  getPositions(player).tempRegion.getParrent().isPresent() && !getPositions(player).tempRegion.getParrent().get().getCuboid().containsIntersectsPosition(blockPosition);
			if(out || !getPositions(player).tempRegion.getChilds().isEmpty()) {
				Optional<Region> optChild = Optional.empty();
				if(!out) {
					Cuboid cuboid = Cuboid.of(getPositions(player).oppositeCorner, blockPosition);
					if(getPositions(player).tempRegion.getCuboid().getSelectorType() == SelectorTypes.FLAT) cuboid.toFlat(player.world());
					optChild = getPositions(player).tempRegion.getChilds().stream().filter(rg -> (!cuboid.containsIntersectsPosition(rg.getCuboid().getMin()) || !cuboid.containsIntersectsPosition(rg.getCuboid().getMax()))).findFirst();
				}
				if(optChild.isPresent() || out) {
					player.sendMessage(getEvents(player).getRegion().getResize().getChildOut());
					getPositions(player).clear();
					clearVisual(player);
					if(optChild.isPresent()) {
						plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(optChild.get(), player, false, false);
					} else if(region.getParrent().isPresent()) {
						plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region.getParrent().get(), player, false, false);
					}
					return true;
				}
			} else if(getPositions(player).tempRegion.getChilds().isEmpty()) {
				RegionResizeEvent resizeEvent = tryResizeRegion(player, getPositions(player).tempRegion, blockPosition, getPositions(player).oppositeCorner);
				if(!resizeEvent.isCancelled()) {
					if(isIntersects(resizeEvent.getRegion(), player, blockPosition, resizeEvent.getOppositeCorner())) return true;
					resizeEvent.getRegion().setCuboid(resizeEvent.getOppositeCorner(), blockPosition, resizeEvent.getRegion().getCuboid().getSelectorType());
					if(resizeEvent.getMessage().isPresent()) player.sendMessage(resizeEvent.getMessage().get());
					getPositions(player).clear();
					plugin.getAPI().unregisterRegion(resizeEvent.getRegion().getPrimaryParent());
					plugin.getAPI().registerRegion(resizeEvent.getRegion().getPrimaryParent());
					plugin.getAPI().saveRegion(resizeEvent.getRegion().getPrimaryParent());
					clearVisual(player);
					plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(resizeEvent.getRegion(), player, true, false);
				}
				return true;
			}
		}
		if(getPositions(player).tempRegion != null && isIntersects(getPositions(player).tempRegion, player, blockPosition)) return true;
		if((region.getTrustType(player) != TrustTypes.OWNER && !region.isGlobal()) || getPositions(player).pos1 != null) return false;
		if(!region.isGlobal() && region.getCuboid().isCorner(blockPosition) && !getPositions(player).resize) {
			getPositions(player).oppositeCorner = region.getCuboid().getOppositeCorner(blockPosition);
			RegionResizeEvent resizeEvent = tryResizeRegion(player, region, null, getPositions(player).oppositeCorner);
			if(!resizeEvent.isCancelled()) {
				if(resizeEvent.getMessage().isPresent()) player.sendMessage(resizeEvent.getMessage().get());
				getPositions(player).resize = true;
				getPositions(player).tempRegion = region;
				plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setDrag(true);
				plugin.getAPI().getWorldEditCUIAPI().revertVisuals(player, null);
				plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setClaimResizing(region);
				plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setLastWandLocation(getPositions(player).oppositeCorner);;
				plugin.getAPI().getWorldEditCUIAPI().sendVisualDrag(player, blockPosition);
			}
			return true;
		} else if(getPositions(player).resize && getPositions(player).tempRegion != null && getPositions(player).oppositeCorner != null) {
			region = getPositions(player).tempRegion;
			Vector3i oppositeCorner = getPositions(player).oppositeCorner;
			if(blockPosition.x() == oppositeCorner.x() || (blockPosition.y() == oppositeCorner.y() && region.getCuboid().getSelectorType() == SelectorTypes.CUBOID) || blockPosition.z() == oppositeCorner.z()) {
				player.sendMessage(getEvents(player).getRegion().getResize().getIncorrectCords());
			} else {
				Cuboid cuboid = Cuboid.of(oppositeCorner, blockPosition);
				if(region.getCuboid().getSelectorType() == SelectorTypes.FLAT) cuboid.toFlat(player.world());
				if(cuboid.getSize() < plugin.getAPI().getMinimalRegionSize(region.getCuboid().getSelectorType())) {
					player.sendMessage(getEvents(player).getRegion().getResize().getSmallVolume(plugin.getAPI().getMinimalRegionSize(region.getCuboid().getSelectorType())));
					getPositions(player).clear();
					return true;
				}
				if(!player.hasPermission(Permissions.UNLIMIT_BLOCKS) && cuboid.getSize() > region.getCuboid().getSize() && plugin.getAPI().getLimitBlocks(player) - plugin.getAPI().getClaimedBlocks(player) - (cuboid.getSize() - region.getCuboid().getSize()) <= 0) {
					long limit = plugin.getAPI().getLimitBlocks(player) - plugin.getAPI().getClaimedBlocks(player);
					player.sendMessage(getEvents(player).getRegion().getResize().getLimitBlocks(cuboid.getSize(), limit < 0 ? 0 : limit));
					getPositions(player).clear();
					return true;
				}
				RegionResizeEvent resizeEvent = tryResizeRegion(player, region, blockPosition, oppositeCorner);
				if(!resizeEvent.isCancelled()) {
					if(isIntersects(getPositions(player).tempRegion, player, blockPosition, resizeEvent.getOppositeCorner())) return true;
					region.setCuboid(cuboid);
					if(resizeEvent.getMessage().isPresent()) player.sendMessage(resizeEvent.getMessage().get());
					getPositions(player).clear();
					plugin.getAPI().unregisterRegion(region.getPrimaryParent());
					plugin.getAPI().registerRegion(region.getPrimaryParent());
					plugin.getAPI().saveRegion(region.getPrimaryParent());
				}
			}
			plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(player);
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setClaimResizing(null);
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setLastWandLocation(null);
			plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region, player, true, false);
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setDrag(false);
			return true;
		}
		return false;
	}

	private boolean getRegionInfo(ServerPlayer player, Region region) {
		if(!plugin.playerPositionsExist(player)) plugin.addPlayerPositions(player, new PlayerPositions());
		DataContainer container = player.itemInHand(HandTypes.MAIN_HAND.get()).toContainer();
		if(!container.get(DataQuery.of("UnsafeData")).isPresent() || !container.get(DataQuery.of("UnsafeData")).get().toString().contains("WandItem")) return false;
		if(System.currentTimeMillis() - (getPositions(player).secondaryLastTime + 200) < 0) return true;
		player.sendMessage(getEvents(player).getRegion().getWandInfo().getType(region));
		player.sendMessage(getEvents(player).getRegion().getWandInfo().getOwner(region));
		if(!region.isGlobal()) plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region, player, true, false);
		getPositions(player).secondaryLastTime = System.currentTimeMillis();
		return true;
	}

	private RegionCreateEvent createRegion(ServerPlayer player, Region region) {
		region.setRegionType(plugin.getAPI().getSelectRegionType(player));
		RegionCreateEvent createMainEvent = new Create(cause, player, region);
		createMainEvent.setMessage(getEvents(player).getRegion().getCreate().getSuccess(region));
		ListenerUtils.postEvent(createMainEvent);
		getPositions(player).clear();
		return createMainEvent;
	}

	private RegionCreateEvent createSubdivision(ServerPlayer player, Region subdivision, Region parrent) {
		RegionCreateEvent createSubdivisionEvent = new Create(cause, player, subdivision);
		if(parrent.getCuboid().getAABB().intersects(subdivision.getCuboid().getAABB())) {
			createSubdivisionEvent.setMessage(getEvents(player).getRegion().getCreate().getSuccessSubdivision(subdivision));
			subdivision.setParrent(parrent);
			ListenerUtils.postEvent(createSubdivisionEvent);
			plugin.removePlayerPositions(player);
		} else {
			createSubdivisionEvent.setCancelled(true);
			createSubdivisionEvent.setMessage(getEvents(player).getRegion().getCreate().getWrongSubdivisionPositions());
		}
		return createSubdivisionEvent;
	}

	private RegionResizeEvent tryResizeRegion(ServerPlayer player, Region region, Vector3i newCorner, Vector3i oppositeCorner) {
		RegionResizeEvent resizeEvent;
		if(newCorner == null) {
			resizeEvent = new Resize(cause, player, region, newCorner, oppositeCorner, getEvents(player).getRegion().getResize().getStart());
		} else {
			resizeEvent = new Resize(cause, player, region, newCorner, oppositeCorner, getEvents(player).getRegion().getResize().getFinish());
		}
		ListenerUtils.postEvent(resizeEvent);
		return resizeEvent;
	}

	private boolean isAllowInteractBlockPrimary(Entity entity, Region region, BlockSnapshot block, boolean first) {
		if(first && (region.getTrustType(entity.uniqueId()) == TrustTypes.OWNER || region.getTrustType(entity.uniqueId()) == TrustTypes.BUILDER || region.getTrustType(entity.uniqueId()) == TrustTypes.MANAGER || region.getTrustType(entity.uniqueId()) == TrustTypes.USER || (region.getTrustType(entity.uniqueId()) == TrustTypes.CONTAINER && ListenerUtils.isContainer(block)))) return true;
		if(entity instanceof ServerPlayer && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.INTERACT_BLOCK_PRIMARY))) return true;
		for(String entityId : ListenerUtils.flagEntityArgs(entity)) {
			for(String blockId : ListenerUtils.flagBlockArgs(block)) {
				Tristate flagResult = region.getFlagResult(Flags.INTERACT_BLOCK_PRIMARY, entityId, blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowInteractBlockPrimary(entity, plugin.getAPI().getGlobalRegion(region.getWorldKey()), block, false);
	}

	private boolean isAllowInteractBlockSecondary(Entity entity, Region region, BlockSnapshot block, boolean first) {
		if(first && (region.getTrustType(entity.uniqueId()) == TrustTypes.OWNER || region.getTrustType(entity.uniqueId()) == TrustTypes.BUILDER || region.getTrustType(entity.uniqueId()) == TrustTypes.MANAGER || region.getTrustType(entity.uniqueId()) == TrustTypes.USER || (region.getTrustType(entity.uniqueId()) == TrustTypes.CONTAINER && ListenerUtils.isContainer(block)))) return true;
		if(first && (region.isTrusted(entity.uniqueId()) && (region.getTrustType(entity.uniqueId()) == TrustTypes.SLEEP || region.getTrustType(entity.uniqueId()) == TrustTypes.CONTAINER) && ListenerUtils.isBedBlock(block))) return true;
		if(entity instanceof ServerPlayer && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.INTERACT_BLOCK_SECONDARY))) return true;
		for(String entityId : ListenerUtils.flagEntityArgs(entity)) {
			for(String blockId : ListenerUtils.flagBlockArgs(block)) {
				Tristate flagResult = region.getFlagResult(Flags.INTERACT_BLOCK_SECONDARY, entityId, blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowInteractBlockSecondary(entity, plugin.getAPI().getGlobalRegion(region.getWorldKey()), block, false);
	}

	private boolean isAllowLiquidFlow(Region region, BlockTransaction transaction) {
		if(ListenerUtils.nonReplacement(transaction)) return true;
		for(String block : ListenerUtils.flagBlockArgs(transaction.defaultReplacement())) {
			Tristate flagResult = region.getFlagResult(Flags.LIQUID_FLOW, null, block);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true: isAllowLiquidFlow(plugin.getAPI().getGlobalRegion(region.getWorldKey()), transaction);
	}

	private boolean isAllowPlace(Region region, BlockTransaction transaction, Entity entity, boolean first) {
		if(ListenerUtils.nonReplacement(transaction) || entity instanceof FallingBlock) return true;
		if(first && (entity != null && (region.getTrustType(entity.uniqueId()) == TrustTypes.OWNER || region.getTrustType(entity.uniqueId()) == TrustTypes.BUILDER || region.getTrustType(entity.uniqueId()) == TrustTypes.MANAGER || region.getTrustType(entity.uniqueId()) == TrustTypes.USER))) return true;
		if(entity != null && (entity instanceof ServerPlayer) && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.BLOCK_PLACE))) return true;
		for(String entityId : ListenerUtils.flagEntityArgs(entity)) {
			for(String blockId : ListenerUtils.flagBlockArgs(transaction.original())) {
				Tristate flagResult = region.getFlagResult(Flags.BLOCK_PLACE, entityId, blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowPlace(plugin.getAPI().getGlobalRegion(region.getWorldKey()), transaction, entity, false);
	}

	private boolean isAllowBreak(Region region, BlockTransaction transaction, Entity entity, boolean first) {
		if(ListenerUtils.nonReplacement(transaction) || entity instanceof FallingBlock) return true;
		if(first && (entity != null && (region.getTrustType(entity.uniqueId()) == TrustTypes.OWNER || region.getTrustType(entity.uniqueId()) == TrustTypes.BUILDER || region.getTrustType(entity.uniqueId()) == TrustTypes.MANAGER || region.getTrustType(entity.uniqueId()) == TrustTypes.USER))) return true;
		if(entity != null && (entity instanceof ServerPlayer) && ((ServerPlayer) entity).hasPermission(Permissions.bypassFlag(Flags.BLOCK_BREAK))) return true;
		for(String entityId : ListenerUtils.flagEntityArgs(entity)) {
			for(String blockId : ListenerUtils.flagBlockArgs(transaction.original())) {
				Tristate flagResult = region.getFlagResult(Flags.BLOCK_BREAK, entityId, blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowPlace(plugin.getAPI().getGlobalRegion(region.getWorldKey()), transaction, entity, false);
	}

	private boolean isAllowFireSpread(Region region, BlockTransaction transaction) {
		return region.getFlagResult(Flags.FIRE_SPREAD, null, null).asBoolean();
	}

	private boolean isAllowGrowth(Region region, BlockTransaction transaction, Object source, boolean first) {
		boolean isEntity = source instanceof Entity;
		if(first && source instanceof ServerPlayer && (region.getTrustType(((ServerPlayer) source).uniqueId()) == TrustTypes.OWNER || region.getTrustType(((ServerPlayer) source).uniqueId()) == TrustTypes.BUILDER || region.getTrustType(((ServerPlayer) source).uniqueId()) == TrustTypes.MANAGER || region.getTrustType(((ServerPlayer) source).uniqueId()) == TrustTypes.USER)) return true;
		if((source instanceof ServerPlayer) && ((ServerPlayer) source).hasPermission(Permissions.bypassFlag(Flags.BLOCK_GROWTH))) return true;
		if(isEntity) {
			for(String entityId : ListenerUtils.flagEntityArgs((Entity) source)) {
				for(String blockId : ListenerUtils.flagBlockArgs(transaction.original())) {
					Tristate flagResult = region.getFlagResult(Flags.BLOCK_GROWTH, entityId, blockId);
					if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
				}
			}
		} else {
			for(String blockId : ListenerUtils.flagBlockArgs(transaction.original())) {
				Tristate flagResult = region.getFlagResult(Flags.BLOCK_GROWTH, "all", blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowGrowth(plugin.getAPI().getGlobalRegion(region.getWorldKey()), transaction, source, false);
	}

	private boolean isAllowDecay(Region region, BlockTransaction transaction) {
		for(String blockId : ListenerUtils.flagBlockArgs(transaction.finalReplacement())) {
			Tristate flagResult = region.getFlagResult(Flags.BLOCK_DECAY, null, blockId);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowDecay(plugin.getAPI().getGlobalRegion(region.getWorldKey()), transaction);
	}

	private boolean isAllowExplosion(Region region, Explosion explosion, BlockTransaction transaction) {
		if(explosion.sourceExplosive().isPresent()) {
			for(String source : ListenerUtils.flagEntityArgs(explosion.sourceExplosive().get())) {
				for(String target : ListenerUtils.flagBlockArgs(transaction.original())) {
					Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, source, target);
					if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
				}
			}
		} else {
			for(String blockId : ListenerUtils.flagBlockArgs(transaction.original())) {
				Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, null, blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowExplosion(plugin.getAPI().getGlobalRegion(region.getWorldKey()), explosion, transaction);
	}

	private boolean isAllowExplosion(Region region, Explosion explosion, BlockState blockState) {
		if(explosion.sourceExplosive().isPresent()) {
			for(String source : ListenerUtils.flagEntityArgs(explosion.sourceExplosive().get())) {
				for(String target : Arrays.asList(ListenerUtils.blockID(blockState), "all")) {
					Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, source, target);
					if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
				}
			}
		} else {
			Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, null, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowExplosion(plugin.getAPI().getGlobalRegion(region.getWorldKey()), explosion, blockState);
	}

	private boolean isAllowPistonMove(Region region, List<String> sources, List<String> targets) {
		for(String source : sources) {
			for(String target : targets) {
				Tristate flagResult = region.getFlagResult(Flags.PISTON, source, target);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return true;
	}

	private boolean isAllowPistonMove(HashSet<Region> regions, List<String> sources, List<String> targets) {
		for(Region region : regions) {
			for(String source : sources) {
				for(String target : targets) {
					Tristate flagResult = region.getFlagResult(Flags.PISTON, source, target);
					if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
				}
			}
		}
		return true;
	}

	private boolean isAllowPistonGrief(Region region, HashSet<Region> regions, List<String> sources, List<String> targets) {
		for(Region other : regions) {
			if(!other.equals(region)) for(String source : sources) {
				for(String target : targets) {
					Tristate flagResult = other.getFlagResult(Flags.PISTON_GRIEF, source, target);
					if(flagResult != Tristate.UNDEFINED && !flagResult.asBoolean()) return false;
				}
			}
		}
		return true;
	}

	public class PlayerPositions {

		private Vector3i pos1;
		private Vector3i pos2;
		private Vector3i oppositeCorner;
		private Region tempRegion;
		private boolean resize = false;
		private long secondaryLastTime = 0;
		
		private void clear() {
			pos1 = null;
			pos2 = null;
			oppositeCorner = null;
			tempRegion = null;
			resize = false;
		}

	}

	private HashSet<Region> getOtherRegions(ServerWorld world, List<Vector3i> locations, Direction direction, Region region, List<String> sources, List<String> targets) {
		HashSet<Region> regions = new HashSet<Region>();
		for(Vector3i location : locations) {
			Region find1 = plugin.getAPI().findRegion(world, location);
			if(!find1.equals(region)) regions.add(find1);
			Vector3i vector2 = location.add(direction.asBlockOffset());
			if(!locations.contains(vector2)) {
				Region find2 = plugin.getAPI().findRegion(world, vector2);
				if(!find2.equals(region)) regions.add(find2);
			}
		}
		return regions;
	}

	private Direction getDirect(BlockSnapshot snapshot) {
		return snapshot != null && snapshot.get(Keys.DIRECTION).isPresent() ? snapshot.get(Keys.DIRECTION).get() : null;
	}

	private boolean isIntersects(Region region, ServerPlayer player, Vector3i blockPosition) {
		final Region copy = getPositions(player).tempRegion.copy();
		if(blockPosition != null) copy.setCuboid(copy.getCuboid().getOppositeCorner(blockPosition), blockPosition, copy.getCuboid().getSelectorType());
		Region find = plugin.getAPI().findIntersectsRegion(copy).getPrimaryParent();
		List<Region> allChilds = find.getAllChilds();
		AABB copyAABB = copy.getCuboid().getAABB();
		if(!find.isGlobal() && (!copy.isSubdivision() && !(find.equals(copy)))) {
			clearVisual(player);
			plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(find, player, false, false);
			player.sendMessage(getEvents(player).getRegion().getResize().getIntersect());
			getPositions(player).clear();
			return true;
		} else if(allChilds.contains(copy)) {
			Optional<Region> optChild = allChilds.stream().filter(child -> !child.equals(copy) && child.getCuboid().getAABB().intersects(copyAABB)).findFirst();
			if(optChild.isPresent()) {
				clearVisual(player);
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(optChild.get(), player, false, false);
				player.sendMessage(getEvents(player).getRegion().getResize().getIntersect());
				getPositions(player).clear();
				return true;
			}
		}
		return false;
	}

	private boolean isIntersects(Region region, ServerPlayer player, Vector3i blockPosition, Vector3i oppositeCorner) {
		Region copy = getPositions(player).tempRegion.copy();
		copy.setCuboid(oppositeCorner, blockPosition, copy.getCuboid().getSelectorType());
		Region find = plugin.getAPI().findIntersectsRegion(copy).getPrimaryParent();
		List<Region> allChilds = find.getAllChilds();
		AABB copyAABB = copy.getCuboid().getAABB();
		if(!find.isGlobal() && (!copy.isSubdivision() && !(find.equals(copy)))) {
			clearVisual(player);
			plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(find, player, false, false);
			player.sendMessage(getEvents(player).getRegion().getResize().getIntersect());
			getPositions(player).clear();
			return true;
		} else if(allChilds.contains(copy)) {
			Optional<Region> optChild = allChilds.stream().filter(child -> !child.equals(copy) && child.getCuboid().getAABB().intersects(copyAABB)).findFirst();
			if(optChild.isPresent()) {
				clearVisual(player);
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(optChild.get(), player, false, false);
				player.sendMessage(getEvents(player).getRegion().getResize().getIntersect());
				getPositions(player).clear();
				return true;
			}
		}
		return false;
	}

	private void clearVisual(ServerPlayer player) {
		plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setClaimResizing(null);
		plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setDrag(false);
		plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setDragCuboid(null);
		plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setLastWandLocation(null);
		plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(player);
		plugin.getAPI().getWorldEditCUIAPI().revertVisuals(player, null);
	}

}
