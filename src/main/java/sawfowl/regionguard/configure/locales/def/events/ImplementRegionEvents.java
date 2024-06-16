package sawfowl.regionguard.configure.locales.def.events;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.RegionEvents;
import sawfowl.regionguard.configure.locales.def.events.region.ImplementCreate;
import sawfowl.regionguard.configure.locales.def.events.region.ImplementResize;
import sawfowl.regionguard.configure.locales.def.events.region.ImplementWandInfo;

@ConfigSerializable
public class ImplementRegionEvents implements RegionEvents {

	public ImplementRegionEvents() {}

	@Setting("Create")
	private ImplementCreate create = new ImplementCreate();
	@Setting("Resize")
	private ImplementResize resize = new ImplementResize();
	@Setting("WandInfo")
	private ImplementWandInfo wandInfo = new ImplementWandInfo();

	@Override
	public Create getCreate() {
		return create;
	}

	@Override
	public Resize getResize() {
		return resize;
	}

	@Override
	public WandInfo getWandInfo() {
		return wandInfo;
	}

}
