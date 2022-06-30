package sawfowl.regionguard.configure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.Map.Entry;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.Region;

public class WorkTables extends Thread implements WorkData {

	private final MySQL mySQL;
	private final String prefix;
	private Statement statement;
	private final UUID serverOwnerUUID = new UUID(0, 0);
	private boolean globalsCreated = false;
	private final RegionGuard plugin;
	public WorkTables(RegionGuard plugin) {
		this.plugin = plugin;
		this.mySQL = plugin.getMySQL();
		prefix = plugin.getRootNode().node("MySQL", "Prefix").getString();
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
				Region region = new Region(serverOwnerUUID, Sponge.server().worldManager().defaultWorld(), null, null, null);
				region.setRegionType(RegionTypes.GLOBAL);
				region.setName("Global#World[" + world.key() + "]", Locales.DEFAULT);
				for(Entry<Object, CommentedConfigurationNode> node : plugin.getRootNode().node("DefaultValues", "Flags", "World").childrenMap().entrySet()) {
					if(Flags.valueOfName((String) node.getKey()) != null) region.setFlag(Flags.valueOfName((String) node.getKey()), node.getValue().getBoolean());
				}
				saveRegion(region);
				plugin.getAPI().updateGlobalRegionData(world, region);
			} else {
				plugin.getAPI().updateGlobalRegionData(world, global);
			}
		});
	}

	@Override
	public Region getWorldRegion(ServerWorld world) {
		try {
			ResultSet results = resultSet("SELECT * FROM " + prefix + "worlds;");
			while(!results.isClosed() && results.next()) {
				String regionData = results.getString("region_data");
	            StringReader source = new StringReader(regionData);
				HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocales().getLocaleService().getConfigurationOptions()).source(() -> new BufferedReader(source)).build();
				ConfigurationNode node = loader.load();
				Region region = node.node("Content").get(Region.class);
				if(region.getServerWorldKey().equals(world.key())) {
					globalsCreated = true;
					return region;
				}
			}
		} catch (SQLException | ConfigurateException e) {
			plugin.getLogger().error("Get global region data. World " + world.key().asString() + "\n" + e.getLocalizedMessage());
		}
		return globalsCreated ? new Region(new UUID(0, 0), world, null, null, null) : null;
	}

	@Override
	public void saveRegion(Region region) {
		StringWriter sink = new StringWriter();
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocales().getLocaleService().getConfigurationOptions()).sink(() -> new BufferedWriter(sink)).build();
        ConfigurationNode node = loader.createNode();
        try {
			node.node("Content").set(Region.class, region);
            loader.save(node);
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
        String sql = null;
        if(region.isGlobal()) {
    		sql = "REPLACE INTO " + prefix + "worlds(world, region_data) VALUES('" + region.getServerWorldKey().toString() + "', '" + sink.toString() + "');";
        } else {
    		sql = "REPLACE INTO " + prefix + "world_" + region.getServerWorldKey().asString().replace(':', '_') + "(uuid, region_data) VALUES('" + region.getUniqueId().toString() + "', '" + sink.toString() + "');";
        }
		executeSQL(sql);
	}

	@Override
	public void deleteRegion(Region region) {
		executeSQL("DELETE FROM " + prefix + "world_" + region.getServerWorldKey().asString().replace(':', '_') + " WHERE " + prefix + "world_" + region.getServerWorldKey().asString().replace(':', '_') + ".uuid = '" + region.getUniqueId() + "';");
	}

	@Override
	public void loadRegions() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			try {
				ResultSet results = resultSet("SELECT * FROM " + prefix + "world_" + world.key().asString().replace(':', '_') + ";");
				while(!results.isClosed() && results.next()) {
					String regionData = results.getString("region_data");
		            StringReader source = new StringReader(regionData);
					HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocales().getLocaleService().getConfigurationOptions()).source(() -> new BufferedReader(source)).build();
					ConfigurationNode node = loader.load();
					Region region = node.node("Content").get(Region.class);
					setParentAfterLoad(region);
					plugin.getAPI().registerRegion(region);
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
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocales().getLocaleService().getConfigurationOptions()).sink(() -> new BufferedWriter(sink)).build();
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
	public PlayerData getPlayerData(ServerPlayer player) {
		try {
			ResultSet results = resultSet("SELECT " + player.uniqueId() + " FROM " + prefix + "player_data");
			while(!results.isClosed() && results.next()) {
				String playerData = results.getString("player_data");
	            StringReader source = new StringReader(playerData);
				HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocales().getLocaleService().getConfigurationOptions()).source(() -> new BufferedReader(source)).build();
				ConfigurationNode node = loader.load();
				return node.node("Content").get(PlayerData.class);
			}
		} catch (SQLException | ConfigurateException e) {
			plugin.getLogger().error("Get player data. Player: " + player.name() + "\n" + e.getLocalizedMessage());
		}
		return new PlayerData();
	}

	@Override
	public void loadDataOfPlayers() {
		try {
			ResultSet results = resultSet("SELECT * FROM " + prefix + "player_data");
			while(!results.isClosed() && results.next()) {
				UUID uuid = UUID.fromString(results.getString("uuid"));
				String playerData = results.getString("player_data");
	            StringReader source = new StringReader(playerData);
				HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocales().getLocaleService().getConfigurationOptions()).source(() -> new BufferedReader(source)).build();
				ConfigurationNode node = loader.load();
				plugin.getAPI().setPlayerData(uuid, node.node("Content").get(PlayerData.class));
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