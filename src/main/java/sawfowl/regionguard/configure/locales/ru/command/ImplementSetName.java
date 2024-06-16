package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.SetName;

@ConfigSerializable
public class ImplementSetName implements SetName {

	public ImplementSetName() {}

	@Setting("Description")
	private Component description = deserialize("&6Установка имени региона");
	@Setting("NotTrusted")
	private Component notTrusted = deserialize("&cВы не являетесь участником текущего региона и не можете изменить его название.");
	@Setting("LowTrust")
	private Component lowTrust = deserialize("&cУ вас низкий уровень доверия в текущем регионе, и вы не можете изменить его название.");
	@Setting("TooLong")
	private Component tooLong = deserialize("&cНовое имя слишком длинное. Максимальная длина - 20 символов.");
	@Setting("Set")
	private Component set = deserialize("&aВы установили новое имя для региона.");
	@Setting("Clear")
	private Component clear = deserialize("&aНазвание региона было очищено.");

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
	public Component getSuccess(boolean clear) {
		return clear ? this.clear : set;
	}

}
