package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.UpdateDefaultFlags;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementUpdateDefaultFlags implements UpdateDefaultFlags {

	public ImplementUpdateDefaultFlags() {}

	@Setting("Description")
	private Component description = deserialize("&6Setting default flags based on those available in the region at the player location.");
	@Setting("Success")
	private Component success = deserialize("&aYou have changed the default flags for regions with type &b" + PlaceholderKeys.TYPE + "&a.");
	@Setting("Exception")
	private Component exception = deserialize("&cSomething has gone wrong. The details may be in the console.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getSuccess(RegionTypes type) {
		return replace(success, PlaceholderKeys.TYPE, type.toString());
	}

	@Override
	public Component getException() {
		return exception;
	}

}
