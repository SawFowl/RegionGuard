package sawfowl.regionguard.configure;

import java.util.UUID;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerWorld;

import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.Region;

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

}
