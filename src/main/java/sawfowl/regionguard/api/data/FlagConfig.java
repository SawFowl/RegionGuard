package sawfowl.regionguard.api.data;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;

import net.kyori.adventure.builder.AbstractBuilder;

public interface FlagConfig extends DataSerializable {

	private static Builder builder() {
		return Sponge.game().builderProvider().provide(Builder.class);
	}

	/**
	 * Build a flag configuration with the specified name and parameters.
	 */
	static FlagConfig of(@NotNull String name, @NotNull FlagSettings settings) {
		return builder().setName(name).setSettings(settings).build();
	}

	String getName();

	FlagSettings getSettings();

	interface Builder extends AbstractBuilder<FlagConfig>, org.spongepowered.api.util.Builder<FlagConfig, Builder> {

		/**
		 * Setting the name of the flag.
		 */
		Builder setName(String name);

		/**
		 * Setting the flag parameters.
		 */
		Builder setSettings(FlagSettings flagSettings);

	}

}
