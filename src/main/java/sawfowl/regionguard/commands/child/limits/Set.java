package sawfowl.regionguard.commands.child.limits;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.commands.abstractcommands.AbstractCommand;
import sawfowl.regionguard.commands.child.limits.set.Blocks;
import sawfowl.regionguard.commands.child.limits.set.Claims;
import sawfowl.regionguard.commands.child.limits.set.Members;
import sawfowl.regionguard.commands.child.limits.set.Subdivisions;
import sawfowl.regionguard.configure.LocalesPaths;

public class Set extends AbstractCommand {

	public Set(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return getChildExecutors().values().stream().filter(child -> cause.hasPermission(child.permission())).findFirst().isPresent();
	}

	@Override
	public void process(CommandCause cause, Audience audience, Locale locale, boolean isPlayer, Mutable arguments, RawArgumentsMap args) throws CommandException {
		sendPaginationList(audience, getComponent(locale, LocalesPaths.COMMANDS_TITLE), getComponent(locale, LocalesPaths.PADDING), 10, getChildExecutors().values().stream().filter(child -> child.canExecute(cause)).map(child -> child.usage(cause)).toList());
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_SETLIMIT);
	}

	@Override
	public String permission() {
		return null;
	}

	@Override
	public String command() {
		return "set";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg limits set &7<Limit> <Player> <Volume>&f - ").clickEvent(ClickEvent.runCommand("/rg limits set ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return Arrays.asList(new Blocks(plugin), new Claims(plugin), new Members(plugin), new Subdivisions(plugin));
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return null;
	}

}
