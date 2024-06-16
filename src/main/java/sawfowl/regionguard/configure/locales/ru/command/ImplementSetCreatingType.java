package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.SetCreatingType;

@ConfigSerializable
public class ImplementSetCreatingType implements SetCreatingType {

	public ImplementSetCreatingType() {}

	@Setting("Description")
	private Component description = TextUtils.deserialize("&6Выбор типа создаваемого региона.");
	@Setting("Basic")
	private Component basic = TextUtils.deserialize("&dТеперь вы будете создавать базовые регионы.");
	@Setting("Arena")
	private Component arena = TextUtils.deserialize("&dТеперь вы будете создавать арены. Они отличаются от базовых регионов только сеткой подсветки WECui. Рекомендуется для мини-игр и тематических режимов.");
	@Setting("Admin")
	private Component admin = TextUtils.deserialize("&dТеперь вы создадите административные регионы. В регионах этого типа владельцем всегда является сервер.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component get(RegionTypes type) {
		return switch (type) {
			case ADMIN: yield admin;
			case ARENA: yield arena;
			default: yield basic;
		};
	}

}
