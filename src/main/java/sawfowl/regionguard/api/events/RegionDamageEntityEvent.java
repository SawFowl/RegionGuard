package sawfowl.regionguard.api.events;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import sawfowl.regionguard.api.data.Region;

public interface RegionDamageEntityEvent extends Event, RegionMessageEvent, Cancellable {

	/**
	 * Get the {@link Region} where the event occurred.
	 *
	 * @return The world encompassing these block changes
	 */
	public Region getRegion();

	/**
	 * Getting the {@link ServerPlayer} if he is the cause of the event.
	 */
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Get protect result.
	 */
	public boolean isAllowDamage();

	/**
	 * Get {@link DamageEntityEvent}.
	 */
	public DamageEntityEvent spongeEvent();

}
