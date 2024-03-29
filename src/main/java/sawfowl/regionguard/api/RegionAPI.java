package sawfowl.regionguard.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.api.data.ChunkNumber;
import sawfowl.regionguard.api.data.FlagConfig;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.worldedit.WorldEditCUIAPI;

public interface RegionAPI {

	/**
	 * Getting all registered flags.
	 */
	Map<String, FlagConfig> getRegisteredFlags();

	/**
	 * FlagConfig registration. Values cannot be null.
	 */
	void registerFlag(String flagName, FlagConfig settings);

	/**
	 * Update global region data.
	 * 
	 * @param serverWorld - world
	 * @param region - world region.
	 */
	void updateGlobalRegionData(ServerWorld serverWorld, Region region);

	/**
	 * Adding a temporary region
	 * 
	 * @param region
	 */
	@Deprecated
	void addTempRegion(Region region);

	/**
	 * Adding a temporary region
	 * 
	 * @param region
	 */
	void addTempRegion(UUID user, Region region);

	/**
	 * Removing a temporary region
	 * 
	 */
	void removeTempRegion(Region region);

	/**
	 * Removing a temporary region
	 * 
	 * @param uuid - owner uuid
	 */
	void removeTempRegion(UUID uuid);

	/**
	 * Get a temporary region
	 * 
	 * @param uuid - owner uuid
	 * @return Optional<Region> or empty
	 */
	Optional<Region> getTempRegion(UUID uuid);

	/**
	 * Obtaining a type of region selector from the player.
	 * 
	 * @param uuid
	 * @return Player-selected region selector type, or default selection type.
	 */
	SelectorTypes getSelectorType(UUID uuid);

	/**
	 * Set a type of region selector for the player.
	 * 
	 * @param selectorType - SelectorTypes
	 */
	void setSelectorType(ServerPlayer player, SelectorTypes selectorType);

	/**
	 * Set a type of region selector for the player.
	 * 
	 * @param uuid - player uuid
	 * @param selectorType - SelectorTypes
	 */
	void setSelectorType(UUID uuid, SelectorTypes selectorType);

	/**
	 * Get world region.
	 * 
	 * @param serverWorld - the world of the region.
	 * @return Region
	 */
	Region getGlobalRegion(ServerWorld serverWorld);

	/**
	 * Get world region.
	 * 
	 * @param worldkey - the world key of the region.
	 * @return Region
	 */
	Region getGlobalRegion(ResourceKey worldkey);

	/**
	 * Getting all registered regions except globals.
	 * 
	 * @return Collection<Region>
	 */
	Collection<Region> getRegions();

	/**
	 * Obtaining a map of regions by worlds. <br>
	 * Without global regions.
	 */
	Map<ResourceKey, Map<ChunkNumber, ArrayList<Region>>> getRegionsPerWorld();

	/**
	 * Region registration. <br>
	 * The method does not save the region data to disk.
	 * 
	 * @param region - Registrable region.
	 */
	void registerRegion(Region region);

	/**
	 * Region registration in asynchronous mode.
	 * 
	 * @param region - Registrable region.
	 */
	void registerRegionAsync(Region region);

	/**
	 * Remove the region from the list of registered regions.<br>
	 * This method will not remove the region from the disk.
	 */
	void unregisterRegion(Region region);

	/**
	 * Saving region data to disk.
	 * 
	 * @param region - Preservable region..
	 */
	void saveRegion(Region region);

	/**
	 * Deleting a region.
	 * 
	 * @param region - Removable region.
	 */
	void deleteRegion(Region region);

	/**
	 * Search for a region in the world using the specified coordinates. <br>
	 * If the region contains child regions, the deepest child region will be returned.
	 * 
	 * @param world - The world in which the search will be performed.
	 * @param position - Checkable position.
	 * @return - Found region or global region.
	 */
	Region findRegion(ServerWorld world, Vector3i position);

	/**
	 * Search for a region in the world using the specified coordinates. <br>
	 * If the region contains child regions, the deepest child region will be returned.
	 * 
	 * @param world - The world in which the search will be performed.
	 * @param position - Checkable position.
	 * @return - Found region or global region.
	 */
	Optional<Region> findRegion(ServerWorld world, Vector3i position, Predicate<Region> filter);

	/**
	 * Search for a region in the world using the specified coordinates.<br>
	 * If the region contains child regions, the deepest child region will be returned.
	 * 
	 * @param worldkey - The key of the world in which you want to search.
	 * @param position - Checkable position.
	 * @return - Found region or global region.
	 */
	Region findRegion(ResourceKey worldkey, Vector3i position);

	/**
	 * Search for the region with which the intersection occurs.
	 * 
	 * @param region - Original region.
	 * @return The first region found with which there is an intersection.<br>Or the original region if no intersection is found.
	 */
	Region findIntersectsRegion(Region region);

	/**
	 * Obtaining all player regions.
	 * 
	 * @param player - Owner of the regions.
	 * @return - List of player regions.
	 */
	List<Region> getPlayerRegions(ServerPlayer player);

	/**
	 * Obtaining all player regions.
	 * 
	 * @param playerUUID - UUID of owner of the regions.
	 * @return - List of player regions.
	 */
	List<Region> getPlayerRegions(UUID playerUUID);

	/**
	 * Getting an item for selecting regions.
	 */
	ItemStack getWandItem();

	/**
	 * Getting the type of regions creating by the player.
	 * 
	 * @return {@link RegionTypes}
	 */
	RegionTypes getSelectRegionType(ServerPlayer player);

	/**
	 * Getting the type of regions creating by the player.
	 * 
	 * @param uuid - player uuid
	 * @return {@link RegionTypes}
	 */
	RegionTypes getSelectRegionType(UUID uuid);

	/**
	 * Specifies the type of regions created by the player. <br>
	 * You cannot assign the types: global, subdivision, unset.
	 */
	void setCreatingRegionType(ServerPlayer player, RegionTypes claim);

	/**
	 * Specifies the type of regions created by the player. <br>
	 * You cannot assign the types: global, subdivision, unset.
	 */
	void setCreatingRegionType(UUID uuid, RegionTypes regionType);

	/**
	 * Get the minimum size of the region, which is set in the main configuration file.
	 * 
	 * @param selectorType - Type of region selection.
	 * @return - The minimum number of blocks that should be in the region.
	 */
	int getMinimalRegionSize(SelectorTypes selectorType);

	/**
	 * Get the default flags for a certain type of region.
	 */
	Map<String, Set<FlagValue>> getDefaultFlags(RegionTypes regionType);

	/**
	 * Get the number of blocks that belong to the player.
	 * 
	 * @param player - Checked player
	 * @return - The amount of blocks the player owns according to the type of allocation of each region.
	 */
	long getClaimedBlocks(ServerPlayer player);

	/**
	 * Get the number of regions belonging to the player.
	 * 
	 * @param player - Checked player
	 * @return - The number of regions belonging to the player.
	 */
	long getClaimedRegions(ServerPlayer player);

	/**
	 * Getting the limit of blocks that can claiming the player.
	 * 
	 * @param player - Checked player
	 * @return - The number of blocks available to the player for claiming.
	 */
	long getLimitBlocks(ServerPlayer player);

	/**
	 * Getting the limit of regions that can claiming the player.
	 * 
	 * @param player - Checked player
	 * @return - The number of regions available to the player for claiming.
	 */
	long getLimitClaims(ServerPlayer player);

	/**
	 * Get the maximum number of child regions a player can create. <br>
	 * Each basic region has its own limit. <br>
	 * 
	 * @param player - Checked player
	 * @returnThe number of regions available to the player for creating.
	 */
	long getLimitSubdivisions(ServerPlayer player);

	/**
	 * Getting the members limit for each region of a particular player.<br>
	 * Each region has its own separate members limit. <br>
	 * 
	 * @param player - Checked player
	 * @return - The number of members limit for each region of a particular player.
	 */
	long getLimitMembers(ServerPlayer player);

	/**
	 * Getting the members limit for each region of a particular player.<br>
	 * Each region has its own separate members limit. <br>
	 * 
	 * @param player - Checked player
	 * @return - The number of members limit for each region of a particular player.
	 */
	long getLimitMembers(UUID player);

	/**
	 * The maximum limit of blocks a player can claim.
	 * 
	 * @param player - Checked player
	 * @return - The number of blocks available to the player for claiming.
	 */
	long getLimitMaxBlocks(ServerPlayer player);

	/**
	 * The maximum limit of regions a player can claim.
	 * 
	 * @param player - Checked player
	 * @return - The number of regions available to the player for claiming.
	 */
	long getLimitMaxClaims(ServerPlayer player);

	/**
	 * The maximum limit of child regions a player can create. <br>
	 * Each basic region has its own limit. <br>
	 * 
	 * @param player - Checked player
	 * @return - The number of subdivisions available to the player for creating.
	 */
	long getLimitMaxSubdivisions(ServerPlayer player);

	/**
	 * The maximum limit of members of the player region.. <br>
	 * Each basic region has its own limit. <br>
	 * 
	 * @param player - Checked player
	 * @return - The maximum number of region members a player can add.
	 */
	long getLimitMaxMembers(ServerPlayer player);

	/**
	 * Get the value of the block price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	double getBuyBlockPrice(ServerPlayer player);

	/**
	 * Get the value of the region price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	double getBuyClaimPrice(ServerPlayer player);

	/**
	 * Get the value of the subdivision price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	double getBuySubdivisionPrice(ServerPlayer player);

	/**
	 * Getting the value of the price of the region's participant limit increase. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	double getBuyMembersPrice(ServerPlayer player);


	/**
	 * Get the value of the block price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	double getSellBlockPrice(ServerPlayer player);

	/**
	 * Get the value of the region price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	double getSellClaimPrice(ServerPlayer player);

	/**
	 * Get the value of the subdivision price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	double getSellSubdivisionPrice(ServerPlayer player);

	/**
	 * Getting the value of the price of the region members limit decrease. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	double getSellMembersPrice(ServerPlayer player);
	/**
	 * Getting the currency in which the player will perform the transaction.
	 * Depending on how the permissions plugin is set up, different players may have different currencies.
	 */
	Currency getCurrency(ServerPlayer player);

	/**
	 * Change the limit of blocks a player can claim.
	 */
	void setLimitBlocks(ServerPlayer player, long limit);

	/**
	 * Set the number of regions a player can own.
	 */
	void setLimitClaims(ServerPlayer player, long limit);

	/**
	 * Set a limit to the subdivisions a player can create.
	 */
	void setLimitSubdivisions(ServerPlayer player, long limit);

	/**
	 * Set the number of players that each player region can contain.
	 */
	void setLimitMembers(ServerPlayer player, long limit);

	/**
	 * Setting limits and other information on the player.
	 */
	void setPlayerData(ServerPlayer player, PlayerData playerData);

	/**
	 * Setting limits and other information on the player.
	 */
	void setPlayerData(UUID player, PlayerData playerData);

	/**
	 * Update cached data on regions and blocks claimed by the player.
	 */
	void updatePlayerData(ServerPlayer player);

	/**
	 * Update cached data on regions and blocks claimed by the player.
	 */
	void updatePlayerData(UUID player);

	/**
	 * Getting limits and other information on the player.
	 */
	Optional<PlayerData> getPlayerData(ServerPlayer player);

	/**
	 * Getting limits and other information on the player.
	 */
	Optional<PlayerData> getPlayerData(UUID player);

	/**
	 * Go to the API for visually highlighting regions with a mod on the WECui client.
	 */
	WorldEditCUIAPI getWorldEditCUIAPI();

	/**
	 * This interface is designed to access the plugin's API.
	 */
	interface PostAPI extends Event {

		public RegionAPI getAPI();

	}

}
