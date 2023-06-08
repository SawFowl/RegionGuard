package sawfowl.regionguard.listeners;

import java.util.Optional;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.CollideBlockEvent.Impact;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.projectile.source.ProjectileSource;
import org.spongepowered.api.util.Tristate;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionImpactEvent;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ListenerUtils;

public class ImpactListener{

	private final RegionGuard plugin;
	private Cause cause;
	public ImpactListener(RegionGuard plugin) {
		this.plugin = plugin;
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void impactBlock(CollideBlockEvent.Impact event) {
		Optional<Projectile> optProjectile = event.cause().first(Projectile.class);
		if(!optProjectile.isPresent() || !optProjectile.get().shooter().isPresent()) return;
		Projectile projectile = optProjectile.get();
		ProjectileSource projectileSource = projectile.shooter().get().get();
		if(!(projectileSource instanceof Entity)) return;
		Entity entity = (Entity) projectileSource;
		ServerPlayer player = entity instanceof ServerPlayer ? (ServerPlayer) entity : null;
		BlockState blockState = event.targetBlock();
		Region region = plugin.getAPI().findRegion(event.impactPoint().world(), event.impactPoint().blockPosition());
		boolean isAllow = isAllowImpactBlock(region, entity, blockState);
		class ImpactEvent implements RegionImpactEvent.Block {

			boolean cancelled;
			Component message;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public Impact spongeEvent() {
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
			public org.spongepowered.api.entity.Entity getEntitySource() {
				return entity;
			}

			@Override
			public boolean isAllow() {
				return isAllow;
			}

			@Override
			public Optional<ServerPlayer> getPlayer() {
				return Optional.ofNullable(player);
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
			public Region getRegion() {
				return region;
			}
			
		}
		RegionImpactEvent.Block rgEvent = new ImpactEvent();
		rgEvent.setCancelled(!isAllow);
		if(player != null) rgEvent.setMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.IMPACT_BLOCK));
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			projectile.remove();
			if(rgEvent.getPlayer().isPresent() && rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		}
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void impactEntity(CollideEntityEvent.Impact event) {
		Optional<Projectile> optProjectile = event.cause().first(Projectile.class);
		Optional<Entity> optTargetEntity = event.entities().stream().filter(e -> (e.blockPosition().equals(event.impactPoint().blockPosition()))).findFirst();
		if(!optProjectile.isPresent() || !optProjectile.get().shooter().isPresent() || !optTargetEntity.isPresent()) return;
		Projectile projectile = optProjectile.get();
		ProjectileSource projectileSource = projectile.shooter().get().get();
		if(!(projectileSource instanceof Entity)) return;
		Entity targetEntity = optTargetEntity.get();
		Entity entity = (Entity) projectileSource;
		ServerPlayer player = entity instanceof ServerPlayer ? (ServerPlayer) entity : null;
		Region region = plugin.getAPI().findRegion(event.impactPoint().world(), event.impactPoint().blockPosition());
		boolean isAllow = isAllowImpactEntity(region, entity, targetEntity);
		class ImpactEvent implements RegionImpactEvent.Entity {

			boolean cancelled;
			Component message;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public org.spongepowered.api.event.entity.CollideEntityEvent.Impact spongeEvent() {
				return event;
			}

			@Override
			public org.spongepowered.api.entity.Entity getEntitySource() {
				return entity;
			}

			@Override
			public boolean isAllow() {
				return isAllow;
			}

			@Override
			public Optional<ServerPlayer> getPlayer() {
				return Optional.ofNullable(player);
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
			public boolean isCancelled() {
				return cancelled;
			}

			@Override
			public void setCancelled(boolean cancel) {
				cancelled = cancel;
			}

			@Override
			public Region getRegion() {
				return region;
			}
			
		}
		RegionImpactEvent.Entity rgEvent = new ImpactEvent();
		rgEvent.setCancelled(!isAllow);
		if(player != null) rgEvent.setMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.IMPACT_ENTITY));
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			projectile.remove();
			if(rgEvent.getPlayer().isPresent() && rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		}
	}

	private boolean isAllowImpactBlock(Region region, Entity entity, BlockState blockState) {
		if(entity instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) entity;
			if(player.hasPermission(Permissions.bypassFlag(Flags.PROJECTILE_IMPACT_BLOCK))) return true;
			if(region.isCurrentTrustType(player, TrustTypes.BUILDER) || region.isCurrentTrustType(player, TrustTypes.USER) || region.isCurrentTrustType(player, TrustTypes.MANAGER) || region.isCurrentTrustType(player, TrustTypes.OWNER)) return true;
		}
		for(String entityId : ListenerUtils.flagEntityArgs(entity)) {
			for(String blockId : ListenerUtils.flagBlockArgs(blockState)) {
				Tristate flagResult = region.getFlagResult(Flags.PROJECTILE_IMPACT_BLOCK, entityId, blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowImpactBlock(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), entity, blockState);
	}

	private boolean isAllowImpactEntity(Region region, Entity source, Entity target) {
		if(source instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) source;
			if(player.hasPermission(Permissions.bypassFlag(Flags.PROJECTILE_IMPACT_ENTITY))) return true;
			if(region.isCurrentTrustType(player, TrustTypes.HUNTER) || region.isCurrentTrustType(player, TrustTypes.USER) || region.isCurrentTrustType(player, TrustTypes.MANAGER) || region.isCurrentTrustType(player, TrustTypes.OWNER)) return true;
		}
		for(String entityId : ListenerUtils.flagEntityArgs(source)) {
			for(String blockId : ListenerUtils.flagEntityArgs(target)) {
				Tristate flagResult = region.getFlagResult(Flags.PROJECTILE_IMPACT_ENTITY, entityId, blockId);
				if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
			}
		}
		return region.isGlobal() ? true : isAllowImpactEntity(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), source, target);
	}

}
