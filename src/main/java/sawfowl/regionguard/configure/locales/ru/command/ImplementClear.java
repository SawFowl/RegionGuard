package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Clear;

@ConfigSerializable
public class ImplementClear implements Clear {

	public ImplementClear() {}

	@Setting("Description")
	private Component description = deserialize("&6Очистка выделения региона.");
	@Setting("Success")
	private Component success = deserialize("&aОбласть выделения очищена.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
