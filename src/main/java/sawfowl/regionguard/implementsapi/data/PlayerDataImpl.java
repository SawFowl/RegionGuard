package sawfowl.regionguard.implementsapi.data;

import org.spongepowered.api.data.persistence.DataContainer;

import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;

public class PlayerDataImpl implements PlayerData {

	public PlayerDataImpl(){}
	public PlayerData.Builder builder() {
		return new Builder() {
			
			@Override
			public PlayerData build() {
				return PlayerDataImpl.this;
			}
			
			@Override
			public Builder setLimits(PlayerLimits limits) {
				PlayerDataImpl.this.limits = (PlayerLimitsImpl) (limits instanceof PlayerLimitsImpl ? limits : PlayerLimits.of(limits.getBlocks(), limits.getRegions(), limits.getSubdivisions(), limits.getMembers()));
				return this;
			}
			
			@Override
			public Builder setClaimed(ClaimedByPlayer claimed) {
				PlayerDataImpl.this.claimed = (ClaimedByPlayerImpl) (claimed instanceof ClaimedByPlayerImpl ? claimed : ClaimedByPlayer.of(claimed.getBlocks(), claimed.getRegions()));
				return this;
			}
		};
	}

	private PlayerLimits limits;
	private ClaimedByPlayer claimed;

	@Override
	public PlayerLimits getLimits() {
		if(limits == null) limits = new PlayerLimitsImpl();
		return limits;
	}

	@Override
	public PlayerData setLimits(PlayerLimits limits) {
		this.limits = limits;
		return this;
	}

	@Override
	public ClaimedByPlayer getClaimed() {
		if(claimed == null) claimed = new ClaimedByPlayerImpl();
		return claimed;
	}

	@Override
	public PlayerData setClaimed(ClaimedByPlayer claimed) {
		this.claimed = claimed;
		return this;
	}

	@Override
	public int contentVersion() {
		return 0;
	}

	@Override
	public DataContainer toContainer() {
		return null;
	}

}
