package sawfowl.regionguard.configure.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;

public class H2Storage extends AbstractSqlStorage {

	private Statement statement;
	public H2Storage(RegionGuard plugin) {
		super(plugin);
		createTableForPlayers();
		createWorldsTables();
		createDataForWorlds();
	}

	@Override
	public Region getWorldRegion(ServerWorld world) {
		try {
			ResultSet results = resultSet("SELECT * FROM " + prefix + "worlds;");
			while(!results.isClosed() && results.next()) {
				if(world.key().asString().equals(results.getString("WORLD"))) return getGlobalRegionfromResultSet(results, world);
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
			sql = "MERGE INTO " + prefix + "worlds(UUID, WORLD, NAME, CREATION_TIME, JOIN_MESSAGE, EXIT_MESSAGE, FLAGS, MEMBERS, ADDITIONAL_DATA) VALUES('"
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
			sql = "MERGE INTO " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + "(UUID, NAME, REGION_TYPE, CREATION_TIME, JOIN_MESSAGE, EXIT_MESSAGE, FLAGS, MEMBERS, MIN_X, MIN_Y, MIN_Z, MAX_X, MAX_Y, MAX_Z, SELECTOR_TYPE, ADDITIONAL_DATA, PARRENT) VALUES('"
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
		executeSQL("DELETE FROM " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + " WHERE " + prefix + "world_" + region.getWorldKey().asString().replace(':', '_') + ".UUID = '" + region.getUniqueId().toString() + "';");
		if(!region.getChilds().isEmpty()) region.getChilds().forEach(this::deleteRegion);
	}

	@Override
	public void loadRegions() {
		Sponge.server().worldManager().worlds().forEach(world -> {
			Map<UUID, Set<Region>> childs = new HashMap<>();
			try {
				ResultSet results = resultSet("SELECT * FROM " + prefix + "world_" + world.key().asString().replace(':', '_') + ";");
				while(!results.isClosed() && results.next()) {
					Region region = getRegionfromResultSet(results, world);
					UUID uuid = region.getUniqueId();
					String parrent = results.getString("PARRENT");
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
		String sql = "MERGE INTO " + prefix + "player_data(UUID, CLAIMED_BLOCKS, CLAIMED_REGIONS, LIMIT_BLOCKS, LIMIT_CLAIMS, LIMIT_SUBDIVISIONS, LIMIT_MEMBERS) VALUES("
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
			ResultSet results = resultSet("SELECT * FROM " + prefix + "player_data");
			while(!results.isClosed() && results.next()) {
				plugin.getAPI().setPlayerData(UUID.fromString(results.getString("UUID")), getPlayerDataFromResultSet(results));
			}
		} catch (SQLException e) {
			plugin.getLogger().error("Get players data.\n" + e.getLocalizedMessage());
		}
	}

	@Override
	protected Statement getStatement() throws SQLException {
		return statement == null || statement.isClosed() ? statement = getConnection().createStatement() : statement;
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:h2:" + plugin.getConfigDir().resolve("StorageData").toFile().getAbsolutePath(), "", "");
	}

	private void createTableForPlayers() {
		executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "player_data(UUID VARCHAR(128) UNIQUE, CLAIMED_BLOCKS BIGINT, CLAIMED_REGIONS BIGINT, LIMIT_BLOCKS BIGINT, LIMIT_CLAIMS BIGINT, LIMIT_SUBDIVISIONS BIGINT, LIMIT_MEMBERS BIGINT, PRIMARY KEY(UUID));");
	}

	private void createWorldsTables() {
		executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "worlds(UUID VARCHAR(128) UNIQUE, WORLD VARCHAR(128) UNIQUE, NAME LONGTEXT, CREATION_TIME BIGINT, JOIN_MESSAGE LONGTEXT, EXIT_MESSAGE LONGTEXT, FLAGS LONGTEXT, MEMBERS LONGTEXT, ADDITIONAL_DATA LONGTEXT, PRIMARY KEY(WORLD));");
		for(ResourceKey worldKey : Sponge.server().worldManager().worldKeys()) {
			executeSQL("CREATE TABLE IF NOT EXISTS " + prefix + "world_" + worldKey.asString().replace(':', '_')  + "(UUID VARCHAR(128) UNIQUE, NAME LONGTEXT, REGION_TYPE TEXT, CREATION_TIME BIGINT, JOIN_MESSAGE LONGTEXT, EXIT_MESSAGE LONGTEXT, FLAGS LONGTEXT, MEMBERS LONGTEXT, MIN_X BIGINT, MIN_Y BIGINT, MIN_Z BIGINT, MAX_X BIGINT, MAX_Y BIGINT, MAX_Z BIGINT, SELECTOR_TYPE TEXT, ADDITIONAL_DATA LONGTEXT, PARRENT VARCHAR(128), PRIMARY KEY(UUID));");
		}
	}

	private ClaimedByPlayer getClaimedByPlayer(ResultSet results) throws SQLException {
		return ClaimedByPlayer.of(results.getLong("CLAIMED_BLOCKS"), results.getLong("CLAIMED_REGIONS"));
	}

	private PlayerLimits getPlayerLimits(ResultSet results) throws SQLException {
		return PlayerLimits.of(results.getLong("LIMIT_BLOCKS"), results.getLong("LIMIT_CLAIMS"), results.getLong("LIMIT_SUBDIVISIONS"), results.getLong("LIMIT_MEMBERS"));
	}

	private PlayerData getPlayerDataFromResultSet(ResultSet results) throws SQLException {
		return PlayerData.of(getPlayerLimits(results), getClaimedByPlayer(results));
	}

	private Region getRegionfromResultSet(ResultSet results, ServerWorld world) throws SQLException, ConfigurateException {
		UUID uuid = UUID.fromString(results.getString("UUID"));
		String mames = results.getString("NAME");
		String joinMessage = results.getString("JOIN_MESSAGE");
		String exitMessage = results.getString("EXIT_MESSAGE");
		String flags = results.getString("FLAGS");
		String members = results.getString("MEMBERS");
		String additionalData = results.getString("ADDITIONAL_DATA");
		return Region.builder()
				.setUniqueId(uuid)
				.addNames(mames != null && !mames.isEmpty() ? createTempConfigReader(mames, mapComponentsToken) : null)
				.setType(RegionTypes.valueOfName(results.getString("REGION_TYPE")))
				.setCreationTime(results.getLong("CREATION_TIME"))
				.setWorld(world)
				.addJoinMessages(joinMessage != null && !joinMessage.isEmpty() ? createTempConfigReader(joinMessage, mapComponentsToken) : null)
				.addExitMessages(exitMessage != null && !exitMessage.isEmpty() ? createTempConfigReader(exitMessage, mapComponentsToken) : null)
				.setFlags(flags != null && !flags.isEmpty() ? createTempConfigReader(flags, flagsToken) : null)
				.addMembers(members != null && !members.isEmpty() ? convertMembersToList(createTempConfigReader(members, membersToken)) : null)
				.setCuboid(Cuboid.of(SelectorTypes.checkType(results.getString("SELECTOR_TYPE")), Vector3i.from(results.getInt("MIN_X"), results.getInt("MIN_Y"), results.getInt("MIN_Z")), Vector3i.from(results.getInt("MAX_X"), results.getInt("MAX_Y"), results.getInt("MAX_Z"))))
				.addAdditionalData(additionalData != null && !additionalData.isEmpty() ? createTempConfigReader(additionalData, dataMapToken) : null)
				.build();
	}

	private Region getGlobalRegionfromResultSet(ResultSet results, ServerWorld world) throws SQLException, ConfigurateException {
		String mames = results.getString("NAME");
		String joinMessage = results.getString("JOIN_MESSAGE");
		String exitMessage = results.getString("EXIT_MESSAGE");
		String flags = results.getString("FLAGS");
		String members = results.getString("MEMBERS");
		String additionalData = results.getString("ADDITIONAL_DATA");
		return Region.builder()
				.setUniqueId(UUID.fromString(results.getString("UUID")))
				.setCreationTime(results.getLong("CREATION_TIME"))
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
