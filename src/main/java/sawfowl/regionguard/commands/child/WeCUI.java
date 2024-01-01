package sawfowl.regionguard.commands.child;

import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.worldedit.CUIUser;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;

public class WeCUI extends AbstractPlayerCommand {

	public WeCUI(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		CUIUser user = plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(src);
		boolean newValue = !user.isSupportCUI();
		if(newValue) {
			src.sendMessage(getComponent(locale, LocalesPaths.COMMAND_WECUI_ENABLE));
		} else {
			plugin.getAPI().getWorldEditCUIAPI().revertVisuals(src, null);
			plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(src);
			src.sendMessage(getComponent(locale, LocalesPaths.COMMAND_WECUI_DISABLE));
		}
		user.setSupportCUI(newValue);
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_WECUI);
	}

	@Override
	public String permission() {
		return Permissions.CUI_COMMAND;
	}

	@Override
	public String command() {
		return "wecui";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg wecui&f - ").clickEvent(ClickEvent.runCommand("/rg wecui")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return null;
	}

}
