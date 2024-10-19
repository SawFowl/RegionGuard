package sawfowl.regionguard.configure.locales.def.events.world;

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
	private Component growth = deserialize("&cYou do not have a permission to grow in this region.");
	@Setting("Place")
	private Component place = deserialize("&cYou do not have permission to place or modify blocks in this region.");
	@Setting("Break")
	private Component breakBlock = deserialize("&cYou do not have permission to break or remove blocks in this region.");
	@Setting("Impact")
	private Component impact = deserialize("&cThe block is in a region in which you have no permission to shoot blocks.");

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
