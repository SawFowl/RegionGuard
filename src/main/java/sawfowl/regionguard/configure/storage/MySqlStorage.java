package sawfowl.regionguard.configure.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.math.vector.Vector3i;

import io.leangen.geantyref.TypeToken;

import net.kyori.adventure.text.Component;

import sawfowl.commandpack.utils.StorageType;
import sawfowl.localeapi.api.serializetools.SerializeOptions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionSerializerCollection;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.api.data.AdditionalData;
import sawfowl.regionguard.api.data.AdditionalDataMap;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.MySQL;
import sawfowl.regionguard.data.PlayerDataImpl;
import sawfowl.regionguard.data.RegionImpl;

public class MySqlStorage extends AbstractSqlStorage {

	private final MySQL mySQL;
	private Statement statement;
	private ScheduledTask sync;
	private final TypeToken<Map<String, Component>> mapComponentsToken = new TypeToken<Map<String, Component>>(){};
	private final TypeToken<Map<String, Set<FlagValue>>> flagsToken = new TypeToken<Map<String, Set<FlagValue>>>(){};
	private final TypeToken<List<MemberData>> membersToken = new TypeToken<List<MemberData>>(){};
	private final TypeToken<AdditionalDataMap<? extends AdditionalData>> dataMapToken = new TypeToken<AdditionalDataMap<?>>(){};
	private String lastGlobalSync;
	public MySqlStorage(RegionGuard plugin) {
		super(plugin);
		this.mySQL = plugin.getMySQL();
		try {
			statement = mySQL.getOrOpenConnection().createStatement();
		} catch (SQLException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		if(!plugin.getConfig().getSplitStorage().isEnable() || plugin.getConfig().getSplitStorage().getPlayers() == StorageType.MYSQL) createTableForPlayers();
		if(!plugin.getConfig().getSplitStorage().isEnable() || plugin.getConfig().getSplitStorage().getRegions() == StorageType.MYSQL) {
			createWorldsTables();
			createDataForWorlds();
		}
		sync = sync();
		Sponge.eventManager().registerListeners(plugin.getPluginContainer(), this);
	}

	@Override
	protected Statement getStatement() throws SQLException {
		return statement == null || statement.isClosed() ? statement = plugin.getMySQL().getOrOpenConnection().createStatement() : statement;
	}

	public void createTableForPlayers() {
		executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "player_data(uuid VARCHAR(128) UNIQUE, claimed_blocks BIGINT, claimed_regions BIGINT, limit_blocks BIGINT, limit_regions BIGINT, limit_subdivisions BIGINT, limit_members BIGINT, written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(uuid));");
	}

	public void createWorldsTables() {
		executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "worlds(uuid VARCHAR(128) UNIQUE, world VARCHAR(128) UNIQUE, name LONGTEXT, region_type TEXT, creation_time BIGINT, join_message LONGTEXT, exit_message LONGTEXT, flags LONGTEXT, additional_data LONGTEXT, written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(world));");
		for(ResourceKey worldKey : Sponge.server().worldManager().worldKeys()) {
			executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "world_" + worldKey.asString().replace(':', '_')  + "(uuid VARCHAR(128) UNIQUE, name LONGTEXT, region_type TEXT, creation_time BIGINT, join_message LONGTEXT, exit_message LONGTEXT, flags LONGTEXT, members LONGTEXT, min_x BIGINT, min_y BIGINT, min_z BIGINT, max_x BIGINT, max_y BIGINT, max_z BIGINT, selector_type TEXT, additional_data LONGTEXT, parrent VARCHAR(128), written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(uuid));");
		}
	}

	@Override
	public void createDataForWorlds() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			Region global = getWorldRegion(world);
			if(global == null) {
				global = Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags());
				saveRegion(global);
				plugin.getAPI().updateGlobalRegionData(world, global);
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
				if(world.key().asString().equals(results.getString("world"))) {
					lastGlobalSync = results.getString("written");
					return getGlobalRegionfromResultSet(results, world);
				}
			}
		} catch (SQLException | ConfigurateException e) {
			plugin.getLogger().error("Get global region data. World " + world.key().asString() + "\n" + e.getLocalizedMessage());
		}
		return globalsCreated ? (RegionImpl) Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags()) : null;
	}

	@Override
	public void saveRegion(Region region) {
		String sql = null;
		if(region.isGlobal()) {
			sql = "REPLACE INTO " + prefix + "worlds(uuid, world, name, region_type, creation_time, join_message, exit_message, flags, additional_data) VALUES('"
			+ region.getUniqueId().toString() + "', '"
			+ region.getWorldKey().asString() + "', '"
			+ getSerializedData(region.getNames(), mapComponentsToken) + "', '"
			+ region.getType().toString() + "', '"
			+ region.getCreationTime() + "', '"
			+ getSerializedData(region.getJoinMessages(), mapComponentsToken) + "', '"
			+ getSerializedData(region.getExitMessages(), mapComponentsToken) + "', '"
			+ getSerializedData(region.getFlags(), flagsToken) + "', '"
			+ getSerializedData(region.getAllAdditionalData(), dataMapToken) + "', '"
			+ region.getWorldKey().asString()
			+ "');";
		} else {
			sql = "REPLACE INTO " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + "(uuid, name, region_type, creation_time, join_message, exit_message, flags, members, min_x, min_y, min_z, max_x, max_y, max_z, selector_type, additional_data, parrent) VALUES('"
			+ region.getUniqueId().toString() + "', '"
			+ getSerializedData(region.getNames(), mapComponentsToken) + "', '"
			+ region.getType().toString() + "', '"
			+ region.getCreationTime() + "', '"
			+ getSerializedData(region.getJoinMessages(), mapComponentsToken) + "', '"
			+ getSerializedData(region.getExitMessages(), mapComponentsToken) + "', '"
			+ getSerializedData(region.getFlags(), flagsToken) + "', '"
			+ getSerializedData(region.getMembers(), membersToken) + "', '"
			+ region.getCuboid().getMin().x() + "', '"
			+ region.getCuboid().getMin().y() + "', '"
			+ region.getCuboid().getMin().z() + "', '"
			+ region.getCuboid().getMax().x() + "', '"
			+ region.getCuboid().getMax().y() + "', '"
			+ region.getCuboid().getMax().z() + "', '"
			+ getSerializedData(region.getAllAdditionalData(), dataMapToken) + "', '"
			+ region.getParrent().map(rg -> rg.getUniqueId().toString()).orElse(null)
			+ "');";
		}
		executeSQL(sql);
		if(!region.getChilds().isEmpty()) region.getChilds().forEach(this::saveRegion);
	}

	@Override
	public void deleteRegion(Region region) {
		executeSQL("DELETE FROM " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + " WHERE " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + ".uuid = '" + region.getUniqueId().toString() + "';");
		if(!region.getChilds().isEmpty()) region.getChilds().forEach(this::deleteRegion);
	}

	@Override
	public void loadRegions() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			Map<UUID, Set<Region>> childs = new HashMap<>();
			try {
				ResultSet results = resultSet("SELECT * FROM " + prefix + "world_" + world.key().asString().replace(':', '_') + ";");
				while(!results.isClosed() && results.next()) {
					UUID uuid = UUID.fromString(results.getString("uuid"));
					UUID parrent = UUID.fromString(results.getString("parrent"));
					Region region = getRegionfromResultSet(results, world);
					if(parrent != null) {
						if(plugin.getAPI().getRegions().stream().filter(rg -> rg.getUniqueId().equals(parrent)).findFirst().isPresent()) {
							plugin.getAPI().getRegions().stream().filter(rg -> rg.getUniqueId().equals(parrent)).findFirst().get().addChild(region);
						} else {
							if(!childs.containsKey(parrent)) childs.put(parrent, new HashSet<Region>());
							childs.get(parrent).add(region);
						}
					} else {
						if(childs.containsKey(uuid)) childs.get(uuid).forEach(child -> region.addChild(region));
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
		String sql = "REPLACE INTO " + prefix + "player_data(uuid, claimed_blocks, claimed_regions, limit_blocks, limit_regions, limit_subdivisions, limit_members) VALUES("
				+ "'" + player.toString() + "', '"
				+ playerData.getClaimed().getBlocks() + "', '"
				+ playerData.getClaimed().getRegions() + "', '"
				+ playerData.getLimits().getBlocks() + "', '"
				+ playerData.getLimits().getRegions() + "', '"
				+ playerData.getLimits().getSubdivisions() + "', '"
				+ playerData.getLimits().getMembers() + "');";
		executeSQL(sql);
	}

	@Override
	public PlayerData getPlayerData(ServerPlayer player) {
		try {
			ResultSet results = resultSet("SELECT " + player.uniqueId() + " FROM " + prefix + "player_data");
			if(!results.isClosed() && results.next()) return getPlayerDataFromResultSet(results);
		} catch (SQLException e) {
			plugin.getLogger().error("Get player data. Player: " + player.name() + "\n" + e.getLocalizedMessage());
		}
		return (PlayerDataImpl) PlayerData.zero();
	}

	@Override
	public void loadDataOfPlayers() {
		try {
			ResultSet results = resultSet("SELECT * FROM " + prefix + "player_data");
			while(!results.isClosed() && results.next()) {
				plugin.getAPI().setPlayerData(UUID.fromString(results.getString("uuid")), getPlayerDataFromResultSet(results));
			}
		} catch (SQLException e) {
			plugin.getLogger().error("Get players data.\n" + e.getLocalizedMessage());
		}
	}

	@Listener
	public void onRefresh(RefreshGameEvent event) {
		if(!sync.isCancelled()) sync.cancel();
		sync = sync();
	}

	private ScheduledTask sync() {
		return Sponge.asyncScheduler().submit(Task.builder().plugin(plugin.getPluginContainer()).interval(plugin.getConfig().getMySQLConfig().getSyncInterval(), TimeUnit.SECONDS).execute(() -> {
			for(ServerWorld world : Sponge.server().worldManager().worlds()) {
				try {
					syncLoad(world);
				} catch (SQLException | ConfigurateException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
		}).build());
	}

	//private void syncLoad() {
		
	//}

	private void syncLoad(ServerWorld world) throws SQLException, ConfigurateException {
		ResultSet globalSet = resultSet(this.lastGlobalSync == null ? "SELECT * FROM " + prefix + "worlds ORDER BY written DESC LIMIT 1;" : "SELECT * FROM " + prefix + "worlds WHERE written > '" + this.lastGlobalSync + "' ORDER BY written;");
		boolean updateTimeGlobal = false;
		while(!globalSet.isClosed() && globalSet.next()) {
			String key = globalSet.getString("world");
			if(key.equals(world.key().asString())) {
				if(lastGlobalSync == null || !updateTimeGlobal) {
					lastGlobalSync = globalSet.getString("written");
					updateTimeGlobal = true;
				}
				plugin.getAPI().updateGlobalRegionData(world, getGlobalRegionfromResultSet(globalSet, world));
			}
		}
	}

	private <T> ValueReference<T, CommentedConfigurationNode> createTempConfigReader(String string, TypeToken<T> token) throws ConfigurateException {
		return SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).source(() -> new BufferedReader(new StringReader(string))).build().loadToReference().referenceTo(token);
	}

	private <T> String getSerializedData(T object, TypeToken<T> token) {
		StringWriter sink = new StringWriter();
		try {
			SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).sink(() -> new BufferedWriter(sink)).build().loadToReference().referenceTo(token).setAndSave(object);
			return sink.toString();
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ClaimedByPlayer getClaimedByPlayer(ResultSet results) throws SQLException {
		return ClaimedByPlayer.of(results.getLong("claimed_blocks"), results.getLong("claimed_regions"));
	}

	private PlayerLimits getPlayerLimits(ResultSet results) throws SQLException {
		return PlayerLimits.of(results.getLong("limit_blocks"), results.getLong("limit_regions"), results.getLong("limit_subdivisions"), results.getLong("limit_members"));
	}

	private PlayerData getPlayerDataFromResultSet(ResultSet results) throws SQLException {
		return PlayerData.of(getPlayerLimits(results), getClaimedByPlayer(results));
	}

	private Region getRegionfromResultSet(ResultSet results, ServerWorld world) throws SQLException, ConfigurateException {
		UUID uuid = UUID.fromString(results.getString("uuid"));
		String mames = results.getString("names");
		String joinMessage = results.getString("join_message");
		String exitMessage = results.getString("exit_message");
		String flags = results.getString("flags");
		String members = results.getString("members");
		String additionalData = results.getString("additional_data");
		return Region.builder()
				.setUniqueId(uuid)
				.addNames(mames != null && !mames.isEmpty() ? createTempConfigReader(mames, mapComponentsToken).get() : null)
				.setType(RegionTypes.valueOfName(results.getString("region_type")))
				.setCreationTime(results.getLong("creation_time"))
				.setWorld(world)
				.addJoinMessages(joinMessage != null && !joinMessage.isEmpty() ? createTempConfigReader(joinMessage, mapComponentsToken).get() : null)
				.addExitMessages(exitMessage != null && !exitMessage.isEmpty() ? createTempConfigReader(exitMessage, mapComponentsToken).get() : null)
				.setFlags(flags != null && !flags.isEmpty() ? createTempConfigReader(flags, flagsToken).get() : null)
				.addMembers(members != null && !members.isEmpty() ? createTempConfigReader(members, membersToken).get() : null)
				.setCuboid(Cuboid.of(SelectorTypes.checkType(results.getString("selector_type")), Vector3i.from(results.getInt("min_x"), results.getInt("min_y"), results.getInt("min_z")), Vector3i.from(results.getInt("max_x"), results.getInt("max_y"), results.getInt("max_z"))))
				.addAdditionalData(additionalData != null && !additionalData.isEmpty() ? createTempConfigReader(additionalData, dataMapToken).get() : null)
				.setServerOwner()
				.build();
	}

	private Region getGlobalRegionfromResultSet(ResultSet results, ServerWorld world) throws SQLException, ConfigurateException {
		String mames = results.getString("names");
		String joinMessage = results.getString("join_message");
		String exitMessage = results.getString("exit_message");
		String flags = results.getString("flags");
		String additionalData = results.getString("additional_data");
		return Region.builder()
				.setUniqueId(UUID.fromString(results.getString("uuid")))
				.setCreationTime(results.getLong("creation_time"))
				.setWorld(world)
				.addNames(mames != null && !mames.isEmpty() ? createTempConfigReader(mames, mapComponentsToken).get() : null)
				.addJoinMessages(joinMessage != null && !joinMessage.isEmpty() ? createTempConfigReader(joinMessage, mapComponentsToken).get() : null)
				.addExitMessages(exitMessage != null && !exitMessage.isEmpty() ? createTempConfigReader(exitMessage, mapComponentsToken).get() : null)
				.setFlags(flags != null && !flags.isEmpty() ? createTempConfigReader(flags, flagsToken).get() : null)
				.addAdditionalData(additionalData != null && !additionalData.isEmpty() ? createTempConfigReader(additionalData, dataMapToken).get() : null)
				.setServerOwner()
				.setType(RegionTypes.GLOBAL)
				.build();
	}

}
