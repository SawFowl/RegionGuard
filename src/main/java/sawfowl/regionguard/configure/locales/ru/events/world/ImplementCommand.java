package sawfowl.regionguard.configure.locales.ru.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Command;

@ConfigSerializable
public class ImplementCommand implements Command {

	public ImplementCommand() {}

	@Setting("Execute")
	private Component execute = deserialize("&cВы не можете использовать эту команду в текущем регионе.");
	@Setting("ExecutePvP")
	private Component executePvP = deserialize("&cВы не можете использовать эту команду в текущем регионе, находясь в бою с другим игроком.");

	@Override
	public Component getExecute() {
		return execute;
	}

	@Override
	public Component getExecutePvP() {
		return executePvP;
	}

}
