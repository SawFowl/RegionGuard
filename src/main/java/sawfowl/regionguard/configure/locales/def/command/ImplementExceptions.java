package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Exceptions;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementExceptions implements Exceptions {

	public ImplementExceptions() {}

	@Setting("OnlyPlayer")
	private Component onlyPlayer = deserialize("&cThe command can only be executed by a player.");
	@Setting("PlayerNotPresent")
	private Component playerNotPresent = deserialize("&cYou must specify the player's nickname.");
	@Setting("TargetSelf")
	private Component targetSelf = deserialize("&cYou can't point to yourself.");
	@Setting("RegionNotFound")
	private Component regionNotFound = deserialize("&cThere is no region in your position.");
	@Setting("FlagNotPresent")
	private Component flagNotPresent = deserialize("&cYou must specify a flag.");
	@Setting("MessageNotPresent")
	private Component messageNotPresent = deserialize("&cYou must enter a message.");
	@Setting("NameNotPresent")
	private Component nameNotPresent = deserialize("&cYou must provide a name.");
	@Setting("VolumeNotPresent")
	private Component volumeNotPresent = deserialize("&cYou must specify the volume.");
	@Setting("EnteredZero")
	private Component enteredZero = deserialize("&cYou must enter a number greater than 0.");
	@Setting("NotEnoughMoney")
	private Component notEnoughMoney = deserialize("&cNot enough money.");
	@Setting("NotOwner")
	private Component notOwner = deserialize("&cYou do not own this region.");
	@Setting("EconomyException")
	private Component economyException = deserialize("&cSomething went wrong while executing the transaction. The details may be in the server console.");
	@Setting("MaxValue")
	private Component maxValue = deserialize("&cThe maximum allowed value is " + PlaceholderKeys.MAX + ".");
	@Setting("RegionTypeNotPresent")
	private Component regionTypeNotPresent = deserialize("&cYou must specify the type of region.");
	@Setting("SelectorTypeNotPresent")
	private Component selectorTypeNotPresent = deserialize("&cYou must specify the type of selection.");
	@Setting("TrustTypeNotPresent")
	private Component trustTypeNotPresent = deserialize("&cYou must specify the type of trust.");

	@Override
	public Component getOnlyPlayer() {
		return onlyPlayer;
	}

	@Override
	public Component getPlayerNotPresent() {
		return playerNotPresent;
	}

	@Override
	public Component getTargetSelf() {
		return targetSelf;
	}

	@Override
	public Component getRegionNotFound() {
		return regionNotFound;
	}

	@Override
	public Component getFlagNotPresent() {
		return null;
	}

	@Override
	public Component getMessageNotPresent() {
		return messageNotPresent;
	}

	@Override
	public Component getNameNotPresent() {
		return nameNotPresent;
	}

	@Override
	public Component getVolumeNotPresent() {
		return volumeNotPresent;
	}

	@Override
	public Component getEnteredZero() {
		return enteredZero;
	}

	@Override
	public Component getNotEnoughMoney() {
		return notEnoughMoney;
	}

	@Override
	public Component getNotOwner() {
		return notOwner;
	}

	@Override
	public Component getEconomyException() {
		return economyException;
	}

	@Override
	public Component getMaxValue(long value) {
		return replace(maxValue, PlaceholderKeys.MAX, value);
	}

	@Override
	public Component getRegionTypeNotPresent() {
		return regionTypeNotPresent;
	}

	@Override
	public Component getSelectorTypeNotPresent() {
		return selectorTypeNotPresent;
	}

	@Override
	public Component getTrustTypeNotPresent() {
		return trustTypeNotPresent;
	}

}
