package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.WeCUI;

@ConfigSerializable
public class ImplementWeCUI implements WeCUI {

	public ImplementWeCUI() {}

	@Setting("Description")
	private Component description = deserialize("&6Переключение статуса отправки пакетов WECui.");
	@Setting("Enable")
	private Component enable = deserialize("&aТеперь ваш игровой клиент будет получать пакеты данных для отображения границ регионов, которые обрабатываются модом WECui. Если у вас нет этого мода, рекомендуется ввести эту команду еще раз, чтобы отключить отправку пакетов.");
	@Setting("Disable")
	private Component disable = deserialize("&aТеперь ваш клиент не будет получать пакеты данных для мода WECui.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component get(boolean enable) {
		return enable ? this.enable : disable;
	}

}
