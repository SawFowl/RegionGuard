package sawfowl.regionguard.configure.configs;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import com.google.gson.JsonParser;

import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;
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
	@Comment("Saving plugin data to different storages.\nValid options: File, H2, MySql.\nTo use MySql, you need to configure the appropriate section of this configuration.\nThe use of H2 is preferable when the amount of data is small.\nTo use MySql or H2, the appropriate driver is required.\nYou can find plugins with drivers on the ORE.")
	private SplitStorage splitStorage = new SplitStorage();

	@Setting("MySQL")
	@Comment("Saving plugin data to the MySQL database.")
	private MySQLConfig mySQLConfig = new MySQLConfig();

	@Setting("WandItem")
	private SerializedItemStackJsonNbt wanditem = new SerializedItemStackJsonNbt("minecraft:stone_axe", 1, JsonParser.parseString("{\"Damage\":131}").getAsJsonObject());

	@Setting("RegisterForgeListeners")
	@Comment("Sometimes Sponge does not intercept Forge events. Enabling this option may fix the problem.\nIf the territory protection does not work at any action, please report it to the plugin developer's Discord server - https://discord.gg/4SMShjQ3Pe\nIn some cases, modifications to the mod code may be required.")
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

	public SerializedItemStackJsonNbt getWanditem() {
		return wanditem;
	}

	public boolean isDebugEconomy() {
		return debugEconomy;
	}

	public boolean isRegisterForgeListeners() {
		return registerForgeListeners;
	}

}
