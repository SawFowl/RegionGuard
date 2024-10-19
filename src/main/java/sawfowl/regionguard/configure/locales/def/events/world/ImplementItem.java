package sawfowl.regionguard.configure.locales.def.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Item;

@ConfigSerializable
public class ImplementItem implements Item {

	public ImplementItem() {}

	@Setting("Drop")
	private Component drop = deserialize("&cYou cannot drop items in the current region.");
	@Setting("Pickup")
	private Component pickup = deserialize("&cYou cannot pick up items in the current region.");
	@Setting("Interact")
	private Component interact = deserialize("&cYou cannot interact with this item in the current region.");
	@Setting("Use")
	private Component use = deserialize("&cYou cannot use this item in the current region.");

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
