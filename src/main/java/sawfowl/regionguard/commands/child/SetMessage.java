package sawfowl.regionguard.commands.child;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.commands.raw.arguments.RawArguments;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;

public class SetMessage extends AbstractPlayerCommand {

	private Map<String, Locale> locales;
	private List<String> flags;
	public SetMessage(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale srcLocale, String[] args, Mutable arguments) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(srcLocale, LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND);
		if(!src.hasPermission(Permissions.STAFF_SET_MESSAGE)) {
			if(!region.isTrusted(src)) exception(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_NOT_TRUSTED);
			if(region.isCurrentTrustType(src, TrustTypes.OWNER) || region.isCurrentTrustType(src, TrustTypes.MANAGER)) exception(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_LOW_TRUST);
		}
		if(args.length == 0) exception(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_NOT_PRESENT);
		boolean clearFlag = getString(args, 2).isPresent();
		boolean exit = getString(args, 1).filter(string -> string.equals("-e") || string.equals("-exit")).isPresent();
		boolean join = getString(args, 1).filter(string -> string.equals("-j") || string.equals("-join")).isPresent() || !exit;
		Locale locale = getString(args, 0).isPresent() ? locales.get(getString(args, 0).get()) : srcLocale;
		if(clearFlag) {
			if(join) {
				region.setJoinMessage(null, locale);
				src.sendMessage(plugin.getLocales().getComponent(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_CLEAR_JOIN));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else if(exit) {
				region.setExitMessage(null, locale);
				src.sendMessage(plugin.getLocales().getComponent(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_CLEAR_EXIT));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else {
				exception(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_TYPE_NOT_PRESENT);
			}
		} else {
			Component message = getArgument(Component.class, cause, args, 3).get();
			if(TextUtils.clearDecorations(message).length() > 50) exception(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_TOO_LONG);
			if(join) {
				region.setJoinMessage(message, locale);
				src.sendMessage(plugin.getLocales().getComponent(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_JOIN));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else if(exit) {
				region.setExitMessage(message, locale);
				src.sendMessage(plugin.getLocales().getComponent(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_SUCCESS_EXIT));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else {
				exception(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_TYPE_NOT_PRESENT);
			}
		}
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_SET_MESSAGE);
	}

	@Override
	public String permission() {
		return Permissions.SET_MESSAGE;
	}

	@Override
	public String command() {
		return "setmessage";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg setmessage &7[Locale] [Flags] <Message>&f - ").clickEvent(ClickEvent.suggestCommand("/rg setmessage ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		if(locales == null) locales = plugin.getLocales().getLocaleService().getLocalesList().stream().collect(Collectors.toMap(locale -> locale.toLanguageTag(), locale -> locale));
		if(flags == null) flags = Arrays.asList("-j", "-join", "-e", "-exit");
		return Arrays.asList(
			RawArguments.createStringArgument(locales.keySet(), true, true, 0, null, null),
			RawArguments.createStringArgument(flags, true, true, 1, null, null),
			RawArguments.createStringArgument(Arrays.asList("-c", "-clear"), true, true, 2, null, null),
			RawArgument.of(
				Component.class,
				null,
				(cause, args) -> Stream.empty(),
				null,
				(cause, args) -> Optional.ofNullable(args.length > 0 ? TextUtils.deserialize(String.join(" ", ArrayUtils.removeElements(args, getString(args, 0).orElse(""), getString(args, 1).orElse("")))) : null),
				null,
				false,
				false,
				3,
				null,
				LocalesPaths.COMMAND_SET_MESSAGE_TYPE_NOT_PRESENT
			)
		);
	}

}
