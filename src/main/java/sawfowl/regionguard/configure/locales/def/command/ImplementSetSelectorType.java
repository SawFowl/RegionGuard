package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.SetSelectorType;

@ConfigSerializable
public class ImplementSetSelectorType implements SetSelectorType {

	public ImplementSetSelectorType() {}

	@Setting("Description")
	private Component description = deserialize("&6Select the type of area selection.");
	@Setting("Flat")
	private Component flat = deserialize("&dYou have selected the flat selector type");
	@Setting("Cuboid")
	private Component cuboid = deserialize("&dYou have selected the cuboid selector type.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component get(boolean flat) {
		return flat ? this.flat : cuboid;
	}

}
