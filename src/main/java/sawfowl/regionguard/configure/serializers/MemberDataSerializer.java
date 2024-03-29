package sawfowl.regionguard.configure.serializers;

import java.lang.reflect.Type;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.MemberData;

public class MemberDataSerializer implements TypeSerializer<MemberData> {

	@Override
	public MemberData deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return node.virtual() || node.empty() ? null : MemberData.builder().setPlayer(GameProfile.of(node.node("UUID").get(UUID.class, new UUID(0,0)), node.node("Name").getString("UnknownUserName")), TrustTypes.checkType(node.node("TrustLevel").getString("WITHOUT_TRUST"))).build();
	}

	@Override
	public void serialize(Type arg0, @Nullable MemberData data, ConfigurationNode node) throws SerializationException {
		if(data.getName() != null) node.node("Name").set(String.class, data.getName());
		if(data.getUniqueId() != null) node.node("UUID").set(UUID.class, data.getUniqueId());
		if(data.getTrustType() != null) node.node("TrustLevel").set(String.class, data.getTrustType().toString());
		if(!data.isReplaceNameInTitle()) {
			if(!node.node("ReplaceNameInTitle").virtual()) node.removeChild("ReplaceNameInTitle");
		} else node.node("ReplaceNameInTitle").set(Boolean.class, true);
	}

}
