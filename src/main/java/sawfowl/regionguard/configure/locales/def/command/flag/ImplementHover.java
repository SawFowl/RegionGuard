package sawfowl.regionguard.configure.locales.def.command.flag;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Flag.Hover;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementHover implements Hover {

	public ImplementHover(){}

	@Setting("Remove")
	private Component remove = deserialize("&2Delete flag data.");
	@Setting("True")
	private Component trueValue = deserialize("&2Set the allowing value.");
	@Setting("False")
	private Component falseValue = deserialize("&2Set a disallowing value.");
	@Setting("SuggestArgs")
	private Component suggestArgs = deserialize("&2Click to specify arguments.");
	@Setting("Values")
	private Component hoverValues = deserialize("&5Event source &b" + PlaceholderKeys.SOURCE + "&5.\n&5Event target &b" + PlaceholderKeys.TARGET + "&5.");

	@Override
	public Component getRemove() {
		return remove;
	}

	@Override
	public Component getTrue() {
		return trueValue;
	}

	@Override
	public Component getFalse() {
		return falseValue;
	}

	@Override
	public Component getSuggestArgs() {
		return suggestArgs;
	}

	@Override
	public Component getHoverValues(FlagValue flagValue) {
		return replace(hoverValues, array(PlaceholderKeys.SOURCE, PlaceholderKeys.TARGET), flagValue.getSource(), flagValue.getTarget());
	}

}
