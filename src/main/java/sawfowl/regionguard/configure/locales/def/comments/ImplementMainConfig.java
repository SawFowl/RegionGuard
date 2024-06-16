package sawfowl.regionguard.configure.locales.def.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Comments.MainConfig;

@ConfigSerializable
public class ImplementMainConfig implements MainConfig {

	public ImplementMainConfig() {}

	@Setting("RegenerateTerritory")
	private ImplementRegenerateTerritory regenerateTerritory = new ImplementRegenerateTerritory();
	@Setting("UnloadRegions")
	private String unloadRegions = "If true, when unloading chunks, the regions that are in them will be unloaded to provide a faster search for the region among the remaining regions in the map.\nIf false, all regions will be permanently loaded.\nSet to false if for some reason the regions are not loaded in time.";
	@Setting("MinimalRegionSize")
	private String minimalRegionSize = "The minimum size of the region to be created.";
	@Setting("TankItems")
	private String tankItems = "Items that are liquid containers.";
	@Setting("DefaultSelector")
	private String defaultSelector = "If you select a flat selection, all the regions you create will automatically expand in height.\nAccepted values: \"Flat\", \"Cuboid\"";
	@Setting("SplitStorage")
	private String splitStorage = "Saving plugin data to different storages.\nValid options: File, H2, MySql.\nTo use MySql, you need to configure the appropriate section of this configuration.\nThe use of H2 is preferable when the amount of data is small.\nTo use MySql or H2, the appropriate driver is required.\nYou can find plugins with drivers on the ORE.";
	@Setting("MySQL")
	private String mySQL = "Configure this if you need to store plugin data in a MySQL database.";
	@Setting("WandItem")
	private String wandItem = "An item used to highlight an area and provide a summary of the region.";
	@Setting("RegisterForgeListeners")
	private String registerForgeListeners = "Sometimes Sponge does not intercept Forge events. Enabling this option may fix the problem.\nIf the territory protection does not work at any action, please report it to the plugin developer's Discord server - https://discord.gg/4SMShjQ3Pe\nIn some cases, modifications to the mod code may be required.";
	@Setting("SyncInterval")
	private String syncInterval = "Time between loading new and updated data from the MySql database.\nSynchronization will not be performed if the value is less than 0.";

	@Override
	public RegenerateTerritory getRegenerateTerritory() {
		return regenerateTerritory;
	}

	@Override
	public String getUnloadRegions() {
		return unloadRegions;
	}

	@Override
	public String getMinimalRegionSize() {
		return minimalRegionSize;
	}

	@Override
	public String getTankItems() {
		return tankItems;
	}

	@Override
	public String getDefaultSelector() {
		return defaultSelector;
	}

	@Override
	public String getSplitStorage() {
		return splitStorage;
	}

	@Override
	public String getMySQL() {
		return mySQL;
	}

	@Override
	public String getWandItem() {
		return wandItem;
	}

	@Override
	public String getRegisterForgeListeners() {
		return registerForgeListeners;
	}

	@Override
	public String getSyncInterval() {
		return syncInterval;
	}

}
