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
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;

public class SetName extends AbstractPlayerCommand {

	private Map<String, Locale> locales;
	public SetName(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale srcLocale, String[] args, Mutable arguments) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) exception(srcLocale, LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND);
		if(!src.hasPermission(Permissions.STAFF_SET_NAME)) {
			if(!region.isTrusted(src)) exception(srcLocale, LocalesPaths.COMMAND_SET_NAME_NOT_TRUSTED);
			if(region.isCurrentTrustType(src, TrustTypes.OWNER) || region.isCurrentTrustType(src, TrustTypes.MANAGER)) exception(srcLocale, LocalesPaths.COMMAND_SET_NAME_LOW_TRUST);
		}
		Locale locale = getString(args, cause, 0).isPresent() ? locales.get(getString(args, cause, 0).get()) : srcLocale;
		boolean clearFlag = getString(args, cause, 1).isPresent();
		if(clearFlag) {
			region.setName(null, locale);
			plugin.getAPI().saveRegion(region.getPrimaryParent());
			src.sendMessage(plugin.getLocales().getComponent(srcLocale, LocalesPaths.COMMAND_SET_NAME_CLEARED));
		} else {
			Component newName = getArgument(Component.class, cause, args, 2).get();
			if(TextUtils.clearDecorations(newName).length() > 20) exception(srcLocale, LocalesPaths.COMMAND_SET_NAME_TOO_LONG);
			region.setName(newName, locale);
			plugin.getAPI().saveRegion(region.getPrimaryParent());
			src.sendMessage(plugin.getLocales().getComponent(srcLocale, LocalesPaths.COMMAND_SET_NAME_SUCCESS));
		}
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_SET_NAME);
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
		return Arrays.asList(
			RawArguments.createLocaleArgument(true, true, 0, null, null),
			RawArguments.createStringArgument("Clear | Message", Arrays.asList("-c", "-clear"), true, true, 1, null, null, null),
			RawArgument.of(
				Component.class,
				CommandTreeNodeTypes.STRING.get().createNode().greedy(),
				(cause, args) -> Stream.empty(),
				(cause, args) -> Optional.ofNullable(args.length > 0 ? TextUtils.deserialize(String.join(" ", ArrayUtils.removeElements(args, getString(args, cause, 0).orElse("")))) : null),
				"Message",
				false,
				false,
				2,
				null,
				LocalesPaths.COMMAND_SET_NAME_NOT_PRESENT
			)
		);
	}

}
