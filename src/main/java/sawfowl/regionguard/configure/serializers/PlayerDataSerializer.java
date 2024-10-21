package sawfowl.regionguard.configure.serializers;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;

public class PlayerDataSerializer implements TypeSerializer<PlayerData> {

	@Override
	public PlayerData deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return node.virtual() || node.empty() ? PlayerData.zero() : PlayerData.of(
				PlayerLimits.of(
					node.node("Limit", "Blocks").getLong(0),
					node.node("Limit", "Claims").getLong(0),
					node.node("Limit", "Subdivisions").getLong(0),
					node.node("Limit", "Members").getLong(0)
				),
				ClaimedByPlayer.of(
					node.node("Claimed", "Blocks").getLong(0),
					node.node("Claimed", "Regions").getLong(0)
				)
			);
	}

	@Override
	public void serialize(Type type, @Nullable PlayerData data, ConfigurationNode node) throws SerializationException {
		if(data == null) return;
		node.node("Limit", "Blocks").set(data.getLimits().getBlocks());
		node.node("Limit", "Claims").set(data.getLimits().getRegions());
		node.node("Limit", "Subdivisions").set(data.getLimits().getSubdivisions());
		node.node("Limit", "Members").set(data.getLimits().getMembers());
		node.node("Claimed", "Blocks").set(data.getClaimed().getBlocks());
		node.node("Claimed", "Regions").set(data.getClaimed().getRegions());
	}

}
