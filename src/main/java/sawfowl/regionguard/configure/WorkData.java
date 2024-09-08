package sawfowl.regionguard.configure;

import java.util.UUID;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.implementsapi.data.PlayerDataImpl;
import sawfowl.regionguard.implementsapi.data.RegionImpl;

public interface WorkData {

	public void createDataForWorlds();

	public Region getWorldRegion(ServerWorld world);

	public void saveRegion(Region region);

	public void deleteRegion(Region region);

	public void loadRegions();

	public void savePlayerData(ServerPlayer player, PlayerData playerData);

	public void savePlayerData(UUID player, PlayerData playerData);

	public PlayerData getPlayerData(ServerPlayer player);

	public void loadDataOfPlayers();

	default void setParentAfterLoad(Region region) {
		if(!region.containsChilds()) return;
		for(Region child : region.getChilds()) {
			child.setParrent(region);
			setParentAfterLoad(child);
		}
	}

	default RegionImpl getRegionFromConfig(ConfigurationNode node, String fileOrUUID) {
		try {
			return node.get(RegionImpl.class);
		} catch (SerializationException e) {
			RegionGuard.getInstance().getLogger().error("Error when loading region: " + fileOrUUID + "\n" + e.getLocalizedMessage());
			return null;
		}
	}

	default PlayerDataImpl getPlayerDataFromConfig(ConfigurationNode node, String fileOrUUID) {
		try {
			return node.get(PlayerDataImpl.class);
		} catch (SerializationException e) {
			RegionGuard.getInstance().getLogger().error("Error when loading player data: " + fileOrUUID + "\n" + e.getLocalizedMessage());
			return null;
		}
	}

}
