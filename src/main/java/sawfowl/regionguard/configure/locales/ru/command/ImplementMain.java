package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Main;

@ConfigSerializable
public class ImplementMain implements Main {

	public ImplementMain() {}

	@Setting("Description")
	private Component description = TextUtils.deserialize("&6Основная команда плагина RegionGuard.");
	@Setting("Title")
	private Component title = TextUtils.deserializeLegacy("&bСписок команд");
	@Setting("Padding")
	private Component padding = TextUtils.deserializeLegacy("&b=");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getTitle() {
		return title;
	}

	@Override
	public Component getPadding() {
		return padding;
	}
	
}
