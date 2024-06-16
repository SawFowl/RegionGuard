package sawfowl.regionguard.configure.locales.ru.command;

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
	private Component description = deserialize("&6Установка параметров флага.");
	@Setting("Title")
	private Component title = deserialize("&3Флаги");
	@Setting("Padding")
	private Component padding = deserialize("&3=");
	@Setting("NotPermittedRegion")
	private Component notPermitted = deserialize("&cВы не можете менять флаги в этом регионе.");
	@Setting("NotPermittedFlag")
	private Component notPermittedFlag = deserialize("&cВы не можете изменить или удалить флаг: &b" + Placeholders.FLAG + "&c.");
	@Setting("ValueNotPresent")
	private Component valueNotPresent = deserialize("&cВы должны указать значение флага.");
	@Setting("InvalidSource")
	private Component invalidSource = deserialize("&cДля флага указан недопустимый источник событий.");
	@Setting("InvalidTarget")
	private Component invalidTarget = deserialize("&cДля флага указана недопустимая цель события.");
	@Setting("Success")
	private Component success = deserialize("&aЗначение флага установлено.");

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
	public Component getNotPermittedFlag(String flag) {
		return replace(notPermittedFlag, Placeholders.FLAG, flag);
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
