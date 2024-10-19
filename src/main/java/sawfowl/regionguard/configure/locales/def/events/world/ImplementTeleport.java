package sawfowl.regionguard.configure.locales.def.events.world;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.configure.locales.abstractlocale.Events.Teleport;
import sawfowl.regionguard.configure.locales.def.events.world.teleport.ImplementFromRegion;
import sawfowl.regionguard.configure.locales.def.events.world.teleport.ImplementToRegion;

@ConfigSerializable
public class ImplementTeleport implements Teleport {

	public ImplementTeleport() {}

	@Setting("FromRegion")
	private ImplementFromRegion fromRegion = new ImplementFromRegion();
	@Setting("ToRegion")
	private ImplementToRegion toRegion = new ImplementToRegion();
	@Setting("PortalUse")
	private Component portalUse = deserialize("&cYou cannot use the portal in your current region.");

	@Override
	public FromRegion getFromRegion() {
		return fromRegion;
	}

	@Override
	public ToRegion getToRegion() {
		return toRegion;
	}

	@Override
	public Component getPortalUse() {
		return portalUse;
	}

}
