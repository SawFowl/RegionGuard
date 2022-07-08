package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.LocalesPaths;

public class LeaveCommand implements Command.Raw {

	private final RegionGuard plugin;
	private List<CommandCompletion> empty = new ArrayList<>();
	public LeaveCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		Region region = plugin.getAPI().findRegion(player.world(), player.blockPosition());
		if(region.isGlobal()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND));
		if(region.isCurrentTrustType(player, TrustTypes.OWNER)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_LEAVE_PLAYER_IS_OWNER));
		if(!region.isTrusted(player)) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_LEAVE_PLAYER_NOT_TRUSTED));
		player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_LEAVE_CONFIRMATION_REQUEST).clickEvent(SpongeComponents.executeCallback(messageCause -> {
			if(region.isTrusted(player)) {
				region.untrust(player);
				plugin.getAPI().saveRegion(region);
				player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_LEAVE_SUCCESS));
			}
		})));
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return true;
	}

	@Override
	public Optional<Component> shortDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Leave from region."));
	}

	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Leave from region."));
	}

	@Override
	public Component usage(CommandCause cause) {
		return Component.text("/rg leave");
	}

}
