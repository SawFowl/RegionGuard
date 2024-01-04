package sawfowl.regionguard.api.events.world;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.server.ServerLocation;

import sawfowl.regionguard.api.data.Region;

public interface RegionPistonEvent extends RegionWorldEvent, RegionMessageEvent  {

	public interface OneRegion extends RegionPistonEvent {

		/**
		 * Get protect result.
		 */
		public boolean isAllowMove();

	}

	public interface Grief extends RegionPistonEvent {

		/**
		 * Getting all regions with which the piston interacts.<br>
		 * The collection does not contain the region in which the piston is located.<br>
		 * Because of an incomprehensible error with the list of locations when searching for a region is taken not only the original position, but also the next following it.
		 */
		List<Region> getAffectedRegions();

		/**
		 * Get protect result.
		 */
		public boolean isAllowGrief();

	}

	@SuppressWarnings("unchecked")
	public ChangeBlockEvent.Pre getSpongeEvent();

	/**
	 * Getting piston direction.
	 */
	public Direction getDirection();

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
	@SuppressWarnings("unchecked")
	Optional<ServerPlayer> getPlayer();

	/**
	 * Getting the {@link Entity} who activated the piston.
	 */
	Optional<Entity> getEntity();

}
