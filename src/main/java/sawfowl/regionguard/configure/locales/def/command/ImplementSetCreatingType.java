package sawfowl.regionguard.configure.locales.def.command;

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
	private Component description = TextUtils.deserialize("&6The main command of the RegionGuard plugin.");
	@Setting("Basic")
	private Component basic = TextUtils.deserialize("&dYou will now create basic regions.");
	@Setting("Arena")
	private Component arena = TextUtils.deserialize("&dYou will now create arenas. They differ from the basic regions only by the WECui highlighting grid. Recommended for minigames and theme modes.");
	@Setting("Admin")
	private Component admin = TextUtils.deserialize("&dYou will now create administrative regions. With this type of regions, the owner is always the server.");

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
