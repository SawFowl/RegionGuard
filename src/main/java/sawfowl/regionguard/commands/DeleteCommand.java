package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionDeleteEvent;
import sawfowl.regionguard.configure.LocalesPaths;

public class DeleteCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	List<CommandCompletion> empty = new ArrayList<CommandCompletion>();
	List<CommandCompletion> regen = Arrays.asList(CommandCompletion.of("-r"), CommandCompletion.of("-regen"));
	public DeleteCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		if(region.isGlobal()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND));
		if(!region.getOwnerUUID().equals(player.uniqueId()) && !player.hasPermission(Permissions.STAFF_DELETE)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_PLAYER_NOT_OWNER));
		boolean regen = !region.getParrent().isPresent() && (player.hasPermission(Permissions.STAFF_DELETE) ? (args.contains("-regen") || args.contains("-r")) && plugin.getConfig().regenStaff() : plugin.getConfig().regenAll());
		if(regen) player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_DELETE_REGEN));
		player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_DELETE_CONFIRMATION_REQUEST).clickEvent(SpongeComponents.executeCallback(messageCause -> {
			if(region.getParrent().isPresent()) {
				Region parrent = region.getParrent().get();
				RegionDeleteEvent event = new RegionDeleteEvent() {
					boolean canceled;
					Component send;
					@Override
					public void setCancelled(boolean cancel) {
						canceled = cancel;
					}
					@Override
					public boolean isCancelled() {
						return canceled;
					}
					@Override
					public void setMessage(Component message) {
						send = message;
					}
					@Override
					public Optional<Component> getMessage() {
						return Optional.ofNullable(send);
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
					public Cause cause() {
						return messageCause.cause();
					}
					@Override
					public Object getSource() {
						return player;
					}
				};
				event.setMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_DELETE_CHILD_DELETED));
				Sponge.eventManager().post(event);
				if(!event.isCancelled()) {
					parrent.removeChild(region);
					plugin.getAPI().saveRegion(parrent.getPrimaryParent());
				}
				if(event.getMessage().isPresent()) player.sendMessage(event.getMessage().get());
			} else {
				RegionDeleteEvent event = new RegionDeleteEvent() {
					boolean canceled;
					Component send;
					@Override
					public void setCancelled(boolean cancel) {
						canceled = cancel;
					}
					@Override
					public boolean isCancelled() {
						return canceled;
					}
					@Override
					public void setMessage(Component message) {
						send = message;
					}
					@Override
					public Optional<Component> getMessage() {
						return Optional.ofNullable(send);
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
					public Cause cause() {
						return messageCause.cause();
					}
					@Override
					public Object getSource() {
						return player;
					}
				};
				event.setMessage(region.containsChilds() ? plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_DELETE_DELETED_MAIN_AND_CHILDS) : plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_DELETE_DELETED));
				Sponge.eventManager().post(event);
				if(!event.isCancelled()) {
					if(region.getType() != RegionTypes.UNSET) {
						region.setRegionType(RegionTypes.UNSET);
						if(regen) region.regen(plugin.getConfig().asyncRegen(), plugin.getConfig().delayRegen());
						plugin.getAPI().deleteRegion(region);
						Optional<PlayerData> optPlayerData = plugin.getAPI().getPlayerData(player);
						if(optPlayerData.isPresent()) {
							optPlayerData.get().getClaimed().setRegions(plugin.getAPI().getClaimedRegions(player) - 1);
							optPlayerData.get().getClaimed().setBlocks(plugin.getAPI().getClaimedBlocks(player) - region.getCuboid().getSize());
							plugin.getPlayersDataWork().savePlayerData(player, optPlayerData.get());
						} else {
							PlayerData playerData = new PlayerData(
								new PlayerLimits(plugin.getAPI().getLimitBlocks(player), plugin.getAPI().getLimitClaims(player), plugin.getAPI().getLimitSubdivisions(player), plugin.getAPI().getLimitMembers(player)), 
								new ClaimedByPlayer(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player))
							);
							plugin.getPlayersDataWork().savePlayerData(player, playerData);
						}
					}
					if(event.getMessage().isPresent()) player.sendMessage(event.getMessage().get());
				}
			}
		plugin.getAPI().getWorldEditCUIAPI().revertVisuals(player, region.getUniqueId());
		})));
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(!cause.hasPermission(Permissions.STAFF_DELETE)) return empty;
		String plainArgs = arguments.input();
		if(!plainArgs.contains("delete ")) return empty;
		if(args.size() == 0 && plugin.getConfig().regenStaff() && cause.hasPermission(Permissions.STAFF_DELETE)) return regen;
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.DELETE);
	}

}
