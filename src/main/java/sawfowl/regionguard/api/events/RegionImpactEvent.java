package sawfowl.regionguard.api.events;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;

import sawfowl.regionguard.api.data.Region;

public interface RegionImpactEvent extends RegionMessageEvent, Cancellable {

	interface Block extends RegionImpactEvent {

		/**
		 * Getting the {@link CollideBlockEvent.Impact}.
		 */
		public CollideBlockEvent.Impact spongeEvent();
		
	}

	interface Entity extends RegionImpactEvent {

		/**
		 * Getting the {@link CollideEntityEvent.Impact}.
		 */
		public CollideEntityEvent.Impact spongeEvent();
		
	}

	/**
	 * Get the entity that is the source of the event.
	 * 
	 * @return entity
	 */
	public org.spongepowered.api.entity.Entity getEntitySource();

	/**
	 * Get protect result.
	 */
	public boolean isAllow();

	/**
	 * Getting the {@link ServerPlayer} if he is the cause of the event.
	 */
	public Optional<ServerPlayer> getPlayer();

	/**
     * Get the {@link Region} where the event occurred.
     */
	public Region getRegion();

}
