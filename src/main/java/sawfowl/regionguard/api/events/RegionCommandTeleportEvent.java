package sawfowl.regionguard.api.events;

import org.spongepowered.math.vector.Vector3d;

import sawfowl.regionguard.api.data.Region;

public interface RegionCommandTeleportEvent extends RegionManagementEvent {

	/**
	 * Getting the region in which the command is executed.
	 */
	public Region from();

	/**
	 * Getting the original destination region.
	 */
	public Region getOriginalDestinationRegion();

	/**
	 * Getting the player's original position.
	 */
	public Vector3d getOriginalLocation();

	/**
	 * Getting the original position for teleporting the player.
	 */
	public Vector3d getOriginalDestinationLocation();

	/**
	 * Getting the position for teleporting the player.
	 */
	public Vector3d getDestinationLocation();

	/**
	 * Assigning a position for player teleportation.
	 */
	public void setDestination(Vector3d location);

}
