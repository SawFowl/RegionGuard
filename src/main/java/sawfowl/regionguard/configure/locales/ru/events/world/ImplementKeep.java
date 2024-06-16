package sawfowl.regionguard.configure.locales.ru.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Keep;

@ConfigSerializable
public class ImplementKeep implements Keep {

	public ImplementKeep() {}

	@Setting("Exp")
	private Component exp = deserialize("&aРегион сохранил ваш опыт, и он будет восстановлен после респавна.");
	@Setting("Inventory")
	private Component inventory = deserialize("&aРегион сохранил ваш инвентарь, и он будет восстановлен после респавна.");

	@Override
	public Component getExp() {
		return exp;
	}

	@Override
	public Component getInventory() {
		return inventory;
	}

}
