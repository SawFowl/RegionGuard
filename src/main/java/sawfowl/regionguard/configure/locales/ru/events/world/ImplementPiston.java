package sawfowl.regionguard.configure.locales.ru.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Piston;

@ConfigSerializable
public class ImplementPiston implements Piston {

	public ImplementPiston() {}

	@Setting("Use")
	private Component use = deserialize("&cРегион заблокировал использование поршня.");
	@Setting("Grief")
	private Component grief = deserialize("&aРегион заблокировал использование поршня извне.");

	@Override
	public Component getUse() {
		return use;
	}

	@Override
	public Component getGrief() {
		return grief;
	}

}
