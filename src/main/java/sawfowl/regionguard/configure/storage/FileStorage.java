package sawfowl.regionguard.configure.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ValueReference;

import sawfowl.commandpack.utils.StorageType;
import sawfowl.localeapi.api.serializetools.ItemStackSerializerType;
import sawfowl.localeapi.api.serializetools.SerializeOptions;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionSerializerCollection;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.WorkData;
import sawfowl.regionguard.configure.serializers.PlayerDataSerializer;
import sawfowl.regionguard.implementsapi.data.PlayerDataImpl;

public class FileStorage implements WorkData {

	private final RegionGuard plugin;
	public FileStorage(RegionGuard plugin) {
		this.plugin = plugin;
		if(!plugin.getConfig().getSplitStorage().isEnable() || plugin.getConfig().getSplitStorage().getRegions() == StorageType.FILE) {
			createGlobalRegions();
			loadAll();
		}
	}

	@Override
	public void cleanNotExistWorldsData() {
		if(!plugin.isLoaded()) return;
		Set<String> worlds = new HashSet<>();
		worlds.addAll(Sponge.server().worldManager().worlds().stream().map(world -> world.key().asString().replace(":", "-")).toList());
		worlds.addAll(Sponge.server().worldManager().offlineWorldKeys().stream().map(key -> key.asString().replace(":", "-")).toList());
		for(File file : plugin.getConfigDir().resolve("Worlds").toFile().listFiles()) {
			if(!worlds.contains(file.getName())) try {
				FileUtils.deleteDirectory(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		worlds.clear();
		worlds = null;
	}

	@Override
	public void removeAllWorldData(ResourceKey world) {
		removeFiles(plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-")).toFile());
	}

	@Override
	public void createGlobalRegionForWorld(ResourceKey world) {
		checkWorldsFolder();
		if(!plugin.getAPI().isRegisteredGlobal(world)) {
			if(!plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-")).toFile().exists()) plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-")).toFile().mkdir();
			try {
				ValueReference<Region, CommentedConfigurationNode> reference = createRegionConfig(plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "WorldRegion.conf"));
				if(reference.node().virtual() || reference.node().empty()) reference.setAndSave(Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags()));
				plugin.getAPI().updateGlobalRegionData(world, reference.get());
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
			if(!plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "Regions").toFile().exists()) {
				plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "Regions").toFile().mkdir();
			}
		}
	
	}

	@Override
	public Region getWorldRegion(ResourceKey world) {
		checkWorldsFolder();
		if((plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "WorldRegion.conf")).toFile().exists()) {
			try {
				ValueReference<Region, CommentedConfigurationNode> reference = createRegionConfig(plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "WorldRegion.conf"));
				if(reference.node().virtual() || reference.node().empty()) reference.setAndSave(Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags()));
				return reference.get();
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
		Region region = Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags());
		save(region);
		return region;
	}

	@Override
	public void save(Region region) {
		checkWorldsFolder();
		try {
			ValueReference<Region, CommentedConfigurationNode> reference = createRegionConfig(region.isGlobal() ? plugin.getConfigDir().resolve("Worlds" + File.separator + region.getWorldKey().asString().replace(":", "-") + File.separator + "WorldRegion.conf") : plugin.getConfigDir().resolve(
					"Worlds" + File.separator + 
					region.getWorldKey().asString().replace(":", "-")
					+ File.separator + "Regions"
					+ File.separator + region.getUniqueId().toString()
					+ ".conf"));
			reference.setAndSave(region);
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void delete(Region region) {
		File file = plugin.getConfigDir().resolve(
				"Worlds" + File.separator + 
				region.getWorldKey().asString().replace(":", "-")
				+ File.separator + "Regions"
				+ File.separator + region.getUniqueId().toString()
				+ ".conf").toFile();
		if(file.exists()) file.delete(); 
	}

	public void loadRegions(ResourceKey world) {
		checkWorldsFolder();
		try {
			ValueReference<Region, CommentedConfigurationNode> globalReference = createRegionConfig(plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "WorldRegion.conf"));
			if(globalReference.node().virtual() || globalReference.node().empty() || globalReference.get().getWorldKey() == null) globalReference.setAndSave(Region.createGlobal(world, !globalReference.node().virtual() && !globalReference.node().empty() && globalReference.get() != null && globalReference.get().getFlags() != null && !globalReference.get().getFlags().isEmpty() ? globalReference.get().getFlags() : plugin.getDefaultFlagsConfig().getGlobalFlags()));
			plugin.getAPI().updateGlobalRegionData(world, globalReference.get());
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		if(plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "Regions").toFile().exists()) {
			for(File file : plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "Regions").toFile().listFiles()) {
				if(file.getName().startsWith(".") && file.getName().endsWith(".tmp")) file.delete();
				if(file.getName().endsWith(".conf")) {
					try {
						ValueReference<Region, CommentedConfigurationNode> reference = createRegionConfig(file.toPath());
						if(!reference.node().virtual() && !reference.node().empty()) {
							Region region = reference.get();
							if(region != null && region.getWorldKey() != null) {
								//setParentAfterLoad(region);
								plugin.getAPI().registerRegion(region);
							}
						}
					} catch (ConfigurateException e) {
						plugin.getLogger().error(e.getLocalizedMessage());
					}
				}
			}
		}
	}

	@Override
	public void save(ServerPlayer player, PlayerData playerData) {
		save(player.uniqueId(), playerData);
	}

	@Override
	public void save(UUID player, PlayerData playerData) {
		checkPlayersFolder();
		try {
			createPlayerDataConfig(plugin.getConfigDir().resolve("PlayersData" + File.separator + player.toString() + ".conf")).setAndSave(playerData);
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	
	}

	@Override
	public PlayerData getPlayerData(UUID player) {
		checkPlayersFolder();
		try {
			ValueReference<PlayerData, CommentedConfigurationNode> reference = createPlayerDataConfig(plugin.getConfigDir().resolve("PlayersData" + File.separator + player.toString() + ".conf"));
			if(reference.node().virtual() || reference.node().empty()) reference.setAndSave(PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player))));
			return reference.get();
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		return (PlayerDataImpl) PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player)));
	}

	@Override
	public void loadAll() {
		checkPlayersFolder();
		for(File file : plugin.getConfigDir().resolve("PlayersData").toFile().listFiles()) {
			if(file.getName().endsWith(".conf")) try {
				ValueReference<PlayerData, CommentedConfigurationNode> reference = createPlayerDataConfig(file.toPath());
				if(!reference.node().virtual() && !reference.node().empty()) {
					UUID uuid = UUID.fromString(file.getName().split(".conf")[0]);
					if(reference.get() != null) plugin.getAPI().setPlayerData(uuid, reference.get());
				}
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
	}

	private void checkPlayersFolder() {
		if(!plugin.getConfigDir().resolve("PlayersData").toFile().exists()) plugin.getConfigDir().resolve("PlayersData").toFile().mkdir();
	}

	private void checkWorldsFolder() {
		if(!plugin.getConfigDir().resolve("Worlds").toFile().exists()) plugin.getConfigDir().resolve("Worlds").toFile().mkdir();
	}

	private ValueReference<Region, CommentedConfigurationNode> createRegionConfig(Path path) throws ConfigurateException {
		return SerializeOptions.createHoconConfigurationLoader(ItemStackSerializerType.JSON).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).path(path).build().loadToReference().referenceTo(Region.class);
	}

	private ValueReference<PlayerData, CommentedConfigurationNode> createPlayerDataConfig(Path path) throws ConfigurateException {
		return SerializeOptions.createHoconConfigurationLoader(ItemStackSerializerType.JSON).defaultOptions(options -> options.serializers(serializers -> serializers.register(PlayerData.class, new PlayerDataSerializer()))).path(path).build().loadToReference().referenceTo(PlayerData.class);
	}

	private void removeFiles(File file) {
		if(file.isDirectory()) for(File child : file.listFiles()) removeFiles(child);
		if(file.exists()) file.delete();
	}

}
