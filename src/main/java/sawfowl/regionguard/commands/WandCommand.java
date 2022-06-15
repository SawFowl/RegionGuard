package sawfowl.regionguard.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.util.locale.Locales;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.configure.LocalesPaths;

public class WandCommand implements Command.Raw {


	private final RegionGuard plugin;
	private final List<CommandCompletion> empty = new ArrayList<>();
	public WandCommand(RegionGuard instance) {
		plugin = instance;
	}

	@Override
	public CommandResult process(CommandCause cause, Mutable arguments) throws CommandException {
		Object src = cause.root();
		if(!(src instanceof ServerPlayer)) throw new CommandException(plugin.getLocales().getText(src instanceof LocaleSource ? ((LocaleSource) src).locale() : Locales.DEFAULT, LocalesPaths.COMMANDS_ONLY_PLAYER));
		ServerPlayer player = (ServerPlayer) src;
		if(player.inventory().contains(plugin.getAPI().getWandItem())) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_WAND_EXCEPTION_ITEM_EXIST));
		if(player.inventory().freeCapacity() == 0) throw new CommandException(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_WAND_EXCEPTION_INVENTORY_IS_FULL));
		player.inventory().offer(plugin.getAPI().getWandItem());
		player.sendMessage(plugin.getLocales().getText(player.locale(), LocalesPaths.COMMAND_WAND_SUCCESS));
		return CommandResult.success();
	}

	@Override
	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments) throws CommandException {
		return empty;
	}

	@Override
	public boolean canExecute(CommandCause cause) {
		return cause.hasPermission(Permissions.WAND);
	}

	@Override
	public Optional<Component> shortDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Give wand item."));
	}

	@Override
	public Optional<Component> extendedDescription(CommandCause cause) {
		return Optional.ofNullable(Component.text("Give wand item."));
	}

	@Override
	public Component usage(CommandCause cause) {
		return Component.text("/wand");
	}

}
