package sawfowl.regionguard.configure.locales.ru.events.world.teleport;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Teleport.FromRegion;

@ConfigSerializable
public class ImplementFromRegion implements FromRegion {

	public ImplementFromRegion() {}

	@Setting("Other")
	private Component other = deserialize("&cВы не можете телепортировать игрока из его текущей позиции.");
	@Setting("Self")
	private Component self = deserialize("&cВы не можете телепортироваться из текущего региона.");
	@Setting("Enderpearl")
	private Component enderpearl = deserialize("&cВы не можете телепортироваться из этого региона. Жемчуг возвращен в ваш инвентарь.");

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
