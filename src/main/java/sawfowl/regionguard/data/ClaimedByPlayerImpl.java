package sawfowl.regionguard.data;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.api.data.ClaimedByPlayer;

@ConfigSerializable
public class ClaimedByPlayerImpl implements ClaimedByPlayer {

	public ClaimedByPlayerImpl(){}

	public ClaimedByPlayer.Builder builder() {
		return new Builder() {

			@Override
			public @NotNull ClaimedByPlayer build() {
				return ClaimedByPlayerImpl.this;
			}

			@Override
			public Builder setBlocks(long value) {
				blocks = value;
				return this;
			}

			@Override
			public Builder setRegions(long value) {
				regions = value;
				return this;
			}
		};
	}

	@Setting("Blocks")
	private Long blocks = 0l;
	@Setting("Regions")
	private Long regions = 0l;

	public Long getBlocks() {
		return blocks;
	}

	public Long getRegions() {
		return regions;
	}

	public ClaimedByPlayer setBlocks(long limit) {
		blocks = limit;
		if(blocks < 0) blocks = 0l;
		return this;
	}

	public ClaimedByPlayer setRegions(long limit) {
		regions = limit;
		if(regions < 0) regions = 0l;
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
