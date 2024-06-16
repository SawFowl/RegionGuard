package sawfowl.regionguard.configure.locales.def.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Comments.MainConfig.RegenerateTerritory;

@ConfigSerializable
public class ImplementRegenerateTerritory implements RegenerateTerritory {

	public ImplementRegenerateTerritory() {}

	@Setting("Title")
	private String title = "Configuring the territory to be restored to its original state when a region is deleted.";
	@Setting("Async")
	private String async = "Using asynchronous mode. May not work stably.";
	@Setting("Delay")
	private String delay = "It is used for CPU load balancing during asynchronous regeneration of regions. If you have a powerful CPU, you can set it to 0 to instantly regenerate blocks after loading chunks into copies of the world.";
	@Setting("AllPlayers")
	private String allPlayers = "If true, when any player deletes his region, the territory will be restored.";
	@Setting("Staff")
	private String staff = "This parameter applies only to the `/rg delete` command, and you must specify the flag in the command for regenerating the area.";

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getAsync() {
		return async;
	}

	@Override
	public String getDelay() {
		return delay;
	}

	@Override
	public String getAllPlayers() {
		return allPlayers;
	}

	@Override
	public String getStaff() {
		return staff;
	}

}
