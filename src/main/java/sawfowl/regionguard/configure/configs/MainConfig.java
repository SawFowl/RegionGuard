package sawfowl.regionguard.configure.configs;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.localeapi.api.LocalisedComment;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStack;
import sawfowl.regionguard.api.SelectorTypes;

@ConfigSerializable
public class MainConfig {

	public MainConfig(){}

	@Setting("DebugEconomy")
	private boolean debugEconomy = false;

	@Setting("UnloadRegions")
	@LocalisedComment(path = {"Comments", "MainConfig", "UnloadRegions"}, plugin = "regionguard")
	private boolean unloadRegions = false;

	@Setting("MinimalRegionSize")
	private MinimalRegionSize minimalRegionSize = new MinimalRegionSize();

	@Setting("RegenerateTerritory")
	@LocalisedComment(path = {"Comments", "MainConfig", "RegenerateTerritory", "Title"}, plugin = "regionguard")
	private RegenerateTerritory regenerateTerritory = new RegenerateTerritory();

	@Setting("TankItems")
	@LocalisedComment(path = {"Comments", "MainConfig", "TankItems"}, plugin = "regionguard")
	private List<String> tankItems = Arrays.asList("minecraft:bucket", "minecraft:glass_bottle");

	@Setting("DefaultSelector")
	@LocalisedComment(path = {"Comments", "MainConfig", "DefaultSelector"}, plugin = "regionguard")
	private String defaultSelector = SelectorTypes.FLAT.toString();

	@Setting("SplitStorage")
	@LocalisedComment(path = {"Comments", "MainConfig", "SplitStorage"}, plugin = "regionguard")
	private SplitStorage splitStorage = new SplitStorage();

	@Setting("MySQL")
	@LocalisedComment(path = {"Comments", "MainConfig", "MySQL"}, plugin = "regionguard")
	private MySQLConfig mySQLConfig = new MySQLConfig();

	@Setting("WandItem")
	@LocalisedComment(path = {"Comments", "MainConfig", "WandItem"}, plugin = "regionguard")
	private SerializedItemStack wanditem = new SerializedItemStack("minecraft:stone_axe", 1, "{\"minecraft\":{\"damage\":131}}").toJsonComponents();

	@Setting("RegisterForgeListeners")
	@LocalisedComment(path = {"Comments", "MainConfig", "RegisterForgeListeners"}, plugin = "regionguard")
	private boolean registerForgeListeners = true;

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

	public SelectorTypes getDefaultSelector() {
		return SelectorTypes.checkType(defaultSelector);
	}

	public SplitStorage getSplitStorage() {
		return splitStorage;
	}

	public MySQLConfig getMySQLConfig() {
		return mySQLConfig;
	}

	public SerializedItemStack getWanditem() {
		return wanditem;
	}

	public boolean isDebugEconomy() {
		return debugEconomy;
	}

	public boolean isRegisterForgeListeners() {
		return registerForgeListeners;
	}

}
