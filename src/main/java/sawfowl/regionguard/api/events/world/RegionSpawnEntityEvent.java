package sawfowl.regionguard.api.events.world;

import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

import sawfowl.regionguard.api.data.Region;

public interface RegionSpawnEntityEvent extends RegionMessageEvent, RegionWorldEvent {

	/**
	 * Get the {@link Entity} that is the source of the event.
	 * 
	 * @return entity
	 */
	public Optional<Entity> getEntity();

	/**
	 * Get the {@link ServerPlayer} that is the source of the event.
	 * 
	 * @return player
	 */
	@SuppressWarnings("unchecked")
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Get the {@link Region} where the event occurred.
	 */
	public Region getRegion();

	/**
	 * Get protect result.
	 */
	public boolean isAllow();

	@SuppressWarnings("unchecked")
	public SpawnEntityEvent.Pre getSpongeEvent();

}
