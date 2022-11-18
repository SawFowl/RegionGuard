package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.configure.LocalesPaths;

public class SetCreatingTypeCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	private List<CommandCompletion> completions = Arrays.asList(CommandCompletion.of("admin"), CommandCompletion.of("arena"), CommandCompletion.of("claim"));
	private List<CommandCompletion> empty = new ArrayList<>();
	public SetCreatingTypeCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		if(args.isEmpty()) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_REGION_TYPE_EXCEPTION_NOT_PRESENT));
		boolean claim = args.get(0).equalsIgnoreCase("claim");
		boolean arena = args.get(0).equalsIgnoreCase("arena");
		boolean admin = args.get(0).equalsIgnoreCase("admin");
		if(claim) {
			plugin.getAPI().setCreatingRegionType(player, RegionTypes.CLAIM);
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_REGION_TYPE_CLAIM));
		} else if(arena) {
			plugin.getAPI().setCreatingRegionType(player, RegionTypes.ARENA);
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_REGION_TYPE_ARENA));
		} else if(admin) {
			plugin.getAPI().setCreatingRegionType(player, RegionTypes.ADMIN);
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_REGION_TYPE_ADMIN));
		} else {
			throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_REGION_TYPE_EXCEPTION_NOT_PRESENT));
		}
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(args.isEmpty()) return completions;
		if(args.size() == 1 && !args.get(0).equals("admin") && !args.get(0).equals("arena") && !args.get(0).equals("claim")) return completions.stream().filter(type -> (type.completion().startsWith(args.get(0)))).collect(Collectors.toList());
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.STAFF_SET_REGION_TYPE);
	}

	@Override
	public CommandException usage() throws CommandException {
		throw new CommandException(text("Usage: /rg setcreatingtype [Type]"));
	}

}
