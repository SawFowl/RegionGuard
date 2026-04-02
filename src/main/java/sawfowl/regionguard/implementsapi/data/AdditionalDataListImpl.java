package sawfowl.regionguard.implementsapi.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonObject;

import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.api.serializetools.ItemStackSerializerType;
import sawfowl.localeapi.api.services.ConfigurationService;
import sawfowl.regionguard.api.RegionSerializerCollection;
import sawfowl.regionguard.api.data.AdditionalData;

public class AdditionalDataListImpl {

	public AdditionalDataListImpl() {}

	public AdditionalDataListImpl(Map<String, JsonObject> rawData) {
		this.rawData = rawData;
	}

	public AdditionalDataListImpl(JsonObject asJson) {
		asJson.asMap().forEach((k, v) -> {
			if(v.isJsonObject()) rawData.put(k, v.getAsJsonObject());
		});
	}

	private Map<String, AdditionalData> additionalData = new HashMap<>();
	private Map<String, JsonObject> rawData = new HashMap<>();

	public <T extends AdditionalData> void set(String key, AdditionalData data) {
		if(additionalData.containsKey(key)) additionalData.remove(key);
		if(rawData.containsKey(key)) rawData.remove(key);
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
			var config = ConfigurationService.getInstance().createVirtualReferencedConfig(clazz).setItemStackSerializerType(ItemStackSerializerType.JSON).addSerializers(RegionSerializerCollection.COLLETCTION).setType(ConfigTypes.HOCON).build();
			T data = config.convertFromJson(rawData.get(key));
			if(data == null) return Optional.empty();
			set(key, data);
			return Optional.ofNullable(data);
		}
		return Optional.empty();
	}

	public JsonObject serialize() {
		JsonObject json = new JsonObject();
		additionalData.forEach((k, v) -> {
			var jsonE = v.toJsonObject();
			if(jsonE == null) jsonE = ConfigurationService.getInstance().createVirtualReferencedConfig(v).setItemStackSerializerType(ItemStackSerializerType.JSON).addSerializers(RegionSerializerCollection.COLLETCTION).setType(ConfigTypes.HOCON).build().toJson(JsonObject.class);
			if(jsonE != null) json.add(k, jsonE);
		});
		rawData.forEach((k, v) -> {
			if(!json.has(k)) json.add(k, v);
		});
		return json;
	}

	public Map<String, JsonObject> getRawMap() {
		Map<String, JsonObject> raw = new HashMap<>(rawData);
		additionalData.forEach((k, v) -> {
			 if(raw.containsKey(k)) raw.remove(k);
			var json = v.toJsonObject();
			if(json == null) json = ConfigurationService.getInstance().createVirtualReferencedConfig(v).setItemStackSerializerType(ItemStackSerializerType.JSON).addSerializers(RegionSerializerCollection.COLLETCTION).setType(ConfigTypes.HOCON).build().toJson(JsonObject.class);
			raw.put(k, json);
		});
		return raw;
	}

	public AdditionalDataListImpl copy() {
		AdditionalDataListImpl copy = new AdditionalDataListImpl();
		getRawMap().forEach((k, v) -> copy.rawData.put(k, v));
		return copy;
	}

}
