package sawfowl.regionguard.configure.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ValueReference;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import sawfowl.localeapi.api.serializetools.SerializeOptions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionSerializerCollection;
import sawfowl.regionguard.api.data.AdditionalData;
import sawfowl.regionguard.api.data.AdditionalDataMap;
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
	final TypeToken<List<MemberData>> membersToken = new TypeToken<List<MemberData>>(){};
	final TypeToken<AdditionalDataMap<? extends AdditionalData>> dataMapToken = new TypeToken<AdditionalDataMap<?>>(){};
	public AbstractSqlStorage(RegionGuard plugin) {
		this.plugin = plugin;
		prefix = plugin.getConfig().getMySQLConfig().getPrefix();
	}

	protected abstract Statement getStatement() throws SQLException;

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
	public void savePlayerData(ServerPlayer player, PlayerData playerData) {
		savePlayerData(player.uniqueId(), playerData);
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

	protected <T> ValueReference<T, CommentedConfigurationNode> createTempConfigReader(String string, TypeToken<T> token) throws ConfigurateException {
		return SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).source(() -> new BufferedReader(new StringReader(string))).build().loadToReference().referenceTo(token);
	}

	protected <T> String getSerializedData(T object, TypeToken<T> token) {
		StringWriter sink = new StringWriter();
		try {
			SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).sink(() -> new BufferedWriter(sink)).build().loadToReference().referenceTo(token).setAndSave(object);
			return sink.toString();
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
		return null;
	}

}