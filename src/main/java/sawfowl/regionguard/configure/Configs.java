package sawfowl.regionguard.configure;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import io.leangen.geantyref.TypeToken;
import sawfowl.localeapi.serializetools.SerializedItemStack;
import sawfowl.localeapi.serializetools.TypeTokens;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.api.data.Region;

public class Configs {

	private final RegionGuard plugin;
	public Configs(RegionGuard instance) {
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
		check(getNode("TankItems"), null, Arrays.asList("minecraft:bucket", "minecraft:glass_bottle"), TypeTokens.LIST_STRINGS_TOKEN);
		check(getNode("LocaleJsonSerialize"), "Select the style of the used localization files. If you change the setting, you need to delete the localization files and restart the server.", true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("DefaultValues"), "Default values of the flags and selector type.", null, null);
		check(getNode("DefaultValues", "Selector"), "If you select a flat selection, all the regions you create will automatically expand in height.\nAccepted values: \"Flat\", \"Cuboid\"", SelectorTypes.FLAT.toString(), TypeTokens.STRING_TOKEN);
		check(getNode("DefaultValues", "Flags"), "Default values of the flags.", null, null);
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
		for(Flags flagName : plugin.getAPI().getFlags()) {
			check(getNode("DefaultValues", "Flags", "World", flagName.toString()), null, true, TypeTokens.BOOLEAN_TOKEN);
			check(getNode("DefaultValues", "Flags", "Claim", flagName.toString()), null, true, TypeTokens.BOOLEAN_TOKEN);
		}
	}

	public void writeDefaultWandItem() {
		check(getNode("Items", "Wand"), "An item to highlight the position.", new SerializedItemStack(ItemStack.of(ItemTypes.STONE_AXE)), TypeTokens.SERIALIZED_STACK_TOKEN);
		if(save) plugin.saveConfig();
	}

	public void createConfigsForWorlds() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			if(!plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-")).toFile().exists()) {
				plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-")).toFile().mkdir();
			}
			ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf")).build();
			try {
				CommentedConfigurationNode worldNode = configLoader.load();
				if(worldNode.node("RegionData").virtual()) {
					Region region = new Region(new UUID(0, 0), Sponge.server().worldManager().defaultWorld(), null, null, null);
					region.setRegionType(RegionTypes.GLOBAL);
					region.setName("Global#World[" + world.key() + "]", Locales.DEFAULT);
					for(Entry<Object, CommentedConfigurationNode> node : plugin.getRootNode().node("DefaultValues", "Flags", "World").childrenMap().entrySet()) {
						if(Flags.valueOfName((String) node.getKey()) != null) region.setFlag(Flags.valueOfName((String) node.getKey()), node.getValue().getBoolean());
					}
					worldNode.node("RegionData").set(Region.class, region);
					configLoader.save(worldNode);
				}
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
			if(!plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "Regions").toFile().exists()) {
				plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "Regions").toFile().mkdir();
			}
		});
	}

	public Region getWorldRegion(ServerWorld world) {
		if((plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf")).toFile().exists()) {
			ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf")).build();
			CommentedConfigurationNode worldNode;
			try {
				worldNode = configLoader.load();
				return worldNode.node("RegionData").get(Region.class);
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
		return new Region(new UUID(0, 0), world, null, null, null);
	}

	public void saveRegion(Region region) {
		ConfigurationLoader<CommentedConfigurationNode> configLoader;
				if(region.isGlobal()) {
					configLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(plugin.getConfigDir().resolve("Worlds" + File.separator + region.getServerWorldKey().asString().replace(":", "-") + File.separator + "WorldRegion.conf"))
							.build();
				} else {
					configLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(plugin.getConfigDir().resolve(
							"Worlds" + File.separator + 
							region.getServerWorldKey().asString().replace(":", "-")
							+ File.separator + "Regions"
							+ File.separator + region.getUniqueId().toString()
							+ ".conf"))
							.build();
				}
				
		CommentedConfigurationNode regionNode;
		try {
			regionNode = configLoader.load();
			regionNode.node("RegionData").set(Region.class, region);
			configLoader.save(regionNode);
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	public void deleteRegion(Region region) {
		File file = plugin.getConfigDir().resolve(
				"Worlds" + File.separator + 
				region.getServerWorldKey().asString().replace(":", "-")
				+ File.separator + "Regions"
				+ File.separator + region.getUniqueId().toString()
				+ ".conf").toFile();
		if(file.exists()) file.delete(); 
	}

	public void loadRegions() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			ConfigurationLoader<CommentedConfigurationNode> worldConfigLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf")).build();
			CommentedConfigurationNode worldNode;
			try {
				worldNode = worldConfigLoader.load();
				Region region = worldNode.node("RegionData").get(Region.class);
				plugin.getAPI().updateGlobalRegionData(world, region);
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
			if(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "Regions").toFile().exists()) {
				for(File file : plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "Regions").toFile().listFiles()) {
					if(file.getName().contains(".conf")) {
						ConfigurationLoader<CommentedConfigurationNode> regionConfigLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(file.toPath()).build();
						CommentedConfigurationNode regionNode;
						try {
							regionNode = regionConfigLoader.load();
							Region region = regionNode.node("RegionData").get(Region.class);
							setParentAfterLoad(region);
							plugin.getAPI().registerRegion(region);
						} catch (ConfigurateException e) {
							plugin.getLogger().error(e.getLocalizedMessage());
						}
					}
				}
			}
		});
	}

	private void setParentAfterLoad(Region region) {
		if(!region.containsChilds()) return;
		for(Region child : region.getChilds()) {
			child.setParrent(region);
			setParentAfterLoad(child);
		}
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

}
