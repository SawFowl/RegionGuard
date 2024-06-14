package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.List;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementList implements List {

	public ImplementList() {}

	@Setting("Description")
	private Component description = deserialize("&6Show list of regions.");
	@Setting("Title")
	private Component title = deserialize("&3Regions: " + Placeholders.PLAYER);
	@Setting("Padding")
	private Component padding = deserialize("&3=");
	@Setting("EmptyOther")
	private Component emptyOther = deserialize("&cThe player has no regions.");
	@Setting("EmptySelf")
	private Component emptySelf = deserialize("&cYou don't have any regions.");
	@Setting("TeleportNotSafe")
	private Component teleportNotSafe = deserialize("&cThe position is not safe. Are you sure you want to teleport? Click on this message to confirm.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getTitle(String player) {
		return replace(title, Placeholders.PLAYER, player);
	}

	@Override
	public Component getPadding() {
		return padding;
	}

	@Override
	public Component getEmptyOther() {
		return emptyOther;
	}

	@Override
	public Component getEmptySelf() {
		return emptySelf;
	}

	@Override
	public Component getTeleportNotSafe() {
		return teleportNotSafe;
	}

}
