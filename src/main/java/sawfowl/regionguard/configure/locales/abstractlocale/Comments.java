package sawfowl.regionguard.configure.locales.abstractlocale;

public interface Comments {

	interface MainConfig {

		interface RegenerateTerritory {

			String getTitle();

			String getAsync();

			String getDelay();

			String getAllPlayers();

			String getStaff();

		}

		RegenerateTerritory getRegenerateTerritory();

		String getUnloadRegions();

		String getMinimalRegionSize();

		String getTankItems();

		String getDefaultSelector();

		String getSplitStorage();

		String getMySQL();

		String getWandItem();

		String getRegisterForgeListeners();

		String getSyncInterval();

	}

	MainConfig getMainConfig();

}
