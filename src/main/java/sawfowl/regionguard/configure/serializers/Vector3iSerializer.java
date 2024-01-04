package sawfowl.regionguard.configure.serializers;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.math.vector.Vector3i;

public class Vector3iSerializer implements TypeSerializer<Vector3i> {

	@Override
	public Vector3i deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return node.virtual() || node.empty() ? Vector3i.ZERO : Vector3i.from(
				node.node("X").getInt(0),
				node.node("Y").getInt(0),
				node.node("Z").getInt(0)
			);
	}

	@Override
	public void serialize(Type type, @Nullable Vector3i vector3i, ConfigurationNode node) throws SerializationException {
		node.node("X").set(vector3i.x());
		node.node("Y").set(vector3i.y());
		node.node("Z").set(vector3i.z());
	}

}
