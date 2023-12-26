package sawfowl.regionguard.configure.serializers;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.api.data.Cuboid;

public class CuboidSerializer implements TypeSerializer<Cuboid> {

	@Override
	public Cuboid deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return node.virtual() || node.empty() ? null : Cuboid.of(SelectorTypes.checkType(node.node("SelectorType").getString()), node.node("Min").get(Vector3i.class), node.node("Max").get(Vector3i.class));
	}

	@Override
	public void serialize(Type type, @Nullable Cuboid cuboid, ConfigurationNode node) throws SerializationException {
		node.node("Min").set(Vector3i.class, cuboid.getMin());
		node.node("Max").set(Vector3i.class, cuboid.getMax());
		node.node("SelectorType").set(String.class, cuboid.getSelectorType().toString());
	}

}
