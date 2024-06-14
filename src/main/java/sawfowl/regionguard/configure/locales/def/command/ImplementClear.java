package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Clear;

@ConfigSerializable
public class ImplementClear implements Clear {

	public ImplementClear() {}

	@Setting("Description")
	private Component description = deserialize("&6Clear the selection of the area.");
	@Setting("Success")
	private Component success = deserialize("&aThe area selection has been reset.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
