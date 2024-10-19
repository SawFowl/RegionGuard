package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.SetSelectorType;

@ConfigSerializable
public class ImplementSetSelectorType implements SetSelectorType {

	public ImplementSetSelectorType() {}

	@Setting("Description")
	private Component description = deserialize("&6Выбор типа выделения области.");
	@Setting("Flat")
	private Component flat = deserialize("&dВы выбрали выделение плоскости.");
	@Setting("Cuboid")
	private Component cuboid = deserialize("&dВы выбрали выделение кубоида.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component get(boolean flat) {
		return flat ? this.flat : cuboid;
	}

}
