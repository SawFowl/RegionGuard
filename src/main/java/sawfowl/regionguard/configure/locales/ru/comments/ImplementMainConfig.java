package sawfowl.regionguard.configure.locales.ru.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Comments.MainConfig;

@ConfigSerializable
public class ImplementMainConfig implements MainConfig {

	public ImplementMainConfig() {}

	@Setting("RegenerateTerritory")
	private ImplementRegenerateTerritory regenerateTerritory = new ImplementRegenerateTerritory();
	@Setting("UnloadRegions")
	private String unloadRegions = "Если true, то при выгрузке чанков будут выгружены регионы, находящиеся в них, чтобы обеспечить более быстрый поиск региона среди оставшихся регионов на карте.\nЕсли false, то все регионы будут постоянно загружены.\nУстановите значение false, если по какой-то причине регионы не загружаются вовремя.";
	@Setting("MinimalRegionSize")
	private String minimalRegionSize = "Минимальный размер создаваемого региона.";
	@Setting("TankItems")
	private String tankItems = "Предметы, которые являются контейнерами для жидкостей.";
	@Setting("DefaultSelector")
	private String defaultSelector = "Если вы выберите плоское выделение, все созданные вами области будут автоматически увеличиваться по высоте.\nПринимаемые значения: \"Flat\", \"Cuboid\"";
	@Setting("SplitStorage")
	private String splitStorage = "Сохранение данных плагина в различных хранилищах.\nПринимаемые значения: File, H2, MySql.\nЧтобы использовать MySql, необходимо настроить соответствующий раздел этой конфигурации.\nИспользование H2 предпочтительно, когда объем данных невелик.\nДля использования MySql или H2 необходим соответствующий драйвер.\nВы можете найти плагины с драйверами на ORE.";
	@Setting("MySQL")
	private String mySQL = "Настройте этот параметр, если вам нужно хранить данные плагина в базе данных MySQL.";
	@Setting("WandItem")
	private String wandItem = "Предмет, используемый для выделения области и предоставления краткой информации о ней.";
	@Setting("RegisterForgeListeners")
	private String registerForgeListeners = "Иногда Sponge не перехватывает события Forge. Включение этой опции может устранить проблему.\nЕсли защита территории не работает при любом действии, сообщите об этом на сервер Discord разработчика плагина - https://discord.gg/4SMShjQ3Pe\nВ некоторых случаях может потребоваться внесение изменений в код мода.";
	@Setting("SyncInterval")
	private String syncInterval = "Время между загрузкой новых и обновленных данных из базы данных MySql.\nСинхронизация не будет выполняться, если значение меньше 0.";

	@Override
	public RegenerateTerritory getRegenerateTerritory() {
		return regenerateTerritory;
	}

	@Override
	public String getUnloadRegions() {
		return unloadRegions;
	}

	@Override
	public String getMinimalRegionSize() {
		return minimalRegionSize;
	}

	@Override
	public String getTankItems() {
		return tankItems;
	}

	@Override
	public String getDefaultSelector() {
		return defaultSelector;
	}

	@Override
	public String getSplitStorage() {
		return splitStorage;
	}

	@Override
	public String getMySQL() {
		return mySQL;
	}

	@Override
	public String getWandItem() {
		return wandItem;
	}

	@Override
	public String getRegisterForgeListeners() {
		return registerForgeListeners;
	}

	@Override
	public String getSyncInterval() {
		return syncInterval;
	}

}
