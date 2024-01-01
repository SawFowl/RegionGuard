package sawfowl.regionguard.configure.configs;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class RegenerateTerritory {

	public RegenerateTerritory(){}

	@Setting("Async")
	@Comment("Using asynchronous mode. May not work stably.")
	private boolean async = true;

	@Setting("Delay")
	@Comment("It is used for CPU load balancing during asynchronous regeneration of regions. If you have a powerful CPU, you can set it to 0 to instantly regenerate blocks after loading chunks into copies of the world.")
	private int delay = 3;

	@Setting("AllPlayers")
	@Comment("If true, when any player deletes his region, the territory will be restored.")
	private boolean all = false;

	@Setting("Staff")
	@Comment("This parameter applies only to the `/rg delete` command, and you must specify the flag in the command for regenerating the area.")
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
