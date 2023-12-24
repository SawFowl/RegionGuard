package sawfowl.regionguard.configure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import sawfowl.localeapi.api.serializetools.SerializeOptions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.data.PlayerDataImpl;
import sawfowl.regionguard.data.RegionImpl;

public class WorkTables extends Thread implements WorkData {

	private final MySQL mySQL;
	private final String prefix;
	private Statement statement;
	private boolean globalsCreated = false;
	private final RegionGuard plugin;
	public WorkTables(RegionGuard plugin) {
		this.plugin = plugin;
		this.mySQL = plugin.getMySQL();
		prefix = plugin.getConfig().getMySQLConfig().getPrefix();
		try {
			statement = mySQL.getOrOpenConnection().createStatement();
		} catch (SQLException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	public void createTableForPlayers() {
		executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "player_data(uuid VARCHAR(128) UNIQUE, player_data LONGTEXT, PRIMARY KEY(uuid));");
	}

	public void createWorldsTables() {
		for(ResourceKey worldKey : Sponge.server().worldManager().worldKeys()) {
			executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "world_" + worldKey.asString().replace(':', '_')  + "(uuid VARCHAR(128) UNIQUE, region_data LONGTEXT, PRIMARY KEY(uuid));");
			executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "worlds(world VARCHAR(128) UNIQUE, region_data LONGTEXT, PRIMARY KEY(world));");
		}
	}

	@Override
	public void createDataForWorlds() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			Region global = getWorldRegion(world);
			if(global == null) {
				Region region = Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags());
				saveRegion(region);
				plugin.getAPI().updateGlobalRegionData(world, region);
			} else {
				plugin.getAPI().updateGlobalRegionData(world, global);
			}
		});
	}

	@Override
	public RegionImpl getWorldRegion(ServerWorld world) {
		try {
			ResultSet results = resultSet("SELECT * FROM " + prefix + "worlds;");
			while(!results.isClosed() && results.next()) {
				String regionData = results.getString("region_data");
				StringReader source = new StringReader(regionData);
				HoconConfigurationLoader loader = SerializeOptions.createHoconConfigurationLoader(2).source(() -> new BufferedReader(source)).build();
				ConfigurationNode node = loader.load();
				RegionImpl region = getRegionFromConfig(node.node("Content"), results.getString("world"));
				if(region == null) {
					region = (RegionImpl) Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags());
					saveRegion(region);
				}
				if(region.getWorldKey().equals(world.key())) {
					globalsCreated = true;
					return region;
				}
			}
		} catch (SQLException | ConfigurateException e) {
			plugin.getLogger().error("Get global region data. World " + world.key().asString() + "\n" + e.getLocalizedMessage());
		}
		return globalsCreated ? (RegionImpl) Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags()) : null;
	}

	@Override
	public void saveRegion(Region region) {
		StringWriter sink = new StringWriter();
		HoconConfigurationLoader loader = SerializeOptions.createHoconConfigurationLoader(2).sink(() -> new BufferedWriter(sink)).build();
		ConfigurationNode node = loader.createNode();
		try {
			node.node("Content").set(Region.class, region);
			loader.save(node);
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		String sql = null;
		if(region.isGlobal()) {
			sql = "REPLACE INTO " + prefix + "worlds(world, region_data) VALUES('" + region.getWorldKey().toString() + "', '" + sink.toString() + "');";
		} else {
			sql = "REPLACE INTO " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + "(uuid, region_data) VALUES('" + region.getUniqueId().toString() + "', '" + sink.toString() + "');";
		}
		executeSQL(sql);
	}

	@Override
	public void deleteRegion(Region region) {
		executeSQL("DELETE FROM " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + " WHERE " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + ".uuid = '" + region.getUniqueId() + "';");
	}

	@Override
	public void loadRegions() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			try {
				ResultSet results = resultSet("SELECT * FROM " + prefix + "world_" + world.key().asString().replace(':', '_') + ";");
				while(!results.isClosed() && results.next()) {
					String regionData = results.getString("region_data");
					StringReader source = new StringReader(regionData);
					HoconConfigurationLoader loader = SerializeOptions.createHoconConfigurationLoader(2).source(() -> new BufferedReader(source)).build();
					ConfigurationNode node = loader.load();
					Region region = getRegionFromConfig(node.node("Content"), world.key().asString() + " " + results.getString("uuid"));
					if(region != null && region.getWorldKey() != null) {
						setParentAfterLoad(region);
						plugin.getAPI().registerRegion(region);
					}
				}
			} catch (SQLException | ConfigurateException e) {
				plugin.getLogger().error("Load region data\n" + e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void savePlayerData(ServerPlayer player, PlayerData playerData) {
		savePlayerData(player.uniqueId(), playerData);
	}

	@Override
	public void savePlayerData(UUID player, PlayerData playerData) {
		StringWriter sink = new StringWriter();
		HoconConfigurationLoader loader = SerializeOptions.createHoconConfigurationLoader(2).sink(() -> new BufferedWriter(sink)).build();
		ConfigurationNode node = loader.createNode();
		try {
			node.node("Content").set(PlayerData.class, playerData);
			loader.save(node);
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		String sql = "REPLACE INTO " + prefix + "player_data(uuid, player_data) VALUES('" + player.toString() + "', '" + sink.toString() + "');";
		executeSQL(sql);
	}

	@Override
	public PlayerDataImpl getPlayerData(ServerPlayer player) {
		try {
			ResultSet results = resultSet("SELECT " + player.uniqueId() + " FROM " + prefix + "player_data");
			while(!results.isClosed() && results.next()) {
				String playerData = results.getString("player_data");
				StringReader source = new StringReader(playerData);
				HoconConfigurationLoader loader = SerializeOptions.createHoconConfigurationLoader(2).source(() -> new BufferedReader(source)).build();
				ConfigurationNode node = loader.load();
				if(!node.node("Content").virtual()) {
					PlayerData data = getPlayerDataFromConfig(node.node("Content"), player.uniqueId().toString());
					if(data == null) {
						data = PlayerData.of(PlayerLimits.zero(), ClaimedByPlayer.of(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player)));
						savePlayerData(player, data);
					}
					return (PlayerDataImpl) data;
				}
			}
		} catch (SQLException | ConfigurateException e) {
			plugin.getLogger().error("Get player data. Player: " + player.name() + "\n" + e.getLocalizedMessage());
		}
		return (PlayerDataImpl) PlayerData.zero();
	}

	@Override
	public void loadDataOfPlayers() {
		try {
			ResultSet results = resultSet("SELECT * FROM " + prefix + "player_data");
			while(!results.isClosed() && results.next()) {
				UUID uuid = UUID.fromString(results.getString("uuid"));
				String playerData = results.getString("player_data");
				StringReader source = new StringReader(playerData);
				HoconConfigurationLoader loader = SerializeOptions.createHoconConfigurationLoader(2).source(() -> new BufferedReader(source)).build();
				ConfigurationNode node = loader.load();
				if(!node.node("Content").virtual()) {
					PlayerData data = getPlayerDataFromConfig(node.node("Content"), uuid.toString());
					if(playerData == null) savePlayerData(uuid, PlayerData.zero());
					plugin.getAPI().setPlayerData(uuid, data);
				} else plugin.getAPI().setPlayerData(uuid, PlayerData.zero());
			}
		} catch (SQLException | ConfigurateException e) {
			plugin.getLogger().error("Get players data.\n" + e.getLocalizedMessage());
		}
	}

	private ResultSet resultSet(String sql) throws SQLException {
		if(statement == null || statement.isClosed()) statement = plugin.getMySQL().getOrOpenConnection().createStatement();
		return statement.executeQuery(sql);
	}

	private void executeSQL(String sql) {
		try {
			if(statement == null || statement.isClosed()) statement = plugin.getMySQL().getOrOpenConnection().createStatement();
			statement.execute(sql);
		}
		catch (SQLException e) {
			plugin.getLogger().error("Error during SQL query execution '" + sql + "'...\n" + e.getLocalizedMessage());
		}
	}

}