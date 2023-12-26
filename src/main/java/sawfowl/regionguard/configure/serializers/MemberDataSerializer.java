package sawfowl.regionguard.configure.serializers;

import java.lang.reflect.Type;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.data.MemberDataImpl;

public class MemberDataSerializer implements TypeSerializer<MemberData> {

	@Override
	public MemberData deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return node.virtual() || node.empty() ? null : new MemberDataImpl(node.node("Name").getString("UnknownUserName"), node.node("UUID").get(UUID.class, new UUID(0,0)), TrustTypes.checkType(node.node("TrustLevel").getString("WITHOUT_TRUST")));
	}

	@Override
	public void serialize(Type arg0, @Nullable MemberData data, ConfigurationNode node) throws SerializationException {
		node.node("Name").set(String.class, data.getName());
		node.node("UUID").set(UUID.class, data.getUniqueId());
		node.node("TrustLevel").set(String.class, data.getTrustType().toString());
		if(!data.isReplaceNameInTitle()) {
			if(!node.node("ReplaceNameInTitle").virtual()) node.removeChild("ReplaceNameInTitle");
		} else node.node("ReplaceNameInTitle").set(Boolean.class, true);
	}

}
