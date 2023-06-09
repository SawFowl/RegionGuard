package sawfowl.regionguard.api.events;

import java.util.Optional;

import org.spongepowered.math.vector.Vector3i;

public interface RegionResizeEvent extends RegionEvent {

	/**
	 * Getting a new corner position.
	 */
	public Optional<Vector3i> getNewCorner();

	/**
	 * Getting the opposite corner.
	 */
	public Vector3i getOppositeCorner();

}
