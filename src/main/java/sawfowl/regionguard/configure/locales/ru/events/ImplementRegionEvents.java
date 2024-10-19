package sawfowl.regionguard.configure.locales.ru.events;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.RegionEvents;
import sawfowl.regionguard.configure.locales.ru.events.region.ImplementCreate;
import sawfowl.regionguard.configure.locales.ru.events.region.ImplementResize;
import sawfowl.regionguard.configure.locales.ru.events.region.ImplementWandInfo;

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
