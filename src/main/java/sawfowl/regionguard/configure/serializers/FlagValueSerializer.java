package sawfowl.regionguard.configure.serializers;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.regionguard.api.data.FlagValue;

public class FlagValueSerializer implements TypeSerializer<FlagValue> {

	@Override
	public FlagValue deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return node.virtual() || node.empty() ? null : FlagValue.of(node.node("Value").getBoolean(), node.node("Source").getString("all"), node.node("Target").getString("all"));
	}

	@Override
	public void serialize(Type type, @Nullable FlagValue flagValue, ConfigurationNode node) throws SerializationException {
		if(flagValue == null) return;
		node.node("Value").set(Boolean.class, flagValue.getValue());
		node.node("Source").set(String.class, flagValue.getSource());
		node.node("Target").set(String.class, flagValue.getTarget());
	}

}
