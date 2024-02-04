package sawfowl.regionguard.commands.child;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

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
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;
import sawfowl.regionguard.configure.LocalesPaths;

public class SetSelectorType extends AbstractPlayerCommand {

	public SetSelectorType(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, String[] args, Mutable arguments) throws CommandException {
		switch (getArgument(SelectorTypes.class, cause, args, 0).get()) {
		case CUBOID: {
			plugin.getAPI().setSelectorType(src, SelectorTypes.CUBOID);
			src.sendMessage(getComponent(locale, LocalesPaths.COMMAND_SELECTOR_CUBOID));
			break;
		}
		default:
			plugin.getAPI().setSelectorType(src, SelectorTypes.FLAT);
			src.sendMessage(getComponent(locale, LocalesPaths.COMMAND_SELECTOR_FLAT));
		}
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_SET_SELECTOR_TYPE);
	}

	@Override
	public String permission() {
		return Permissions.CHANGE_SELECTOR;
	}

	@Override
	public String command() {
		return "setselector";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg setselector &7<Type>&f - ").clickEvent(ClickEvent.suggestCommand("/rg setselector ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(
			RawArgument.of(
				SelectorTypes.class,
				null,
				(cause, args) -> Stream.of(SelectorTypes.values()).map(type -> type.toString()),
				null,
				(cause, args) -> args.length > 0 ? Optional.ofNullable(SelectorTypes.checkType(args[0])) : Optional.empty(),
				null,
				false,
				false,
				0,
				null,
				LocalesPaths.COMMAND_REGION_TYPE_EXCEPTION_NOT_PRESENT
			)
		);
	}

}
