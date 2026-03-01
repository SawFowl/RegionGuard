package sawfowl.regionguard.api.data;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

/**
 * Using this interface, you can search for a region that belongs to a particular world.<br>
 * You cannot add or remove a region through this interface, instead use the main API of the plugin.
 */
public interface WorldRegions {

	/**
	 * The key of the world.
	 */
	ResourceKey getWorldKey();

	/**
	 * Get world region.
	 * 
	 * @return Region
	 */
	Region getGlobal();

	/**
	 * Getting all registered regions except global.<br>
	 * This collection is not modifiable.
	 * 
	 * @return Collection<Region>
	 */
	Collection<Region> getRegions();

	/**
	 * Search for a region in the world using the specified coordinates. <br>
	 * If the region contains child regions, the deepest child region will be returned.
	 * 
	 * @param position - Checkable position.
	 * @return - Found region or global region.
	 */
	Region findRegion(Vector3i position);

	/**
	 * Search for a region in the world using the specified coordinates. <br>
	 * If the region contains child regions, the deepest child region will be returned.
	 * 
	 * @param position - Checkable position.
	 * @return - Found region or global region.
	 */
	Optional<Region> findRegion(Vector3i position, Predicate<Region> filter);

	/**
	 * Search for the region with which the intersection occurs.
	 * 
	 * @param region - Original region.
	 * @return The first region found with which there is an intersection.<br>Or the original region if no intersection is found.
	 */
	Region findIntersectsRegion(Region region);

	/**
	 * The method will return the total number of regions related to a particular world.<br>
	 * The global region is not included in this number.
	 */
	int total();

	/**
	 * An attempt to get a world object.<br>
	 * It may be empty if the world does not exist or is not loaded.
	 */
	default Optional<ServerWorld> getWorld() {
		return Sponge.server().worldManager().world(getWorldKey());
	}

}
