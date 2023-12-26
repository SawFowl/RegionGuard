package sawfowl.regionguard.configure.serializers;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.data.PlayerDataImpl;

public class PlayerDataSerializer implements TypeSerializer<PlayerData> {

	@Override
	public PlayerData deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return node.virtual() || node.empty() ? PlayerData.zero() : (PlayerData) node.get(PlayerDataImpl.class);
	}

	@Override
	public void serialize(Type type, @Nullable PlayerData data, ConfigurationNode node) throws SerializationException {
		node.set(PlayerDataImpl.class, (PlayerDataImpl) (data instanceof PlayerDataImpl ? data : PlayerData.of(data.getLimits(), data.getClaimed())));
	}

}
