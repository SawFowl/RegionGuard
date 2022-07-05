package sawfowl.regionguard.configure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

public class Config {

	private final RegionGuard plugin;
	public Config(RegionGuard instance) {
		plugin = instance;
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
		check(getNode("RegenerateChunks", "Async"), "Using asynchronous mode. May not work stably.", false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("RegenerateChunks", "Delay"), "It is used for CPU load balancing during asynchronous regeneration of regions. If you have a powerful CPU, you can set it to 0 to instantly regenerate blocks after loading chunks into copies of the world.", 3, TypeTokens.INTEGER_TOKEN);
		check(getNode("RegenerateChunks", "AllPlayers"), "This option applies to any variant of deleting a region.", false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("RegenerateChunks", "Staff"), "This parameter applies only to the `/rg delete` command, and you must specify the flag in the command for regenerating the area.", true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("TankItems"), null, Arrays.asList("minecraft:bucket", "minecraft:glass_bottle"), TypeTokens.LIST_STRINGS_TOKEN);
		check(getNode("LocaleJsonSerialize"), "Select the style of the used localization files. If you change the setting, you need to delete the localization files and restart the server.", true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues"), "Default values of the flags and selector type.", null, null);
		check(getNode("DefaultValues", "Selector"), "If you select a flat selection, all the regions you create will automatically expand in height.\nAccepted values: \"Flat\", \"Cuboid\"", SelectorTypes.FLAT.toString(), TypeTokens.STRING_TOKEN);
		check(getNode("DefaultValues", "Flags"), "Default values of the flags.", null, null);
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
		check(getNode("DefaultValues", "Flags", "Claim", Flags.BLOCK_BREAK.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.BLOCK_PLACE.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.PISTON_GRIEF.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.ENTITY_DAMAGE.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.ETITY_RIDING.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.INTERACT_BLOCK_PRIMARY.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.INTERACT_BLOCK_SECONDARY.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.INTERACT_ENTITY_PRIMARY.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.INTERACT_ENTITY_SECONDARY.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.INTERACT_ITEM.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.ITEM_PICKUP.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.ITEM_USE.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.PROJECTILE_IMPACT_BLOCK.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.PROJECTILE_IMPACT_ENTITY.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues", "Flags", "Claim", Flags.PORTAL_USE.toString()), null, false, TypeTokens.BOOLEAN_TOKEN);
		for(String flagName : plugin.getAPI().getFlags()) {
			check(getNode("DefaultValues", "Flags", "World", flagName), null, true, TypeTokens.BOOLEAN_TOKEN);
			check(getNode("DefaultValues", "Flags", "Claim", flagName), null, true, TypeTokens.BOOLEAN_TOKEN);
		}
	}

	public void writeDefaultWandItem() {
		check(getNode("Items", "Wand"), "An item to highlight the position.", new SerializedItemStack(ItemStack.of(ItemTypes.STONE_AXE)), TypeTokens.SERIALIZED_STACK_TOKEN);
		if(save) plugin.saveConfig();
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

	public List<String> getTankItems() {
		try {
			return getNode("TankItems").get(TypeTokens.LIST_STRINGS_TOKEN);
		} catch (SerializationException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
			return Arrays.asList("minecraft:bucket", "minecraft:glass_bottle");
		}
	}

	public Map<Flags, Boolean> getDefaultClaimFlags() {
		Map<Flags, Boolean> map = new HashMap<Flags, Boolean>();
		for(Entry<Object, CommentedConfigurationNode> node : plugin.getRootNode().node("DefaultValues", "Flags", "Claim").childrenMap().entrySet()) {
			if(Flags.valueOfName((String) node.getKey()) != null) map.put(Flags.valueOfName((String) node.getKey()), node.getValue().getBoolean());
		}
		return map;
	}

	public boolean unloadRegions() {
		return getNode("UnloadRegions").getBoolean();
	}

	public int getMinimalRegionSize(SelectorTypes selectorType) {
		return selectorType == SelectorTypes.FLAT ? getNode("MinimalRegionSize", "2D").getInt() : getNode("MinimalRegionSize", "3D").getInt();
	}

	public SelectorTypes getDefaultSelectorType() {
		return SelectorTypes.checkType(getNode("DefaultValues", "Selector").getString());
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

}
