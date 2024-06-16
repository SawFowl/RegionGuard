package sawfowl.regionguard.configure.locales.def.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Piston;

@ConfigSerializable
public class ImplementPiston implements Piston {

	public ImplementPiston() {}

	@Setting("Use")
	private Component use = deserialize("&cThe region has blocked the use of the piston.");
	@Setting("Grief")
	private Component grief = deserialize("&aThe region has blocked the use of the piston from outside.");

	@Override
	public Component getUse() {
		return use;
	}

	@Override
	public Component getGrief() {
		return grief;
	}

}
