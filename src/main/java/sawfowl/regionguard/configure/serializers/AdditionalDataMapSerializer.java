package sawfowl.regionguard.configure.serializers;

import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import sawfowl.regionguard.api.data.AdditionalData;
import sawfowl.regionguard.api.data.AdditionalDataMap;
import sawfowl.regionguard.implementsapi.data.AdditionalDataHashMap;

public class AdditionalDataMapSerializer implements TypeSerializer<AdditionalDataMap<? extends AdditionalData>> {

	@SuppressWarnings("unchecked")
	@Override
	public AdditionalDataMap<? extends AdditionalData> deserialize(Type type, ConfigurationNode node) throws SerializationException {
		return node.virtual() || node.empty() ? null : node.get(AdditionalDataHashMap.class);
	}

	@Override
	public void serialize(Type type, @Nullable AdditionalDataMap<? extends AdditionalData> map, ConfigurationNode node) throws SerializationException {
		if(map == null || map.isEmpty()) return;
		node.set(AdditionalDataHashMap.class, map);
	}

}
