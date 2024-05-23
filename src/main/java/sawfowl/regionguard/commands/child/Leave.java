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
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;

public class Leave extends AbstractPlayerCommand {

	public Leave(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(locale, LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND);
		if(region.isCurrentTrustType(src, TrustTypes.OWNER)) exception(plugin.getLocales().getComponent(locale, LocalesPaths.COMMAND_LEAVE_PLAYER_IS_OWNER));
		if(!region.isTrusted(src)) exception(locale, LocalesPaths.COMMAND_LEAVE_PLAYER_NOT_TRUSTED);
		src.sendMessage(getText(locale, LocalesPaths.COMMAND_LEAVE_CONFIRMATION_REQUEST).createCallBack(messageCause -> {
			if(region.isTrusted(src)) {
				region.untrust(src);
				plugin.getAPI().saveRegion(region);
				src.sendMessage(plugin.getLocales().getComponent(locale, LocalesPaths.COMMAND_LEAVE_SUCCESS));
			}
		}).get());
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.audience() instanceof ServerPlayer;
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_LEAVE);
	}

	@Override
	public String permission() {
		return null;
	}

	@Override
	public String command() {
		return "leave";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg leave &f - ").clickEvent(ClickEvent.runCommand("/rg leave ")).append(extendedDescription(getLocale(cause)));
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
