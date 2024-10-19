package sawfowl.regionguard.api.events.world;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;

import sawfowl.regionguard.api.data.Region;

public interface RegionImpactEvent extends RegionWorldEvent, RegionMessageEvent {

	interface Block extends RegionImpactEvent {

		/**
		 * Getting the {@link CollideBlockEvent.Impact}.
		 */
		@SuppressWarnings("unchecked")
		public CollideBlockEvent.Impact getSpongeEvent();
		
	}

	interface Entity extends RegionImpactEvent {

		/**
		 * Getting the {@link CollideEntityEvent.Impact}.
		 */
		@SuppressWarnings("unchecked")
		public CollideEntityEvent.Impact getSpongeEvent();
		
	}

	/**
	 * Get the entity that is the source of the event.
	 * 
	 * @return entity
	 */
	public org.spongepowered.api.entity.Entity getSource();

	/**
	 * Get protect result.
	 */
	public boolean isAllow();

	/**
	 * Getting the {@link ServerPlayer} if he is the cause of the event.
	 */
	@SuppressWarnings("unchecked")
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Get the {@link Region} where the event occurred.
	 */
	public Region getRegion();

}
