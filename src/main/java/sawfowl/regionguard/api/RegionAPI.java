package sawfowl.regionguard.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.api.data.ChunkNumber;
import sawfowl.regionguard.api.data.Region;

public interface RegionAPI {

	/**
	 * Getting all registered flag types.
	 */
	List<Flags> getFlags();

	/**
	 * Update global region data.
	 * 
	 * @param serverWorld - world
	 * @param region - world region.
	 */
	public void updateGlobalRegionData(ServerWorld serverWorld, Region region);

	/**
	 * Adding a temporary region
	 * 
	 * @param region
	 */
	public void addTempRegion(Region region);

	/**
	 * Removing a temporary region
	 * 
	 */
	public void removeTempRegion(Region region);

	/**
	 * Removing a temporary region
	 * 
	 * @param uuid - owner uuid
	 */
	public void removeTempRegion(UUID uuid);

	/**
	 * Get a temporary region
	 * 
	 * @param uuid - owner uuid
	 * @return Optional<Region> or empty
	 */
	public Optional<Region> getTempRegion(UUID uuid);

	/**
	 * Obtaining a type of region selector from the player.
	 * 
	 * @param uuid
	 * @return Player-selected region selector type, or default selection type.
	 */
	public SelectorTypes getSelectorType(UUID uuid);

	/**
	 * Set a type of region selector for the player.
	 * 
	 * @param selectorType - SelectorTypes
	 */
	public void setSelectorType(ServerPlayer player, SelectorTypes selectorType);

	/**
	 * Set a type of region selector for the player.
	 * 
	 * @param uuid - player uuid
	 * @param selectorType - SelectorTypes
	 */
	public void setSelectorType(UUID uuid, SelectorTypes selectorType);

	/**
	 * Get world region.
	 * 
	 * @param serverWorld - the world of the region.
	 * @return Region
	 */
	public Region getGlobalRegion(ServerWorld serverWorld);

	/**
	 * Get world region.
	 * 
	 * @param worldkey - the world key of the region.
	 * @return Region
	 */
	public Region getGlobalRegion(ResourceKey worldkey);

	/**
	 * Getting all registered regions except globals.
	 * 
	 * @return List<Region>
	 */
	public List<Region> getRegions();

	/**
	 * Obtaining a map of regions by worlds. <br>
	 * Without global regions.
	 * 
	 * @return List<Region>
	 */
	public Map<ResourceKey, Map<ChunkNumber, ArrayList<Region>>> getRegionsPerWorld();

	/**
	 * Region registration. <br>
	 * The method does not save the region data to disk.
	 * 
	 * @param region - Registrable region.
	 */
	public void registerRegion(Region region);

	/**
	 * Region registration in asynchronous mode.
	 * 
	 * @param region - Registrable region.
	 */
	public void registerRegionAsync(Region region);

	/**
	 * Saving region data to disk.
	 * 
	 * @param region - Preservable region..
	 */
	public void saveRegion(Region region);

	/**
	 * Deleting a region.
	 * 
	 * @param region - Removable region.
	 */
	public void deleteRegion(Region region);

	/**
	 * Search for a region in the world using the specified coordinates. <br>
	 * If the region contains child regions, the deepest child region will be returned.
	 * 
	 * @param world - The world in which the search will be performed.
	 * @param position - Checkable position.
	 * @return - Found region or global region.
	 */
	public Region findRegion(ServerWorld world, Vector3i position);

	/**
	 * Search for a region in the world using the specified coordinates.<br>
	 * If the region contains child regions, the deepest child region will be returned.
	 * 
	 * @param worldkey - The key of the world in which you want to search.
	 * @param position - Checkable position.
	 * @return - Found region or global region.
	 */
	public Region findRegion(ResourceKey worldkey, Vector3i position);

	/**
	 * Obtaining all player regions.
	 * 
	 * @param player - Owner of the regions.
	 * @return - List of player regions.
	 */
	public List<Region> getPlayerRegions(ServerPlayer player);

	/**
	 * Obtaining all player regions.
	 * 
	 * @param playerUUID - UUID of owner of the regions.
	 * @return - List of player regions.
	 */
	public List<Region> getPlayerRegions(UUID playerUUID);

	/**
	 * Getting an item for selecting regions.
	 */
	public ItemStack getWandItem();

	/**
	 * Getting the type of regions creating by the player.
	 * 
	 * @return {@link RegionTypes}
	 */
	public RegionTypes getSelectRegionType(ServerPlayer player);

	/**
	 * Getting the type of regions creating by the player.
	 * 
	 * @param uuid - player uuid
	 * @return {@link RegionTypes}
	 */
	public RegionTypes getSelectRegionType(UUID uuid);

	/**
	 * Specifies the type of regions created by the player. <br>
	 * You cannot assign the types: global, subdivision, unset.
	 */
	public void setCreatingRegionType(ServerPlayer player, RegionTypes claim);

	/**
	 * Specifies the type of regions created by the player. <br>
	 * You cannot assign the types: global, subdivision, unset.
	 */
	public void setCreatingRegionType(UUID uuid, RegionTypes regionType);

	/**
	 * Get the minimum size of the region, which is set in the main configuration file.
	 * 
	 * @param selectorType - Type of region selection.
	 * @return - The minimum number of blocks that should be in the region.
	 */
	public int getMinimalRegionSize(SelectorTypes selectorType);

	/**
	 * Get the number of blocks that belong to the player.
	 * 
	 * @param player - Checked player
	 * @return - The amount of blocks the player owns according to the type of allocation of each region.
	 */
	public long getClaimedBlocks(ServerPlayer player);

	/**
	 * Get the number of regions belonging to the player.
	 * 
	 * @param player - Checked player
	 * @return - The number of regions belonging to the player.
	 */
	public long getClaimedRegions(ServerPlayer player);

	/**
	 * Getting the limit of blocks that can claiming the player.
	 * 
	 * @param player - Checked player
	 * @return - The number of blocks available to the player for claiming.
	 */
	public long getLimitBlocks(ServerPlayer player);

	/**
	 * Getting the limit of regions that can claiming the player.
	 * 
	 * @param player - Checked player
	 * @return - The number of regions available to the player for claiming.
	 */
	public long getLimitClaims(ServerPlayer player);

	/**
	 * Get the maximum number of child regions a player can create. <br>
	 * Each basic region has its own limit. <br>
	 * 
	 * @param player - Checked player
	 * @returnThe number of regions available to the player for creating.
	 */
	public long getLimitSubdivisions(ServerPlayer player);

	/**
	 * Go to the API for visually highlighting regions with a mod on the WECui client.
	 */
	public WorldEditCUIAPI getWorldEditCUIAPI();

}
