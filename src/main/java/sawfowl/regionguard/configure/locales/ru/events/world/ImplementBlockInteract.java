package sawfowl.regionguard.configure.locales.ru.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Block.Interact;

@ConfigSerializable
public class ImplementBlockInteract implements Interact {

	public ImplementBlockInteract() {}

	@Setting("Primary")
	private Component primary = deserialize("&cВы не можете взаимодействовать с блоком в текущем регионе, нажимая левую кнопку мыши.");
	@Setting("Secondary")
	private Component secondary = deserialize("&cВы не можете взаимодействовать с блоком в текущем регионе, нажимая правую кнопку мыши.");

	@Override
	public Component getPrimary() {
		return primary;
	}

	@Override
	public Component getSecondary() {
		return secondary;
	}

}
