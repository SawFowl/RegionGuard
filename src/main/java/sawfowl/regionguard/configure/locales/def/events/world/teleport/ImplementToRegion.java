package sawfowl.regionguard.configure.locales.def.events.world.teleport;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Teleport.ToRegion;

@ConfigSerializable
public class ImplementToRegion implements ToRegion {

	public ImplementToRegion() {}

	@Setting("Other")
	private Component other = deserialize("&cYou cannot teleport a player to a specified position.");
	@Setting("Self")
	private Component self = deserialize("&cThe teleportation position is in a region that cannot be teleported to.");
	@Setting("Enderpearl")
	private Component enderpearl = deserialize("&cYou cannot teleport to this region. Pearls returned to your inventory.");

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
