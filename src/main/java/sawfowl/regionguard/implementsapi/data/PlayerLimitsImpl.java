package sawfowl.regionguard.implementsapi.data;

import org.spongepowered.api.data.persistence.DataContainer;

import com.google.gson.JsonObject;

import sawfowl.regionguard.api.data.PlayerLimits;

public class PlayerLimitsImpl implements PlayerLimits {

	public PlayerLimitsImpl(){}
	public PlayerLimits.Builder builder() {
		return new Builder() {

			@Override
			public PlayerLimits build() {
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

			@Override
			public PlayerLimits fromJson(JsonObject jsonObject) {
				if(jsonObject.has("Blocks")) {
					blocks = jsonObject.get("Blocks").getAsLong();
				}
				if(jsonObject.has("Claims")) {
					claims = jsonObject.get("Claims").getAsLong();
				}
				if(jsonObject.has("Subdivisions")) {
					subdivisions = jsonObject.get("Subdivisions").getAsLong();
				}
				if(jsonObject.has("Members")) {
					membersPerRegion = jsonObject.get("Members").getAsLong();
				}
				return build();
			}
		};
	}

	private long blocks;
	private long claims;
	private long subdivisions;
	private long membersPerRegion;

	@Override
	public long getBlocks() {
		return blocks;
	}

	@Override
	public long getRegions() {
		return claims;
	}

	@Override
	public long getSubdivisions() {
		return subdivisions;
	}

	@Override
	public long getMembers() {
		return membersPerRegion;
	}

	@Override
	public PlayerLimits setBlocks(long limit) {
		blocks = limit;
		return this;
	}

	@Override
	public PlayerLimits setClaims(long limit) {
		claims = limit;
		return this;
	}

	@Override
	public PlayerLimits setSubdivisions(long limit) {
		subdivisions = limit;
		return this;
	}

	@Override
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
	@Override
	public JsonObject asJson() {
		JsonObject json = new JsonObject();
		json.addProperty("Blocks", blocks);
		json.addProperty("Claims", claims);
		json.addProperty("Subdivisions", subdivisions);
		json.addProperty("Members", membersPerRegion);
		return json;
	}

}
