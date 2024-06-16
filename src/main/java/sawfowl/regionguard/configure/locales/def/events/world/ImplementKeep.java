package sawfowl.regionguard.configure.locales.def.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Keep;

@ConfigSerializable
public class ImplementKeep implements Keep {

	public ImplementKeep() {}

	@Setting("Exp")
	private Component exp = deserialize("&aThe region has saved your experience and it will be restored after the respawn.");
	@Setting("Inventory")
	private Component inventory = deserialize("&aThe region has saved your inventory and it will be restored after the respawn.");

	@Override
	public Component getExp() {
		return exp;
	}

	@Override
	public Component getInventory() {
		return inventory;
	}

}
