package sawfowl.regionguard.api.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import net.kyori.adventure.builder.AbstractBuilder;

@ConfigSerializable
public interface ClaimedByPlayer extends DataSerializable {

	static Builder builder() {
		return Sponge.game().builderProvider().provide(Builder.class);
	}

	static ClaimedByPlayer of(long blocks, long regions) {
		return builder().setBlocks(blocks).setRegions(regions).build();
	}

	static ClaimedByPlayer zero() {
		return builder().build();
	}

	Long getBlocks();

	Long getRegions();

	ClaimedByPlayer setBlocks(long value);

	ClaimedByPlayer setRegions(long value);

	interface Builder extends AbstractBuilder<ClaimedByPlayer>, org.spongepowered.api.util.Builder<ClaimedByPlayer, Builder> {

		Builder setBlocks(long value);

		Builder setRegions(long value);

	}

}
