package sawfowl.regionguard.data;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.api.data.PlayerLimits;

@ConfigSerializable
public class PlayerLimitsImpl implements PlayerLimits {

	public PlayerLimitsImpl(){}
	public PlayerLimits.Builder builder() {
		return new Builder() {

			@Override
			public @NotNull PlayerLimits build() {
				return PlayerLimitsImpl.this;
			}

			@Override
			public Builder setBlocks(long limit) {
				blocks = limit;
				return this;
			}

			@Override
			public Builder setRegions(long limit) {
				claims = limit;
				return this;
			}

			@Override
			public Builder setSubdivisions(long limit) {
				subdivisions = limit;
				return this;
			}

			@Override
			public Builder setMembersPerRegion(long limit) {
				membersPerRegion = limit;
				return this;
			}
		};
	}

	@Setting("Blocks")
	private long blocks;
	@Setting("Claims")
	private long claims;
	@Setting("Subdivisions")
	private long subdivisions;
	@Setting("Members")
	private long membersPerRegion;

	public long getBlocks() {
		return blocks;
	}

	public long getRegions() {
		return claims;
	}

	public long getSubdivisions() {
		return subdivisions;
	}

	public long getMembers() {
		return membersPerRegion;
	}

	public PlayerLimits setBlocks(long limit) {
		blocks = limit;
		return this;
	}

	public PlayerLimits setClaims(long limit) {
		claims = limit;
		return this;
	}

	public PlayerLimits setSubdivisions(long limit) {
		subdivisions = limit;
		return this;
	}

	public PlayerLimits setMembersPerRegion(long limit) {
		membersPerRegion = limit;
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
