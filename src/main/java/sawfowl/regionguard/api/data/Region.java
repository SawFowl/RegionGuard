package sawfowl.regionguard.api.data;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.api.world.schematic.Schematic;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.math.vector.Vector3i;
import org.spongepowered.plugin.PluginContainer;

import com.google.gson.JsonObject;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.SelectorTypes;

@ConfigSerializable
public interface Region extends DataSerializable {

	static Builder builder() {
		return Sponge.game().builderProvider().provide(Builder.class);
	}

	static Region createGlobal(ResourceKey world, Map<String, Set<FlagValue>> flags) {
		return builder().setWorld(world).setName(Locales.DEFAULT, Component.text("Global#World[" + world.asString() + "]")).setFlags(flags).setType(RegionTypes.GLOBAL).setServerOwner().build();
	}

	static Region createGlobal(ResourceKey world) {
		return builder().setWorld(world).setName(Locales.DEFAULT, Component.text("Global#World[" + world.asString() + "]")).setType(RegionTypes.GLOBAL).setServerOwner().build();
	}

	static Region createGlobal(ServerWorld world, Map<String, Set<FlagValue>> flags) {
		return createGlobal(world.key(), flags);
	}

	static Region createGlobal(ServerWorld world) {
		return createGlobal(world.key());
	}

	/**
	 * Getting the region name.
	 *
	 * @param locale - language to be checked
	 */
	Optional<String> getPlainName(Locale locale);

	/**
	 * Getting the region name as kyori component.
	 *
	 * @param locale - language to be checked
	 */
	Component getName(Locale locale);

	/**
	 * Set region name.
	 */
	Region setName(Component name, Locale locale);

	Map<String, Component> getNames();

	/**
	 * Getting the player-owner of the region, if the owner of the region is a player and he is online.
	 */
	Optional<ServerPlayer> getOwner();

	/**
	 * Getting the UUID of the region owner.
	 */
	UUID getOwnerUUID();

	/**
	 * Getting the name of the region owner.
	 */
	String getOwnerName();

	/**
	 * Make the player the new owner of the region.
	 *
	 * @param owner - new owner
	 */
	Region setOwner(ServerPlayer owner);

	/**
	 * Make the player the new owner of the region.
	 *
	 * @param owner - new owner
	 */
	Region setOwner(GameProfile owner);

	/**
	 * Adding a player to a region.
	 *
	 * @param player - addable player
	 * @param type - assignable trust type
	 */
	Region setTrustType(ServerPlayer player, TrustTypes type);

	/**
	 * Adding a player to a region.
	 *
	 * @param player - addable player
	 * @param type - assignable trust type
	 */
	Region setTrustType(GameProfile player, TrustTypes type);

	/**
	 * Adding a entity to a region.
	 *
	 * @param uuid - addable entity
	 * @param type - assignable trust type
	 */
	Region setTrustType(UUID uuid, TrustTypes type);

	/**
	 * Getting the data of the region owner
	 */
	MemberData getOwnerData();

	/**
	 * Getting a collection of the region's members.
	 */
	List<MemberData> getMembers();

	/**
	 * Getting the data of the region member.
	 *
	 * @param player - checked player.
	 */
	Optional<MemberData> getMemberData(ServerPlayer player);

	/**
	 * Getting the data of the region member.
	 *
	 * @param uuid - checked player or entity.
	 */
	Optional<MemberData> getMemberData(UUID uuid);

	/**
	 * Total number of members of the region.
	 */
	int getTotalMembers();

	/**
	 * Checking the type of trust in the region.
	 *
	 * @param player - checked player
	 * @param level - checked trust type
	 * @return true - if the player has the trust type specified in the check <br>
	 * false - if the player or entity does not have the type of trust specified in the check, or region is global, or region is admin
	 */
	boolean isCurrentTrustType(ServerPlayer player, TrustTypes level);

	/**
	 * Checking the type of trust in the region.
	 *
	 * @param uuid - player uuid
	 * @param level - checked trust type
	 * @return true - if the player or entity has the trust type specified in the check <br>
	 * false - if the player or entity does not have the type of trust specified in the check, or region is global, or region is admin
	 */
	boolean isCurrentTrustType(UUID uuid, TrustTypes level);

	/**
	 * Getting a player's type of trust.
	 *
	 * @param player - checked player
	 * @return the type without trust(TrustTypes.WITHOUT_TRUST) if the player is not a member of the region
	 */
	TrustTypes getTrustType(ServerPlayer player);


	/**
	 * Gaining the trust of a player or entity
	 *
	 * @param uuid - checked entity
	 * @return the type without trust(TrustTypes.WITHOUT_TRUST) if the entity is not a member of the region
	 */
	TrustTypes getTrustType(UUID uuid);

	/**
	 * Removing a player from a region.
	 */
	void untrust(ServerPlayer player);


	/**
	 * Removing a player or entity from a region.
	 */
	void untrust(UUID uuid);

	/**
	 * Checking whether a player is a member of a region.
	 *
	 * @param player - checked player
	 */
	boolean isTrusted(ServerPlayer player);

	/**
	 * Checking whether a entity is a member of a region.
	 *
	 * @param uuid - checked entity
	 */
	boolean isTrusted(UUID uuid);

	/**
	 * Getting the UUID of a region.
	 */
	UUID getUniqueId();

	/**
	 * Getting the region world if it is loaded.
	 */
	Optional<ServerWorld> getWorld();

	/**
	 * Getting the world key.
	 */
	ResourceKey getWorldKey();

	/**
	 * Getting the region cuboid.
	 */
	Cuboid getCuboid();

	/**
	 * Getting the region type.
	 */
	RegionTypes getType();

	/**
	 * Changing the type of region. <br>
	 * Depending on the specified type, some parameters may change.
	 *
	 * @param type - assignable type
	 */
	boolean setRegionType(RegionTypes type);

	/**
	 * Check if the region is global.
	 */
	boolean isGlobal();

	/**
	 * Check whether the region is an admin region.
	 */
	boolean isAdmin();

	/**
	 * Check if the region is an arena.
	 */
	boolean isArena();

	/**
	 * Check if the region is basic.
	 */
	boolean isBasicClaim();

	/**
	 * Check if the region is subdivision.
	 */
	boolean isSubdivision();

	/**
	 * Setting new boundaries for the region.
	 *
	 * @param first - first position
	 * @param second - second position
	 * @param selectorType - type of area selection
	 */
	Region setCuboid(Vector3i first, Vector3i second, SelectorTypes selectorType);

	/**
	 * Setting new boundaries for the region.
	 */
	Region setCuboid(Cuboid cuboid);

	/**
	 * Getting a parent region if it is available.
	 */
	Optional<Region> getParrent();

	/**
	 * Getting the primary parent region.
	 *
	 * @return the primary parent region if it exists, or the current region if the region has no parent
	 */
	Region getPrimaryParent();

	/**
	 * Specifying the parent region.
	 */
	Region setParrent(Region region);

	/**
	 * Checking if there are child regions in the region.
	 */
	boolean containsChilds();

	/**
	 * Searching for a child region.<br>
	 * If no region is found, it will return the current region.
	 */
	Region getChild(Vector3i position);

	/**
	 * Adding a child region.
	 */
	Region addChild(Region region);

	/**
	 * Removing a child region.
	 */
	Region removeChild(Region region);

	/**
	 * Recursive search and making a list of all child regions.
	 */
	List<Region> getAllChilds();

	/**
	 * Getting a list of child regions.
	 */
	List<Region> getChilds();

	/**
	 * Recursively search and list flagValues from the current region to the oldest parent.
	 */
	Map<String, Set<FlagValue>> getFlags();

	/**
	 * Recursive search and enumeration of flagValues with parameters from the current region to the oldest parent.
	 */
	Map<String, Set<FlagValue>> getCustomFlags();

	/**
	 * Getting the values of the flag in the region. <br>
	 * If there is no flag, an attempt will be made to check it in the parent region.
	 */
	Set<FlagValue> getFlagValues(Flags flagName);

	/**
	 * Getting the values of the flag in the region. <br>
	 * If there is no flag, an attempt will be made to check it in the parent region.
	 */
	Set<FlagValue> getFlagValues(String flagName);

	/**
	 * Checking if a flag is set in the region.
	 */
	boolean containsFlag(Flags flagName);

	/**
	 * Checking if a flag is set in the region.
	 */
	boolean containsFlag(String flagName);

	/**
	 * Getting the value of the flag in the region. <br>
	 * If there is no flag, an attempt will be made to check it in the parent region.
	 */
	Tristate getFlagResult(Flags flag, String source, String target);

	/**
	 * Getting the value of the flag in the region. <br>
	 * If there is no flag, an attempt will be made to check it in the parent region.
	 */
	Tristate getFlagResult(String flag, String source, String target);

	/**
	 * Getting the value of the flag if there is one. <br>
	 */
	Tristate getFlagResultWhithoutParrents(Flags flag, String source, String target);

	/**
	 * Getting the value of the flag if there is one. <br>
	 */
	Tristate getFlagResultWhithoutParrents(String flag, String source, String target);

	/**
	 * Setting the value of the flag.
	 */
	Region setFlag(Flags flagName, boolean value);

	/**
	 * Setting the value of the flag.
	 */
	Region setFlag(String flagName, boolean value);

	/**
	 * Setting the value of the flag.
	 */
	Region setFlag(Flags flagName, boolean value, String source, String target);

	/**
	 * Setting the value of the flag.
	 */
	Region setFlag(String flagName, boolean value, String source, String target);

	/**
	 * Setting the value of the flag.<br>
	 * The flag set via this method will not be saved to disk.
	 */
	Region setTempFlag(Flags flagName, boolean value, String source, String target);

	/**
	 * Setting the value of the flag.<br>
	 * The flag set via this method will not be saved to disk.
	 */
	Region setTempFlag(String flagName, boolean value, String source, String target);

	/**
	 * Set the values of a set of flags.
	 *
	 * @param flags - Map with flags and their values.
	 */
	Region setFlags(Map<String, Set<FlagValue>> flags);

	/**
	 * Removing the flag from the region.
	 */
	Region removeFlag(Flags flagName);

	/**
	 * Removing the flag from the region.
	 */
	Region removeFlag(String flagName);

	/**
	 * Removing the flag from the region.
	 */
	Region removeFlag(Flags flagName, String source, String target);

	/**
	 * Removing the flag from the region.
	 */
	Region removeFlag(String flagName, String source, String target);

	/**
	 * Removing a temporary flag from a region.
	 */
	Region removeTempFlag(Flags flagName, String source, String target);

	/**
	 * Removing a temporary flag from a region.
	 */
	Region removeTempFlag(String flagName, String source, String target);

	/**
	 * Removing the flag from the region.
	 */
	Region removeFlag(Flags flagName, FlagValue flagValue);

	/**
	 * Removing the flag from the region.
	 */
	Region removeFlag(String flagName, FlagValue flagValue);

	/**
	 * Get the region creation time in unix format.
	 */
	long getCreationTime();

	/**
	 * Getting region join message.<br>
	 * If the specified localization is not found, the default localization will be checked.<br>
	 * If no default localization is found, the first value found will be returned or an empty optional value if the list is empty.
	 *
	 * @param locale - checking locale
	 */
	Optional<Component> getJoinMessage(Locale locale);

	/**
	 * Setting region join message.<br>
	 *
	 * @param message - setting message
	 * @param locale - setting locale
	 */
	Region setJoinMessage(Component message, Locale locale);

	/**
	 * Getting a collection of localized messages reporting entry into a region..<br>
	 * The key is the localization tag.
	 */
	Map<String, Component> getJoinMessages();

	/**
	 * Getting region exit message.<br>
	 * If the specified localization is not found, the default localization will be checked.<br>
	 * If no default localization is found, the first value found will be returned or an empty optional value if the list is empty.
	 *
	 * @param locale - checking locale
	 */
	Optional<Component> getExitMessage(Locale locale);

	/**
	 * Setting region exit message.<br>
	 *
	 * @param message - setting message
	 * @param locale - setting locale
	 */
	Region setExitMessage(Component message, Locale locale);

	/**
	 * Getting a collection of localized messages reporting region exits.<br>
	 * The key is the localization tag.
	 */
	Map<String, Component> getExitMessages();

	/**
	 * Getting additional data that is created by other plugins.<br>
	 * After getting the data, they must be converted to the desired type.<br>
	 * Exemple: YourDataClass yourDataClass = (YourDataClass) additionalData;
	 */
	<T extends AdditionalData> Optional<T> getAdditionalData(PluginContainer container, String dataName, Class<T> clazz);

	/**
	 * Write additional data created by another plugin.
	 */
	public <T extends AdditionalData> Region setAdditionalData(PluginContainer container, String dataName, T additionalData);

	/**
	 * Deleting additional data created by another plugin.
	 */
	Region removeAdditionalData(PluginContainer container, String dataName);

	/**
	 * Getting a copy of the entire collection of the region's supplementary data.
	 */
	Map<String, Map<String, JsonObject>> getAllAdditionalData();

	/**
	 * Checking whether the position belongs to the region.
	 *
	 * @param serverWorld - checkable World
	 * @param vector3i - checkable position
	 */
	boolean isIntersectsWith(ServerWorld serverWorld, Vector3i vector3i);

	/**
	 * Checking whether the position belongs to the region.
	 *
	 * @param worldkey - checkable World
	 * @param vector3i - checkable position
	 */
	boolean isIntersectsWith(ResourceKey worldkey, Vector3i vector3i);

	/**
	 * Checking whether the position belongs to the region.
	 * This method should only be applied to child regions, as it does not perform a world matching check.
	 *
	 * @param vector3i - checkable position
	 */
	boolean isIntersectsWith(Vector3i vector3i);

	/**
	 * Getting a list of loaded chunks.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	List<WorldChunk> getLoadedChunks();

	/**
	 * Getting a list of blocks in the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	List<BlockState> getBlocks();

	/**
	 * Getting a list of block entities in the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	List<BlockEntity> getBlockEntities();

	/**
	 * Getting a list of entities in the region.<br>
	 * This list will not contain players.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	List<Entity> getEntities();

	/**
	 * Getting a list of players in the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	List<ServerPlayer> getPlayers();

	/**
	 * Getting a list of the chunk numbers that the region occupies.
	 */
	List<ChunkNumber> getChunkNumbers();

	/**
	 * Create a Schematic from the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	Optional<Schematic> getSchematic(String schematicName, String altAuthor);

	/**
	 * Insert Schematic into the region.<br><br>
	 * This operation can work overload the server.
	 *
	 * @param schematic
	 * @param heigt - the height at which the insertion will be made
	 */
	boolean putSchematic(Schematic schematic, int heigt);


	/**
	 * Insert Schematic into the region.<br><br>
	 * This operation can work overload the server.
	 *
	 * @param schematic
	 * @param vector3i - central position
	 */
	boolean putSchematic(Schematic schematic, Vector3i vector3i);


	/**
	 * Territory regeneration in the region.
	 *
	 * @param async - regen in async mode
	 * @param delay - delay before regeneration in async mode
	 */
	boolean regen(boolean async, int delay);

	/**
	 * Checking for matching region owners.
	 */
	boolean equalsOwners(Region region);

	/**
	 * Create a copy of the current region.
	 */
	Region copy();

	interface Builder extends AbstractBuilder<Region>, org.spongepowered.api.util.Builder<Region, Builder> {

		Builder setUniqueId(UUID uuid);

		Builder setCreationTime(long time);

		Builder setName(Locale locale, Component name);

		Builder setOwner(ServerPlayer player);

		Builder setServerOwner();

		Builder setCuboid(Cuboid cuboid);

		Builder setWorld(ServerWorld serverWorld);

		Builder setWorld(ResourceKey worldKey);

		Builder setType(RegionTypes type);

		Builder setParrent(Region region);

		Builder setFlags(Map<String, Set<FlagValue>> flags);

		Builder addMembers(Collection<MemberData> members);

		Builder addJoinMessages(Map<String, Component> messages);

		Builder addExitMessages(Map<String, Component> messages);

		Builder addNames(Map<String, Component> names);

		Builder addAdditionalData(Map<String, Map<String, JsonObject>> dataMap);

		Builder addChilds(Collection<Region> regions);

	}
}
