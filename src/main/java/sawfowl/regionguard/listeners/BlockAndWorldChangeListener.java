package sawfowl.regionguard.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
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
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
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
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionCreateEvent;
import sawfowl.regionguard.api.events.RegionInteractBlockEvent;
import sawfowl.regionguard.api.events.RegionPistonEvent;
import sawfowl.regionguard.api.events.RegionChangeBlockEvent;
import sawfowl.regionguard.api.events.RegionResizeEvent;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ListenerUtils;
import sawfowl.regionguard.utils.ReplaceUtil;

public class BlockAndWorldChangeListener extends CustomRegionEvents {

	private final RegionGuard plugin;
	private Cause cause;
	public BlockAndWorldChangeListener(RegionGuard instance) {
		plugin = instance;
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	private Map<UUID, PlayerPositions> positions = new HashMap<UUID, PlayerPositions>();

	
	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onPrimary(InteractBlockEvent.Primary.Start event, @Root Entity entity) {
		ServerPlayer player = event.source() instanceof ServerPlayer ? (ServerPlayer) event.source() : null;
		Region region = plugin.getAPI().findRegion(ListenerUtils.getWorld(event.block().world()), event.block().position());
		if(player != null && resizeOrCreateRegion(player, event.block().position(), region)) {
			event.setCancelled(true);
			return;
		}
		boolean allow = isAllowInteractBlockPrimary(entity, region, event.block(), true);
		class InteractBlockRegionEventPrimary implements RegionInteractBlockEvent {

			Component message;
			boolean cancelled;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public InteractBlockEvent spongeEvent() {
				return event;
			}

			@Override
			public Entity getEntity() {
				return entity;
			}

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
			
		}
		RegionInteractBlockEvent rgEvent = new InteractBlockRegionEventPrimary();
		rgEvent.setCancelled(!allow);
		if(player != null) rgEvent.setMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.INTERACT_BLOCK_CANCEL_PRIMARY));
		ListenerUtils.postEvent(rgEvent);
		event.setCancelled(rgEvent.isCancelled());
		if(rgEvent.isCancelled() && player != null && rgEvent.getMessage().isPresent()) {
			player.sendMessage(rgEvent.getMessage().get());
		}
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onSecondary(InteractBlockEvent.Secondary event, @Root Entity entity) {
		ServerPlayer player = event.source() instanceof ServerPlayer ? (ServerPlayer) event.source() : null;
		Region region = plugin.getAPI().findRegion(ListenerUtils.getWorld(event.block().world()), event.block().position());
		if(player != null && getRegionInfo(player, region)) {
			event.setCancelled(true);
			return;
		}
		boolean allow = isAllowInteractBlockSecondary(entity, region, event.block(), true);
		class InteractBlockRegionEventSecondary implements RegionInteractBlockEvent {

			Component message;
			boolean cancelled;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public InteractBlockEvent spongeEvent() {
				return event;
			}

			@Override
			public Entity getEntity() {
				return entity;
			}

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
			
		}
		RegionInteractBlockEvent rgEvent = new InteractBlockRegionEventSecondary();
		rgEvent.setCancelled(!allow);
		if(player != null) rgEvent.setMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.INTERACT_BLOCK_CANCEL_SECONDARY));
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
			if(event.isCancelled() && isPlayer) player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.CANCEL_GROWTH));
			return;
		}
		if(ListenerUtils.isExplosion(event.source())) {
			Explosion explosion = ((Explosion) event.source());
			Region region = plugin.getAPI().findRegion(event.world(), explosion.blockPosition());
			event.transactions().forEach(transaction -> {
				if(!isAllowExplosion(plugin.getAPI().findRegion(event.world(), transaction.original().position()), explosion, transaction)) transaction.setValid(false);
			});
			boolean allow = isAllowExplosion(region, explosion);
			class ExplodeEvent implements RegionChangeBlockEvent.Explode.Surface {

				Explosion explosion;
				boolean cancellded;
				@Override
				public Cause cause() {
					return cause;
				}

				@Override
				public Cause spongeCause() {
					return event.cause();
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
				
			}
			RegionChangeBlockEvent.Explode.Surface rgEvent = new ExplodeEvent();
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
			class LiquidFlowEvent implements RegionChangeBlockEvent.LiquidFlow {

				boolean cancelled;
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
				
			}
			RegionChangeBlockEvent.LiquidFlow rgEvent = new LiquidFlowEvent();
			rgEvent.setCancelled(!allow);
			ListenerUtils.postEvent(rgEvent);
			event.setCancelled(rgEvent.isCancelled());
			return;
		}
		if(ListenerUtils.isPlaceBlock(event.transactions()) && event.source() instanceof Entity) {
			BlockTransaction blockTransaction = ListenerUtils.getTransaction(event.transactions(), Operations.PLACE.get());
			Region region = plugin.getAPI().findRegion(event.world(), blockTransaction.defaultReplacement().position());
			Entity entity = (Entity) event.source();
			boolean allow = isAllowPlace(region, blockTransaction, entity, true);
			class PlaceEvent implements RegionChangeBlockEvent.Place {

				Component component;
				boolean cancelled;
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
				
			}
			RegionChangeBlockEvent.Place rgEvent = new PlaceEvent();
			rgEvent.setCancelled(!allow);
			if(isPlayer) rgEvent.setMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.CANCEL_PLACE));
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
			class BreakEvent implements RegionChangeBlockEvent.Break {

				Component component;
				boolean cancelled;
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
				
			}
			RegionChangeBlockEvent.Break rgEvent = new BreakEvent();
			rgEvent.setCancelled(!allow);
			if(isPlayer) rgEvent.setMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.CANCEL_BREAK));
			ListenerUtils.postEvent(rgEvent);
			event.setCancelled(rgEvent.isCancelled());
			if(rgEvent.isCancelled() && rgEvent.getMessage().isPresent() && isPlayer) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
			return;
		}
		if(ListenerUtils.isModify(event.transactions()) && ListenerUtils.blockID(ListenerUtils.getTransaction(event.transactions(), Operations.MODIFY.get()).defaultReplacement()).equals("minecraft:fire")) {
			BlockTransaction blockTransaction = ListenerUtils.getTransaction(event.transactions(), Operations.MODIFY.get());
			Region region = plugin.getAPI().findRegion(event.world(), blockTransaction.defaultReplacement().position());
			if(!isAllowFireSpread(region, blockTransaction)) event.setCancelled(true);
			return;
		}
	}

	@Listener
	public void worldChange(ChangeEntityWorldEvent.Pre event, @Root ServerPlayer player) {
		if(positions.containsKey(player.uniqueId())) positions.get(player.uniqueId()).clear();
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
			class MoveEvent implements RegionPistonEvent.OneRegion {

				Component text;
				boolean cancelled;
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
				
			}
			RegionPistonEvent.OneRegion rgEvent = new MoveEvent();
			rgEvent.setCancelled(!isAllow);
			if(!isAllow && rgEvent.getPlayer().isPresent()) rgEvent.setMessage(plugin.getLocales().getText(rgEvent.getPlayer().get().locale(), LocalesPaths.DENY_PISTON));
			ListenerUtils.postEvent(rgEvent);
			if(rgEvent.isCancelled()) {
				event.setCancelled(true);
			}
			if(rgEvent.getMessage().isPresent() && rgEvent.getPlayer().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		} else {
			boolean isAllow = isAllowPistonGrief(region, affectedRegions, sources, targets) && isAllowPistonMove(affectedRegions, sources, targets);
			class GriefEvent implements RegionPistonEvent.Grief {

				Component text;
				boolean cancelled;
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
				
			}
			RegionPistonEvent.Grief rgEvent = new GriefEvent();
			rgEvent.setCancelled(!isAllow);
			if(!isAllow && rgEvent.getPlayer().isPresent()) rgEvent.setMessage(plugin.getLocales().getText(rgEvent.getPlayer().get().locale(), LocalesPaths.DENY_PISTON_GRIEF));
			ListenerUtils.postEvent(rgEvent);
			if(rgEvent.isCancelled()) {
				event.setCancelled(true);
			}
			if(rgEvent.getMessage().isPresent() && rgEvent.getPlayer().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		}
	
	}

	private boolean resizeOrCreateRegion(ServerPlayer player, Vector3i blockPosition, Region region) {
		if(!positions.containsKey(player.uniqueId())) positions.put(player.uniqueId(), new PlayerPositions());
		DataContainer container = player.itemInHand(HandTypes.MAIN_HAND.get()).toContainer();
		if(!container.get(DataQuery.of("UnsafeData")).isPresent() || !container.get(DataQuery.of("UnsafeData")).get().toString().contains("WandItem")) return false;
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			if(region.isAdmin() && !player.hasPermission(Permissions.STAFF_ADMINCLAIM)) player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_CREATE_EXCEPTION_ADMIN_CLAIM));
			if(!region.isGlobal() && !region.getOwnerUUID().equals(player.uniqueId()) && !player.hasPermission(Permissions.STAFF_RESIZE)) player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_CREATE_EXCEPTION_POSITION_LOCKED));
			if(!resizeRegion(player, region, blockPosition) && !positions.get(player.uniqueId()).resize) createRegion(player, blockPosition, region);
		});
		return true;
	}

	private void createRegion(ServerPlayer player, Vector3i blockPosition, Region region) {
		if(positions.get(player.uniqueId()).pos1 == null) {
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setDrag(true);
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setLastWandLocation(blockPosition);
			plugin.getAPI().getWorldEditCUIAPI().sendVisualDrag(player, blockPosition);
			positions.get(player.uniqueId()).pos1 = blockPosition;
			player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.POS, ReplaceUtil.Keys.TARGET), Arrays.asList(1, positions.get(player.uniqueId()).pos1)), LocalesPaths.REGION_CREATE_SETPOS));
			return;
		}
		if(positions.get(player.uniqueId()).pos2 == null) {
			plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(player);
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setLastWandLocation(null);
			plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setDrag(false);
			positions.get(player.uniqueId()).pos2 = blockPosition;
			player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.POS, ReplaceUtil.Keys.TARGET), Arrays.asList(2, positions.get(player.uniqueId()).pos2)), LocalesPaths.REGION_CREATE_SETPOS));
		}
		if(positions.get(player.uniqueId()).pos1.x() == blockPosition.x() || (positions.get(player.uniqueId()).pos1.y() == blockPosition.y() && plugin.getAPI().getSelectorType(player.uniqueId()).equals(SelectorTypes.CUBOID)) || positions.get(player.uniqueId()).pos1.z() == blockPosition.z()) {
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_CREATE_EXCEPTION_INCORRECT_COORDS));
			positions.get(player.uniqueId()).clear();
			return;
		}
		if(positions.get(player.uniqueId()).pos1 != null && positions.get(player.uniqueId()).pos2 != null) {
			if(!region.isGlobal() && (positions.get(player.uniqueId()).pos1.x() == blockPosition.x() || positions.get(player.uniqueId()).pos1.y() == blockPosition.y() || positions.get(player.uniqueId()).pos1.z() == blockPosition.z())) {
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_CREATE_EXCEPTION_INCORRECT_COORDS));
				positions.get(player.uniqueId()).clear();
				return;
			}
			if(!region.isGlobal() && region.getCuboid().getAABB().intersects(AABB.of(positions.get(player.uniqueId()).pos1, positions.get(player.uniqueId()).pos2))) {
				positions.get(player.uniqueId()).tempRegion = new Region(player, player.world(), positions.get(player.uniqueId()).pos1, positions.get(player.uniqueId()).pos2, SelectorTypes.CUBOID);
			} else {
				positions.get(player.uniqueId()).tempRegion = new Region(player, player.world(), positions.get(player.uniqueId()).pos1, positions.get(player.uniqueId()).pos2, plugin.getAPI().getSelectorType(player.uniqueId()));
			}
		} else return;
		if(region.isGlobal()) {
			if(!player.hasPermission(Permissions.UNLIMIT_CLAIMS) && plugin.getAPI().getLimitClaims(player) - plugin.getAPI().getClaimedRegions(player) <= 0) {
				player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE), Arrays.asList((plugin.getAPI().getClaimedRegions(player) + "/" + plugin.getAPI().getLimitClaims(player)))), LocalesPaths.REGION_CREATE_EXCEPTION_LARGE_VOLUME_REGIONS));
				return;
			}
			if(!player.hasPermission(Permissions.UNLIMIT_BLOCKS)) {
				long limit = plugin.getAPI().getLimitBlocks(player) - plugin.getAPI().getClaimedBlocks(player);
				limit = limit >= 0 ? limit : 0;
				if(limit - positions.get(player.uniqueId()).tempRegion.getCuboid().getSize() <= 0) {
					player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SELECTED, ReplaceUtil.Keys.MAX), Arrays.asList(positions.get(player.uniqueId()).tempRegion.getCuboid().getSize(), (limit + "/" + plugin.getAPI().getLimitBlocks(player)))), LocalesPaths.REGION_CREATE_EXCEPTION_LARGE_VOLUME_BLOCKS));
					positions.get(player.uniqueId()).clear();
					return;
				}
			}
			Region find = plugin.getAPI().findIntersectsRegion(positions.get(player.uniqueId()).tempRegion);
			if(!find.equals(positions.get(player.uniqueId()).tempRegion)) {
				plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(player);
				plugin.getAPI().getWorldEditCUIAPI().revertVisuals(player, positions.get(player.uniqueId()).tempRegion.getUniqueId());
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(find, player, false, false);
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_CREATE_EXCEPTION_REGIONS_INTERSECT));
				positions.get(player.uniqueId()).clear();
				return;
			}
			RegionCreateEvent createRegionEvent = createRegion(player, positions.get(player.uniqueId()).tempRegion);
			if(!createRegionEvent.isCancelled()) {
				plugin.getAPI().addTempRegion(player.uniqueId(), createRegionEvent.getRegion());
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(createRegionEvent.getRegion(), player, true, true);
				if(createRegionEvent.getMessage().isPresent()) player.sendMessage(createRegionEvent.getMessage().get());
			} else {
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_CREATE_EXCEPTION_CENCELLED_EVENT));
				if(createRegionEvent.getMessage().isPresent()) player.sendMessage(createRegionEvent.getMessage().get());
			}
		} else {
			if(!player.hasPermission(Permissions.UNLIMIT_SUBDIVISIONS)) {
				long limit = plugin.getAPI().getLimitSubdivisions(player);
				limit = limit >= 0 ? limit : 0;
				if(region.getAllChilds().size() >= limit) {
					player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE), Arrays.asList((region.getAllChilds().size() + "/" + limit))), LocalesPaths.REGION_CREATE_EXCEPTION_LARGE_VOLUME_SUBDIVISIONS));
					return;
				}
			}
			if(region.getCuboid().getAABB().contains(positions.get(player.uniqueId()).tempRegion.getCuboid().getMin()) && region.getCuboid().getAABB().contains(positions.get(player.uniqueId()).tempRegion.getCuboid().getMax())) {
				if(region.isAdmin()) positions.get(player.uniqueId()).tempRegion.setTrustType(new UUID(0, 0), TrustTypes.OWNER);
				RegionCreateEvent createSubdivisionEvent = createSubdivision(player, positions.get(player.uniqueId()).tempRegion, region);
				if(!createSubdivisionEvent.isCancelled()) {
					createSubdivisionEvent.getRegion().setParrent(region);
					plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(createSubdivisionEvent.getRegion(), player, true, true);
					region.addChild(createSubdivisionEvent.getRegion());
					if(createSubdivisionEvent.getMessage().isPresent()) player.sendMessage(createSubdivisionEvent.getMessage().get());
					plugin.getAPI().saveRegion(region.getPrimaryParent());
					plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(createSubdivisionEvent.getRegion(), player, true, false);
				} else {
					player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_CREATE_EXCEPTION_CENCELLED_EVENT));
					if(createSubdivisionEvent.getMessage().isPresent()) player.sendMessage(createSubdivisionEvent.getMessage().get());
				}
			} else {
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_CREATE_EXCEPTION_WRONG_SUBDIVISION_POSITIONS));
				positions.get(player.uniqueId()).clear();
			}
			return;
		}
		return;
	}

	private boolean resizeRegion(ServerPlayer player, Region region, Vector3i blockPosition) {
		if(positions.get(player.uniqueId()).tempRegion != null && positions.get(player.uniqueId()).oppositeCorner != null) {
			boolean out =  positions.get(player.uniqueId()).tempRegion.getParrent().isPresent() && !positions.get(player.uniqueId()).tempRegion.getParrent().get().getCuboid().containsIntersectsPosition(blockPosition);
			if(out || !positions.get(player.uniqueId()).tempRegion.getChilds().isEmpty()) {
				Optional<Region> optChild = Optional.empty();
				if(!out) {
					Cuboid cuboid = new Cuboid();
					cuboid.setPositions(positions.get(player.uniqueId()).oppositeCorner, blockPosition, positions.get(player.uniqueId()).tempRegion.getCuboid().getSelectorType(), player.world());
					optChild = positions.get(player.uniqueId()).tempRegion.getChilds().stream().filter(rg -> (!cuboid.containsIntersectsPosition(rg.getCuboid().getMin()) || !cuboid.containsIntersectsPosition(rg.getCuboid().getMax()))).findFirst();
				}
				if(optChild.isPresent() || out) {
					player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_RESIZE_EXCEPTION_CHILD_OUT));
					positions.get(player.uniqueId()).clear();
					clearVisual(player);
					if(optChild.isPresent()) {
						plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(optChild.get(), player, false, false);
					} else if(region.getParrent().isPresent()) {
						plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region.getParrent().get(), player, false, false);
					}
					return true;
				}
			} else if(positions.get(player.uniqueId()).tempRegion.getChilds().isEmpty()) {
				RegionResizeEvent resizeEvent = tryResizeRegion(player, positions.get(player.uniqueId()).tempRegion, blockPosition, positions.get(player.uniqueId()).oppositeCorner);
				if(!resizeEvent.isCancelled()) {
					if(isIntersects(resizeEvent.getRegion(), player, blockPosition, resizeEvent.getOppositeCorner())) return true;
					resizeEvent.getRegion().setCuboid(resizeEvent.getOppositeCorner(), blockPosition, resizeEvent.getRegion().getCuboid().getSelectorType());
					if(resizeEvent.getMessage().isPresent()) player.sendMessage(resizeEvent.getMessage().get());
					positions.get(player.uniqueId()).clear();
					plugin.getAPI().unregisterRegion(resizeEvent.getRegion().getPrimaryParent());
					plugin.getAPI().registerRegion(resizeEvent.getRegion().getPrimaryParent());
					plugin.getAPI().saveRegion(resizeEvent.getRegion().getPrimaryParent());
					clearVisual(player);
					plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(resizeEvent.getRegion(), player, true, false);
				}
				return true;
			}
		}
		if(positions.get(player.uniqueId()).tempRegion != null && isIntersects(positions.get(player.uniqueId()).tempRegion, player, blockPosition)) return true;
		if((region.getTrustType(player) != TrustTypes.OWNER && !region.isGlobal()) || positions.get(player.uniqueId()).pos1 != null) return false;
		if(!region.isGlobal() && region.getCuboid().isCorner(blockPosition) && !positions.get(player.uniqueId()).resize) {
			positions.get(player.uniqueId()).oppositeCorner = region.getCuboid().getOppositeCorner(blockPosition);
			RegionResizeEvent resizeEvent = tryResizeRegion(player, region, null, positions.get(player.uniqueId()).oppositeCorner);
			if(!resizeEvent.isCancelled()) {
				if(resizeEvent.getMessage().isPresent()) player.sendMessage(resizeEvent.getMessage().get());
				positions.get(player.uniqueId()).resize = true;
				positions.get(player.uniqueId()).tempRegion = region;
				plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setDrag(true);
				plugin.getAPI().getWorldEditCUIAPI().revertVisuals(player, null);
				plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setClaimResizing(region);
				plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).setLastWandLocation(positions.get(player.uniqueId()).oppositeCorner);;
				plugin.getAPI().getWorldEditCUIAPI().sendVisualDrag(player, blockPosition);
			}
			return true;
		} else if(positions.get(player.uniqueId()).resize && positions.get(player.uniqueId()).tempRegion != null && positions.get(player.uniqueId()).oppositeCorner != null) {
			region = positions.get(player.uniqueId()).tempRegion;
			Vector3i oppositeCorner = positions.get(player.uniqueId()).oppositeCorner;
			if(blockPosition.x() == oppositeCorner.x() || (blockPosition.y() == oppositeCorner.y() && region.getCuboid().getSelectorType() == SelectorTypes.CUBOID) || blockPosition.z() == oppositeCorner.z()) {
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_RESIZE_EXCEPTION_INCORRECT_COORDS));
			} else {
				Cuboid cuboid = new Cuboid();
				cuboid.setPositions(oppositeCorner, blockPosition, region.getCuboid().getSelectorType(), player.world());
				if(cuboid.getSize() < plugin.getAPI().getMinimalRegionSize(region.getCuboid().getSelectorType())) {
					player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.VOLUME), Arrays.asList(plugin.getAPI().getMinimalRegionSize(region.getCuboid().getSelectorType()) - cuboid.getSize())), LocalesPaths.REGION_RESIZE_EXCEPTION_SMALL_VOLUME));
					positions.get(player.uniqueId()).clear();
					return true;
				}
				if(!player.hasPermission(Permissions.UNLIMIT_BLOCKS) && cuboid.getSize() > region.getCuboid().getSize() && plugin.getAPI().getLimitBlocks(player) - plugin.getAPI().getClaimedBlocks(player) - (cuboid.getSize() - region.getCuboid().getSize()) <= 0) {
					long limit = plugin.getAPI().getLimitBlocks(player) - plugin.getAPI().getClaimedBlocks(player);
					player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SELECTED, ReplaceUtil.Keys.VOLUME), Arrays.asList(cuboid.getSize(), limit < 0 ? 0 : limit)), LocalesPaths.REGION_RESIZE_EXCEPTION_LARGE_VOLUME));
					positions.get(player.uniqueId()).clear();
					return true;
				}
				RegionResizeEvent resizeEvent = tryResizeRegion(player, region, blockPosition, oppositeCorner);
				if(!resizeEvent.isCancelled()) {
					if(isIntersects(positions.get(player.uniqueId()).tempRegion, player, blockPosition, resizeEvent.getOppositeCorner())) return true;
					region.setCuboid(resizeEvent.getOppositeCorner(), blockPosition, region.getCuboid().getSelectorType());
					if(resizeEvent.getMessage().isPresent()) player.sendMessage(resizeEvent.getMessage().get());
					positions.get(player.uniqueId()).clear();
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
		if(!positions.containsKey(player.uniqueId())) positions.put(player.uniqueId(), new PlayerPositions());
		DataContainer container = player.itemInHand(HandTypes.MAIN_HAND.get()).toContainer();
		if(!container.get(DataQuery.of("UnsafeData")).isPresent() || !container.get(DataQuery.of("UnsafeData")).get().toString().contains("WandItem")) return false;
		if(System.currentTimeMillis() - (positions.get(player.uniqueId()).secondaryLastTime + 200) < 0) return true;
		player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.TYPE), Arrays.asList(region.getType())), LocalesPaths.REGION_WAND_TYPE));
		player.sendMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.OWNER), Arrays.asList(region.getOwnerName())), LocalesPaths.REGION_WAND_OWNER));
		if(!region.isGlobal()) plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(region, player, true, false);
		positions.get(player.uniqueId()).secondaryLastTime = System.currentTimeMillis();
		return true;
	}

	private RegionCreateEvent createRegion(ServerPlayer player, Region region) {
		region.setRegionType(plugin.getAPI().getSelectRegionType(player));
		RegionCreateEvent createMainEvent = new RegionCreate(cause, player, region);
		createMainEvent.setMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.VOLUME), Arrays.asList(region.getCuboid().getSize())), LocalesPaths.REGION_CREATE_CREATE_BASIC));
		ListenerUtils.postEvent(createMainEvent);
		positions.get(player.uniqueId()).clear();
		return createMainEvent;
	}

	private RegionCreateEvent createSubdivision(ServerPlayer player, Region subdivision, Region parrent) {
		RegionCreateEvent createSubdivisionEvent = new RegionCreate(cause, player, subdivision);
		if(parrent.getCuboid().getAABB().intersects(subdivision.getCuboid().getAABB())) {
			createSubdivisionEvent.setMessage(plugin.getLocales().getTextWithReplaced(player.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.VOLUME), Arrays.asList(subdivision.getCuboid().getSize())), LocalesPaths.REGION_CREATE_SUBDIVISION));
			subdivision.setParrent(parrent);
			ListenerUtils.postEvent(createSubdivisionEvent);
			positions.remove(player.uniqueId());
		} else {
			createSubdivisionEvent.setCancelled(true);
			createSubdivisionEvent.setMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_CREATE_EXCEPTION_WRONG_SUBDIVISION_POSITIONS));
		}
		return createSubdivisionEvent;
	}

	private RegionResizeEvent tryResizeRegion(ServerPlayer player, Region region, Vector3i newCorner, Vector3i oppositeCorner) {
		RegionResizeEvent resizeEvent;
		if(newCorner == null) {
			resizeEvent = new RegionResizeEventClass(cause, player, region, newCorner, oppositeCorner, plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_RESIZE_START));
		} else {
			resizeEvent = new RegionResizeEventClass(cause, player, region, newCorner, oppositeCorner, plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_RESIZE_FINISH));
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
		return region.isGlobal() ? true : isAllowInteractBlockPrimary(entity, plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), block, false);
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
		return region.isGlobal() ? true : isAllowInteractBlockSecondary(entity, plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), block, false);
	}

	private boolean isAllowLiquidFlow(Region region, BlockTransaction transaction) {
		if(ListenerUtils.nonReplacement(transaction)) return true;
		for(String block : ListenerUtils.flagBlockArgs(transaction.defaultReplacement())) {
			Tristate flagResult = region.getFlagResult(Flags.LIQUID_FLOW, null, block);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true: isAllowLiquidFlow(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), transaction);
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
		return region.isGlobal() ? true : isAllowPlace(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), transaction, entity, false);
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
		return region.isGlobal() ? true : isAllowPlace(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), transaction, entity, false);
	}

	private boolean isAllowFireSpread(Region region, BlockTransaction transaction) {
		region.getFlagResult(Flags.FIRE_SPREAD, null, null);
		return region.isGlobal() ? true : isAllowFireSpread(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), transaction);
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
		return region.isGlobal() ? true : isAllowGrowth(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), transaction, source, false);
	}

	private boolean isAllowDecay(Region region, BlockTransaction transaction) {
		for(String blockId : ListenerUtils.flagBlockArgs(transaction.finalReplacement())) {
			Tristate flagResult = region.getFlagResult(Flags.BLOCK_DECAY, null, blockId);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowDecay(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), transaction);
	}

	private boolean isAllowExplosion(Region region, Explosion explosion, BlockTransaction transaction) {
		if(explosion.sourceExplosive().isPresent()) {
			for(String entityId : ListenerUtils.flagEntityArgs(explosion.sourceExplosive().get())) {
				for(String blockId : ListenerUtils.flagBlockArgs(transaction.original())) {
					Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, entityId, blockId);
					if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
				}
			}
		} else {
			for(String blockId : ListenerUtils.flagBlockArgs(transaction.original())) {
				Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, null, blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowExplosion(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), explosion, transaction);
	}

	private boolean isAllowExplosion(Region region, Explosion explosion) {
		if(explosion.sourceExplosive().isPresent()) {
			for(String entityId : ListenerUtils.flagEntityArgs(explosion.sourceExplosive().get())) {
				Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, entityId, null);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		} else {
			Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, null, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowExplosion(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), explosion);
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

	class PlayerPositions {

		public Vector3i pos1;
		public Vector3i pos2;
		public Vector3i oppositeCorner;
		public Region tempRegion;
		public boolean resize = false;
		public long secondaryLastTime = 0;
		
		public void clear() {
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
		final Region copy = positions.get(player.uniqueId()).tempRegion.copy();
		if(blockPosition != null) copy.setCuboid(copy.getCuboid().getOppositeCorner(blockPosition), blockPosition, copy.getCuboid().getSelectorType());
		Region find = plugin.getAPI().findIntersectsRegion(copy).getPrimaryParent();
		List<Region> allChilds = find.getAllChilds();
		AABB copyAABB = copy.getCuboid().getAABB();
		if(!find.isGlobal() && (!copy.isSubdivision() && !(find.equals(copy)))) {
			clearVisual(player);
			plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(find, player, false, false);
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_RESIZE_EXCEPTION_REGIONS_INTERSECT));
			positions.get(player.uniqueId()).clear();
			return true;
		} else if(allChilds.contains(copy)) {
			Optional<Region> optChild = allChilds.stream().filter(child -> !child.equals(copy) && child.getCuboid().getAABB().intersects(copyAABB)).findFirst();
			if(optChild.isPresent()) {
				clearVisual(player);
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(optChild.get(), player, false, false);
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_RESIZE_EXCEPTION_REGIONS_INTERSECT));
				positions.get(player.uniqueId()).clear();
				return true;
			}
		}
		return false;
	}

	private boolean isIntersects(Region region, ServerPlayer player, Vector3i blockPosition, Vector3i oppositeCorner) {
		Region copy = positions.get(player.uniqueId()).tempRegion.copy();
		copy.setCuboid(oppositeCorner, blockPosition, copy.getCuboid().getSelectorType());
		Region find = plugin.getAPI().findIntersectsRegion(copy).getPrimaryParent();
		List<Region> allChilds = find.getAllChilds();
		AABB copyAABB = copy.getCuboid().getAABB();
		if(!find.isGlobal() && (!copy.isSubdivision() && !(find.equals(copy)))) {
			clearVisual(player);
			plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(find, player, false, false);
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_RESIZE_EXCEPTION_REGIONS_INTERSECT));
			positions.get(player.uniqueId()).clear();
			return true;
		} else if(allChilds.contains(copy)) {
			Optional<Region> optChild = allChilds.stream().filter(child -> !child.equals(copy) && child.getCuboid().getAABB().intersects(copyAABB)).findFirst();
			if(optChild.isPresent()) {
				clearVisual(player);
				plugin.getAPI().getWorldEditCUIAPI().visualizeRegion(optChild.get(), player, false, false);
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.REGION_RESIZE_EXCEPTION_REGIONS_INTERSECT));
				positions.get(player.uniqueId()).clear();
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
