package sawfowl.regionguard.api.events.world;

import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;

import sawfowl.regionguard.api.data.Region;

public interface RegionChangeInventoryEvent extends RegionWorldEvent, RegionMessageEvent {

	interface Pickup extends RegionChangeInventoryEvent {

		/**
		 * Get {@link ChangeInventoryEvent.Pickup}
		 */
		public ChangeInventoryEvent.Pickup.Pre spongeEvent();

		@SuppressWarnings("unchecked")
		ChangeInventoryEvent.Pickup.Pre getSpongeEvent();

	}

	interface Drop extends RegionChangeInventoryEvent {

		/**
		 * Get {@link ChangeInventoryEvent.Drop}
		 */
		public ChangeInventoryEvent.Drop spongeEvent();

		@SuppressWarnings("unchecked")
		ChangeInventoryEvent.Drop getSpongeEvent();

	}

	/**
	 * Get the entity that is the source of the event.
	 * 
	 * @return entity
	 */
	public Entity getSource();

	/**
	 * Get the {@link Region} where the event occurred.
	 *
	 * @return The world encompassing these block changes
	 */
	public Region getRegion();

	/**
	 * Get protect result.
	 */
	public boolean isAllow();

	/**
	 * Getting a {@link ServerPlayer}.
	 */
	@SuppressWarnings("unchecked")
	public Optional<ServerPlayer> getPlayer();

}
