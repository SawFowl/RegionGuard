package sawfowl.regionguard.commands;

import java.util.List;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public interface PluginRawCommand {

	public CommandResult process(CommandCause cause, Mutable arguments, List<String> args) throws CommandException;

	public List<CommandCompletion> complete(CommandCause cause, Mutable arguments, List<String> args) throws CommandException;

	public boolean canExecute(CommandCause cause);

	default String removeDecor(String string) {
		while(string.indexOf('&') != -1 && !string.endsWith("&") && isStyleChar(string.charAt(string.indexOf("&") + 1))) string = string.replaceAll("&" + string.charAt(string.indexOf("&") + 1), "");
		return string;
	}

	default String removeDecor(Component component) {
		return removeDecor(LegacyComponentSerializer.legacyAmpersand().serialize(component));
	}

	default boolean isStyleChar(char ch) {
		return "0123456789abcdefklmnor".indexOf(ch) != -1;
	}

}
