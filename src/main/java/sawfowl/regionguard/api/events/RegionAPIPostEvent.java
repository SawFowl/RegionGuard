package sawfowl.regionguard.api.events;

import org.spongepowered.api.event.Event;

import sawfowl.regionguard.api.RegionAPI;

/**
 * This interface is designed to access the plugin's API and check status. <br>
 * Use the child interface {@link PostAPI} to access the plugin API. <br>
 * Use the child interface {@link CompleteLoadRegions} to start working with the regions.
 */
public interface RegionAPIPostEvent extends Event {

	/**
	 * At this stage you can change some parameters that have no explicit connection to the game world.
	 */
	public interface PostAPI extends RegionAPIPostEvent {

		public RegionAPI getAPI();

	}

	/**
	 * At this point, RegionGuard reports that the regions are loaded and provides information about the total number of loaded regions. <br>
	 * From this moment, you can work with the regions and with the game objects.
	 */
	public interface CompleteLoadRegions extends RegionAPIPostEvent {

		public long getTotalLoaded();

	}

}
