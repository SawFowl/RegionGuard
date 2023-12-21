package sawfowl.regionguard.commands.abstractcommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.plugin.PluginContainer;

import net.kyori.adventure.text.Component;

import sawfowl.commandpack.api.commands.raw.RawCommand;
import sawfowl.commandpack.api.commands.raw.arguments.RawArgument;
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.regionguard.RegionGuard;

public abstract class AbstractCommand implements RawCommand {

	protected final RegionGuard plugin;
	private final List<CommandCompletion> empty = new ArrayList<>();
	private final Map<String, RawCommand> getChildExecutors = new HashMap<>();
	private final Map<Integer, RawArgument<?>> arguments = new HashMap<>();
	public AbstractCommand(RegionGuard regionGuard) {
		plugin = regionGuard;
		if(getChilds() != null) for(RawCommand command : getChilds()) {
			getChildExecutors.put(command.command(), command);
			if(command.getCommandSettings().getAliases() != null && command.getCommandSettings().getAliases().length > 0) for(String alias : command.getCommandSettings().getAliases()) getChildExecutors.put(alias, command);
		}
		if(getArgs() != null) for(RawArgument<?> arg : getArgs()) arguments.put(arg.getCursor(), arg);
	}

	public abstract List<RawCommand> getChilds();
	public abstract List<RawArgument<?>> getArgs();

	@Override
	public Settings getCommandSettings() {
		return Settings.builder().setEnable(true).build();
	}

	@Override
	public Component getComponent(Object[] path) {
		return null;
	}

	@Override
	public PluginContainer getContainer() {
		return plugin.getPluginContainer();
	}

	@Override
	public List<CommandCompletion> getEmptyCompletion() {
		return empty;
	}

	@Override
	public Map<String, RawCommand> getChildExecutors() {
		return getChildExecutors;
	}

	@Override
	public Map<Integer, RawArgument<?>> getArguments() {
		return arguments;
	}

	@Override
	public boolean enableAutoComplete() {
		return true;
	}

	@Override
	public Component shortDescription(Locale locale) {
		return extendedDescription(locale);
	}

}
