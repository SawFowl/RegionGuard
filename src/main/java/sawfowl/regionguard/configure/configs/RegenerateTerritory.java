package sawfowl.regionguard.configure.configs;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.localeapi.api.LocalisedComment;

@ConfigSerializable
public class RegenerateTerritory {

	public RegenerateTerritory(){}

	@Setting("Async")
	@LocalisedComment(path = {"Comments", "MainConfig", "RegenerateTerritory", "Async"}, plugin = "regionguard")
	private boolean async = true;

	@Setting("Delay")
	@LocalisedComment(path = {"Comments", "MainConfig", "RegenerateTerritory", "Delay"}, plugin = "regionguard")
	private int delay = 3;

	@Setting("AllPlayers")
	@LocalisedComment(path = {"Comments", "MainConfig", "RegenerateTerritory", "AllPlayers"}, plugin = "regionguard")
	private boolean all = false;

	@Setting("Staff")
	@LocalisedComment(path = {"Comments", "MainConfig", "RegenerateTerritory", "Staff"}, plugin = "regionguard")
	private boolean staff = true;

	public boolean isAsync() {
		return async;
	}

	public int getDelay() {
		return delay;
	}

	public boolean isAllPlayers() {
		return all;
	}

	public boolean isStaff() {
		return staff;
	}

}
