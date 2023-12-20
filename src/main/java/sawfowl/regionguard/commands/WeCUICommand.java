package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.worldedit.CUIUser;
import sawfowl.regionguard.configure.LocalesPaths;

public class WeCUICommand implements PluginRawCommand {

	private final RegionGuard plugin;
	private List<CommandCompletion> empty = new ArrayList<>();
	public WeCUICommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		CUIUser user = plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player);
		boolean newValue = !user.isSupportCUI();
		if(newValue) {
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_WECUI_ENABLE));
		} else {
			plugin.getAPI().getWorldEditCUIAPI().revertVisuals(player, null);
			plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(player);
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_WECUI_DISABLE));
		}
		user.setSupportCUI(newValue);
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.CUI_COMMAND);
	}

	@Override
	public CommandException usage() throws CommandException {
		throw new CommandException(text("Usage: /rg wecui"));
	}

}
