package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Wand;

@ConfigSerializable
public class ImplementWand implements Wand {

	public ImplementWand() {}

	@Setting("Description")
	private Component description = deserialize("&6Get an item to create regions.");
	@Setting("Exist")
	private Component exist = deserialize("&cYou already have a tool for creating regions and getting information about them.");
	@Setting("FullInventory")
	private Component fullInventory = deserialize("&cYou have no room in your inventory. Free up at least 1 slot.");
	@Setting("Success")
	private Component success = deserialize("&dLeft click to select two points in the world to create a region, then enter the command '/rg claim' in order to claim the region.\n&dThe child regions are added automatically.\n&dA right click on the block will display brief information about the region.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getExist() {
		return exist;
	}

	@Override
	public Component getFullInventory() {
		return fullInventory;
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
