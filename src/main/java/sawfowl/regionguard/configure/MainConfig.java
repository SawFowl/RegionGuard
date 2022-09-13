package sawfowl.regionguard.configure;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.api.SelectorTypes;

@ConfigSerializable
public class MainConfig {

	public MainConfig(){}

	@Setting("DebugEconomy")
	private boolean debugEconomy = false;

	@Setting("UnloadRegions")
	@Comment("If true, when unloading chunks, the regions that are in them will be unloaded to provide a faster search for the region among the remaining regions in the map.\nIf false, all regions will be permanently loaded.\nSet to false if for some reason the regions are not loaded in time.")
	private boolean unloadRegions = false;

	@Setting("MinimalRegionSize")
	private MinimalRegionSize minimalRegionSize = new MinimalRegionSize();

	@Setting("RegenerateTerritory")
	@Comment("If true, the region area will return to its original appearance when the region is deleted.")
	private RegenerateTerritory regenerateTerritory = new RegenerateTerritory();

	@Setting("TankItems")
	private List<String> tankItems = Arrays.asList("minecraft:bucket", "minecraft:glass_bottle");

	@Setting("LocaleJsonSerialize")
	@Comment("Select the style of the used localization files. If you change the setting, you need to delete the localization files and restart the server.")
	private boolean localeJsonSerialize = true;

	@Setting("DefaultSelector")
	@Comment("If you select a flat selection, all the regions you create will automatically expand in height.\nAccepted values: \"Flat\", \"Cuboid\"")
	private String defaultSelector = SelectorTypes.FLAT.toString();

	@Setting("SplitStorage")
	@Comment("Saving plugin data to different storages.\nTo use MySQL, set it to true. In this case, you need to configure and enable MySQL support.")
	private SplitStorage splitStorage = new SplitStorage();

	@Setting("MySQL")
	@Comment("Saving plugin data to the MySQL database.")
	private MySQLConfig mySQLConfig = new MySQLConfig();

	@Setting("WandItem")
	private WandItem wanditem = new WandItem();

	public boolean isUnloadRegions() {
		return unloadRegions;
	}

	public MinimalRegionSize getMinimalRegionSize() {
		return minimalRegionSize;
	}

	public RegenerateTerritory getRegenerateTerritory() {
		return regenerateTerritory;
	}

	public List<String> getTankItems() {
		return tankItems;
	}

	public boolean isLocaleJsonSerialize() {
		return localeJsonSerialize;
	}

	public SelectorTypes getDefaultSelector() {
		return SelectorTypes.checkType(defaultSelector);
	}

	public SplitStorage getSplitStorage() {
		return splitStorage;
	}

	public MySQLConfig getMySQLConfig() {
		return mySQLConfig;
	}

	public WandItem getWanditem() {
		return wanditem;
	}

	public boolean isDebugEconomy() {
		return debugEconomy;
	}

}
