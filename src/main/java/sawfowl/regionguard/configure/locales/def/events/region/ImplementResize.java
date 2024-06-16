package sawfowl.regionguard.configure.locales.def.events.region;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.RegionEvents.Resize;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementResize implements Resize {

	public ImplementResize() {}

	@Setting("IncorrectCords")
	private Component incorrectCords = deserialize("&cOne of the XYZ(3D)/XZ(2D) coordinates of the new point coincides with the same coordinate of the opposite corner of the region. Select a different position.");
	@Setting("SmallVolume")
	private Component smallVolume = deserialize("&cThe volume selected is too small. To complete the operation, select an area larger than the current one by &b" + Placeholders.VOLUME + "&c.");
	@Setting("LimitBlocks")
	private Component limitBlocks = deserialize("&cThe new size is too large. You have selected: &b" + Placeholders.SELECTED + "&c blocks.\nBlocks available: &b" + Placeholders.VOLUME + "&c.");
	@Setting("Intersect")
	private Component intersect = deserialize("&cIt is not possible to change the size of a region, as this would cause it to intersect with another region.");
	@Setting("ChildOut")
	private Component childOut = deserialize("&cIt is not possible to change the size of a region as this would cause the child region to be outside his boundaries.");
	@Setting("Start")
	private Component start = deserialize("&dClick again elsewhere to resize.");
	@Setting("Finish")
	private Component finish = deserialize("&dThe size of the region has been changed.");

	@Override
	public Component getIncorrectCords() {
		return incorrectCords;
	}

	@Override
	public Component getSmallVolume(int size) {
		return replace(smallVolume, Placeholders.VOLUME, size);
	}

	@Override
	public Component getLimitBlocks(long size, long limit) {
		return replace(smallVolume, array(Placeholders.SELECTED, Placeholders.VOLUME), size, limit);
	}

	@Override
	public Component getIntersect() {
		return intersect;
	}

	@Override
	public Component getChildOut() {
		return childOut;
	}

	@Override
	public Component getStart() {
		return start;
	}

	@Override
	public Component getFinish() {
		return finish;
	}

}
