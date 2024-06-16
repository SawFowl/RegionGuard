package sawfowl.regionguard.configure.locales.ru.events.world.teleport;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Teleport.ToRegion;

@ConfigSerializable
public class ImplementToRegion implements ToRegion {

	public ImplementToRegion() {}

	@Setting("Other")
	private Component other = deserialize("&cВы не можете телепортировать игрока в указанное место.");
	@Setting("Self")
	private Component self = deserialize("&cПозиция телепортации находится в регионе, в который нельзя телепортироваться.");
	@Setting("Enderpearl")
	private Component enderpearl = deserialize("&cВы не можете телепортироваться в этот регион. Жемчуг возвращен в ваш инвентарь.");

	@Override
	public Component getOther() {
		return other;
	}

	@Override
	public Component getSelf() {
		return self;
	}

	@Override
	public Component getEnderpearl() {
		return enderpearl;
	}

}
