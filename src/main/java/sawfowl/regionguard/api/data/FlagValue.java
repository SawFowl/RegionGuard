package sawfowl.regionguard.api.data;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import net.kyori.adventure.builder.AbstractBuilder;

@ConfigSerializable
public interface FlagValue extends DataSerializable {

	static Builder builder() {
		return Sponge.game().builderProvider().provide(Builder.class);
	}

	static FlagValue simple(boolean value) {
		return builder().setValue(value).build();
	}

	static FlagValue of(boolean value, String source, String target) {
		return builder().setValue(value).setSource(source).setTarget(target).build();
	}

	/**
	 * Initiator of the event.
	 */
	String getSource();

	/**
	 * Event target.
	 */
	String getTarget();

	/**
	 * Value of the flag.
	 */
	boolean getValue();

	default Tristate asTristate() {
		return Tristate.fromBoolean(getValue());
	}

	default boolean equalsTo(String source, String target) {
		return (source == null ? getSource().equals("all") : source.equals(getSource())) && (target == null ? getTarget().equals("all") : target.equals(getTarget()));
	}

	interface Builder extends AbstractBuilder<FlagValue>, org.spongepowered.api.util.Builder<FlagValue, Builder> {

		Builder setValue(boolean value);

		Builder setSource(String id);

		Builder setTarget(String id);

		default Builder setSource(Entity entity) {
			return setSource(EntityTypes.registry().findValueKey(entity.type()).map(ResourceKey::asString).orElse("all"));
		}

	}

}
