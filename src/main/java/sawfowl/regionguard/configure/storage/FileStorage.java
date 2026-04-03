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

import sawfowl.commandpack.utils.StorageType;
import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.api.config.ReferencedConfig;
import sawfowl.localeapi.api.serializetools.ItemStackSerializerType;
import sawfowl.localeapi.api.services.ConfigurationService;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.TestData;
import sawfowl.regionguard.api.RegionSerializerCollection;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.WorkData;

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
			var reference = createRegionConfig(plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-")), "WorldRegion");
			if(reference.getRootNode().virtual() || reference.getRootNode().empty()) reference.save(Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags()));
			plugin.getAPI().updateGlobalRegionData(world, reference.get());
			if(!plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "Regions").toFile().exists()) {
				plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "Regions").toFile().mkdir();
			}
		}
	
	}

	@Override
	public Region getWorldRegion(ResourceKey world) {
		checkWorldsFolder();
		if((plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "WorldRegion.conf")).toFile().exists()) {
			var reference = createRegionConfig(plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-")), "WorldRegion");
			if(reference.getRootNode().virtual() || reference.getRootNode().empty()) reference.save(Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags()));
			return reference.get();
		}
		Region region = Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags());
		save(region);
		return region;
	}

	@Override
	public void save(Region region) {
		checkWorldsFolder();
		var reference = createRegionConfig(region.isGlobal() ? plugin.getConfigDir().resolve("Worlds" + File.separator + region.getWorldKey().asString().replace(":", "-") + File.separator + "WorldRegion.conf") : plugin.getConfigDir().resolve(
				"Worlds" + File.separator + 
				region.getWorldKey().asString().replace(":", "-")
				+ File.separator + "Regions"), region.getUniqueId().toString());
		reference.save(region);
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
		var globalReference = createRegionConfig(plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-")), "WorldRegion");
		if(globalReference.getRootNode().virtual() || globalReference.getRootNode().empty() || globalReference.get().getWorldKey() == null) globalReference.save(Region.createGlobal(world, !globalReference.getRootNode().virtual() && !globalReference.getRootNode().empty() && globalReference.get() != null && globalReference.get().getFlags() != null && !globalReference.get().getFlags().isEmpty() ? globalReference.get().getFlags() : plugin.getDefaultFlagsConfig().getGlobalFlags()));
		plugin.getAPI().updateGlobalRegionData(world, globalReference.get());
		if(plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "Regions").toFile().exists()) {
			for(File file : plugin.getConfigDir().resolve("Worlds" + File.separator + world.asString().replace(":", "-") + File.separator + "Regions").toFile().listFiles()) {
				if(file.getName().startsWith(".") && file.getName().endsWith(".tmp")) file.delete();
				if(ConfigTypes.isValidExtension(ConfigTypes.getExtension(file.getName()))) {
					var reference = createRegionConfig(file);
					if(!reference.getRootNode().virtual() && !reference.getRootNode().empty()) {
						Region region = reference.get();
						if(region != null && region.getWorldKey() != null) {
							//setParentAfterLoad(region);
							plugin.getAPI().registerRegion(region);
						}
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
		createPlayerDataConfig(plugin.getConfigDir().resolve("PlayersData"), player.toString()).save(playerData);
	
	}

	@Override
	public PlayerData getPlayerData(UUID player) {
		checkPlayersFolder();
		var reference = createPlayerDataConfig(plugin.getConfigDir().resolve("PlayersData"), player.toString());
		if(reference.getRootNode().virtual() || reference.getRootNode().empty()) reference.save(PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player))));
		var value = reference.get();
		return value != null ? value : PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player)));
	}

	@Override
	public void loadAll() {
		checkPlayersFolder();
		for(File file : plugin.getConfigDir().resolve("PlayersData").toFile().listFiles()) {
			if(file.getName().startsWith(".") && file.getName().endsWith(".tmp")) file.delete();
			if(ConfigTypes.isValidExtension(ConfigTypes.getExtension(file.getName()))) {
				var reference = createPlayerDataConfig(file);
				if(!reference.getRootNode().virtual() && !reference.getRootNode().empty()) {
					UUID uuid = UUID.fromString(file.getName().replace(ConfigTypes.find(ConfigTypes.getExtension(file.getName())).toString(), ""));
					if(reference.get() != null) plugin.getAPI().setPlayerData(uuid, reference.get());
				}
			}
		}
	}

	private void checkPlayersFolder() {
		if(!plugin.getConfigDir().resolve("PlayersData").toFile().exists()) plugin.getConfigDir().resolve("PlayersData").toFile().mkdir();
	}

	private void checkWorldsFolder() {
		if(!plugin.getConfigDir().resolve("Worlds").toFile().exists()) plugin.getConfigDir().resolve("Worlds").toFile().mkdir();
	}

	private ReferencedConfig<Region> createRegionConfig(Path path, String region) {
		return ConfigurationService.getInstance().createReferencedConfig(Region.class).setItemStackSerializerType(ItemStackSerializerType.JSON).setPath(path).setName(region).setType(ConfigTypes.HOCON).addSerializers(RegionSerializerCollection.COLLETCTION).build();
	}

	private ReferencedConfig<Region> createRegionConfig(File file) {
		return ConfigurationService.getInstance().createReferencedConfig(Region.class).setItemStackSerializerType(ItemStackSerializerType.JSON).fromFile(file).addSerializers(RegionSerializerCollection.COLLETCTION).build();
	}

	private ReferencedConfig<PlayerData> createPlayerDataConfig(Path path, String player) {
		return ConfigurationService.getInstance().createReferencedConfig(PlayerData.class).setItemStackSerializerType(ItemStackSerializerType.JSON).setPath(path).setName(player).setType(ConfigTypes.HOCON).addSerializers(RegionSerializerCollection.COLLETCTION).build();
	}

	private ReferencedConfig<PlayerData> createPlayerDataConfig(File file) {
		return ConfigurationService.getInstance().createReferencedConfig(PlayerData.class).setItemStackSerializerType(ItemStackSerializerType.JSON).fromFile(file).setType(ConfigTypes.HOCON).addSerializers(RegionSerializerCollection.COLLETCTION).build();
	}

	private void removeFiles(File file) {
		if(file.isDirectory()) for(File child : file.listFiles()) removeFiles(child);
		if(file.exists()) file.delete();
	}

}
