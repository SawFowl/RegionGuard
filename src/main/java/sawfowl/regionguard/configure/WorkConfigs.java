package sawfowl.regionguard.configure;

import java.io.File;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;

public class WorkConfigs implements WorkData {

	private final RegionGuard plugin;
	public WorkConfigs(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Override
	public void createDataForWorlds() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			if(!plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-")).toFile().exists()) plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-")).toFile().mkdir();
			ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf")).build();
			try {
				CommentedConfigurationNode worldNode = configLoader.load();
				if(worldNode.node("RegionData").virtual()) {
					Region region = new Region(new UUID(0, 0), Sponge.server().worldManager().defaultWorld(), null, null, null);
					region.setRegionType(RegionTypes.GLOBAL);
					region.setName("Global#World[" + world.key() + "]", Locales.DEFAULT);
					region.setFlags(plugin.getDefaultFlagsConfig().getGlobalFlags());
					worldNode.node("RegionData").set(Region.class, region);
					configLoader.save(worldNode);
					plugin.getAPI().updateGlobalRegionData(world, region);
				} else {
					plugin.getAPI().updateGlobalRegionData(world, getWorldRegion(world));
				}
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
			if(!plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "Regions").toFile().exists()) {
				plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "Regions").toFile().mkdir();
			}
		});
	}

	@Override
	public Region getWorldRegion(ServerWorld world) {
		if((plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf")).toFile().exists()) {
			ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf")).build();
			CommentedConfigurationNode worldNode;
			try {
				worldNode = configLoader.load();
				Region region = getRegionFromConfig(worldNode.node("RegionData"), File.separator + world.key().asString() + " WorldRegion.conf");
				if(region == null) {
					region = new Region(new UUID(0, 0), world, null, null, null);
					region.setRegionType(RegionTypes.GLOBAL);
					region.setName("Global#World[" + world.key().asString() + "]", Locales.DEFAULT);
					region.setFlags(plugin.getDefaultFlagsConfig().getGlobalFlags());
					saveRegion(region);
				}
				return region;
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
		Region region = new Region(new UUID(0, 0), world, null, null, null);
		region.setRegionType(RegionTypes.GLOBAL);
		region.setName("Global#World[" + world.key().asString() + "]", Locales.DEFAULT);
		region.setFlags(plugin.getDefaultFlagsConfig().getGlobalFlags());
		saveRegion(region);
		return region;
	}

	@Override
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

	@Override
	public void deleteRegion(Region region) {
		File file = plugin.getConfigDir().resolve(
				"Worlds" + File.separator + 
				region.getServerWorldKey().asString().replace(":", "-")
				+ File.separator + "Regions"
				+ File.separator + region.getUniqueId().toString()
				+ ".conf").toFile();
		if(file.exists()) file.delete(); 
	}

	@Override
	public void loadRegions() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			ConfigurationLoader<CommentedConfigurationNode> worldConfigLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(plugin.getConfigDir().resolve("Worlds" + File.separator + world.key().asString().replace(":", "-") + File.separator + "WorldRegion.conf")).build();
			CommentedConfigurationNode worldNode;
			try {
				worldNode = worldConfigLoader.load();
				Region region = worldNode.node("RegionData").get(Region.class);
				if(region.getServerWorldKey() != null) plugin.getAPI().updateGlobalRegionData(world, region);
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
							Region region = getRegionFromConfig(regionNode.node("RegionData"), world.key().asString() + " " + file.getName());
							if(region != null && region.getServerWorldKey() != null) {
								setParentAfterLoad(region);
								plugin.getAPI().registerRegion(region);
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
		ConfigurationLoader<CommentedConfigurationNode> playerConfig = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(plugin.getConfigDir().resolve("PlayersData" + File.separator + player.toString() + ".conf")).build();
		try {
			CommentedConfigurationNode playerNode = playerConfig.load();
			playerNode.node("Content").set(PlayerData.class, playerData);
			playerConfig.save(playerNode);
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	
	}

	@Override
	public PlayerData getPlayerData(ServerPlayer player) {
		checkPlayersFolder();
		ConfigurationLoader<CommentedConfigurationNode> playerConfig = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(plugin.getConfigDir().resolve("PlayersData" + File.separator + player.uniqueId().toString() + ".conf")).build();
		try {
			CommentedConfigurationNode playerNode = playerConfig.load();
			if(!playerNode.node("Content").virtual()) {
				PlayerData playerData = getPlayerDataFromConfig(playerNode.node("Content"), player.uniqueId().toString());
				if(playerData == null) {
					playerData = new PlayerData(new PlayerLimits(0l, 0l, 0l, 0l), new ClaimedByPlayer(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player)));
					savePlayerData(player, playerData);
				}
				return playerData;
			}
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		return new PlayerData(new PlayerLimits(0l, 0l, 0l, 0l), new ClaimedByPlayer(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player)));
	}

	@Override
	public void loadDataOfPlayers() {
		checkPlayersFolder();
		for(File file : plugin.getConfigDir().resolve("PlayersData").toFile().listFiles()) {
			if(file.getName().contains(".conf")) {
				ConfigurationLoader<CommentedConfigurationNode> regionConfigLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getConfigurationOptions()).path(file.toPath()).build();
				try {
					CommentedConfigurationNode playerNode = regionConfigLoader.load();
					if(!playerNode.node("Content").virtual()) {
						PlayerData playerData = getPlayerDataFromConfig(playerNode.node("Content"), file.getName());
						UUID uuid = UUID.fromString(file.getName().split(".conf")[0]);
						if(playerData == null) savePlayerData(uuid, new PlayerData(new PlayerLimits(0l, 0l, 0l, 0l), new ClaimedByPlayer()));
						plugin.getAPI().setPlayerData(uuid, playerData);
					} else plugin.getAPI().setPlayerData(UUID.fromString(file.getName().split(".conf")[0]), new PlayerData(new PlayerLimits(0l, 0l, 0l, 0l), new ClaimedByPlayer()));
				} catch (ConfigurateException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
		}
		
	}

	private void checkPlayersFolder() {
		if(!plugin.getConfigDir().resolve("PlayersData").toFile().exists()) plugin.getConfigDir().resolve("PlayersData").toFile().mkdir();
	}

}
