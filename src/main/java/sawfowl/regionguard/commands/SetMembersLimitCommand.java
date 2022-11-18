package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.audience.Audience;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.utils.ReplaceUtil;

public class SetMembersLimitCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	private List<CommandCompletion> empty = new ArrayList<>();
	public SetMembersLimitCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(args.isEmpty()) usage();
		Object src = cause.root();
		Locale sourceLocale = src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT;
		String sourceName = src instanceof ServerPlayer ? ((ServerPlayer) src).name() : "ServerConsole";
		if(args.size() == 0 || !Sponge.server().player(args.get(0)).isPresent()) throw new CommandException(plugin.getLocales().getText(sourceLocale, LocalesPaths.COMMAND_SETLIMITMEMBERS_EXCEPTION_PLAYER_NOT_PRESENT));
		if(args.size() == 1) throw new CommandException(plugin.getLocales().getText(sourceLocale, LocalesPaths.COMMAND_SETLIMITMEMBERS_EXCEPTION_VOLUME_NOT_PRESENT));
		if(!NumberUtils.isParsable(args.get(1))) throw new CommandException(plugin.getLocales().getText(sourceLocale, LocalesPaths.COMMAND_SETLIMITMEMBERS_EXCEPTION_WRONG_ARGUMENT));
		long toSet = Long.valueOf(args.get(1));
		if(toSet < 0) throw new CommandException(plugin.getLocales().getText(sourceLocale, LocalesPaths.COMMAND_SETLIMITMEMBERS_EXCEPTION_LESS_THEN_ZERO));
		ServerPlayer target = Sponge.server().player(args.get(0)).get();
		plugin.getAPI().setLimitMembers(target, toSet);
		((Audience) src).sendMessage(plugin.getLocales().getTextWithReplaced(sourceLocale, ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.PLAYER), Arrays.asList(toSet, target.name())), LocalesPaths.COMMAND_SETLIMITMEMBERS_SUCCESS_SOURCE));
		target.sendMessage(plugin.getLocales().getTextWithReplaced(target.locale(), ReplaceUtil.replaceMap(Arrays.asList(ReplaceUtil.Keys.SIZE, ReplaceUtil.Keys.PLAYER), Arrays.asList(toSet, sourceName)), LocalesPaths.COMMAND_SETLIMITMEMBERS_SUCCESS_TARGET));
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(args.isEmpty()) return Sponge.server().onlinePlayers().stream().map(player -> (CommandCompletion.of(player.name()))).collect(Collectors.toList());
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.STAFF_SETLIMIT_MEMBERS);
	}

	@Override
	public CommandException usage() throws CommandException {
		throw new CommandException(text("Usage: /rg setlimit members [Player] [Volume]"));
	}

}
