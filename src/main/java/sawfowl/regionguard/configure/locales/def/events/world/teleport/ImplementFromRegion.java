package sawfowl.regionguard.configure.locales.def.events.world.teleport;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Teleport.FromRegion;

@ConfigSerializable
public class ImplementFromRegion implements FromRegion {

	public ImplementFromRegion() {}

	@Setting("Other")
	private Component other = deserialize("&cYou cannot teleport a player from his current position.");
	@Setting("Self")
	private Component self = deserialize("&cYou cannot teleport from the current region.");
	@Setting("Enderpearl")
	private Component enderpearl = deserialize("&cYou cannot teleport from this region. Pearls returned to your inventory.");

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
