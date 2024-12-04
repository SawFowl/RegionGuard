package sawfowl.regionguard.api.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import com.google.gson.JsonObject;

import net.kyori.adventure.builder.AbstractBuilder;

@ConfigSerializable
public interface PlayerData extends DataSerializable {

	static Builder builder() {
		return Sponge.game().builderProvider().provide(Builder.class);
	}

	static PlayerData of(PlayerLimits limits, ClaimedByPlayer claimed) {
		return builder().setLimits(limits).setClaimed(claimed).build();
	}

	static PlayerData zero() {
		return builder().setLimits(PlayerLimits.zero()).setClaimed(ClaimedByPlayer.zero()).build();
	}

	/**
	 * Getting additional limits available to the player.
	 */
	PlayerLimits getLimits();

	/**
	 * Set block limits, region limits, and so on for the player.
	 */
	PlayerData setLimits(PlayerLimits limits);

	/**
	 * Getting the volume of blocks claimed by the player and the number of regions created.
	 */
	ClaimedByPlayer getClaimed();

	/**
	 * Changing the volume of player-owned blocks and regions.
	 */
	PlayerData setClaimed(ClaimedByPlayer claimed);

	/**
	 * Convert all data of this object to Json format.
	 */
	JsonObject asJson();

	interface Builder extends AbstractBuilder<PlayerData>, org.spongepowered.api.util.Builder<PlayerData, Builder> {

		Builder setLimits(PlayerLimits limits);

		Builder setClaimed(ClaimedByPlayer claimed);

		/**
		 * Getting object data from Json.
		 */
		PlayerData fromJson(JsonObject jsonObject);

	}

}
