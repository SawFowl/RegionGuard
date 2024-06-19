package sawfowl.regionguard.configure.locales.def.events.region;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.locales.abstractlocale.Events.RegionEvents.WandInfo;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementWandInfo implements WandInfo {

	public ImplementWandInfo() {}

	@Setting("RegionType")
	private Component regionType = deserialize("&dRegion type: &b" + PlaceholderKeys.TYPE);
	@Setting("Owner")
	private Component owner = deserialize("&dOwner: &b" + PlaceholderKeys.OWNER);

	@Override
	public Component getType(Region region) {
		return replace(regionType, PlaceholderKeys.TYPE, region.getType().toString());
	}

	@Override
	public Component getOwner(Region region) {
		return replace(owner, PlaceholderKeys.OWNER, region.getOwnerName());
	}

}
