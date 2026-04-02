package sawfowl.regionguard.configure.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.ConfigurateException;

import com.google.gson.JsonObject;

import io.leangen.geantyref.TypeToken;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.api.serializetools.ItemStackSerializerType;
import sawfowl.localeapi.api.services.ConfigurationService;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionSerializerCollection;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.WorkData;

public abstract class AbstractSqlStorage extends Thread implements WorkData {

	final String prefix;
	final RegionGuard plugin;
	final TypeToken<Map<String, Component>> mapComponentsToken = new TypeToken<Map<String, Component>>(){};
	final TypeToken<Map<String, Set<FlagValue>>> flagsToken = new TypeToken<Map<String, Set<FlagValue>>>(){};
	final TypeToken<Map<UUID, MemberData>> membersToken = new TypeToken<Map<UUID, MemberData>>(){};
	final TypeToken<Map<String, Map<String, JsonObject>>> dataMapToken = new TypeToken<Map<String, Map<String, JsonObject>>>(){};
	public AbstractSqlStorage(RegionGuard plugin) {
		this.plugin = plugin;
		prefix = plugin.getConfig().getMySQLConfig().getPrefix();
	}

	protected abstract Statement getStatement() throws SQLException;

	@Override
	public void createGlobalRegionForWorld(ResourceKey world) {
		Region global = getWorldRegion(world);
		if(global == null) {
			global = Region.createGlobal(world, plugin.getDefaultFlagsConfig().getGlobalFlags());
			save(global);
			plugin.getAPI().updateGlobalRegionData(world, global);
		} else {
			plugin.getAPI().updateGlobalRegionData(world, global);
		}
	
	}

	@Override
	public void save(ServerPlayer player, PlayerData playerData) {
		save(player.uniqueId(), playerData);
	}

	protected ResultSet resultSet(String sql) throws SQLException {
		return getStatement().executeQuery(sql);
	}

	protected void executeSQL(String sql) {
		try {
			getStatement().execute(sql);
		}
		catch (SQLException e) {
			plugin.getLogger().error("Error during SQL query execution '" + sql + "'...\n" + e.getLocalizedMessage());
		}
	}

	protected <T> T createTempConfigReader(String string, TypeToken<T> token) {
		if(string == null || string.isEmpty() || string.equalsIgnoreCase("null") || string.startsWith("{}")) return null;
		ConfigurationService.getInstance().createVirtualConfig().setType(ConfigTypes.JSON).setItemStackSerializerType(ItemStackSerializerType.JSON).addSerializers(RegionSerializerCollection.COLLETCTION).setData(string).build();
		try {
			return ConfigurationService.getInstance().createVirtualConfig().setType(ConfigTypes.JSON).setItemStackSerializerType(ItemStackSerializerType.JSON).addSerializers(RegionSerializerCollection.COLLETCTION).setData(string).build().getRootNode().get(token);
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage() + " " + string);
		}
		return null;
	}

	protected <T> String getSerializedData(T object, TypeToken<T> token) {
		if(object == null) return null;
		return ConfigurationService.getInstance().createVirtualReferencedConfig(object).setType(ConfigTypes.JSON).setItemStackSerializerType(ItemStackSerializerType.JSON).addSerializers(RegionSerializerCollection.COLLETCTION).build().getRawData();
	}

	protected Map<UUID, MemberData> convertMembersToMap(List<MemberData> list) {
		Map<UUID, MemberData> map = new HashMap<UUID, MemberData>();
		list.forEach(member -> map.put(member.getUniqueId(), MemberData.builder().setName(member.getName()).setTrustType(member.getTrustType()).build()));
		return map;
	}

	protected List<MemberData> convertMembersToList(Map<UUID, MemberData> map) {
		if(map == null) return new ArrayList<MemberData>();
		List<MemberData> list = new ArrayList<MemberData>();
		map.forEach((k,v) -> list.add(MemberData.builder().setName(v.getName()).setUUID(k).setTrustType(v.getTrustType()).build()));
		return list;
	}

}