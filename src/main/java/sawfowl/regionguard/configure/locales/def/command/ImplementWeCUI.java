package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.WeCUI;

@ConfigSerializable
public class ImplementWeCUI implements WeCUI {

	public ImplementWeCUI() {}

	@Setting("Description")
	private Component description = deserialize("&6Switch the sending status of WECui packets.");
	@Setting("Enable")
	private Component enable = deserialize("&aYour game client will now receive data packets to display region boundaries, which are handled by the WECui mod. If you do not have this mod, it is recommended that you enter this command again to disable sending packets.");
	@Setting("Disable")
	private Component disable = deserialize("&aNow your client will not receive data packets for the WECui mod.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component get(boolean enable) {
		return enable ? this.enable : disable;
	}

}
