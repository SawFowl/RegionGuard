package sawfowl.regionguard.api.events;

import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.api.data.Region;

public interface RegionChangeInventoryEvent extends Event, Cancellable {

	interface Pickup extends RegionChangeInventoryEvent {

		/**
		 * Get {@link ChangeInventoryEvent.Pickup}
		 */
		public ChangeInventoryEvent.Pickup spongeEvent();

	}

	interface Drop extends RegionChangeInventoryEvent {

		/**
		 * Get {@link ChangeInventoryEvent.Drop}
		 */
		public ChangeInventoryEvent.Drop spongeEvent();

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
	 * Getting the player if he is the cause of the event.
	 */
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Set the message that will be shown to the player if the event is canceled.
	 */
	public void setMessage(Component message);

	/**
	 * Getting a message that will be shown to the player when the message is canceled.
	 */
	public Optional<Component> getMessage();

}
