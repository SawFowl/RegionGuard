package sawfowl.regionguard.commands.child;

import java.util.Arrays;
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
import sawfowl.commandpack.api.commands.raw.arguments.RawArguments;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.commandpack.api.commands.raw.arguments.RawBasicArgumentData;
import sawfowl.commandpack.api.commands.raw.arguments.RawOptional;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.commands.abstractcommands.AbstractPlayerCommand;

public class SetSelectorType extends AbstractPlayerCommand {

	public SetSelectorType(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, ServerPlayer src, Locale locale, Mutable arguments, RawArgumentsMap args) throws CommandException {
		SelectorTypes type = args.get(SelectorTypes.class, 0).get();
		plugin.getAPI().setSelectorType(src, type);
		src.sendMessage(getCommand(locale).getSetSelectorType().get(type == SelectorTypes.FLAT));
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getCommand(locale).getSetSelectorType().getDescription();
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
		return Arrays.asList(RawArguments.createStringArgument(SelectorTypes.getValues(), new RawBasicArgumentData<String>(null, "Type", 0, null, null), RawOptional.notOptional(), locale -> getExceptions(locale).getSelectorTypeNotPresent()));
	}

}
