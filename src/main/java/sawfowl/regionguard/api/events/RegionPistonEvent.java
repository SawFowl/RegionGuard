package sawfowl.regionguard.api.events;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.server.ServerLocation;

import sawfowl.regionguard.api.data.Region;

public interface RegionPistonEvent {

	public interface OneRegion extends RegionPistonEvent, RegionMessageEvent, Event, Cancellable {

		/**
		 * Get protect result.
		 */
		public boolean isAllowMove();

	}

	public interface Grief extends RegionPistonEvent, RegionMessageEvent, Event, Cancellable {

		/**
		 * Getting the region to which the block is attempted to move or vice versa.
		 */
		public Region getGriefedRegion();

		/**
		 * Get protect result.
		 */
		public boolean isAllowGrief();

	}

	/**
	 * Getting piston direction.
	 */
	public Direction getDirection();

	/**
	 * Get {@link ChangeBlockEvent.Pre} event.
	 */
	public ChangeBlockEvent.Pre getSpongeEvent();

	/**
	 * Getting the region in which the piston is located.
	 */
	public Region getRegion();

	/**
	 * Getting a collection of all blocks that are moved by the piston.
	 */
	public List<ServerLocation> getPistonMovedBlocks();

	/**
	 * Getting the {@link ServerPlayer} who activated the piston.
	 */
	Optional<ServerPlayer> getPlayer();

	/**
	 * Getting the {@link Entity} who activated the piston.
	 */
	Optional<Entity> getEntity();

}
