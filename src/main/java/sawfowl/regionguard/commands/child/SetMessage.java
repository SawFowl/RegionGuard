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
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentData;
import sawfowl.commandpack.api.commands.raw.arguments.RawArguments;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.commandpack.api.commands.raw.arguments.RawBasicArgumentData;
import sawfowl.commandpack.api.commands.raw.arguments.RawOptional;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class SetMessage extends AbstractPlayerCommand {

	private Map<String, Locale> locales;
	private List<String> flags;
	private List<String> clear;
	public SetMessage(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale srcLocale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(getExceptions(srcLocale).getRegionNotFound());
		if(!src.hasPermission(Permissions.STAFF_SET_MESSAGE)) {
			if(!region.isTrusted(src)) exception(getSetMessage(srcLocale).getNotTrusted());
			if(region.isCurrentTrustType(src, TrustTypes.OWNER) || region.isCurrentTrustType(src, TrustTypes.MANAGER)) exception(getSetMessage(srcLocale).getLowTrust());
		}
		if(args.getInput().length == 0) exception(getExceptions(srcLocale).getMessageNotPresent());
		boolean clearFlag = args.getString(2).isPresent();
		boolean exit = args.getString(1).filter(string -> string.equals("-e") || string.equals("-exit")).isPresent();
		boolean join = args.getString(1).filter(string -> string.equals("-j") || string.equals("-join")).isPresent() || !exit;
		Locale locale = args.getString(0).isPresent() ? locales.get(args.getString(0).get()) : srcLocale;
		if(clearFlag) {
			if(join) {
				region.setJoinMessage(null, locale);
				src.sendMessage(getSetMessage(srcLocale).getSuccessJoin(true));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else if(exit) {
				region.setExitMessage(null, locale);
				src.sendMessage(getSetMessage(srcLocale).getSuccessExit(true));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else {
				exception(getSetMessage(srcLocale).getTypeNotPresent());
			}
		} else {
			Component message = args.get(Component.class, 3).get();
			if(TextUtils.clearDecorations(message).length() > 50) exception(getSetMessage(srcLocale).getTooLong());
			if(join) {
				region.setJoinMessage(message, locale);
				src.sendMessage(getSetMessage(srcLocale).getSuccessJoin(false));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else if(exit) {
				region.setExitMessage(message, locale);
				src.sendMessage(getSetMessage(srcLocale).getSuccessExit(false));
				plugin.getAPI().saveRegion(region.getPrimaryParent());
			} else {
				exception(getSetMessage(srcLocale).getTypeNotPresent());
			}
		}
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getSetMessage(locale).getDescription();
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
		if(clear == null) clear = Arrays.asList("-c", "-clear");
		return Arrays.asList(
			RawArguments.createStringArgument(flags, new RawBasicArgumentData<String>(null, "MessageType", 0, null, null), RawOptional.notOptional(), locale -> getCommand(locale).getSetMessage().getTypeNotPresent()),
			RawArguments.createLocaleArgument(RawBasicArgumentData.createLocale(1, null, null), RawOptional.optional(), null),
			RawArguments.createStringArgument(clear, new RawBasicArgumentData<String>(null, "Clear | Message", 2, null, null), RawOptional.optional(), null),
			RawArgument.of(
				Component.class,
				(cause, args) -> Stream.empty(),
				(cause, args) -> Optional.ofNullable(args.length > 1 ? TextUtils.deserialize(String.join(" ", ArrayUtils.removeElements(args, (locales.containsKey(args[1]) ? args[1] : "")))) : null),
				new RawArgumentData<>("Message", CommandTreeNodeTypes.STRING.get().createNode().greedy(), 3, null, null),
				RawOptional.notOptional(),
				locale -> getExceptions(locale).getMessageNotPresent()
			)
		);
	}

	private sawfowl.regionguard.configure.locales.abstractlocale.Command.SetMessage getSetMessage(Locale locale) {
		return getCommand(locale).getSetMessage();
	}

}
