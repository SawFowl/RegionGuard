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
import sawfowl.regionguard.api.data.WorldRegions;
import sawfowl.regionguard.api.worldedit.WorldEditCUIAPI;

public abstract class RegionAPI {

	public abstract void cleanNotExistWorldsData();

	/**
	 * Removes all world data from the plugin if the world does not exist.
	 */
	public abstract boolean cleanWorldData(ResourceKey world);

	/**
	 * Getting all registered flags.
	 */
	public abstract Map<String, FlagConfig> getRegisteredFlags();

	/**
	 * FlagConfig registration. Values cannot be null.
	 */
	public abstract void registerFlag(String flagName, FlagConfig settings);

	/**
	 * Update global region data.
	 * 
	 * @param serverWorld - world
	 * @param region - world region.
	 */
	public abstract void updateGlobalRegionData(ServerWorld serverWorld, Region region);

	/**
	 * Update global region data.
	 * 
	 * @param serverWorld - world key
	 * @param region - world region.
	 */
	public abstract void updateGlobalRegionData(ResourceKey serverWorld, Region region);

	public WorldRegions getRegions(ServerWorld world) {
		return getRegions(world.key());
	}

	public abstract WorldRegions getRegions(ResourceKey world);

	/**
	 * Adding a temporary region
	 * 
	 * @param region
	 */
	@Deprecated
	public abstract void addTempRegion(Region region);

	/**
	 * Adding a temporary region
	 * 
	 * @param region
	 */
	public abstract void addTempRegion(UUID user, Region region);

	/**
	 * Removing a temporary region
	 */
	public abstract void removeTempRegion(Region region);

	/**
	 * Removing a temporary region
	 * 
	 * @param uuid - owner uuid
	 */
	public abstract void removeTempRegion(UUID uuid);

	/**
	 * Get a temporary region
	 * 
	 * @param uuid - owner uuid
	 * @return Optional<Region> or empty
	 */
	public abstract Optional<Region> getTempRegion(UUID uuid);

	/**
	 * Obtaining a type of region selector from the player.
	 * 
	 * @param uuid
	 * @return Player-selected region selector type, or default selection type.
	 */
	public abstract SelectorTypes getSelectorType(UUID uuid);

	/**
	 * Set a type of region selector for the player.
	 * 
	 * @param selectorType - SelectorTypes
	 */
	public abstract void setSelectorType(ServerPlayer player, SelectorTypes selectorType);

	/**
	 * Set a type of region selector for the player.
	 * 
	 * @param uuid - player uuid
	 * @param selectorType - SelectorTypes
	 */
	public abstract void setSelectorType(UUID uuid, SelectorTypes selectorType);

	/**
	 * Get world region.
	 * 
	 * @param serverWorld - the world of the region.
	 * @return Region
	 */
	@Deprecated
	public abstract Region getGlobalRegion(ServerWorld serverWorld);

	/**
	 * Get world region.
	 * 
	 * @param worldkey - the world key of the region.
	 * @return Region
	 */
	@Deprecated
	public abstract Region getGlobalRegion(ResourceKey worldkey);

	/**
	 * Getting all registered regions except globals.
	 * 
	 * @return Collection<Region>
	 */
	public abstract Collection<Region> getRegions();

	/**
	 * Obtaining a map of regions by worlds. <br>
	 * Without global regions.
	 */
	@Deprecated
	public abstract Map<ResourceKey, Map<ChunkNumber, ArrayList<Region>>> getRegionsPerWorld();

	/**
	 * Region registration. <br>
	 * The method does not save the region data to disk.
	 * 
	 * @param region - Registrable region.
	 */
	public abstract void registerRegion(Region region);

	/**
	 * Region registration in asynchronous mode.
	 * 
	 * @param region - Registrable region.
	 */
	public abstract void registerRegionAsync(Region region);

	/**
	 * Remove the region from the list of registered regions.<br>
	 * This method will not remove the region from the disk.
	 */
	public abstract void unregisterRegion(Region region);

	/**
	 * Saving region data to disk.
	 * 
	 * @param region - Preservable region..
	 */
	public abstract void saveRegion(Region region);

	/**
	 * Deleting a region.
	 * 
	 * @param region - Removable region.
	 */
	public abstract void deleteRegion(Region region);

	/**
	 * Search for a region in the world using the specified coordinates. <br>
	 * If the region contains child regions, the deepest child region will be returned.
	 * 
	 * @param world - The world in which the search will be performed.
	 * @param position - Checkable position.
	 * @return - Found region or global region.
	 */
	@Deprecated
	public abstract Region findRegion(ServerWorld world, Vector3i position);

	/**
	 * Search for a region in the world using the specified coordinates. <br>
	 * If the region contains child regions, the deepest child region will be returned.
	 * 
	 * @param world - The world in which the search will be performed.
	 * @param position - Checkable position.
	 * @return - Found region or global region.
	 */
	@Deprecated
	public abstract Optional<Region> findRegion(ServerWorld world, Vector3i position, Predicate<Region> filter);

	/**
	 * Search for a region in the world using the specified coordinates.<br>
	 * If the region contains child regions, the deepest child region will be returned.
	 * 
	 * @param worldkey - The key of the world in which you want to search.
	 * @param position - Checkable position.
	 * @return - Found region or global region.
	 */
	@Deprecated
	public abstract Region findRegion(ResourceKey worldkey, Vector3i position);

	/**
	 * Search for the region with which the intersection occurs.
	 * 
	 * @param region - Original region.
	 * @return The first region found with which there is an intersection.<br>Or the original region if no intersection is found.
	 */
	@Deprecated
	public abstract Region findIntersectsRegion(Region region);

	/**
	 * Obtaining all player regions.
	 * 
	 * @param player - Owner of the regions.
	 * @return - List of player regions.
	 */
	public abstract List<Region> getPlayerRegions(ServerPlayer player);

	/**
	 * Obtaining all player regions.
	 * 
	 * @param playerUUID - UUID of owner of the regions.
	 * @return - List of player regions.
	 */
	public abstract List<Region> getPlayerRegions(UUID playerUUID);

	/**
	 * Getting an item for selecting regions.
	 */
	public abstract ItemStack getWandItem();

	/**
	 * Getting the type of regions creating by the player.
	 * 
	 * @return {@link RegionTypes}
	 */
	public abstract RegionTypes getSelectRegionType(ServerPlayer player);

	/**
	 * Getting the type of regions creating by the player.
	 * 
	 * @param uuid - player uuid
	 * @return {@link RegionTypes}
	 */
	public abstract RegionTypes getSelectRegionType(UUID uuid);

	/**
	 * Specifies the type of regions created by the player. <br>
	 * You cannot assign the types: global, subdivision, unset.
	 */
	public abstract void setCreatingRegionType(ServerPlayer player, RegionTypes claim);

	/**
	 * Specifies the type of regions created by the player. <br>
	 * You cannot assign the types: global, subdivision, unset.
	 */
	public abstract void setCreatingRegionType(UUID uuid, RegionTypes regionType);

	/**
	 * Get the minimum size of the region, which is set in the main configuration file.
	 * 
	 * @param selectorType - Type of region selection.
	 * @return - The minimum number of blocks that should be in the region.
	 */
	public abstract int getMinimalRegionSize(SelectorTypes selectorType);

	/**
	 * Get the default flags for a certain type of region.
	 */
	public abstract Map<String, Set<FlagValue>> getDefaultFlags(RegionTypes regionType);

	/**
	 * Get the number of blocks that belong to the player.
	 * 
	 * @param player - Checked player
	 * @return - The amount of blocks the player owns according to the type of allocation of each region.
	 */
	public long getClaimedBlocks(ServerPlayer player) {
		return getClaimedBlocks(player.uniqueId());
	}

	/**
	 * Get the number of blocks that belong to the player.
	 * 
	 * @param player - Checked player
	 * @return - The amount of blocks the player owns according to the type of allocation of each region.
	 */
	public abstract long getClaimedBlocks(UUID player);

	/**
	 * Get the number of regions belonging to the player.
	 * 
	 * @param player - Checked player
	 * @return - The number of regions belonging to the player.
	 */
	public long getClaimedRegions(ServerPlayer player) {
		return getClaimedRegions(player.uniqueId());
	}

	/**
	 * Get the number of regions belonging to the player.
	 * 
	 * @param player - Checked player
	 * @return - The number of regions belonging to the player.
	 */
	public abstract long getClaimedRegions(UUID player);

	/**
	 * Getting the limit of blocks that can claiming the player.
	 * 
	 * @param player - Checked player
	 * @return - The number of blocks available to the player for claiming.
	 */
	public abstract long getLimitBlocks(ServerPlayer player);

	/**
	 * Getting the limit of regions that can claiming the player.
	 * 
	 * @param player - Checked player
	 * @return - The number of regions available to the player for claiming.
	 */
	public abstract long getLimitClaims(ServerPlayer player);

	/**
	 * Get the maximum number of child regions a player can create. <br>
	 * Each basic region has its own limit. <br>
	 * 
	 * @param player - Checked player
	 * @returnThe number of regions available to the player for creating.
	 */
	public abstract long getLimitSubdivisions(ServerPlayer player);

	/**
	 * Getting the members limit for each region of a particular player.<br>
	 * Each region has its own separate members limit. <br>
	 * 
	 * @param player - Checked player
	 * @return - The number of members limit for each region of a particular player.
	 */
	public abstract long getLimitMembers(ServerPlayer player);

	/**
	 * Getting the members limit for each region of a particular player.<br>
	 * Each region has its own separate members limit. <br>
	 * 
	 * @param player - Checked player
	 * @return - The number of members limit for each region of a particular player.
	 */
	public abstract long getLimitMembers(UUID player);

	/**
	 * The maximum limit of blocks a player can claim.
	 * 
	 * @param player - Checked player
	 * @return - The number of blocks available to the player for claiming.
	 */
	public abstract long getLimitMaxBlocks(ServerPlayer player);

	/**
	 * The maximum limit of regions a player can claim.
	 * 
	 * @param player - Checked player
	 * @return - The number of regions available to the player for claiming.
	 */
	public abstract long getLimitMaxClaims(ServerPlayer player);

	/**
	 * The maximum limit of child regions a player can create. <br>
	 * Each basic region has its own limit. <br>
	 * 
	 * @param player - Checked player
	 * @return - The number of subdivisions available to the player for creating.
	 */
	public abstract long getLimitMaxSubdivisions(ServerPlayer player);

	/**
	 * The maximum limit of members of the player region.. <br>
	 * Each basic region has its own limit. <br>
	 * 
	 * @param player - Checked player
	 * @return - The maximum number of region members a player can add.
	 */
	public abstract long getLimitMaxMembers(ServerPlayer player);

	/**
	 * Get the value of the block price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	public abstract double getBuyBlockPrice(ServerPlayer player);

	/**
	 * Get the value of the region price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	public abstract double getBuyClaimPrice(ServerPlayer player);

	/**
	 * Get the value of the subdivision price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	public abstract double getBuySubdivisionPrice(ServerPlayer player);

	/**
	 * Getting the value of the price of the region's participant limit increase. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	public abstract double getBuyMembersPrice(ServerPlayer player);


	/**
	 * Get the value of the block price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	public abstract double getSellBlockPrice(ServerPlayer player);

	/**
	 * Get the value of the region price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	public abstract double getSellClaimPrice(ServerPlayer player);

	/**
	 * Get the value of the subdivision price for the player. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	public abstract double getSellSubdivisionPrice(ServerPlayer player);

	/**
	 * Getting the value of the price of the region members limit decrease. <br>
	 * Depending on how the permissions plugin is set up, different players may have different prices.
	 */
	public abstract double getSellMembersPrice(ServerPlayer player);
	/**
	 * Getting the currency in which the player will perform the transaction.
	 * Depending on how the permissions plugin is set up, different players may have different currencies.
	 */
	public abstract Currency getCurrency(ServerPlayer player);

	/**
	 * Change the limit of blocks a player can claim.
	 */
	public abstract void setLimitBlocks(ServerPlayer player, long limit);

	/**
	 * Set the number of regions a player can own.
	 */
	public abstract void setLimitClaims(ServerPlayer player, long limit);

	/**
	 * Set a limit to the subdivisions a player can create.
	 */
	public abstract void setLimitSubdivisions(ServerPlayer player, long limit);

	/**
	 * Set the number of players that each player region can contain.
	 */
	public abstract void setLimitMembers(ServerPlayer player, long limit);

	/**
	 * Setting limits and other information on the player.
	 */
	public abstract void setPlayerData(ServerPlayer player, PlayerData playerData);

	/**
	 * Setting limits and other information on the player.
	 */
	public abstract void setPlayerData(UUID player, PlayerData playerData);

	/**
	 * Update cached data on regions and blocks claimed by the player.
	 */
	public abstract void updatePlayerData(ServerPlayer player);

	/**
	 * Update cached data on regions and blocks claimed by the player.
	 */
	public abstract void updatePlayerData(UUID player);

	/**
	 * Getting limits and other information on the player.
	 */
	public abstract Optional<PlayerData> getPlayerData(ServerPlayer player);

	/**
	 * Getting limits and other information on the player.
	 */
	public abstract Optional<PlayerData> getPlayerData(UUID player);

	/**
	 * Go to the API for visually highlighting regions with a mod on the WECui client.
	 */
	public abstract WorldEditCUIAPI getWorldEditCUIAPI();

	/**
	 * This interface is designed to access the plugin's API.
	 */
	@Deprecated
	public interface PostAPI extends Event {

		public RegionAPI getAPI();

	}

}
