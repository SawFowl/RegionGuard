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

public class SetName extends AbstractPlayerCommand {

	private Map<String, Locale> locales;
	private List<String> clear;
	public SetName(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale srcLocale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(getExceptions(srcLocale).getRegionNotFound());
		if(!src.hasPermission(Permissions.STAFF_SET_NAME)) {
			if(!region.isTrusted(src)) exception(getSetName(srcLocale).getNotTrusted());
			if(region.isCurrentTrustType(src, TrustTypes.OWNER) || region.isCurrentTrustType(src, TrustTypes.MANAGER)) exception(getSetName(srcLocale).getLowTrust());
		}
		Locale locale = args.<Locale>get(0).orElse(srcLocale);
		boolean clearFlag = args.getString(1).isPresent();
		if(clearFlag) {
			region.setName(null, locale);
			plugin.getAPI().saveRegion(region.getPrimaryParent());
			src.sendMessage(getSetName(locale).getSuccess(true));
		} else {
			Component newName = args.<Component>get(2).get();
			if(TextUtils.clearDecorations(newName).length() > 20) exception(getSetName(locale).getTooLong());
			region.setName(newName, locale);
			plugin.getAPI().saveRegion(region.getPrimaryParent());
			src.sendMessage(getSetName(locale).getSuccess(false));
		}
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getSetName(locale).getDescription();
	}

	@Override
	public String permission() {
		return Permissions.SET_NAME;
	}

	@Override
	public String command() {
		return "setname";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg setname &7[Locale] [Flags] <Name>&f - ").clickEvent(ClickEvent.suggestCommand("/rg setname ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		if(locales == null) locales = plugin.getLocales().getLocaleService().getLocalesList().stream().collect(Collectors.toMap(locale -> locale.toLanguageTag(), locale -> locale));
		if(clear == null) clear = Arrays.asList("-c", "-clear");
		return Arrays.asList(
			RawArguments.createLocaleArgument(RawBasicArgumentData.createLocale(0, null, null), RawOptional.optional(), null),
			RawArguments.createStringArgument(clear, new RawBasicArgumentData<String>(null, "Clear | Message", 1, null, null), RawOptional.optional(), null),
			RawArgument.of(
				Component.class,
				(cause, args) -> Stream.empty(),
				(cause, args) -> Optional.ofNullable(args.length > 0 ? TextUtils.deserialize(String.join(" ", ArrayUtils.removeElements(args, locales.containsKey(args[0]) ? args[0] : ""))) : null),
				new RawArgumentData<>("Name", CommandTreeNodeTypes.MESSAGE.get().createNode(), 2, null, null),
				RawOptional.notOptional(),
				locale -> getExceptions(locale).getNameNotPresent()
			)
		);
	}

	private sawfowl.regionguard.configure.locales.abstractlocale.Command.SetName getSetName(Locale locale) {
		return getCommand(locale).getSetName();
	}

}
