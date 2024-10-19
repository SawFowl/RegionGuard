package sawfowl.regionguard.configure.locales.ru.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Comments.MainConfig.RegenerateTerritory;

@ConfigSerializable
public class ImplementRegenerateTerritory implements RegenerateTerritory {

	public ImplementRegenerateTerritory() {}

	@Setting("Title")
	private String title = "Настройка регенерации территории до первоначального состояния при удалении региона.";
	@Setting("Async")
	private String async = "Использование асинхронного режима. Может работать нестабильно.";
	@Setting("Delay")
	private String delay = "Используется для балансировки нагрузки на процессор во время асинхронной регенерации регионов. Если у вас мощный процессор, вы можете установить значение 0, чтобы мгновенно регенерировать блоки после загрузки чанков в копии мира.";
	@Setting("AllPlayers")
	private String allPlayers = "Если значение равно true, то когда любой игрок удалит свой регион, территория будет восстановлена.";
	@Setting("Staff")
	private String staff = "Этот параметр применяется только к команде `/rg delete`, и вы должны указать флаг в команде для регенерации области.";

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getAsync() {
		return async;
	}

	@Override
	public String getDelay() {
		return delay;
	}

	@Override
	public String getAllPlayers() {
		return allPlayers;
	}

	@Override
	public String getStaff() {
		return staff;
	}

}
