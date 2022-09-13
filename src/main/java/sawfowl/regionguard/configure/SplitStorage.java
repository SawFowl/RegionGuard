package sawfowl.regionguard.configure;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class SplitStorage {

	public SplitStorage(){}

	@Setting("Enable")
	private boolean enable = false;

	@Setting("Players")
	private boolean players = false;

	@Setting("Regions")
	private boolean regions = false;

	public boolean isEnable() {
		return enable;
	}

	public boolean isPlayers() {
		return players;
	}

	public boolean isRegions() {
		return regions;
	}

}
