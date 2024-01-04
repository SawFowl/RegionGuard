package sawfowl.regionguard.api.events.world;

import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.block.InteractBlockEvent;

import sawfowl.regionguard.api.data.Region;

public interface RegionInteractBlockEvent extends RegionWorldEvent, RegionMessageEvent {

	/**
	 * Get plugin {@link Cause}.
	 */
	public Cause cause();

	/**
	 * Getting the {@link InteractBlockEvent}.
	 */
	@SuppressWarnings("unchecked")
	public InteractBlockEvent getSpongeEvent();

	/**
	 * Getting the {@link Entity}.
	 */
	public Entity getEntity();

	/**
	 * Getting the {@link ServerPlayer} if he is the cause of the event.
	 */
	@SuppressWarnings("unchecked")
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Get the {@link Region} where the event occurred.
	 */
	public Region getRegion();

	/**
	 * Checks that the interaction with the block was performed by the primary hand.
	 */
	public boolean isPrimary();

	/**
	 * Get protect result.
	 */
	boolean isAllowInteract();

}
