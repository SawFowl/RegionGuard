package sawfowl.regionguard.configure.locales.ru;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Economy;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementEconomy implements Economy {

	public ImplementEconomy() {}

	@Setting("EconomyNotFound")
	private String economyNotFound = "На сервере отсутствует плагин экономики. Некоторые функции будут недоступны.";
	@Setting("ErrorGiveMoney")
	private String errorGiveMoney = "Не удалось добавить игровую валюту на аккаунт " + PlaceholderKeys.PLAYER + ".";
	@Setting("ErrorTakeMoney")
	private String errorTakeMoney = "Не удалось списать игровую валюту с аккаунта " + PlaceholderKeys.PLAYER + ".";

	@Override
	public String getEconomyNotFound() {
		return economyNotFound;
	}

	@Override
	public String getErrorGiveMoney(ServerPlayer player) {
		return errorGiveMoney.replaceAll(PlaceholderKeys.PLAYER, player.name());
	}

	@Override
	public String getErrorTakeMoney(ServerPlayer player) {
		return errorTakeMoney.replaceAll(PlaceholderKeys.PLAYER, player.name());
	}

}
