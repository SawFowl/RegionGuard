package sawfowl.regionguard.api.data;


import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class PlayerData {

	public PlayerData(){}

	public PlayerData(PlayerLimits limits, ClaimedByPlayer claimed) {
		this.limits = limits;
		this.claimed = claimed;
	}

	@Setting("Limits")
	private PlayerLimits limits;
	@Setting("Claimed")
	private ClaimedByPlayer claimed;

	public PlayerLimits getLimits() {
		return limits;
	}

	public PlayerData setLimits(PlayerLimits limits) {
		this.limits = limits;
		return this;
	}

	public ClaimedByPlayer getClaimed() {
		return claimed;
	}

	public PlayerData setClaimed(ClaimedByPlayer claimed) {
		this.claimed = claimed;
		return this;
	}

	public boolean isEmpty( ) {
		return limits == null && claimed == null;
	}

}
