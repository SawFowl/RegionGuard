package sawfowl.regionguard.commands.child;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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

public class SetName extends AbstractPlayerCommand {

	private final Map<String, Locale> locales = new HashMap<String, Locale>();
	public SetName(RegionGuard plugin) {
		super(plugin);
		plugin.getLocales().getLocaleService().getLocalesList().forEach(locale -> {
			locales.put(locale.toLanguageTag(), locale);
		});
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale srcLocale, String[] args, Mutable arguments) throws CommandException {
		Region region = plugin.getAPI().findRegion(src.world(), src.blockPosition());
		if(region.isGlobal()) throw new CommandException(plugin.getLocales().getText(srcLocale, LocalesPaths.COMMANDS_EXCEPTION_REGION_NOT_FOUND));
		if(!src.hasPermission(Permissions.STAFF_SET_NAME)) {
			if(!region.isTrusted(src)) throw new CommandException(plugin.getLocales().getText(srcLocale, LocalesPaths.COMMAND_SET_NAME_NOT_TRUSTED));
			if(region.isCurrentTrustType(src, TrustTypes.OWNER) || region.isCurrentTrustType(src, TrustTypes.MANAGER)) throw new CommandException(plugin.getLocales().getText(srcLocale, LocalesPaths.COMMAND_SET_NAME_LOW_TRUST));
		}
		Locale locale = getString(args, 0).isPresent() ? locales.get(getString(args, 0).get()) : srcLocale;
		boolean clearFlag = getString(args, 1).isPresent();
		if(clearFlag) {
			region.setName(null, locale);
			plugin.getAPI().saveRegion(region.getPrimaryParent());
			src.sendMessage(plugin.getLocales().getText(srcLocale, LocalesPaths.COMMAND_SET_NAME_CLEARED));
		} else {
			Component newName = getArgument(Component.class, args, 2).get();
			if(TextUtils.clearDecorations(newName).length() > 20) throw new CommandException(plugin.getLocales().getText(srcLocale, LocalesPaths.COMMAND_SET_NAME_TOO_LONG));
			region.setName(newName, locale);
			plugin.getAPI().saveRegion(region.getPrimaryParent());
			src.sendMessage(plugin.getLocales().getText(srcLocale, LocalesPaths.COMMAND_SET_NAME_SUCCESS));
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
		return Arrays.asList(
			RawArguments.createStringArgument(locales.keySet(), true, true, 0, null, null),
			RawArguments.createStringArgument(Arrays.asList("-c", "-clear"), true, true, 1, null, null),
			RawArgument.of(
				Component.class,
				(cause, args) -> Stream.empty(),
				(cause, args) -> Optional.ofNullable(args.length > 0 ? TextUtils.deserialize(String.join(" ", ArrayUtils.removeElements(args, getString(args, 0).orElse("")))) : null),
				false,
				false,
				2,
				LocalesPaths.COMMAND_SET_NAME_NOT_PRESENT
			)
		);
	}

}
