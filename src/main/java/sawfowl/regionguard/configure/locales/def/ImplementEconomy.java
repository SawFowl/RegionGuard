package sawfowl.regionguard.configure.locales.def;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Economy;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementEconomy implements Economy {

	public ImplementEconomy() {}

	@Setting("EconomyNotFound")
	private String economyNotFound = "There is no economy plugin on the server. Some of the functionality will not be available.";
	@Setting("ErrorGiveMoney")
	private String errorGiveMoney = "Failed to add game currency to player " + PlaceholderKeys.PLAYER + ".";
	@Setting("ErrorTakeMoney")
	private String errorTakeMoney = "Failed to take game currency from player " + PlaceholderKeys.PLAYER + ".";

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
