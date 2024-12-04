package sawfowl.regionguard.api.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import com.google.gson.JsonObject;

import net.kyori.adventure.builder.AbstractBuilder;

/**
 * Getting and changing the volume of player-owned blocks and regions.
 */
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

	/**
	 * Convert all data of this object to Json format.
	 */
	JsonObject asJson();

	interface Builder extends AbstractBuilder<ClaimedByPlayer>, org.spongepowered.api.util.Builder<ClaimedByPlayer, Builder> {

		Builder setBlocks(long value);

		Builder setRegions(long value);

		/**
		 * Getting object data from Json.
		 */
		ClaimedByPlayer fromJson(JsonObject jsonObject);

	}

}
