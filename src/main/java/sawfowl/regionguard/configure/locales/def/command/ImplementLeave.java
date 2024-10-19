package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Leave;

@ConfigSerializable
public class ImplementLeave implements Leave {

	public ImplementLeave() {}

	@Setting("Description")
	private Component description = TextUtils.deserialize("&6Leave from region.");
	@Setting("Owner")
	private Component owner = TextUtils.deserialize("&cYou own a region and cannot leave it. Change the owner or either delete the region.");
	@Setting("NotTrusted")
	private Component notTrusted = TextUtils.deserialize("&cYou are not a member of this region.");
	@Setting("ConfirmRequest")
	private Component confirmRequest = TextUtils.deserialize("&eAre you sure you want to leave from this region? Click on this message to confirm.");
	@Setting("Success")
	private Component success = TextUtils.deserialize("&aYou have leaved the region in your current location.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getOwner() {
		return owner;
	}

	@Override
	public Component getNotTrusted() {
		return notTrusted;
	}

	@Override
	public Component getConfirmRequest() {
		return confirmRequest;
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
