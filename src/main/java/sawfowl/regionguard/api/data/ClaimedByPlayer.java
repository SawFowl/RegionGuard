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
	private Long blocks = 0l;
	@Setting("Regions")
	private Long regions = 0l;

	public Long getBlocks() {
		return blocks;
	}

	public Long getRegions() {
		return regions;
	}

	public ClaimedByPlayer setBlocks(Long limit) {
		blocks = limit;
		if(blocks < 0) blocks = 0l;
		return this;
	}

	public ClaimedByPlayer setRegions(Long limit) {
		regions = limit;
		if(regions < 0) regions = 0l;
		return this;
	}

}
