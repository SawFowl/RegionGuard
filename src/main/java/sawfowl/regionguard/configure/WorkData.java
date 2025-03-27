package sawfowl.regionguard.configure;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.storage.PlayerDataStorage;
import sawfowl.regionguard.api.data.storage.RegionDataStorage;
import sawfowl.regionguard.implementsapi.data.PlayerDataImpl;
import sawfowl.regionguard.implementsapi.data.RegionImpl;

public interface WorkData extends PlayerDataStorage, RegionDataStorage {

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
