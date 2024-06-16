package sawfowl.regionguard.configure.locales.ru.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Move;

@ConfigSerializable
public class ImplementMove implements Move {

	public ImplementMove() {}

	@Setting("Join")
	private Component cancelJoin = deserialize("&cВы не можете войти в этот регион.");
	@Setting("Exit")
	private Component cancelExit = deserialize("&cВы не можете выйти из региона.");

	@Override
	public Component getJoin() {
		return cancelJoin;
	}

	@Override
	public Component getExit() {
		return cancelExit;
	}

}
