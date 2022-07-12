package sawfowl.regionguard.api.events;

import java.util.Optional;

import sawfowl.regionguard.api.data.Region;

public interface RegionCreateEvent extends RegionEvent {

	/**
	 * Getting a parent {@link Region} if it exists.
	 */
	public Optional<Region> getParrent();

	/**
	 * Checking if the region being created is a child region.
	 */
	public boolean isSubdivision();

}
