package sawfowl.regionguard.api.events;

import org.spongepowered.math.vector.Vector3d;

import sawfowl.regionguard.api.data.Region;

public interface RegionCommandTeleportEvent extends RegionEvent {

	public Region from();

	public Vector3d getOriginalLocation();

	public Vector3d getOriginalDestinationLocation();

	public Vector3d getDestinationLocation();

	public void setDestination(Vector3d location);

}
