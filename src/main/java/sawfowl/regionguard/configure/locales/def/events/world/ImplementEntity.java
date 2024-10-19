package sawfowl.regionguard.configure.locales.def.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Entity;

@ConfigSerializable
public class ImplementEntity implements Entity {

	public ImplementEntity() {}

	@Setting("Interact")
	private ImplementEntityInteract interact = new ImplementEntityInteract();
	@Setting("PvP")
	private Component pvp = deserialize("&cPvP is not allowed in this region.");
	@Setting("Damage")
	private Component damage = deserialize("&cYou cannot deal damage to this entity in the current region.");
	@Setting("Impact")
	private Component impact = deserialize("&cThe target is in a region in which you have no rights to shoot entities.");
	@Setting("Spawn")
	private Component spawn = deserialize("&cThe entity, item or experience spawn event has been canceled because the region has a disallowing flag.");
	@Setting("Riding")
	private Component riding = deserialize("&cYou cannot ride in this region.");

	@Override
	public Interact getInteract() {
		return interact;
	}

	@Override
	public Component getPvP() {
		return pvp;
	}

	@Override
	public Component getDamage() {
		return damage;
	}

	@Override
	public Component getImpact() {
		return impact;
	}

	@Override
	public Component getSpawn() {
		return spawn;
	}

	@Override
	public Component getRiding() {
		return riding;
	}

}
