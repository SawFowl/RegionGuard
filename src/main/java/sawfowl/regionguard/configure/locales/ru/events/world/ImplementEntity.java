package sawfowl.regionguard.configure.locales.ru.events.world;

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
	private Component pvp = deserialize("&cPvP в этом регионе запрещено.");
	@Setting("Damage")
	private Component damage = deserialize("&cВы не можете нанести урон этому существу в текущем регионе.");
	@Setting("Impact")
	private Component impact = deserialize("&cЦель находится в регионе, в котором у вас нет прав на стрельбу по сущностям.");
	@Setting("Spawn")
	private Component spawn = deserialize("&cСобытие спавна сущности, предмета или опыта было отменено, поскольку регион имеет запрещающий флаг.");
	@Setting("Riding")
	private Component riding = deserialize("&cВ этом регионе нельзя ездить верхом.");

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
