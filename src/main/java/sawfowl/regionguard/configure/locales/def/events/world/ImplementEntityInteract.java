package sawfowl.regionguard.configure.locales.def.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Entity.Interact;

@ConfigSerializable
public class ImplementEntityInteract implements Interact {

	public ImplementEntityInteract() {}

	@Setting("Primary")
	private Component primary = deserialize("&cYou cannot interact with this entity in the current region by clicking the left mouse button.");
	@Setting("Secondary")
	private Component secondary = deserialize("&cYou cannot interact with this entity in the current region by clicking the right mouse button.");

	@Override
	public Component getPrimary() {
		return primary;
	}

	@Override
	public Component getSecondary() {
		return secondary;
	}

}
