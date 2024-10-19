package sawfowl.regionguard.configure.configs;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.localeapi.api.LocalisedComment;

@ConfigSerializable
public class MySQLConfig {

	public MySQLConfig(){}

	@Setting("Enable")
	private Boolean enable = false;

	@Setting("Host")
	private String host = "localhost";

	@Setting("Port")
	private String port = "3306";

	@Setting("DataBase")
	private String database = "regionguard";

	@Setting("Prefix")
	private String prefix = "regionguard_";

	@Setting("User")
	private String user = "user";

	@Setting("Password")
	private String password = "UNSET";

	@Setting("SSL")
	private String ssl = "false";

	@Setting("SyncInterval")
	@LocalisedComment(path = {"Comments", "MainConfig", "SyncInterval"}, plugin = "regionguard")
	private long syncInterval = 10;

	public boolean isEnable() {
		return enable;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getDatabase() {
		return database;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getSSL() {
		return ssl;
	}

	public long getSyncInterval() {
		return syncInterval;
	}

}
