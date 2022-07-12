package sawfowl.regionguard.commands;

import java.util.List;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;

public interface PluginRawCommand {

	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException;

	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException;

	public boolean canExecute(CommandCause cause);

}
