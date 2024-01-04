package sawfowl.regionguard.configure.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.commandpack.utils.StorageType;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.MySQL;

public class MySqlStorage extends AbstractSqlStorage {

	private final MySQL mySQL;
	private Statement statement;
	private ScheduledTask sync;
	private String lastGlobalSync;
	private String lastRegionSync;
	private String lastPlayerSync;
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
		sync = syncTask();
	}

	@Override
	protected Statement getStatement() throws SQLException {
		return statement == null || statement.isClosed() ? statement = plugin.getMySQL().getOrOpenConnection().createStatement() : statement;
	}

	@Override
	public Region getWorldRegion(ServerWorld world) {
		try {
			ResultSet results = resultSet("SELECT * FROM " + prefix + "worlds;");
			while(!results.isClosed() && results.next()) {
				if(world.key().asString().equals(results.getString("world"))) {
					if(lastGlobalSync == null) lastGlobalSync = results.getString("written");
					return getGlobalRegionfromResultSet(results, world);
				}
			}
		} catch (SQLException | ConfigurateException e) {
			plugin.getLogger().error("Get global region data. World " + world.key().asString() + "\n" + e.getLocalizedMessage());
		}
		Region region = Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags());
		saveRegion(region);
		return region;
	}

	@Override
	public void saveRegion(Region region) {
		String sql = null;
		if(region.isGlobal()) {
			sql = "REPLACE INTO " + prefix + "worlds(uuid, world, name, creation_time, join_message, exit_message, flags, members, additional_data) VALUES('"
			+ region.getUniqueId().toString() + "', '"
			+ region.getWorldKey().asString() + "', '"
			+ getSerializedData(region.getNames(), mapComponentsToken) + "', '"
			+ region.getCreationTime() + "', '"
			+ getSerializedData(region.getJoinMessages(), mapComponentsToken) + "', '"
			+ getSerializedData(region.getExitMessages(), mapComponentsToken) + "', '"
			+ getSerializedData(region.getFlags(), flagsToken) + "', '"
			+ getSerializedData(convertMembersToMap(region.getMembers()), membersToken) + "', '"
			+ getSerializedData(region.getAllAdditionalData(), dataMapToken) + "');";
		} else {
			sql = "REPLACE INTO " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + "(uuid, name, region_type, creation_time, join_message, exit_message, flags, members, min_x, min_y, min_z, max_x, max_y, max_z, selector_type, additional_data, parrent) VALUES('"
			+ region.getUniqueId().toString() + "', '"
			+ getSerializedData(region.getNames(), mapComponentsToken) + "', '"
			+ region.getType().toString() + "', '"
			+ region.getCreationTime() + "', '"
			+ getSerializedData(region.getJoinMessages(), mapComponentsToken) + "', '"
			+ getSerializedData(region.getExitMessages(), mapComponentsToken) + "', '"
			+ getSerializedData(region.getFlags(), flagsToken) + "', '"
			+ getSerializedData(convertMembersToMap(region.getMembers()), membersToken) + "', '"
			+ region.getCuboid().getMin().x() + "', '"
			+ region.getCuboid().getMin().y() + "', '"
			+ region.getCuboid().getMin().z() + "', '"
			+ region.getCuboid().getMax().x() + "', '"
			+ region.getCuboid().getMax().y() + "', '"
			+ region.getCuboid().getMax().z() + "', '"
			+ region.getCuboid().getSelectorType().toString() + "', '"
			+ getSerializedData(region.getAllAdditionalData(), dataMapToken) + "', '"
			+ region.getParrent().map(rg -> rg.getUniqueId().toString()).orElse(null) + "');";
		}
		executeSQL(sql);
		if(!region.getChilds().isEmpty()) region.getChilds().forEach(this::saveRegion);
	}

	@Override
	public void deleteRegion(Region region) {
		if(region.isGlobal()) return;
		executeSQL("DELETE FROM " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + " WHERE " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + ".uuid = '" + region.getUniqueId().toString() + "';");
		if(!region.getChilds().isEmpty()) region.getChilds().forEach(this::deleteRegion);
	}

	@Override
	public void loadRegions() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			Map<UUID, Set<Region>> childs = new HashMap<>();
			try {
				ResultSet results = resultSet("SELECT * FROM " + prefix + "world_" + world.key().asString().replace(':', '_') + " ORDER BY written;");
				while(!results.isClosed() && results.next()) {
					if(lastRegionSync == null) lastRegionSync =  results.getString("written");
					Region region = getRegionfromResultSet(results, world);
					UUID uuid = region.getUniqueId();
					String parrent = results.getString("parrent");
					if(parrent != null && !parrent.equalsIgnoreCase("null")) {
						if(plugin.getAPI().getRegions().stream().filter(rg -> rg.getUniqueId().toString().equals(parrent)).findFirst().isPresent()) {
							plugin.getAPI().getRegions().stream().filter(rg -> rg.getUniqueId().toString().equals(parrent)).findFirst().get().addChild(region);
						} else {
							if(!childs.containsKey(UUID.fromString(parrent))) childs.put(UUID.fromString(parrent), new HashSet<Region>());
							childs.get(UUID.fromString(parrent)).add(region);
						}
					} else {
						if(childs.containsKey(uuid)) childs.get(uuid).forEach(child -> region.addChild(region));
						plugin.getAPI().registerRegion(region);
					}
				}
				plugin.getAPI().updateGlobalRegionData(world, getWorldRegion(world));
			} catch (SQLException | ConfigurateException e) {
				plugin.getLogger().error("Load region data\n" + e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void savePlayerData(UUID player, PlayerData playerData) {
		if(player == null || playerData == null) return;
		String sql = "REPLACE INTO " + prefix + "player_data(uuid, claimed_blocks, claimed_regions, limit_blocks, limit_claims, limit_subdivisions, limit_members) VALUES("
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
		return PlayerData.zero();
	}

	@Override
	public void loadDataOfPlayers() {
		try {
			ResultSet results = resultSet("SELECT * FROM " + prefix + "player_data ORDER BY written");
			while(!results.isClosed() && results.next()) {
				if(this.lastPlayerSync == null) this.lastPlayerSync = results.getString("written");
				plugin.getAPI().setPlayerData(UUID.fromString(results.getString("uuid")), getPlayerDataFromResultSet(results));
			}
		} catch (SQLException e) {
			plugin.getLogger().error("Get players data.\n" + e.getLocalizedMessage());
		}
	}

	public void updateSync() {
		if(sync != null && !sync.isCancelled()) sync.cancel();
		sync = syncTask();
	}

	private void createTableForPlayers() {
		executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "player_data(uuid VARCHAR(128) UNIQUE, claimed_blocks BIGINT, claimed_regions BIGINT, limit_blocks BIGINT, limit_claims BIGINT, limit_subdivisions BIGINT, limit_members BIGINT, written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(uuid));");
	}

	private void createWorldsTables() {
		executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "worlds(uuid VARCHAR(128) UNIQUE, world VARCHAR(128) UNIQUE, name LONGTEXT, creation_time BIGINT, join_message LONGTEXT, exit_message LONGTEXT, flags LONGTEXT, members LONGTEXT, additional_data LONGTEXT, written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(world));");
		for(ResourceKey worldKey : Sponge.server().worldManager().worldKeys()) {
			executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "world_" + worldKey.asString().replace(':', '_')  + "(uuid VARCHAR(128) UNIQUE, name LONGTEXT, region_type TEXT, creation_time BIGINT, join_message LONGTEXT, exit_message LONGTEXT, flags LONGTEXT, members LONGTEXT, min_x BIGINT, min_y BIGINT, min_z BIGINT, max_x BIGINT, max_y BIGINT, max_z BIGINT, selector_type TEXT, additional_data LONGTEXT, parrent VARCHAR(128), written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(uuid));");
		}
	}

	private ScheduledTask syncTask() {
		if(plugin.getConfig().getMySQLConfig().getSyncInterval() < 1) return null;
		return Sponge.asyncScheduler().submit(Task.builder().plugin(plugin.getPluginContainer()).interval(plugin.getConfig().getMySQLConfig().getSyncInterval(), TimeUnit.SECONDS).execute(() -> {
			for(ServerWorld world : Sponge.server().worldManager().worlds()) {
				try {
					syncGlobals(world);
					syncClaims(world);
					syncPlayers();
				} catch (SQLException | ConfigurateException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
		}).build());
	}

	private void syncPlayers() throws SQLException {
		if(this.lastPlayerSync != null) {
			ResultSet playersSet = resultSet("SELECT * FROM " + prefix + "player_data WHERE written > '" + this.lastPlayerSync + "' ORDER BY written;");
			boolean updateTimePlayers = false;
			while(!playersSet.isClosed() && playersSet.next()) {
				if(!updateTimePlayers) {
					lastPlayerSync = playersSet.getString("written");
					updateTimePlayers = true;
				}
				UUID uuid = UUID.fromString(playersSet.getString("uuid"));
				Optional<PlayerData> optData = plugin.getAPI().getPlayerData(uuid);
				if(optData.isPresent()) {
					optData.get().setLimits(getPlayerLimits(playersSet)).setClaimed(getClaimedByPlayer(playersSet));
				} else plugin.getAPI().setPlayerData(uuid, getPlayerDataFromResultSet(playersSet));
			}
		} else loadDataOfPlayers();
	}

	private void syncGlobals(ServerWorld world) throws SQLException, ConfigurateException {
		if(this.lastGlobalSync != null) {
			ResultSet globalSet = resultSet("SELECT * FROM " + prefix + "worlds WHERE written > '" + this.lastGlobalSync + "' ORDER BY written;");
			boolean updateTimeGlobal = false;
			while(!globalSet.isClosed() && globalSet.next()) {
				String key = globalSet.getString("world");
				if(key.equals(world.key().asString())) {
					if(!updateTimeGlobal) {
						lastGlobalSync = globalSet.getString("written");
						updateTimeGlobal = true;
					}
					plugin.getAPI().updateGlobalRegionData(world, getGlobalRegionfromResultSet(globalSet, world));
				}
			}
		} else plugin.getAPI().updateGlobalRegionData(world, getWorldRegion(world));
	}

	private void syncClaims(ServerWorld world) throws SQLException, ConfigurateException {
		ResultSet regionsSet = resultSet("SELECT * FROM " + prefix + "world_" + world.key().asString().replace(':', '_') + (this.lastRegionSync != null ? " WHERE written > '" + this.lastRegionSync + "'" : "") + " ORDER BY written;");
		boolean updateTimeRegion = false;
		while(!regionsSet.isClosed() && regionsSet.next()) {
			if(!updateTimeRegion) {
				lastRegionSync = regionsSet.getString("written");
				updateTimeRegion = true;
			}
			Region region = getRegionfromResultSet(regionsSet, world);
			UUID uuid = region.getUniqueId();
			String parrent = regionsSet.getString("parrent");
			if(parrent == null || parrent.equalsIgnoreCase("null")) {
				Optional<Region> registered = plugin.getAPI().getRegions().stream().filter(rg -> rg.getUniqueId().equals(uuid)).findFirst();
				if(registered.isPresent()) {
					plugin.getAPI().unregisterRegion(registered.get());
					plugin.getAPI().registerRegion(region);
				}
			} else {
				Optional<Region> findParrent = plugin.getAPI().getRegions().stream().map(rg -> rg.getAllChilds()).flatMap(Collection::stream).filter(rg -> rg.getParrent().isPresent() && rg.getParrent().get().getUniqueId().toString().equals(parrent)).findFirst();
				if(findParrent.isPresent()) {
					findParrent.get().getChilds().removeIf(child -> child.getUniqueId().equals(uuid));
					findParrent.get().addChild(region);
				}
			}
		}
	}

	private ClaimedByPlayer getClaimedByPlayer(ResultSet results) throws SQLException {
		return ClaimedByPlayer.of(results.getLong("claimed_blocks"), results.getLong("claimed_regions"));
	}

	private PlayerLimits getPlayerLimits(ResultSet results) throws SQLException {
		return PlayerLimits.of(results.getLong("limit_blocks"), results.getLong("limit_claims"), results.getLong("limit_subdivisions"), results.getLong("limit_members"));
	}

	private PlayerData getPlayerDataFromResultSet(ResultSet results) throws SQLException {
		return PlayerData.of(getPlayerLimits(results), getClaimedByPlayer(results));
	}

	private Region getRegionfromResultSet(ResultSet results, ServerWorld world) throws SQLException, ConfigurateException {
		UUID uuid = UUID.fromString(results.getString("uuid"));
		String mames = results.getString("name");
		String joinMessage = results.getString("join_message");
		String exitMessage = results.getString("exit_message");
		String flags = results.getString("flags");
		String members = results.getString("members");
		String additionalData = results.getString("additional_data");
		return Region.builder()
				.setUniqueId(uuid)
				.addNames(mames != null && !mames.isEmpty() ? createTempConfigReader(mames, mapComponentsToken) : null)
				.setType(RegionTypes.valueOfName(results.getString("region_type")))
				.setCreationTime(results.getLong("creation_time"))
				.setWorld(world)
				.addJoinMessages(joinMessage != null && !joinMessage.isEmpty() ? createTempConfigReader(joinMessage, mapComponentsToken) : null)
				.addExitMessages(exitMessage != null && !exitMessage.isEmpty() ? createTempConfigReader(exitMessage, mapComponentsToken) : null)
				.setFlags(flags != null && !flags.isEmpty() ? createTempConfigReader(flags, flagsToken) : null)
				.addMembers(members != null && !members.isEmpty() ? convertMembersToList(createTempConfigReader(members, membersToken)) : null)
				.setCuboid(Cuboid.of(SelectorTypes.checkType(results.getString("selector_type")), Vector3i.from(results.getInt("min_x"), results.getInt("min_y"), results.getInt("min_z")), Vector3i.from(results.getInt("max_x"), results.getInt("max_y"), results.getInt("max_z"))))
				.addAdditionalData(additionalData != null && !additionalData.isEmpty() ? createTempConfigReader(additionalData, dataMapToken) : null)
				.build();
	}

	private Region getGlobalRegionfromResultSet(ResultSet results, ServerWorld world) throws SQLException, ConfigurateException {
		String mames = results.getString("name");
		String joinMessage = results.getString("join_message");
		String exitMessage = results.getString("exit_message");
		String flags = results.getString("flags");
		String members = results.getString("members");
		String additionalData = results.getString("additional_data");
		return Region.builder()
				.setUniqueId(UUID.fromString(results.getString("uuid")))
				.setCreationTime(results.getLong("creation_time"))
				.setWorld(world)
				.addNames(mames != null && !mames.isEmpty() ? createTempConfigReader(mames, mapComponentsToken) : null)
				.addJoinMessages(joinMessage != null && !joinMessage.isEmpty() ? createTempConfigReader(joinMessage, mapComponentsToken) : null)
				.addExitMessages(exitMessage != null && !exitMessage.isEmpty() ? createTempConfigReader(exitMessage, mapComponentsToken) : null)
				.setFlags(flags != null && !flags.isEmpty() ? createTempConfigReader(flags, flagsToken) : null)
				.addMembers(members != null && !members.isEmpty() ? convertMembersToList(createTempConfigReader(members, membersToken)) : null)
				.addAdditionalData(additionalData != null && !additionalData.isEmpty() ? createTempConfigReader(additionalData, dataMapToken) : null)
				.setType(RegionTypes.GLOBAL)
				.build();
	}

}
