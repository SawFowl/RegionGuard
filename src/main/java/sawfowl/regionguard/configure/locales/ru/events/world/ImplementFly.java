package sawfowl.regionguard.configure.locales.ru.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Fly;

@ConfigSerializable
public class ImplementFly implements Fly {

	public ImplementFly() {}

	@Setting("Disable")
	private Component disable = deserialize("&cВы не можете летать в этом регионе. Полет отключен.");
	@Setting("DisableOnJoin")
	private Component disableOnJoin = deserialize("&cВы вошли в регион, в котором полеты запрещены. Вы сможете снова летать после того, как покинете этот регион.");

	@Override
	public Component getDisable() {
		return disable;
	}

	@Override
	public Component getDisableOnJoin() {
		return disableOnJoin;
	}

}
