package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.SetMessage;

@ConfigSerializable
public class ImplementSetMessage implements SetMessage {

	public ImplementSetMessage() {}

	@Setting("Description")
	private Component description = deserialize("&6Установка/удаление сообщений региона о входе/выходе.");
	@Setting("NotTrusted")
	private Component notTrusted = deserialize("&cВы не являетесь участником текущего региона и не можете изменять сообщения о входе/выходе.");
	@Setting("LowTrust")
	private Component lowTrust = deserialize("&cУ вас низкий уровень доверия в текущем регионе, и вы не можете изменять сообщения о входе/выходе.");
	@Setting("TooLong")
	private Component tooLong = deserialize("&cСообщение слишком длинное. Максимальная длина составляет 50 символов.");
	@Setting("TypeNotPresent")
	private Component typeNotPresent = deserialize("&cВы не указали тип сообщения.");
	@Setting("SetJoin")
	private Component setJoin = deserialize("&aВы установили приветственное сообщение.");
	@Setting("SetExit")
	private Component setExit = deserialize("&aВы установили прощальное сообщение.");
	@Setting("ClearJoin")
	private Component clearJoin = deserialize("&aВы удалили приветственное сообщение.");
	@Setting("ClearExit")
	private Component clearExit = deserialize("&aВы очистили прощальное сообщение.");

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
