package sawfowl.regionguard.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.plugin.PluginContainer;

import sawfowl.regionguard.api.data.AdditionalData;
import sawfowl.regionguard.api.data.AdditionalDataMap;

public class AdditionalDataHashMap<T extends AdditionalData> extends HashMap<String, Map<String, T>> implements AdditionalDataMap<T> {

	private static final long serialVersionUID = 1L;

	@Override
	public Optional<T> getData(PluginContainer container, String dataName, Class<? extends AdditionalData> clazz) {
		return !containsKey(container.metadata().id()) || !get(container.metadata().id()).containsKey(dataName) ? Optional.empty() : Optional.ofNullable((T) get(container.metadata().id()).get(dataName));
	}

	@Override
	public void checkPlugin(PluginContainer container) {
		checkPlugin(container.metadata().id());
	}

	@Override
	public void remove(PluginContainer container, String dataName) {
		if(!containsKey(container.metadata().id()) || !get(container.metadata().id()).containsKey(dataName)) return;
		get(container.metadata().id()).remove(dataName);
		if(get(container.metadata().id()).isEmpty()) remove(container.metadata().id());
	}

	@Override
	public void put(PluginContainer container, String dataName, AdditionalData additionalData) {
		put(container.metadata().id(), dataName, additionalData);
	}

	@SuppressWarnings("unchecked")
	public void put(String plugin, String dataName, AdditionalData additionalData) {
		remove(plugin, dataName);
		checkPlugin(plugin);
		(get(plugin)).put(dataName, (T) additionalData);
	}

	public void checkPlugin(String plugin) {
		if(!containsKey(plugin)) put(plugin, new HashMap<>());
	}

	public AdditionalDataHashMap<T> from(Map<String, Map<String, T>> dataMap) {
		dataMap.forEach((plugin,map) -> map.forEach((k,v) -> put(plugin, plugin, v)));
		return this;
	}

}
