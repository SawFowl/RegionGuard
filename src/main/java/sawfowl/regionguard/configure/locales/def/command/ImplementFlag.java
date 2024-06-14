package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Flag;
import sawfowl.regionguard.configure.locales.def.command.flag.ImplementHover;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementFlag implements Flag {

	public ImplementFlag() {}

	@Setting("Hover")
	private ImplementHover hover = new ImplementHover();
	@Setting("Description")
	private Component description = deserialize("&6Set the flag parameters.");
	@Setting("Title")
	private Component title = deserialize("&3List of flags");
	@Setting("Padding")
	private Component padding = deserialize("&3=");
	@Setting("NotPermittedRegion")
	private Component notPermitted = deserialize("&cYou cannot change the flags in this region.");
	@Setting("NotPermittedFlag")
	private Component notPermittedFlag = deserialize("&cYou cannot change or delete the flag: &b" + Placeholders.FLAG + "&c.");
	@Setting("ValueNotPresent")
	private Component valueNotPresent = deserialize("&cYou must specify the value of the flag.");
	@Setting("InvalidSource")
	private Component invalidSource = deserialize("&cAn invalid event source is specified for the flag.");
	@Setting("InvalidTarget")
	private Component invalidTarget = deserialize("&cFor the flag there is an invalid event target specified.");
	@Setting("Success")
	private Component success = deserialize("&aThe value of the flag has been set.");

	@Override
	public Hover getHover() {
		return hover;
	}

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getTitle() {
		return title;
	}

	@Override
	public Component getPadding() {
		return padding;
	}

	@Override
	public Component getNotPermitted() {
		return notPermitted;
	}

	@Override
	public Component getNotPermittedFlag() {
		return notPermittedFlag;
	}

	@Override
	public Component getValueNotPresent() {
		return valueNotPresent;
	}

	@Override
	public Component getInvalidSource() {
		return invalidSource;
	}

	@Override
	public Component getInvalidTarget() {
		return invalidTarget;
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
