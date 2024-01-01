package sawfowl.regionguard.listeners;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.SpawnType;
import org.spongepowered.api.event.cause.entity.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent.Pre;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.common.mixin.core.world.entity.item.ItemEntityMixin;

import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.item.ItemEntity;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionSpawnEntityEvent;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ListenerUtils;
import sawfowl.regionguard.utils.ReflectionUtil;

public class SpawnEntityListener {

	private final RegionGuard plugin;
	private Cause cause;
	public SpawnEntityListener(RegionGuard plugin) {
		this.plugin = plugin;
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onSpawn(SpawnEntityEvent.Pre event) {
		if(!event.context().get(EventContextKeys.SPAWN_TYPE).isPresent() || event.entities().isEmpty()) return;
		Optional<Entity> optSource = event.cause().first(Entity.class);
		Optional<ServerPlayer> optPlayer = event.cause().first(ServerPlayer.class);
		ResourceKey worldKey = event.entities().get(0).serverLocation().world().key();
		Region region = plugin.getAPI().findRegion(worldKey, event.entities().get(0).blockPosition());
		SpawnType spawnType = event.context().get(EventContextKeys.SPAWN_TYPE).get();
		String spawnKey = Sponge.game().registry(RegistryTypes.SPAWN_TYPE).valueKey(spawnType).asString();
		boolean spawnExp = spawnType.equals(SpawnTypes.EXPERIENCE.get());
		boolean spawnItem = spawnType.equals(SpawnTypes.DROPPED_ITEM.get());
		boolean spawnEntity = spawnType.equals(SpawnTypes.WORLD_SPAWNER.get()) || spawnType.equals(SpawnTypes.MOB_SPAWNER.get()) || spawnType.equals(SpawnTypes.SPAWN_EGG.get()) || spawnType.equals(SpawnTypes.PLACEMENT.get()) || spawnType.equals(SpawnTypes.PROJECTILE.get()) || spawnType.equals(SpawnTypes.PASSIVE.get());
		if(optPlayer.isPresent() && (spawnExp || spawnItem)) return;
		boolean allowSpawnExp = spawnExp && (optSource.isPresent() ? isAllowExpSpawn(region, optSource.get()) : isAllowExpSpawn(region, null));
		boolean allowSpawnItem = true;
		if(spawnItem) {
			List<ItemStack> items = event.entities().stream().map(entity -> ReflectionUtil.getValueFromMethodWhithTypeNoArgs(net.minecraft.world.item.ItemStack.class, ItemStack.class, ((ItemEntity) entity))).toList();
			allowSpawnItem = optSource.isPresent() ? isAllowItemSpawn(region, optSource.get(), items) : isAllowItemSpawn(region, null, items);
		} else allowSpawnItem = false;
		boolean allowSpawnEntity = spawnEntity && (optSource.isPresent() ? isAllowEntitySpawn(region, optSource.get(), event.entities(), spawnKey) : isAllowEntitySpawn(region, null, event.entities(), spawnKey));
		boolean allowSpawn = allowSpawnExp || allowSpawnItem || allowSpawnEntity;
		class SpawnEvent implements RegionSpawnEntityEvent {

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
			public Optional<Entity> getEntity() {
				return optSource;
			}

			@Override
			public Optional<ServerPlayer> getPlayer() {
				return optPlayer;
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public boolean isAllow() {
				return allowSpawn;
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
			public Pre spongeEvent() {
				return event;
			}
			
		}
		RegionSpawnEntityEvent rgEvent = new SpawnEvent();
		rgEvent.setCancelled(!allowSpawn);
		if(optPlayer.isPresent() && rgEvent.isCancelled()) rgEvent.setMessage(plugin.getLocales().getComponent(optPlayer.get().locale(), LocalesPaths.SPAWN));
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			if(rgEvent.getPlayer().isPresent() && rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		}
	}

	private boolean isAllowExpSpawn(Region region, Entity source) {
		if(source != null && source instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) source;
			if(player.hasPermission(Permissions.bypassFlag(Flags.EXP_SPAWN)) || region.isCurrentTrustType(player, TrustTypes.USER) || region.isCurrentTrustType(player, TrustTypes.MANAGER) || region.isCurrentTrustType(player, TrustTypes.OWNER)) return true;
		}
		for(String entityId : ListenerUtils.flagEntityArgs(source)) {
			Tristate flagResult = region.getFlagResult(Flags.EXP_SPAWN, entityId, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowExpSpawn(plugin.getAPI().getGlobalRegion(region.getWorldKey()), source);
	}

	private boolean isAllowItemSpawn(Region region, Entity source, List<ItemStack> stacks) {
		if(source != null && source instanceof ServerPlayer) return true;
		for(String sourceId : ListenerUtils.flagEntityArgs(source)) {
			for(String targetid : ListenerUtils.flagItemsArgs(stacks)) {
				Tristate flagResult = region.getFlagResult(Flags.ITEM_SPAWN, 
						sourceId, 
						targetid);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowItemSpawn(plugin.getAPI().getGlobalRegion(region.getWorldKey()), source, stacks);
	}

	private boolean isAllowEntitySpawn(Region region, Entity source, List<Entity> entities, String spawnType) {
		if(source != null && source instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) source;
			if(player.hasPermission(Permissions.bypassFlag(Flags.ENTITY_SPAWN)) || region.isCurrentTrustType(player, TrustTypes.USER) || region.isCurrentTrustType(player, TrustTypes.MANAGER) || region.isCurrentTrustType(player, TrustTypes.OWNER)) return true;
		}
		if(source != null) {
			for(String entityId : ListenerUtils.flagEntityArgs(source)) {
				for(String targetid : ListenerUtils.flagEntitiesArgs(entities)) {
					Tristate flagResult = region.getFlagResult(Flags.ENTITY_SPAWN, entityId, targetid);
					if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
				}
			}
		} else {
			for(String targetid : ListenerUtils.flagEntitiesArgs(entities)) {
				Tristate flagResult1 = region.getFlagResult(Flags.ENTITY_SPAWN, spawnType, targetid);
				Tristate flagResult2 = region.getFlagResult(Flags.ENTITY_SPAWN, null, targetid);
				if(flagResult1 != Tristate.UNDEFINED) return flagResult1.asBoolean();
				if(flagResult2 != Tristate.UNDEFINED) return flagResult2.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowEntitySpawn(plugin.getAPI().getGlobalRegion(region.getWorldKey()), source, entities, spawnType);
	}

}
