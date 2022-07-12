package sawfowl.regionguard.api.events;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.entity.InteractEntityEvent;

public interface RegionInteractEntityEvent extends RegionEvent {

	/**
	 * Get the entity that is the target of the event.
	 * 
	 * @return entity
	 */
	public Entity getEntity();

	/**
	 * Get the {@link ServerPlayer} that is the source of the event.
	 */
	public ServerPlayer getPlayer();

	/**
	 * Get {@link InteractEntityEvent}
	 */
	public InteractEntityEvent getSpongeInteractEntityEvent();

	/**
	 * Checks that the interaction with the block was performed by the primary hand.
	 */
	public boolean isPrimary();

	/**
	 * Get protect result.
	 */
	public boolean isAllowInteract();

}
