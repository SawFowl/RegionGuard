package sawfowl.regionguard.api.events.world;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.entity.InteractEntityEvent;

public interface RegionInteractEntityEvent extends RegionWorldEvent, RegionMessageEvent {

	interface Primary extends RegionInteractEntityEvent {

		@SuppressWarnings("unchecked")
		InteractEntityEvent.Primary getSpongeEvent();

	}

	interface Secondary extends RegionInteractEntityEvent {

		@SuppressWarnings("unchecked")
		InteractEntityEvent.Secondary getSpongeEvent();

	}

	@SuppressWarnings("unchecked")
	InteractEntityEvent getSpongeEvent();

	/**
	 * Get the entity that is the target of the event.
	 * 
	 * @return entity
	 */
	public Entity getEntity();

	/**
	 * Get the {@link ServerPlayer} that is the source of the event.
	 */
	@SuppressWarnings("unchecked")
	public ServerPlayer getPlayer();

	/**
	 * Get protect result.
	 */
	public boolean isAllowInteract();

}
