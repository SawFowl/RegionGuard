package sawfowl.regionguard.api.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

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

	PlayerLimits getLimits();

	PlayerData setLimits(PlayerLimits limits);

	ClaimedByPlayer getClaimed();

	PlayerData setClaimed(ClaimedByPlayer claimed);

	boolean isEmpty();

	interface Builder extends AbstractBuilder<PlayerData>, org.spongepowered.api.util.Builder<PlayerData, Builder> {

		Builder setLimits(PlayerLimits limits);

		Builder setClaimed(ClaimedByPlayer claimed);

	}

}
