package sawfowl.regionguard.configure.locales.ru.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.Block;

@ConfigSerializable
public class ImplementBlock implements Block {

	public ImplementBlock() {}

	@Setting("Interact")
	private ImplementBlockInteract interact = new ImplementBlockInteract();
	@Setting("Growth")
	private Component growth = deserialize("&cУ вас нет разрешения на выращивание в этом регионе.");
	@Setting("Place")
	private Component place = deserialize("&cВы не имеете права размещать или изменять блоки в этом регионе.");
	@Setting("Break")
	private Component breakBlock = deserialize("&cУ вас нет разрешения ломать или удалять блоки в этом регионе.");
	@Setting("Impact")
	private Component impact = deserialize("&cБлок находится в регионе, в котором у вас нет разрешения на стрельбу по блокам.");

	@Override
	public Interact getInteract() {
		return interact;
	}

	@Override
	public Component getGrowth() {
		return growth;
	}

	@Override
	public Component getPlace() {
		return place;
	}

	@Override
	public Component getBreak() {
		return breakBlock;
	}

	@Override
	public Component getImpact() {
		return impact;
	}

}
