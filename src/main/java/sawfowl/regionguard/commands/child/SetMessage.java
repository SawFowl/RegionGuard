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
import org.spongepowered.api.command.registrar.tree.CommandTreeNodeTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.commands.raw.arguments.RawArguments;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
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
	public void process(CommandCause cause, ServerPlayer src, Locale srcLocale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(srcLocale, LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND);
		if(!src.hasPermission(Permissions.STAFF_SET_MESSAGE)) {
			if(!region.isTrusted(src)) exception(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_NOT_TRUSTED);
			if(region.isCurrentTrustType(src, TrustTypes.OWNER) || region.isCurrentTrustType(src, TrustTypes.MANAGER)) exception(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_LOW_TRUST);
		}
		if(args.getInput().length == 0) exception(srcLocale, LocalesPaths.COMMAND_SET_MESSAGE_NOT_PRESENT);
		boolean clearFlag = args.getString(2).isPresent();
		boolean exit = args.getString(1).filter(string -> string.equals("-e") || string.equals("-exit")).isPresent();
		boolean join = args.getString(1).filter(string -> string.equals("-j") || string.equals("-join")).isPresent() || !exit;
		Locale locale = args.getString(0).isPresent() ? locales.get(args.getString(0).get()) : srcLocale;
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
			Component message = args.get(Component.class, 3).get();
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
			RawArguments.createLocaleArgument(true, true, 0, null, null),
			RawArguments.createStringArgument("MessageType", flags, true, true, 1, null, null, null),
			RawArguments.createStringArgument("Clear | Message", Arrays.asList("-c", "-clear"), true, true, 2, null, null, null),
			RawArgument.of(
				Component.class,
				CommandTreeNodeTypes.STRING.get().createNode().greedy(),
				(cause, args) -> Stream.empty(),
				(cause, args) -> Optional.ofNullable(args.length > 0 ? TextUtils.deserialize(String.join(" ", ArrayUtils.removeElements(args, (locales.containsKey(args[0]) ? args[0] : ""), (flags.contains(args[1]) ? args[1] : "")))) : null),
				"Message",
				false,
				false,
				3,
				null,
				LocalesPaths.COMMAND_SET_MESSAGE_TYPE_NOT_PRESENT
			)
		);
	}

}
