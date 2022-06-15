package sawfowl.regionguard.api.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class ClaimedByPlayer {

	public ClaimedByPlayer() {}

	public ClaimedByPlayer(Long blocks, Long regions) {
		this.blocks = blocks;
		this.regions = regions;
	}

	@Setting("Blocks")
	private Long blocks;
	@Setting("Regions")
	private Long regions;

	public Long getBlocks() {
		return blocks;
	}

	public Long getRegions() {
		return regions;
	}

	public ClaimedByPlayer setBlocks(Long limit) {
		blocks = limit;
		return this;
	}

	public ClaimedByPlayer setRegions(Long limit) {
		regions = limit;
		return this;
	}

}
