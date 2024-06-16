package sawfowl.regionguard.configure.locales.def.events.region;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.locales.abstractlocale.Events.RegionEvents.Create;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementCreate implements Create {

	public ImplementCreate() {}

	@Setting("NoAdminPerm")
	private Component noAdminPerm = deserialize("&cYou do not have the permission to create child regions in an administrative region.");
	@Setting("PositionLocked")
	private Component positionLocked = deserialize("&cThis position already belongs to another player.");
	@Setting("LimitRegions")
	private Component limitRegions = deserialize("&cYou have reached the limit of regions available to you. Your limit: &b" + Placeholders.SIZE + "&c. Max regions: &b" + Placeholders.MAX + "&c.");
	@Setting("LimitSubdivisions")
	private Component limitSubdivisions = deserialize("&cYou have reached the limit of subdivisions in current region. Your limit: &b" + Placeholders.SIZE + "&c. Max subdivisions: &b" + Placeholders.MAX + "&c.");
	@Setting("LimitBlocks")
	private Component limitBlocks = deserialize("&cYou have chosen too large a volume: &b" + Placeholders.SELECTED + "&c. Select the smaller area to create the region. You can select &b" + Placeholders.MAX + " &clocks.");
	@Setting("SmallVolume")
	private Component smallVolume = deserialize("&cThe volume selected is too small. To complete the operation, select an area larger than the current one by &b" + Placeholders.VOLUME + "&c.");
	@Setting("IncorrectCords")
	private Component incorrectCords = deserialize("&cThe points have a coincidence of one of the coordinates. For cuboids, an XYZ coincidence is unacceptable. For flat regions, an XZ coincidence is not allowed.");
	@Setting("Intersect")
	private Component intersect = deserialize("&cThe region being created partially or completely overlaps an existing region. Select other positions.");
	@Setting("Cancel")
	private Component cancel = deserialize("&cThe creation of the region was cancelled.");
	@Setting("WrongSubdivisionPositions")
	private Component wrongSubdivisionPositions = deserialize("&cA child region cannot be created because it overlaps the boundaries of the parent region. Base regions cannot overlap either.");
	@Setting("SetPos")
	private Component setPos = deserialize("&dThe position &b" + Placeholders.NUMBER + " &d is set to the coordinates of &b" + Placeholders.POS + "&d.");
	@Setting("Success")
	private Component success = deserialize("&aRegion created. Volume: &b" + Placeholders.VOLUME + "&a. Enter the /rg claim command to claim the territory.");
	@Setting("SuccessSubdivision")
	private Component successSubdivision = deserialize("&aSubdivision region created. Volume: &b" + Placeholders.VOLUME + "&a.");

	@Override
	public Component getNoAdminPerm() {
		return noAdminPerm;
	}

	@Override
	public Component getPositionLocked() {
		return positionLocked;
	}

	@Override
	public Component getLimitRegions(long size, long limit) {
		return replace(limitRegions, array(Placeholders.SIZE, Placeholders.MAX), size, limit);
	}

	@Override
	public Component getLimitSubdivisions(long size, long limit) {
		return replace(limitSubdivisions, array(Placeholders.SIZE, Placeholders.MAX), size, limit);
	}

	@Override
	public Component getLimitBlocks(long size, long limit) {
		return replace(limitBlocks, array(Placeholders.SELECTED, Placeholders.MAX), size, limit);
	}

	@Override
	public Component getSmallVolume(int size) {
		return replace(smallVolume, Placeholders.VOLUME, size);
	}

	@Override
	public Component getIncorrectCords() {
		return incorrectCords;
	}

	@Override
	public Component getIntersect() {
		return intersect;
	}

	@Override
	public Component getCancel() {
		return cancel;
	}

	@Override
	public Component getWrongSubdivisionPositions() {
		return wrongSubdivisionPositions;
	}

	@Override
	public Component getSetPos(int pos, Vector3i cords) {
		return replace(setPos, array(Placeholders.NUMBER, Placeholders.POS), pos, cords);
	}

	@Override
	public Component getSuccess(Region region) {
		return replace(success, Placeholders.VOLUME, region.getCuboid().getSize());
	}

	@Override
	public Component getSuccessSubdivision(Region region) {
		return replace(successSubdivision, Placeholders.VOLUME, region.getCuboid().getSize());
	}

}
