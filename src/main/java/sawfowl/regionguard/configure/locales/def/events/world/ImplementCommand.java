package sawfowl.regionguard.configure.locales.def.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Command;

@ConfigSerializable
public class ImplementCommand implements Command {

	public ImplementCommand() {}

	@Setting("Execute")
	private Component execute = deserialize("&cYou cannot use this command in the current region.");
	@Setting("ExecutePvP")
	private Component executePvP = deserialize("&cYou cannot use this command in the current region while in combat with another player.");

	@Override
	public Component getExecute() {
		return execute;
	}

	@Override
	public Component getExecutePvP() {
		return executePvP;
	}

}
