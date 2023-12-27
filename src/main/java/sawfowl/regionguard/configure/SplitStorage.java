package sawfowl.regionguard.configure;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.commandpack.utils.StorageType;

@ConfigSerializable
public class SplitStorage {

	public SplitStorage(){}

	@Setting("Enable")
	private boolean enable = false;

	@Setting("Players")
	private String players = StorageType.FILE.typeName();

	@Setting("Regions")
	private String regions = StorageType.FILE.typeName();

	public boolean isEnable() {
		return enable;
	}

	public StorageType getPlayers() {
		return StorageType.getType(players);
	}

	public StorageType getRegions() {
		return StorageType.getType(regions);
	}

}
