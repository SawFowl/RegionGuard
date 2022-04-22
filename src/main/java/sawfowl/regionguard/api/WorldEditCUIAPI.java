package sawfowl.regionguard.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.utils.worldedit.cuiusers.CUIUser;

public interface WorldEditCUIAPI {

	/**
	 * Creating or getting a WECui user.
	 */
	public CUIUser getOrCreateUser(ServerPlayer player);

	/**
	 * Creating or getting a WECui user.
	 */
	public CUIUser getOrCreateUser(UUID uuid);

	/**
	 * Removing a WECui user.
	 */
	public void removeUser(ServerPlayer player);

	/**
	 * Display the region boundaries.
	 * 
	 * @param region - visualization region
	 * @param player - the player who needs to display the boundaries of the region
	 * @param investigating - checking the region type to change the color of the grid
	 * @param tempRegion - whether the region is temporary
	 */
	public void visualizeRegion(Region region, ServerPlayer player, boolean investigating, boolean tempRegion);

	/**
	 * Display the region boundaries.
	 * 
	 * @param region - target region
	 * @param pos1 - first position
	 * @param pos2 - second position
	 * @param player - the player who needs to display the boundaries of the region
	 * @param user - WECui user
	 * @param investigating - checking the region type to change the color of the grid
	 * @param tempRegion - whether the region is temporary
	 */
	public void visualizeRegion(Region region, Vector3i pos1, Vector3i pos2, ServerPlayer player, CUIUser user, boolean investigating, boolean tempRegion);

	/**
	 * Display borders to multiple regions at the same time.
	 * 
	 * @param regions - список регионов для отображения границ
	 * @param player - the player who needs to display the boundaries of the regions
	 * @param investigating - checking the region type to change the color of the grid
	 */
	public void visualizeRegions(List<Region> regions, ServerPlayer player, boolean investigating);

	/**
	 * Remove border highlighting.
	 * 
	 * @param player - target player
	 * @param regionUniqueId - specify a specific region, if necessary
	 */
	public void revertVisuals(ServerPlayer player, UUID regionUniqueId);

	/**
	 * Stop the dynamic area selection.
	 * 
	 * @param player - target player
	 */
	public void stopVisualDrag(ServerPlayer player);

	/**
	 * Dynamic selection of the area in the direction of the player's gaze.
	 * 
	 * @param player - target player
	 * @param pos - position from which the selection will take place in the direction of the view
	 */
	public void sendVisualDrag(ServerPlayer player, Vector3i pos);

	/**
	 * Location of the block the player is looking at.
	 * 
	 * @param player - target player
	 * @param user - WECui user
	 * @param maxDistance - distance to find the block
	 * 
	 * @return @link ServerLocation}
	 */
	public Optional<ServerLocation> getTargetBlock(ServerPlayer player, CUIUser user, int maxDistance);

}
