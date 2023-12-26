package sawfowl.regionguard.data;


import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;

@ConfigSerializable
public class PlayerDataImpl implements PlayerData {

	public PlayerDataImpl(){}
	public PlayerData.Builder builder() {
		return new Builder() {
			
			@Override
			public @NotNull PlayerData build() {
				return PlayerDataImpl.this;
			}
			
			@Override
			public Builder setLimits(PlayerLimits limits) {
				PlayerDataImpl.this.limits = (PlayerLimitsImpl) (limits instanceof PlayerLimitsImpl ? limits : PlayerLimits.of(limits.getBlocks(), limits.getClaims(), limits.getSubdivisions(), limits.getMembersPerRegion()));
				return this;
			}
			
			@Override
			public Builder setClaimed(ClaimedByPlayer claimed) {
				PlayerDataImpl.this.claimed = (ClaimedByPlayerImpl) (claimed instanceof ClaimedByPlayerImpl ? claimed : ClaimedByPlayer.of(claimed.getBlocks(), claimed.getRegions()));
				return this;
			}
		};
	}

	@Setting("Limits")
	private PlayerLimitsImpl limits;
	@Setting("Claimed")
	private ClaimedByPlayerImpl claimed;

	public PlayerLimits getLimits() {
		return limits;
	}

	public PlayerDataImpl setLimits(PlayerLimits limits) {
		this.limits = (PlayerLimitsImpl) limits;
		return this;
	}

	public ClaimedByPlayerImpl getClaimed() {
		return claimed;
	}

	public PlayerDataImpl setClaimed(ClaimedByPlayer claimed) {
		this.claimed = (ClaimedByPlayerImpl) claimed;
		return this;
	}

	public boolean isEmpty() {
		return limits == null && claimed == null;
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
