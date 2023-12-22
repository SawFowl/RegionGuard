package sawfowl.regionguard.api.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;

import net.kyori.adventure.builder.AbstractBuilder;

public interface FlagConfig extends DataSerializable {

	private static Builder builder() {
		return Sponge.game().builderProvider().provide(Builder.class);
	}

	static FlagConfig of(String name, FlagSettings settings) {
		return builder().setName(name).setSettings(settings).build();
	}

	String getName();

	FlagSettings getSettings();

	interface Builder extends AbstractBuilder<FlagConfig>, org.spongepowered.api.util.Builder<FlagConfig, Builder> {

		Builder setName(String name);

		Builder setSettings(FlagSettings flagSettings);

	}

}
