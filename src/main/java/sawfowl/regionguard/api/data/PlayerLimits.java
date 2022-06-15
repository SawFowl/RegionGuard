package sawfowl.regionguard.api.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class PlayerLimits {

	public PlayerLimits(){}

	public PlayerLimits(Long blocks, Long claims, Long subdivisions, Long membersPerRegion) {
		this.blocks = blocks;
		this.claims = claims;
		this.subdivisions = subdivisions;
		this.membersPerRegion = membersPerRegion;
	}

	@Setting("Blocks")
	private Long blocks;
	@Setting("Claims")
	private Long claims;
	@Setting("Subdivisions")
	private Long subdivisions;
	@Setting("MembersPerRegion")
	private Long membersPerRegion;

	public Long getBlocks() {
		return blocks;
	}

	public Long getClaims() {
		return claims;
	}

	public Long getSubdivisions() {
		return subdivisions;
	}

	public Long getMembersPerRegion() {
		return membersPerRegion;
	}

	public PlayerLimits setBlocks(Long limit) {
		blocks = limit;
		return this;
	}

	public PlayerLimits setClaims(Long limit) {
		claims = limit;
		return this;
	}

	public PlayerLimits setSubdivisions(Long limit) {
		subdivisions = limit;
		return this;
	}

	public PlayerLimits setMembersPerRegion(Long limit) {
		membersPerRegion = limit;
		return this;
	}

}
