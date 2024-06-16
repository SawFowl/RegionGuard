package sawfowl.regionguard.configure.locales.def.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Fly;

@ConfigSerializable
public class ImplementFly implements Fly {

	public ImplementFly() {}

	@Setting("Disable")
	private Component disable = deserialize("&cYou cannot fly in this region. Flight is off.");
	@Setting("DisableOnJoin")
	private Component disableOnJoin = deserialize("&cYou have entered a region in which flying is forbidden. You will be able to fly again after leaving this region.");

	@Override
	public Component getDisable() {
		return disable;
	}

	@Override
	public Component getDisableOnJoin() {
		return disableOnJoin;
	}

}
