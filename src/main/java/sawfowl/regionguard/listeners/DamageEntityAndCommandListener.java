package sawfowl.regionguard.listeners;

import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.event.command.ExecuteCommandEvent.Pre;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.projectile.source.ProjectileSource;
import org.spongepowered.api.util.Tristate;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionDamageEntityEvent;
import sawfowl.regionguard.api.events.RegionExecuteCommandEvent;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ListenerUtils;

public class DamageEntityAndCommandListener {

	private final RegionGuard plugin;
	private Cause cause;
	public DamageEntityAndCommandListener(RegionGuard plugin) {
		this.plugin = plugin;
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	Map<UUID, Long> lastDamage = new HashMap<UUID, Long>();

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onCommand(ExecuteCommandEvent.Pre event, @First ServerPlayer player) {
		if(event.command().contains("pagination") || event.command().contains("callback")) return;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		boolean isPvP = lastDamage.containsKey(player.uniqueId()) && System.currentTimeMillis() - lastDamage.get(player.uniqueId()) < 20000;
		boolean isAllow = isPvP ? isAllowPvPCommand(region, player, event.command()) : isAllowCommand(region, player, event.command());
		Component message = isPvP ? plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_EXECUTE_PVP) : plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_EXECUTE);
		class CommandEvent extends AbstractEvent implements RegionExecuteCommandEvent {

			boolean cencelled;
			Component message;
			@Override
			public Cause cause() {
				return cause;
			}

			@Override
			public boolean isCancelled() {
				return cencelled;
			}

			@Override
			public void setCancelled(boolean cancel) {
				cencelled = cancel;
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public ServerPlayer getPlayer() {
				return player;
			}

			@Override
			public boolean isAllow() {
				return isAllow;
			}

			@Override
			public boolean isPvP() {
				return isPvP;
			}

			@Override
			public Pre spongeEvent() {
				return event;
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
		RegionExecuteCommandEvent rgEvent = new CommandEvent();
		rgEvent.setCancelled(!isAllow);
		rgEvent.setMessage(message);
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			if(rgEvent.getMessage().isPresent()) rgEvent.getPlayer().sendMessage(rgEvent.getMessage().get());
		}
	}

	@Listener(order = Order.FIRST, beforeModifications = true)
	public void onDamage(DamageEntityEvent event) {
		Optional<ServerPlayer> optPlayer = event.cause().first(ServerPlayer.class);
		ServerPlayer player = optPlayer.isPresent() ? optPlayer.get() : null;
		Optional<Entity> optEntity = event.cause().first(Entity.class);
		Optional<DamageSource> optDamageSource = event.cause().first(DamageSource.class);
		ResourceKey worldKey = ResourceKey.resolve(event.entity().world().context().getValue());
		Region region = plugin.getAPI().findRegion(worldKey, event.entity().blockPosition());
		Component message = null;
		boolean isAllow = true;
		if(optPlayer.isPresent() && event.entity() instanceof ServerPlayer && !isAllowPvP(region, optPlayer.get()) && !optPlayer.get().uniqueId().equals(event.entity().uniqueId())) {
			isAllow = false;
			message = plugin.getLocales().getText(optPlayer.get().locale(), LocalesPaths.PVP);
		} else if(optEntity.isPresent())  {
			Entity entity = optEntity.get();
			if(entity instanceof Projectile) {
				Projectile projectile = (Projectile) entity;
				if(projectile.shooter().isPresent()) {
					ProjectileSource projectileSource = projectile.shooter().get().get();
					if(projectileSource instanceof Entity) entity = (Entity) projectileSource;
				}
			}
			boolean isPlayer = entity instanceof ServerPlayer;
			if(entity.uniqueId().equals(event.entity().uniqueId())) return;
			if(isPlayer && event.entity() instanceof ServerPlayer && !isAllowPvP(region, optPlayer.get())) {
				isAllow = false;
				player = (ServerPlayer) entity;
				message = plugin.getLocales().getText(player.locale(), LocalesPaths.PVP);
			} else if(!isAllowDamage(region, event.entity(), entity)) {
				if(isPlayer) {
					message = plugin.getLocales().getText(player.locale(), LocalesPaths.ENTITY_DAMAGE);
					player = (ServerPlayer) entity;
				}
				isAllow = false;
			}
		} else if(optDamageSource.isPresent() && !isAllowDamage(region, event.entity(), optDamageSource.get())) {
			isAllow = false;
		}
		boolean allowDamage = isAllow;
		ServerPlayer finalPlayer = player;
		class DamageEvent extends AbstractEvent implements RegionDamageEntityEvent {

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
			public Region getRegion() {
				return region;
			}

			@Override
			public Optional<ServerPlayer> getPlayer() {
				return Optional.ofNullable(finalPlayer);
			}

			@Override
			public boolean isAllowDamage() {
				return allowDamage;
			}

			@Override
			public DamageEntityEvent spongeEvent() {
				return event;
			}

			@Override
			public Optional<Component> getMessage() {
				return Optional.ofNullable(message);
			}

			@Override
			public void setMessage(Component component) {
				message = component;
			}
			
		};
		RegionDamageEntityEvent rgEvent = new DamageEvent();
		rgEvent.setCancelled(!allowDamage);
		rgEvent.setMessage(message);
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCancelled(true);
			if(rgEvent.getPlayer().isPresent() && rgEvent.getMessage().isPresent()) rgEvent.getPlayer().get().sendMessage(rgEvent.getMessage().get());
		}
		if(!event.isCancelled() && player != null && event.entity() instanceof ServerPlayer) {
			long time = System.currentTimeMillis();
			if(lastDamage.containsKey(player.uniqueId())) lastDamage.remove(player.uniqueId());
			if(lastDamage.containsKey(event.entity().uniqueId())) lastDamage.remove(event.entity().uniqueId());
			lastDamage.put(player.uniqueId(), time);
			if(!event.willCauseDeath()) lastDamage.put(event.entity().uniqueId(), time);
		}
	}

	private boolean isAllowPvP(Region region, ServerPlayer player) {
		if(player.hasPermission(Permissions.bypassFlag(Flags.PVP))) return true;
		Tristate finalFlagResult = region.getFlagResult(Flags.PVP, null, null);
		return region.isGlobal() ? (finalFlagResult == Tristate.UNDEFINED ? true : finalFlagResult.asBoolean()) : isAllowPvP(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), player);
	}

	private boolean isAllowDamage(Region region, Entity entity, Object damageSource) {
		if(damageSource == null) return true;
		if(damageSource instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) damageSource;
			if(player.hasPermission(Permissions.bypassFlag(Flags.ENTITY_DAMAGE))) return true;
			if(!(entity instanceof ServerPlayer) && (region.isCurrentTrustType(player, TrustTypes.OWNER) || region.isCurrentTrustType(player, TrustTypes.MANAGER) || region.isCurrentTrustType(player, TrustTypes.USER) || region.isCurrentTrustType(player, TrustTypes.HUNTER))) return true;
		} else if(damageSource instanceof Entity) {
			Entity entitySource = (Entity) damageSource;
			for(String sourceid : ListenerUtils.flagEntityArgs(entitySource)) {
				for(String targetid : ListenerUtils.flagEntityArgs(entity)) {
					Tristate flagResult = region.getFlagResult(Flags.ENTITY_DAMAGE, sourceid, targetid);
					if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
				}
			}
		} else if(damageSource instanceof DamageSource) {
			DamageSource source = (DamageSource) damageSource;
			for(String sourceid : ListenerUtils.flagDamageSourceArgs(source)) {
				for(String targetid : ListenerUtils.flagEntityArgs(entity)) {
					Tristate flagResult = region.getFlagResult(Flags.ENTITY_DAMAGE, sourceid, targetid);
					if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
				}
			}
		}
		return region.isGlobal() ? true : isAllowDamage(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), entity, damageSource);
	}

	private boolean isAllowPvPCommand(Region region, ServerPlayer player, String command) {
		if(player.hasPermission(Permissions.bypassFlag(Flags.COMMAND_EXECUTE_PVP))) return true;
		for(FlagValue flagValue : region.getFlagValues(Flags.COMMAND_EXECUTE_PVP)) {
			if(!flagValue.isBasic() && flagValue.getTarget().contains(command)) return flagValue.getValue();
		}
		Tristate flagResult = region.getFlagResult(Flags.COMMAND_EXECUTE_PVP, null, null);
		if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		return region.isGlobal() ? true : isAllowPvPCommand(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), player, command);
	}

	private boolean isAllowCommand(Region region, ServerPlayer player, String command) {
		if(player.hasPermission(Permissions.bypassFlag(Flags.COMMAND_EXECUTE))) return true;
		for(FlagValue flagValue : region.getFlagValues(Flags.COMMAND_EXECUTE)) {
			if(!flagValue.isBasic() && flagValue.getTarget().contains(command)) return flagValue.getValue();
		}
		Tristate flagResult = region.getFlagResult(Flags.COMMAND_EXECUTE, null, null);
		if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		return region.isGlobal() ? true : isAllowCommand(plugin.getAPI().getGlobalRegion(region.getServerWorldKey()), player, command);
	}

}
