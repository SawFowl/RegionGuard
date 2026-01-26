package sawfowl.regionguard.api.events.storage;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.api.event.Event;

import sawfowl.regionguard.api.data.storage.PlayerDataStorage;
import sawfowl.regionguard.api.data.storage.RegionDataStorage;

public interface SetStorageEvent extends Event {

	interface Region extends SetStorageEvent {

		@Nullable RegionDataStorage getStorage();

		void setStorage(RegionDataStorage storage);

	}

	interface Player extends SetStorageEvent {

		@Nullable PlayerDataStorage getStorage();

		void setStorage(PlayerDataStorage storage);

	}

}
