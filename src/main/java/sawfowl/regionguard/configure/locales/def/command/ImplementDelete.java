package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Delete;

@ConfigSerializable
public class ImplementDelete implements Delete {

	public ImplementDelete() {}

	@Setting("Description")
	private Component description = deserialize("&6Delete the region.");
	@Setting("ConfirmRequest")
	private Component confirmRequest = deserialize("&eAre you sure you want to delete the region in your location? Click this message to confirm.");
	@Setting("SuccesRegen")
	private Component succesRegen = deserialize("&eATTENTION!!! The area in the region will be restored to its original state!");
	@Setting("SuccesWhithChilds")
	private Component succesWhithChilds = deserialize("&aRegion removed. The child regions it contained were also deleted.");
	@Setting("SuccesChild")
	private Component succesChild = deserialize("&aThe child region has been removed.");
	@Setting("Success")
	private Component success = deserialize("&aRegion has been removed.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getConfirmRequest() {
		return confirmRequest;
	}

	@Override
	public Component getSuccesRegen() {
		return succesRegen;
	}

	@Override
	public Component getSuccesWhithChilds() {
		return succesWhithChilds;
	}

	@Override
	public Component getSuccesChild() {
		return succesChild;
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
