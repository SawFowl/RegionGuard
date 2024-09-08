package sawfowl.regionguard.api.events;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Event;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.world.RegionMessageEvent;

public interface RegionManagementEvent extends Event, RegionMessageEvent, Cancellable {

	/**
	 * Get plugin {@link Cause}.
	 */
	public Cause cause();

	/**
	 * Getting a {@link ServerPlayer}.
	 */
	@SuppressWarnings("unchecked")
	public ServerPlayer getPlayer();

	/**
	 * Get the {@link Region}.
	 */
	public Region getRegion();

}
