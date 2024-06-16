package sawfowl.regionguard.configure.locales.ru.command.info;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Info.Buttons;

@ConfigSerializable
public class ImplementButtons implements Buttons {

	public ImplementButtons() {}

	@Setting("Delete")
	private Component delete = deserialize("&7[&4Удалить&7]");
	@Setting("Flags")
	private Component flags = deserialize("&7[&eПросмотреть флаги&7]");

	@Override
	public Component getDelete() {
		return delete;
	}

	@Override
	public Component getFlags() {
		return flags;
	}
	
}
