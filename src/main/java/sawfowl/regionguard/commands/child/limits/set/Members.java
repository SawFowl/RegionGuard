package sawfowl.regionguard.commands.child.limits.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.commands.raw.arguments.RawArguments;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgumentsMap;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.commands.abstractcommands.AbstractCommand;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.Placeholders;

public class Members extends AbstractCommand {

	public Members(RegionGuard plugin) {
		super(plugin);
	}

	@Override
	public void process(CommandCause cause, Audience audience, Locale locale, boolean isPlayer, Mutable arguments, RawArgumentsMap args) throws CommandException {
		String sourceName = isPlayer ? ((ServerPlayer) audience).name() : "Server";
		ServerPlayer target = args.getPlayer(0).get();
		long toSet = args.getLong(1).get();
		if(toSet < 0) exception(locale, LocalesPaths.COMMAND_SETLIMITMEMBERS_EXCEPTION_LESS_THEN_ZERO);
		plugin.getAPI().setLimitMembers(target, toSet);
		audience.sendMessage(getText(locale, LocalesPaths.COMMAND_SETLIMITMEMBERS_SUCCESS_SOURCE).replace(new String[] {Placeholders.SIZE, Placeholders.PLAYER}, toSet, target.name()).get());
		target.sendMessage(getText(target.locale(), LocalesPaths.COMMAND_SETLIMITMEMBERS_SUCCESS_TARGET).replace(new String[] {Placeholders.SIZE, Placeholders.PLAYER}, toSet, sourceName).get());
	}

	@Override
	public Component extendedDescription(Locale locale) {
		return getComponent(locale, LocalesPaths.COMMANDS_SETLIMITBLOCKS);
	}

	@Override
	public String permission() {
		return Permissions.STAFF_SETLIMIT_MEMBERS;
	}

	@Override
	public String command() {
		return "members";
	}

	@Override
	public Component usage(CommandCause cause) {
		return TextUtils.deserializeLegacy("&6/rg limits set members &7<Player> <Volume>&f - ").clickEvent(ClickEvent.suggestCommand("/rg limits set members ")).append(extendedDescription(getLocale(cause)));
	}

	@Override
	public List<RawCommand> getChilds() {
		return null;
	}

	@Override
	public List<RawArgument<?>> getArgs() {
		return Arrays.asList(
			RawArguments.createPlayerArgument(false, false, 0, null, null, null, LocalesPaths.COMMAND_SETLIMITMEMBERS_EXCEPTION_PLAYER_NOT_PRESENT),
			RawArguments.createLongArgument("Value", new ArrayList<Long>(), false, false, 1, null, null, null, null, LocalesPaths.COMMAND_SETLIMITMEMBERS_EXCEPTION_VOLUME_NOT_PRESENT)
		);
	}

}
