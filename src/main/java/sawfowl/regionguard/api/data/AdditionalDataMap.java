package sawfowl.regionguard.api.data;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.plugin.PluginContainer;

public interface AdditionalDataMap<T extends AdditionalData> extends Map<String, Map<String, T>> {

	Optional<T> getData(PluginContainer container, String dataName, Class<? extends AdditionalData> clazz);

	void checkPlugin(PluginContainer container);

	void remove(PluginContainer container, String dataName);

	void put(PluginContainer container, String dataName, AdditionalData additionalData);

}
