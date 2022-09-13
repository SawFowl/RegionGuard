package sawfowl.regionguard.configure;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.api.SelectorTypes;

@ConfigSerializable
public class MinimalRegionSize {

	public MinimalRegionSize(){}

	@Setting("Flat")
	int flat = 50;
	@Setting("Cuboid")
	int cuboid = 800;

	public int getMinimalRegionSize(SelectorTypes selectorType) {
		return selectorType == SelectorTypes.FLAT ? flat : cuboid;
	}

}
