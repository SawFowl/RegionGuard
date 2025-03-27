package sawfowl.regionguard.api.data.storage;

import java.util.UUID;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import sawfowl.regionguard.api.data.PlayerData;

public interface PlayerDataStorage {

	void save(ServerPlayer player, PlayerData playerData);

	void save(UUID player, PlayerData playerData);

	PlayerData getPlayerData(UUID player);

	void loadAll();

	default PlayerData getPlayerData(ServerPlayer player) {
		return getPlayerData(player.uniqueId());
	}

}
