package sawfowl.regionguard.configure.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ValueReference;

import sawfowl.localeapi.api.serializetools.SerializeOptions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.RegionSerializerCollection;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.WorkData;

public abstract class AbstractSqlStorage extends Thread implements WorkData {

	final String prefix;
	boolean globalsCreated = false;
	final RegionGuard plugin;
	public AbstractSqlStorage(RegionGuard plugin) {
		this.plugin = plugin;
		prefix = plugin.getConfig().getMySQLConfig().getPrefix();
	}

	protected abstract Statement getStatement() throws SQLException;

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

	protected ValueReference<Region, CommentedConfigurationNode> createVirtualConfigWriter(StringWriter sink) throws ConfigurateException {
		return SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).sink(() -> new BufferedWriter(sink)).build().loadToReference().referenceTo(Region.class);
	}

	protected ValueReference<Region, CommentedConfigurationNode> createVirtualConfigReader(StringReader reader) throws ConfigurateException {
		return SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).source(() -> new BufferedReader(reader)).build().loadToReference().referenceTo(Region.class);
	}

}