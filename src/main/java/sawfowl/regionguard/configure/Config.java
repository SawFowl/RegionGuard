package sawfowl.regionguard.configure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import io.leangen.geantyref.TypeToken;
import sawfowl.localeapi.serializetools.SerializedItemStack;
import sawfowl.localeapi.serializetools.TypeTokens;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.utils.worldedit.MultiSelectionColors;

public class Config {

	private final RegionGuard plugin;
	private final TypeToken<Map<String, List<FlagValue>>> MAP_FLAGS_TOKEN;
	private final TypeToken<Map<String, Map<String, String>>> MAP_CUI_COLORS_TOKEN;
	private final TypeToken<Map<String, Integer>> MAP_CUI_SPACES_TOKEN;
	public Config(RegionGuard instance) {
		plugin = instance;
		MAP_FLAGS_TOKEN = new TypeToken<Map<String, List<FlagValue>>>(){};
		MAP_CUI_COLORS_TOKEN = new TypeToken<Map<String, Map<String, String>>>(){};
		MAP_CUI_SPACES_TOKEN = new TypeToken<Map<String, Integer>>(){};
		generate();
	}

	boolean save = false;

	private void generate() {
		if(!plugin.getConfigDir().resolve("Worlds").toFile().exists()) {
			plugin.getConfigDir().resolve("Worlds").toFile().mkdir();
		}
		
		check(getNode("UnloadRegions"), "If true, when unloading chunks, the regions that are in them will be unloaded to provide a faster search for the region among the remaining regions in the map.\nIf false, all regions will be permanently loaded.\nSet to false if for some reason the regions are not loaded in time.", true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("MinimalRegionSize", "2D"), null, 50, TypeTokens.INTEGER_TOKEN);
		check(getNode("MinimalRegionSize", "3D"), null, 800, TypeTokens.INTEGER_TOKEN);
		
		check(getNode("RegenerateChunks"), "If true, the chunks will be restored to their original appearance when the region is deleted.", null, null);
		check(getNode("RegenerateChunks", "Async"), "Using asynchronous mode. May not work stably.", true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("RegenerateChunks", "Delay"), "It is used for CPU load balancing during asynchronous regeneration of regions. If you have a powerful CPU, you can set it to 0 to instantly regenerate blocks after loading chunks into copies of the world.", 3, TypeTokens.INTEGER_TOKEN);
		check(getNode("RegenerateChunks", "AllPlayers"), "This option applies to any variant of deleting a region.", false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("RegenerateChunks", "Staff"), "This parameter applies only to the `/rg delete` command, and you must specify the flag in the command for regenerating the area.", true, TypeTokens.BOOLEAN_TOKEN);
		
		check(getNode("TankItems"), null, Arrays.asList("minecraft:bucket", "minecraft:glass_bottle"), TypeTokens.LIST_STRINGS_TOKEN);
		check(getNode("LocaleJsonSerialize"), "Select the style of the used localization files. If you change the setting, you need to delete the localization files and restart the server.", true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultSelector"), "If you select a flat selection, all the regions you create will automatically expand in height.\nAccepted values: \"Flat\", \"Cuboid\"", SelectorTypes.FLAT.toString(), TypeTokens.STRING_TOKEN);
		
		check(getNode("SplitStorage"), "Saving plugin data to different storages.\nTo use MySQL, set it to true. In this case, you need to configure and enable MySQL support.", null, null);
		check(getNode("SplitStorage", "Enable"), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("SplitStorage", "Players"), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("SplitStorage", "Regions"), null, false, TypeTokens.BOOLEAN_TOKEN);
		
		check(getNode("MySQL"), "Saving plugin data to the MySQL database.", null, null);
		check(getNode("MySQL", "Enable"), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("MySQL", "Host"), null, "localhost", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "Port"), null, "3306", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "DataBase"), null, "regionguard", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "Prefix"), null, "regionguard_", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "User"), null, "user", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "Password"), null, "UNSET", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "SSL"), null, "false", TypeTokens.STRING_TOKEN);
		
		check(getFlagsNode("ClaimFlags"), "Default flags for basic claim", claimDefaultFlags(), MAP_FLAGS_TOKEN);
		check(getFlagsNode("GlobalFlags"), "Default flags for worlds", globalDefaultFlags(), MAP_FLAGS_TOKEN);
		
		check(getCuiNode("Colors"), "Area selection colors. The colors used are in HEX format.", defaultCuiColors(), MAP_CUI_COLORS_TOKEN);
		check(getCuiNode("Spaces"), "The number of blocks between the lines in the area selection grid.", defaultCuiSpaces(), MAP_CUI_SPACES_TOKEN);
		
		// Remove this line in the future...
		if(!getNode("DefaultValues").virtual()) plugin.getRootNode().removeChild("DefaultValues");
	}

	public void writeDefaultWandItem() {
		check(getNode("Items", "Wand"), "An item to highlight the position.", new SerializedItemStack(ItemStack.of(ItemTypes.STONE_AXE)), TypeTokens.SERIALIZED_STACK_TOKEN);
		if(save) plugin.saveConfig();
	}

	public List<String> getTankItems() {
		try {
			return getNode("TankItems").get(TypeTokens.LIST_STRINGS_TOKEN);
		} catch (SerializationException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
			return Arrays.asList("minecraft:bucket", "minecraft:glass_bottle");
		}
	}

	public Map<String, List<FlagValue>> getDefaultClaimFlags() {
		try {
			return getFlagsNode("ClaimFlags").get(MAP_FLAGS_TOKEN);
		} catch (SerializationException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
			return claimDefaultFlags();
		}
	}

	public Map<String, List<FlagValue>> getDefaultGlobalFlags() {
		try {
			return getFlagsNode("GlobalFlags").get(MAP_FLAGS_TOKEN);
		} catch (SerializationException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
			return globalDefaultFlags();
		}
	}

	public Map<String, Map<String, String>> getCuiColors() {
		try {
			return getCuiNode("Colors").get(MAP_CUI_COLORS_TOKEN);
		} catch (SerializationException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
			return defaultCuiColors();
		}
	}

	public Map<String, Integer> getCuiSpaces() {
		try {
			return getCuiNode("Spaces").get(MAP_CUI_SPACES_TOKEN);
		} catch (SerializationException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
			return defaultCuiSpaces();
		}
	}

	public boolean unloadRegions() {
		return getNode("UnloadRegions").getBoolean();
	}

	public int getMinimalRegionSize(SelectorTypes selectorType) {
		return selectorType == SelectorTypes.FLAT ? getNode("MinimalRegionSize", "2D").getInt() : getNode("MinimalRegionSize", "3D").getInt();
	}

	public SelectorTypes getDefaultSelectorType() {
		return SelectorTypes.checkType(getNode("DefaultSelector").getString());
	}

	public boolean regenStaff() {
		return getNode("RegenerateChunks", "AllPlayers").getBoolean();
	}

	public boolean regenAll() {
		return getNode("RegenerateChunks", "AllPlayers").getBoolean();
	}

	public boolean asyncRegen() {
		return getNode("RegenerateChunks", "Async").getBoolean();
	}

	public int delayRegen() {
		return getNode("RegenerateChunks", "Delay").getInt(15);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void check(CommentedConfigurationNode node, String comment, Object value, TypeToken typeToken) {
        if(!node.virtual()) return;
    	save = true;
    	if(comment != null) {
        	node.comment(comment);
    	}
    	if(value != null) {
			try {
				node.set(typeToken, value);
			} catch (SerializationException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
    	}
    }

	private CommentedConfigurationNode getNode(Object... node) {
		return plugin.getRootNode().node(node);
	}

	private CommentedConfigurationNode getFlagsNode(Object... node) {
		return plugin.getFlagsNode().node(node);
	}

	private CommentedConfigurationNode getCuiNode(Object... node) {
		return plugin.getCuiNode().node(node);
	}

	private Map<String, List<FlagValue>> claimDefaultFlags() {
		Map<String, List<FlagValue>> claimFlags = new HashMap<String, List<FlagValue>>();
		claimFlags.put(Flags.BLOCK_BREAK.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.BLOCK_PLACE.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.PISTON_GRIEF.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.ENTITY_DAMAGE.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.ETITY_RIDING.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.INTERACT_BLOCK_PRIMARY.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.INTERACT_BLOCK_SECONDARY.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.INTERACT_ENTITY_PRIMARY.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.INTERACT_ENTITY_SECONDARY.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.INTERACT_ITEM.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.ITEM_PICKUP.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.ITEM_USE.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.PROJECTILE_IMPACT_BLOCK.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.PROJECTILE_IMPACT_ENTITY.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.PORTAL_USE.toString(), Arrays.asList(new FlagValue(false, null, null)));
		for(String flagName : plugin.getAPI().getFlags()) {
			if(!claimFlags.containsKey(flagName)) claimFlags.put(flagName, Arrays.asList(new FlagValue(true, null, null)));
		}
		return claimFlags;
	}

	private Map<String, List<FlagValue>> globalDefaultFlags() {
		Map<String, List<FlagValue>> worldFlags = new HashMap<String, List<FlagValue>>();
		for(String flagName : plugin.getAPI().getFlags()) {
			worldFlags.put(flagName, Arrays.asList(new FlagValue(true, null, null)));
		}
		return worldFlags;
	}

	private Map<String, Map<String, String>> defaultCuiColors() {
		
		Map<String, Map<String, String>> cuiColors = new HashMap<String, Map<String, String>>();
		
		Map<String, String> defaultColors = new HashMap<String, String>();
		Map<String, String> basicClaimColors = new HashMap<String, String>();
		Map<String, String> arenaColors = new HashMap<String, String>();
		Map<String, String> adminColors = new HashMap<String, String>();
		Map<String, String> subdivisionColors = new HashMap<String, String>();
		Map<String, String> tempRegionColors = new HashMap<String, String>();
		Map<String, String> dragColors = new HashMap<String, String>();
		
		defaultColors.put(CuiConfigPaths.FIRST_POSITION, MultiSelectionColors.BLUE);
		defaultColors.put(CuiConfigPaths.SECOND_POSITION, MultiSelectionColors.ORANGE);
		defaultColors.put(CuiConfigPaths.EDGE, MultiSelectionColors.RED);
		basicClaimColors.putAll(defaultColors);
		arenaColors.putAll(defaultColors);
		adminColors.putAll(defaultColors);
		subdivisionColors.putAll(defaultColors);
		tempRegionColors.putAll(defaultColors);
		dragColors.putAll(defaultColors);
		defaultColors.put(CuiConfigPaths.GRID, MultiSelectionColors.RED);
		basicClaimColors.put(CuiConfigPaths.GRID, MultiSelectionColors.YELLOW);
		arenaColors.put(CuiConfigPaths.GRID, MultiSelectionColors.GREEN);
		adminColors.put(CuiConfigPaths.GRID, MultiSelectionColors.RED);
		subdivisionColors.put(CuiConfigPaths.GRID, MultiSelectionColors.GRAY);
		tempRegionColors.put(CuiConfigPaths.GRID, MultiSelectionColors.PURPLE);
		dragColors.put(CuiConfigPaths.GRID, MultiSelectionColors.BURLYWOOD);
		
		cuiColors.put(CuiConfigPaths.DEFAULT, defaultColors);
		cuiColors.put(CuiConfigPaths.CLAIM, basicClaimColors);
		cuiColors.put(CuiConfigPaths.ARENA, arenaColors);
		cuiColors.put(CuiConfigPaths.ADMIN, adminColors);
		cuiColors.put(CuiConfigPaths.SUBDIVISION, subdivisionColors);
		cuiColors.put(CuiConfigPaths.TEMP, tempRegionColors);
		cuiColors.put(CuiConfigPaths.DRAG, dragColors);
		
		return cuiColors;
	}

	private Map<String, Integer> defaultCuiSpaces() {
		Map<String, Integer> cuiSpaces = new HashMap<String, Integer>();
		cuiSpaces.put(CuiConfigPaths.DEFAULT, 5);
		cuiSpaces.put(CuiConfigPaths.CLAIM, 3);
		cuiSpaces.put(CuiConfigPaths.ARENA, 3);
		cuiSpaces.put(CuiConfigPaths.ADMIN, 3);
		cuiSpaces.put(CuiConfigPaths.SUBDIVISION, 3);
		cuiSpaces.put(CuiConfigPaths.TEMP, 3);
		cuiSpaces.put(CuiConfigPaths.DRAG, 3);
		return cuiSpaces;
	}
}
