package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Claim;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementClaim implements Claim {

	public ImplementClaim() {}

	@Setting("Description")
	private Component description = deserialize("&6Claim the allocated region.");
	@Setting("RegionNotFound")
	private Component regionNotFound = deserialize("&cNo region available to create a claim.");
	@Setting("WorldNotFound")
	private Component worldNotFound = deserialize("&cRegion world not found: &b" + Placeholders.WORLD);
	@Setting("Intersect")
	private Component intersect = deserialize("&cThe region touches another already existing region with boundaries from &b" + Placeholders.MIN + " &c to &b" + Placeholders.MAX + "&c. Highlight the other region.");
	@Setting("Success")
	private Component success = deserialize("&aYou have successfully claimed a region.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getRegionNotFound() {
		return regionNotFound;
	}

	@Override
	public Component getWorldNotFound(String world) {
		return replace(worldNotFound, Placeholders.WORLD, world);
	}

	@Override
	public Component getIntersect(Vector3i min, Vector3i max) {
		return replace(intersect, array(Placeholders.MIN, Placeholders.MAX), min, max);
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
