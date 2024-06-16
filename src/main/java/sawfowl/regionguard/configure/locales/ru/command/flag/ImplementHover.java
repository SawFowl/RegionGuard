package sawfowl.regionguard.configure.locales.ru.command.flag;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Flag.Hover;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementHover implements Hover {

	public ImplementHover(){}

	@Setting("Remove")
	private Component remove = deserialize("&2Удаление данных флага.");
	@Setting("True")
	private Component trueValue = deserialize("&2Установка разрешающего значения.");
	@Setting("False")
	private Component falseValue = deserialize("&2Установка запрещающего значения");
	@Setting("SuggestArgs")
	private Component suggestArgs = deserialize("&2Клик для указания аргументов.");
	@Setting("Values")
	private Component hoverValues = deserialize("&5Инициатор события &b" + Placeholders.SOURCE + "&5.\n&5Цель события &b" + Placeholders.TARGET + "&5.");

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
		return replace(hoverValues, array(Placeholders.SOURCE, Placeholders.TARGET), flagValue.getSource(), flagValue.getTarget());
	}

}
