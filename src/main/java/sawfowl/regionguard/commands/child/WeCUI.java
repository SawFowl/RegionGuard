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
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.worldedit.CUIUser;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class WeCUI extends AbstractPlayerCommand {

	public WeCUI(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		CUIUser user = plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(src);
		boolean newValue = !user.isSupportCUI();
		if(!newValue) {
			plugin.getAPI().getWorldEditCUIAPI().revertVisuals(src, null);
			plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(src);
		}
		user.setSupportCUI(newValue);
		src.sendMessage(getCommand(locale).getWeCUI().get(newValue));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getWeCUI().getDescription();
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
