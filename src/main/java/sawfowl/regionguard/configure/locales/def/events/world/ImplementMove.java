package sawfowl.regionguard.configure.locales.def.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Move;

@ConfigSerializable
public class ImplementMove implements Move {

	public ImplementMove() {}

	@Setting("Join")
	private Component cancelJoin = deserialize("&cYou cannot enter the region.");
	@Setting("Exit")
	private Component cancelExit = deserialize("&cYou cannot exit from region.");

	@Override
	public Component getJoin() {
		return cancelJoin;
	}

	@Override
	public Component getExit() {
		return cancelExit;
	}

}
