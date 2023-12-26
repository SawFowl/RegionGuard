package sawfowl.regionguard.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.plugin.PluginContainer;

import sawfowl.regionguard.api.data.AdditionalData;

public class AdditionalDataPluginsMap extends HashMap<String, Map<String, ? extends AdditionalData>> {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public <T extends AdditionalData> Optional<T> getData(PluginContainer container, String dataName, Class<T> dataClass) {
		return !containsKey(container.metadata().id()) || !get(container.metadata().id()).containsKey(dataName) ? Optional.empty() : Optional.ofNullable((T) get(container.metadata().id()).get(dataName));
	}

}
