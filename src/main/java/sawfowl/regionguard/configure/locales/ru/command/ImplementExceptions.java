package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Exceptions;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementExceptions implements Exceptions {

	public ImplementExceptions() {}

	@Setting("OnlyPlayer")
	private Component onlyPlayer = deserialize("&cКоманда может быть выполнена только игроком.");
	@Setting("PlayerNotPresent")
	private Component playerNotPresent = deserialize("&cВы должны указать ник игрока.");
	@Setting("TargetSelf")
	private Component targetSelf = deserialize("&cВы не можете указывать на себя.");
	@Setting("RegionNotFound")
	private Component regionNotFound = deserialize("&cВ вашей позиции нет региона.");
	@Setting("FlagNotPresent")
	private Component flagNotPresent = deserialize("&cВы должны указать флаг.");
	@Setting("MessageNotPresent")
	private Component messageNotPresent = deserialize("&cВы должны ввести сообщение.");
	@Setting("NameNotPresent")
	private Component nameNotPresent = deserialize("&cВы должны указать имя.");
	@Setting("VolumeNotPresent")
	private Component volumeNotPresent = deserialize("&cВы должны указать объем.");
	@Setting("EnteredZero")
	private Component enteredZero = deserialize("&cВы должны ввести число больше 0.");
	@Setting("NotEnoughMoney")
	private Component notEnoughMoney = deserialize("&cНе хватает денег.");
	@Setting("NotOwner")
	private Component notOwner = deserialize("&cВы не владеете этим регионом.");
	@Setting("EconomyException")
	private Component economyException = deserialize("&cПри выполнении транзакции что-то пошло не так. Подробности могут быть в консоли сервера.");
	@Setting("MaxValue")
	private Component maxValue = deserialize("&cМаксимально допустимое значение " + Placeholders.MAX + ".");
	@Setting("RegionTypeNotPresent")
	private Component regionTypeNotPresent = deserialize("&cВы должны указать тип региона.");
	@Setting("SelectorTypeNotPresent")
	private Component selectorTypeNotPresent = deserialize("&cВы должны указать тип выделения.");
	@Setting("TrustTypeNotPresent")
	private Component trustTypeNotPresent = deserialize("&cВы должны указать тип доверия.");

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
		return replace(maxValue, Placeholders.MAX, value);
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
