package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.UpdateDefaultFlags;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementUpdateDefaultFlags implements UpdateDefaultFlags {

	public ImplementUpdateDefaultFlags() {}

	@Setting("Description")
	private Component description = deserialize("&6Установка флагов по умолчанию, основываясь на флагах региона в текущем местоположении игрока.");
	@Setting("Success")
	private Component success = deserialize("&aВы изменили флаги по умолчанию для регионов с типом &b" + PlaceholderKeys.TYPE + "&a.");
	@Setting("Exception")
	private Component exception = deserialize("&cЧто-то пошло не так. Подробности могут быть в консоли.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getSuccess(RegionTypes type) {
		return replace(success, PlaceholderKeys.TYPE, type.toString());
	}

	@Override
	public Component getException() {
		return exception;
	}

}
