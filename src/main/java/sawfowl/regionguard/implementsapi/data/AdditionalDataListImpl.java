package sawfowl.regionguard.implementsapi.data;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.configurate.serialize.SerializationException;

import com.google.gson.JsonObject;

import sawfowl.localeapi.api.serializetools.SerializeOptions;
import sawfowl.regionguard.api.RegionSerializerCollection;
import sawfowl.regionguard.api.data.AdditionalData;

public class AdditionalDataListImpl {

	public AdditionalDataListImpl() {}

	public AdditionalDataListImpl(Map<String, JsonObject> rawData) {
		this.rawData = rawData;
	}

	private Map<String, AdditionalData> additionalData = new HashMap<>();
	private Map<String, JsonObject> rawData = new HashMap<>();

	public <T extends AdditionalData> void add(String key, AdditionalData data) {
		if(additionalData.containsKey(key)) additionalData.remove(key);
		additionalData.put(key, data);
	}

	public void add(String key, JsonObject data) {
		if(rawData.containsKey(key)) rawData.remove(key);
		rawData.put(key, data);
	}

	public void remove(String key) {
		if(additionalData.containsKey(key)) additionalData.remove(key);
		if(rawData.containsKey(key)) rawData.remove(key);
	}

	public boolean contains(String key) {
		return rawData.containsKey(key) || additionalData.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	public <T extends AdditionalData> Optional<T> get(String key, Class<T> clazz) {
		if(additionalData.containsKey(key)) {
			try {
				return Optional.ofNullable((T) additionalData.get(key));
			} catch (Exception e) {
				return Optional.empty();
			}
		} else if(rawData.containsKey(key)) {
			try {
				T data = SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).sink(() -> new BufferedWriter(new StringWriter())).build().createNode().node("Json").set(rawData.get(key)).get(clazz);
				if(data == null) return Optional.empty();
				add(key, data);
				return Optional.ofNullable(data);
			} catch (SerializationException e) {
				e.printStackTrace();
			}
		} return Optional.empty();
	}

	public JsonObject serialize() {
		JsonObject json = new JsonObject();
		additionalData.forEach((k, v) -> {
			try {
				json.add(k, SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).sink(() -> new BufferedWriter(new StringWriter())).build().createNode().node("Json").set(v).get(JsonObject.class));
			} catch (SerializationException e) {
				e.printStackTrace();
			}
		});
		return json;
	}

	public Map<String, JsonObject> getRawMap() {
		Map<String, JsonObject> raw = new HashMap<>();
		additionalData.forEach((k, v) -> {
			JsonObject json = v.toJsonObject();
			try {
				raw.put(k, json != null ? json : SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.registerAll(RegionSerializerCollection.COLLETCTION))).sink(() -> new BufferedWriter(new StringWriter())).build().createNode().node("Json").set(v).get(JsonObject.class));
			} catch (SerializationException e) {
				e.printStackTrace();
			}
		});
		return raw;
	}

}
