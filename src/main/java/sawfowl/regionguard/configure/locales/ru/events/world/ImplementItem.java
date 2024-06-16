package sawfowl.regionguard.configure.locales.ru.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Item;

@ConfigSerializable
public class ImplementItem implements Item {

	public ImplementItem() {}

	@Setting("Drop")
	private Component drop = deserialize("&cВы не можете выбрасывать предметы в текущем регионе.");
	@Setting("Pickup")
	private Component pickup = deserialize("&cВы не можете подбирать предметы в текущем регионе.");
	@Setting("Interact")
	private Component interact = deserialize("&cВы не можете взаимодействовать с этим предметом в текущем регионе.");
	@Setting("Use")
	private Component use = deserialize("&cВы не можете использовать этот предмет в текущем регионе.");

	@Override
	public Component getDrop() {
		return drop;
	}

	@Override
	public Component getPickup() {
		return pickup;
	}

	@Override
	public Component getInteract() {
		return interact;
	}

	@Override
	public Component getUse() {
		return use;
	}

}
