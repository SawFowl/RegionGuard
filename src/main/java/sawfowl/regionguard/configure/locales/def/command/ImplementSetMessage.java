package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.SetMessage;

@ConfigSerializable
public class ImplementSetMessage implements SetMessage {

	public ImplementSetMessage() {}

	@Setting("Description")
	private Component description = deserialize("&6Set/remove the join/exit message in the region.");
	@Setting("NotTrusted")
	private Component notTrusted = deserialize("&cYou are not a member of the current region and cannot change join/exit messages.");
	@Setting("LowTrust")
	private Component lowTrust = deserialize("&cYou have a low trust level in the current region and cannot change join/exit messages.");
	@Setting("TooLong")
	private Component tooLong = deserialize("&cThe message is too long. The maximum length is 50 characters.");
	@Setting("TypeNotPresent")
	private Component typeNotPresent = deserialize("&cYou didn't specify the type of message.");
	@Setting("SetJoin")
	private Component setJoin = deserialize("&aYou have set up a welcome message.");
	@Setting("SetExit")
	private Component setExit = deserialize("&aYou have set up a farewell message.");
	@Setting("ClearJoin")
	private Component clearJoin = deserialize("&aYou have cleared the welcome message.");
	@Setting("ClearExit")
	private Component clearExit = deserialize("&aYou have cleared the farewell message.");

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
	public Component getTypeNotPresent() {
		return typeNotPresent;
	}

	@Override
	public Component getSuccessJoin(boolean clear) {
		return clear ? clearJoin : setJoin;
	}

	@Override
	public Component getSuccessExit(boolean clear) {
		return clear ? clearExit : setExit;
	}

}
