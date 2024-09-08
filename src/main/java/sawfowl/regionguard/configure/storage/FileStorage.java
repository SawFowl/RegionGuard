package sawfowl.regionguard.configure.storage;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ValueReference;

import sawfowl.commandpack.utils.StorageType;
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
			createDataForWorlds();
			loadDataOfPlayers();
		}
	}

	@Override
	public void createDataForWorlds() {
		checkWorldsFolder();
		Sponge.server().worldManager().worlds().forEach(world -> {
			if(!plugin.getAPI().isRegisteredGlobal(world)) {
				if(!plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-")).toFile().exists()) plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-")).toFile().mkdir();
				try {
					ValueReference<Region, CommentedConfigurationNode> reference = createRegionConfig(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf"));
					if(reference.node().virtual() || reference.node().empty()) reference.setAndSave(Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags()));
					plugin.getAPI().updateGlobalRegionData(world, reference.get());
				} catch (ConfigurateException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
				if(!plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "Regions").toFile().exists()) {
					plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "Regions").toFile().mkdir();
				}
			}
		});
	}

	@Override
	public Region getWorldRegion(ServerWorld world) {
		checkWorldsFolder();
		if((plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf")).toFile().exists()) {
			try {
				ValueReference<Region, CommentedConfigurationNode> reference = createRegionConfig(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf"));
				if(reference.node().virtual() || reference.node().empty()) reference.setAndSave(Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags()));
				return reference.get();
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
		Region region = Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags());
		saveRegion(region);
		return region;
	}

	@Override
	public void saveRegion(Region region) {
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
	public void deleteRegion(Region region) {
		File file = plugin.getConfigDir().resolve(
				"Worlds" + File.separator + 
				region.getWorldKey().asString().replace(":", "-")
				+ File.separator + "Regions"
				+ File.separator + region.getUniqueId().toString()
				+ ".conf").toFile();
		if(file.exists()) file.delete(); 
	}

	@Override
	public void loadRegions() {
		checkWorldsFolder();
		Sponge.server().worldManager().worlds().forEach(world -> {
			try {
				ValueReference<Region, CommentedConfigurationNode> globalReference = createRegionConfig(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf"));
				if(globalReference.node().virtual() || globalReference.node().empty() || globalReference.get().getWorldKey() == null) globalReference.setAndSave(Region.createGlobal(world, !globalReference.node().virtual() && !globalReference.node().empty() && globalReference.get() != null && globalReference.get().getFlags() != null && !globalReference.get().getFlags().isEmpty() ? globalReference.get().getFlags() : plugin.getDefaultFlagsConfig().getGlobalFlags()));
				plugin.getAPI().updateGlobalRegionData(world, globalReference.get());
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
			if(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "Regions").toFile().exists()) {
				for(File file : plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "Regions").toFile().listFiles()) {
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
		});
	}

	@Override
	public void savePlayerData(ServerPlayer player, PlayerData playerData) {
		savePlayerData(player.uniqueId(), playerData);
	}

	@Override
	public void savePlayerData(UUID player, PlayerData playerData) {
		checkPlayersFolder();
		try {
			createPlayerDataConfig(plugin.getConfigDir().resolve("PlayersData" + File.separator + player.toString() + ".conf")).setAndSave(playerData);
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	
	}

	@Override
	public PlayerData getPlayerData(ServerPlayer player) {
		checkPlayersFolder();
		try {
			ValueReference<PlayerData, CommentedConfigurationNode> reference = createPlayerDataConfig(plugin.getConfigDir().resolve("PlayersData" + File.separator + player.uniqueId().toString() + ".conf"));
			if(reference.node().virtual() || reference.node().empty()) reference.setAndSave(PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player))));
			return reference.get();
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		return (PlayerDataImpl) PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player)));
	}

	@Override
	public void loadDataOfPlayers() {
		checkPlayersFolder();
		for(File file : plugin.getConfigDir().resolve("PlayersData").toFile().listFiles()) {
			if(file.getName().endsWith(".conf")) try {
				ValueReference<PlayerData, CommentedConfigurationNode> reference = createPlayerDataConfig(file.toPath());
				if(!reference.node().virtual() && !reference.node().empty()) {
					UUID uuid = UUID.fromString(file.getName().split(".conf")[0]);
					savePlayerData(uuid, reference.get());
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
		return SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).path(path).build().loadToReference().referenceTo(Region.class);
	}

	private ValueReference<PlayerData, CommentedConfigurationNode> createPlayerDataConfig(Path path) throws ConfigurateException {
		return SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.register(PlayerData.class, new PlayerDataSerializer()))).path(path).build().loadToReference().referenceTo(PlayerData.class);
	}

}
