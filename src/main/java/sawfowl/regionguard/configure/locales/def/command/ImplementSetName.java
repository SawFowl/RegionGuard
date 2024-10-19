package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.SetName;

@ConfigSerializable
public class ImplementSetName implements SetName {

	public ImplementSetName() {}

	@Setting("Description")
	private Component description = deserialize("&6Set the name of the region.");
	@Setting("NotTrusted")
	private Component notTrusted = deserialize("&cYou are not a member of the current region and cannot change its name.");
	@Setting("LowTrust")
	private Component lowTrust = deserialize("&cYou have a low level of trust in the current region and cannot change its name.");
	@Setting("TooLong")
	private Component tooLong = deserialize("&cThe new name is too long. The maximum length is 20 characters.");
	@Setting("Set")
	private Component set = deserialize("&aYou have set a new name for the region.");
	@Setting("Clear")
	private Component clear = deserialize("&aThe name of the region has been cleared.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getNotTrusted() {
		return notTrusted;
	}

	@Override
	public Component getLowTrust() {
		return lowTrust;
	}

	@Override
	public Component getTooLong() {
		return tooLong;
	}

	@Override
	public Component getSuccess(boolean clear) {
		return clear ? this.clear : set;
	}

}
