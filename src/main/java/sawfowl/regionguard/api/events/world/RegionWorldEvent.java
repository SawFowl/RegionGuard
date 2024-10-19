package sawfowl.regionguard.api.events.world;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.server.ServerWorld;

import sawfowl.regionguard.api.data.Region;

public interface RegionWorldEvent extends Event, Cancellable {

	/**
	 * Get the {@link Region} where the event occurred.
	 */
	Region getRegion();

	/**
	 * Get event {@link ServerWorld}
	 */
	ServerWorld getWorld();

	/**
	 * Getting the Sponge event.<br>
	 * May be `null` if the original event is not a Sponge event.<br>
	 */
	@Nullable
	<T extends Event> T getSpongeEvent();

}
