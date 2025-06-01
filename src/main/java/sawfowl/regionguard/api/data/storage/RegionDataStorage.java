package sawfowl.regionguard.api.data.storage;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;

import sawfowl.regionguard.api.data.Region;

/**
 * Using this interface you can create various data storages of regions. For example, PostgreSql.
 */
public interface RegionDataStorage {

	public void cleanNotExistWorldsData();

	void removeAllWorldData(ResourceKey world);

	/**
	 * Don't forget to register the global region.
	 */
	void createGlobalRegionForWorld(ResourceKey world);

	Region getWorldRegion(ResourceKey world);

	void save(Region region);

	void delete(Region region);

	void loadRegions(ResourceKey world);

	default void setParentAfterLoad(Region region) {
		if(!region.containsChilds()) return;
		for(Region child : region.getChilds()) {
			child.setParrent(region);
			setParentAfterLoad(child);
		}
	}

	default void loadRegions() {
		Sponge.server().worldManager().worlds().forEach(world -> loadRegions(world.key()));
		Sponge.server().worldManager().offlineWorldKeys().forEach(this::loadRegions);
	}

	default void createGlobalRegions() {
		Sponge.server().worldManager().worlds().forEach(world -> createGlobalRegionForWorld(world.key()));
		Sponge.server().worldManager().offlineWorldKeys().forEach(this::createGlobalRegionForWorld);
	}

}
