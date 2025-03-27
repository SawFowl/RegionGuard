package sawfowl.regionguard.implementsapi.storageevents;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.storage.PlayerDataStorage;
import sawfowl.regionguard.api.events.storage.SetStorageEvent.Player;

public class SetPlayerStorageEvent implements Player {

	private final Cause cause;
	private PlayerDataStorage storage;
	public SetPlayerStorageEvent(RegionGuard plugin) {
		cause = Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, plugin.getPluginContainer()).build(), plugin.getPluginContainer());
	}

	@Override
	public Cause cause() {
		return cause;
	}

	@Override
	public @Nullable PlayerDataStorage getStorage() {
		return storage;
	}

	@Override
	public void setStorage(PlayerDataStorage storage) {
		this.storage = storage;
	}

}
