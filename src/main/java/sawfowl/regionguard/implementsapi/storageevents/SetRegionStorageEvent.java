package sawfowl.regionguard.implementsapi.storageevents;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.storage.RegionDataStorage;
import sawfowl.regionguard.api.events.storage.SetStorageEvent.Region;

public class SetRegionStorageEvent implements Region {

	private final Cause cause;
	private RegionDataStorage storage;
	public SetRegionStorageEvent(RegionGuard plugin) {
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	@Override
	public Cause cause() {
		return cause;
	}

	@Override
	public @Nullable RegionDataStorage getStorage() {
		return storage;
	}

	@Override
	public void setStorage(RegionDataStorage storage) {
		this.storage = storage;
	}

}
