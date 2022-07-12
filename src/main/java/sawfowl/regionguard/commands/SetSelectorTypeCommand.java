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
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.configure.LocalesPaths;

public class SetSelectorTypeCommand implements PluginRawCommand {

	private final RegionGuard plugin;
	private List<CommandCompletion> completions = Arrays.asList(CommandCompletion.of("cuboid"), CommandCompletion.of("flat"));
	private List<CommandCompletion> empty = new ArrayList<>();
	public SetSelectorTypeCommand(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		String plainArgs = arguments.input();
		while(plainArgs.contains("  ")) plainArgs = plainArgs.replace("  ", " ");
		boolean cuboid = !args.isEmpty() && args.get(0).equalsIgnoreCase("cuboid");
		boolean flat = !args.isEmpty() && args.get(0).equalsIgnoreCase("flat");
		if(cuboid) {
			plugin.getAPI().setSelectorType(player, SelectorTypes.CUBOID);
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SELECTOR_CUBOID));
		} else if(flat) {
			plugin.getAPI().setSelectorType(player, SelectorTypes.FLAT);
			player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_SELECTOR_FLAT));
		} else {
			throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_REGION_TYPE_EXCEPTION_NOT_PRESENT));
		}
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException {
		if(args.isEmpty()) return completions;
		if(args.size() == 1 && !args.get(0).equals("cuboid") && !args.get(0).equals("flat")) return completions.stream().filter(type -> (type.completion().startsWith(args.get(0)))).collect(Collectors.toList());
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.CHANGE_SELECTOR);
	}

}
