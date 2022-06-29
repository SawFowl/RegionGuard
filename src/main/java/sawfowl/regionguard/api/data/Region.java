package sawfowl.regionguard.api.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.cause.entity.SpawnTypes;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.api.world.generation.config.WorldGenerationConfig;
import org.spongepowered.api.world.schematic.Schematic;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldTemplate;
import org.spongepowered.api.world.volume.archetype.ArchetypeVolume;
import org.spongepowered.api.world.volume.stream.StreamOptions;
import org.spongepowered.api.world.volume.stream.StreamOptions.LoadingStyle;
import org.spongepowered.api.world.volume.stream.VolumeElement;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.math.vector.Vector3i;
import org.spongepowered.plugin.PluginContainer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.SelectorTypes;

@ConfigSerializable
public class Region {

	public Region() {}

	public Region(UUID owner, ServerWorld serverWorld, Vector3i vector1, Vector3i vector2, SelectorTypes selectorType) {
		setServerWorld(serverWorld);
		setCuboid(vector1, vector2, selectorType);
		members.put(owner, new MemberData(TrustTypes.OWNER));
		regionUUID = UUID.randomUUID();
		creationTime = System.currentTimeMillis();
	}

	public Region(ServerPlayer owner, ServerWorld serverWorld, Vector3i vector1, Vector3i vector2, SelectorTypes selectorType) {
		setServerWorld(serverWorld);
		setCuboid(vector1, vector2, selectorType);
		members.put(owner.uniqueId(), new MemberData(owner, TrustTypes.OWNER));
		regionUUID = UUID.randomUUID();
		creationTime = System.currentTimeMillis();
	}

	@Setting("RegionName")
	private Map<String, String> names = new HashMap<String, String>();
	@Setting("RegionUUID")
	private UUID regionUUID;
	@Setting("World")
	private String world;
	@Setting("Cuboid")
	private Cuboid cuboid;
	@Setting("Childs")
	private List<Region> childs = new ArrayList<Region>();
	@Setting("Flags")
	private Map<String, List<FlagValue>> flagValues = new HashMap<String, List<FlagValue>>();
	@Setting("Members")
	private Map<UUID, MemberData> members = new HashMap<UUID, MemberData>();
	@Setting("RegionType")
	private String regionType;
	@Setting("Created")
	private long creationTime = 0;
	@Setting("JoinMessages")
	private Map<String, String> joinMessages = new HashMap<String, String>();
	@Setting("ExitMessages")
	private Map<String, String> exitMessages = new HashMap<String, String>();
	@Setting("EnhancedData")
	private Map<String, AdditionalData> additionalData = null;
	private Region parrent;
	private boolean flat = false;

	/**
	 * Getting the region name.
	 * 
	 * @param locale - language to be checked
	 */
	public Optional<String> getName(Locale locale) {
		return names.isEmpty() ? Optional.empty() : names.containsKey(locale.toLanguageTag()) ? Optional.of(names.get(locale.toLanguageTag())) : names.containsKey(Locales.DEFAULT.toLanguageTag()) ? Optional.of(names.get(Locales.DEFAULT.toLanguageTag())) : joinMessages.values().stream().findFirst();
	}

	/**
	 * Getting the region name as kyori component.
	 * 
	 * @param locale - language to be checked
	 */
	public Component asComponent(Locale locale) {
		Optional<String> name = getName(locale);
		return name.isPresent() ? deserialize(name.get()) : Component.empty();
	}

	/**
	 * Set region name.
	 */
	public Region setName(String name, Locale locale) {
		if(names.containsKey(locale.toLanguageTag())) names.remove(locale.toLanguageTag());
		if(name != null) names.put(locale.toLanguageTag(), name);
		return this;
	}

	/**
	 * Getting the player-owner of the region, if the owner of the region is a player and he is online.
	 */
	public Optional<ServerPlayer> getOwner() {
		return !getOwnerData().isPlayer() || getOwnerUUID().equals(new UUID(0, 0)) ? Optional.empty() : Sponge.server().player(getOwnerUUID());
	}

	/**
	 * Getting the UUID of the region owner.
	 */
	public UUID getOwnerUUID() {
		Optional<Entry<UUID, MemberData>> optOwnerData = members.entrySet().stream().filter(entry -> entry.getValue().getTrustType() == TrustTypes.OWNER).findFirst();
		UUID uuid = optOwnerData.isPresent() ? optOwnerData.get().getKey() : new UUID(0, 0);
		return uuid;
	}

	/**
	 * 
	 */
	public String getOwnerName() {
		return getOwnerData().getName();
	}

	/**
	 * Make the player the new owner of the region.
	 * 
	 * @param owner - new owner
	 */
	public Region setOwner(ServerPlayer owner) {
		members.remove(getOwnerUUID());
		members.put(owner.uniqueId(), new MemberData(owner, TrustTypes.OWNER));
		return this;
	}

	/**
	 * Adding a player to a region.
	 * 
	 * @param player - addable player
	 * @param type - assignable trust type
	 */
	public Region setTrustType(ServerPlayer player, TrustTypes type) {
		setTrustType(player.uniqueId(), type);
		return this;
	}

	/**
	 * Adding a entity to a region.
	 * 
	 * @param uuid - addable entity
	 * @param type - assignable trust type
	 */
	public Region setTrustType(UUID uuid, TrustTypes type) {
		untrust(uuid);
		members.put(uuid, new MemberData(type));
		return this;
	}

	/**
	 * Getting the data of the region owner
	 */
	public MemberData getOwnerData() {
		if(members.size() > 20) members.values().parallelStream().filter(member -> (member.getTrustType() == TrustTypes.OWNER)).findFirst().get();
		return members.values().stream().filter(member -> (member.getTrustType() == TrustTypes.OWNER)).findFirst().get();
	}

	/**
	 * Getting the data of the region member.
	 * 
	 * @param player - checked player.
	 */
	public Optional<MemberData> getMemberData(ServerPlayer player) {
		return getMemberData(player.uniqueId());
	}

	/**
	 * Getting the data of the region member.
	 * 
	 * @param uuid - checked player or entity.
	 */
	public Optional<MemberData> getMemberData(UUID uuid) {
		return members.containsKey(uuid) ? Optional.ofNullable(members.get(uuid)) : Optional.empty();
	}

	/**
	 * Total number of members of the region.
	 */
	public int getTotalMembers() {
		return members.size();
	}

	/**
	 * Checking the type of trust in the region.
	 * 
	 * @param player - checked player
	 * @param level - checked trust type
	 * @return true - if the player has the trust type specified in the check <br>
	 * false - if the player or entity does not have the type of trust specified in the check, or region is global, or region is admin
	 */
	public boolean isCurrentTrustType(ServerPlayer player, TrustTypes level) {
		return isCurrentTrustType(player.uniqueId(), level);
	}

	/**
	 * Checking the type of trust in the region.
	 * 
	 * @param uuid - player uuid
	 * @param level - checked trust type
	 * @return true - if the player or entity has the trust type specified in the check <br>
	 * false - if the player or entity does not have the type of trust specified in the check, or region is global, or region is admin
	 */
	public boolean isCurrentTrustType(UUID uuid, TrustTypes level) {
		return !isAdmin() && !isGlobal() && getTrustType(uuid) == level;
	}

	/**
	 * Getting a player's type of trust.
	 * 
	 * @param player - checked player
	 * @return the type without trust(TrustTypes.WITHOUT_TRUST) if the player is not a member of the region
	 */
	public TrustTypes getTrustType(ServerPlayer player) {
		return getTrustType(player.uniqueId());
	}


	/**
	 * Gaining the trust of a player or entity
	 * 
	 * @param uuid - checked entity
	 * @return the type without trust(TrustTypes.WITHOUT_TRUST) if the entity is not a member of the region
	 */
	public TrustTypes getTrustType(UUID uuid) {
		return members.containsKey(uuid) ? members.get(uuid).getTrustType() : TrustTypes.WITHOUT_TRUST;
	}

	/**
	 * Removing a player from a region.
	 */
	public void untrust(ServerPlayer player) {
		untrust(player.uniqueId());;
	}


	/**
	 * Removing a player or entity from a region.
	 */
	public void untrust(UUID uuid) {
		if(members.containsKey(uuid)) members.remove(uuid);
	}

	/**
	 * Checking whether a player is a member of a region.
	 * 
	 * @param player - checked player
	 */
	public boolean isTrusted(ServerPlayer player) {
		return isTrusted(player.uniqueId());
	}

	/**
	 * Checking whether a entity is a member of a region.
	 * 
	 * @param uuid - checked entity
	 */
	public boolean isTrusted(UUID uuid) {
		return members.containsKey(uuid) && getTrustType(uuid) != TrustTypes.WITHOUT_TRUST;
	}

	/**
	 * Getting the UUID of a region.
	 */
	public UUID getUniqueId() {
		return regionUUID;
	}

	/**
	 * Getting the region world if it is loaded.
	 */
	public Optional<ServerWorld> getServerWorld() {
		return Sponge.server().worldManager().world(ResourceKey.resolve(world));
	}

	/**
	 * Getting the world key.
	 */
	public ResourceKey getServerWorldKey() {
		return ResourceKey.resolve(world);
	}

	/**
	 * Specifies the world in which the region will be located.
	 */
	public Region setServerWorld(ServerWorld serverWorld) {
		world = serverWorld.key().asString();
		return this;
	}

	/**
	 * Getting the region cuboid.
	 */
	public Cuboid getCuboid() {
		if(!flat && cuboid.getSelectorType() == SelectorTypes.FLAT && getServerWorld().isPresent() && (getServerWorld().get().min().y() != cuboid.getAABB().min().y() || getServerWorld().get().max().y() != cuboid.getAABB().max().y())) {
			cuboid.setPositions(cuboid.getMin(), cuboid.getMax(), cuboid.getSelectorType(), getServerWorld().get());
			flat = true;
		}
		return cuboid;
	}

	/**
	 * Getting the region type.
	 */
	public RegionTypes getType() {
		return RegionTypes.valueOfName(regionType);
	}

	/**
	 * Changing the type of region. <br>
	 * Depending on the specified type, some parameters may change.
	 * 
	 * @param type - assignable type
	 */
	public boolean setRegionType(RegionTypes type) {
		if(type == RegionTypes.ADMIN) {
			members.clear();
			members.put(new UUID(0, 0), new MemberData(TrustTypes.OWNER));
			return true;
		} 
		regionType = type.toString();
		return false;
	}

	/**
	 * Check if the region is global.
	 */
	public boolean isGlobal() {
		return regionType.equals(RegionTypes.GLOBAL.toString());
	}

	/**
	 * Check whether the region is an admin region.
	 */
	public boolean isAdmin() {
		return regionType.equals(RegionTypes.ADMIN.toString());
	}

	/**
	 * Check if the region is an arena.
	 */
	public boolean isArena() {
		return regionType.equals(RegionTypes.ARENA.toString());
	}

	/**
	 * Check if the region is basic.
	 */
	public boolean isBasicClaim() {
		return regionType.equals(RegionTypes.CLAIM.toString());
	}

	/**
	 * Check if the region is subdivision.
	 */
	public boolean isSubdivision() {
		return regionType.equals(RegionTypes.SUBDIVISION.toString());
	}

	/**
	 * Setting new boundaries for the region.
	 * 
	 * @param first - first position
	 * @param second - second position
	 * @param selectorType - type of area selection
	 */
	public Region setCuboid(Vector3i first, Vector3i second, SelectorTypes selectorType) {
		if(selectorType == null) return this;
		cuboid = new Cuboid();
		cuboid.setSelectorType(selectorType);
		if(selectorType.equals(SelectorTypes.FLAT)) {
			first = Vector3i.from(first.x(), Sponge.server().worldManager().defaultWorld().min().y(), first.z());
			second = Vector3i.from(second.x(), Sponge.server().worldManager().defaultWorld().max().y(), second.z());
		}
		if(getServerWorld().isPresent() && first != null && second != null) cuboid.setPositions(first, second, selectorType, getServerWorld().get());
		return this;
	}

	/**
	 * Getting a parent region if it is available.
	 */
	public Optional<Region> getParrent() {
		return Optional.ofNullable(parrent);
	}

	/**
	 * Getting the primary parent region.
	 * 
	 * @return the primary parent region if it exists, or the current region if the region has no parent
	 */
	public Region getPrimaryParent() {
		return parrent == null ? this : parrent.getPrimaryParent();
	}

	/**
	 * Specifying the parent region.
	 */
	public Region setParrent(Region region) {
		regionType = RegionTypes.SUBDIVISION.toString();
		parrent = region;
		return this;
	}

	/**
	 * Checking if there are child regions in the region.
	 */
	public boolean containsChilds() {
		return !childs.isEmpty();
	}

	/**
	 * Adding a child region.
	 */
	public Region addChild(Region region) {
		childs.add(region);
		return this;
	}

	/**
	 * Removing a child region.
	 */
	public Region removeChild(Region region) {
		if(childs.contains(region)) childs.remove(region);
		return this;
	}

	/**
	 * Recursive search and making a list of all child regions.
	 */
	public List<Region> getAllChilds() {
		List<Region> childs = new ArrayList<Region>();
		if(!this.childs.isEmpty()) {
			childs.addAll(this.childs);
			for(Region child : this.childs) {
				childs.addAll(child.getAllChilds());
			}
		}
		if(parrent == null && this.childs.size() < childs.size()) Collections.reverse(childs);
		return childs;
	}

	/**
	 * Getting a list of child regions.
	 */
	public List<Region> getChilds() {
		return childs;
	}

	/**
	 * Recursively search and list flagValues from the current region to the oldest parent.
	 */
	public Map<String, List<FlagValue>> getFlags() {
		if(!getParrent().isPresent()) return flagValues;
		Map<String, List<FlagValue>> flagValues = new HashMap<String, List<FlagValue>>();
		flagValues.putAll(getParrent().get().getFlags());
		for(Entry<String, List<FlagValue>> entry : this.flagValues.entrySet()) flagValues.replace(entry.getKey(), entry.getValue());
		return flagValues;
	}

	/**
	 * Recursive search and enumeration of flagValues with parameters from the current region to the oldest parent.
	 */
	public Map<String, List<FlagValue>> getCustomFlags() {
		Map<String, List<FlagValue>> customFlags = new HashMap<String, List<FlagValue>>();
		FlagValue defaultValue = new FlagValue();
		flagValues.entrySet().parallelStream().forEach(entry -> {
			List<FlagValue> custom = new ArrayList<>();
			entry.getValue().stream().filter(flag -> (!flag.equals(defaultValue))).forEach(flag -> {
				custom.add(flag);
			});
			if(!custom.isEmpty()) customFlags.put(entry.getKey(), custom);
		});
		if(parrent != null) customFlags.putAll(parrent.getCustomFlags());
		return customFlags;
	}

	/**
	 * Getting the values of the flag in the region. <br>
	 * If there is no flag, an attempt will be made to check it in the parent region.
	 */
	public List<FlagValue> getFlagValues(Flags flagName) {
		return getFlagValues(flagName.toString());
	}

	/**
	 * Getting the values of the flag in the region. <br>
	 * If there is no flag, an attempt will be made to check it in the parent region.
	 */
	public List<FlagValue> getFlagValues(String flagName) {
		return containsFlag(flagName) ? flagValues.get(flagName) : parrent != null ? parrent.getFlagValues(flagName) : new ArrayList<>();
	}

	/**
	 * Checking if a flag is set in the region.
	 */
	public boolean containsFlag(Flags flagName) {
		return containsFlag(flagName.toString());
	}

	/**
	 * Checking if a flag is set in the region.
	 */
	public boolean containsFlag(String flagName) {
		return flagValues.keySet().parallelStream().filter(flag -> (flag.contains(flagName))).findFirst().isPresent();
	}

	/**
	 * Getting the value of the flag in the region. <br>
	 * If there is no flag, an attempt will be made to check it in the parent region.
	 */
	public Tristate getFlagResult(Flags flag, String source, String target) {
		return getFlagResult(flag.toString(), source, target);
	}

	/**
	 * Getting the value of the flag in the region. <br>
	 * If there is no flag, an attempt will be made to check it in the parent region.
	 */
	public Tristate getFlagResult(String flag, String source, String target) {
		Tristate result = getFlagResultWhithoutParrents(flag, source, target);
		return result != Tristate.UNDEFINED ?  result : (parrent != null ? parrent.getFlagResult(flag, source, target) : Tristate.UNDEFINED);
	}

	/**
	 * Getting the value of the flag if there is one. <br>
	 */
	public Tristate getFlagResultWhithoutParrents(Flags flag, String source, String target) {
		return getFlagResult(flag.toString(), source, target);
	}

	/**
	 * Getting the value of the flag if there is one. <br>
	 */
	public Tristate getFlagResultWhithoutParrents(String flag, String source, String target) {
		FlagValue flagValue = new FlagValue(true, source, target);
		return flagValues.containsKey(flag) && flagValues.get(flag).contains(flagValue) ? Tristate.fromBoolean(flagValues.get(flag).get(flagValues.get(flag).indexOf(flagValue)).getValue()) : Tristate.UNDEFINED;
	}

	/**
	 * Setting the value of the flag.
	 */
	public Region setFlag(Flags flagName, boolean value) {
		setFlag(flagName.toString(), value);
		return this;
	}

	/**
	 * Setting the value of the flag.
	 */
	public Region setFlag(String flagName, boolean value) {
		setFlag(flagName.toString(), value, null, null);
		return this;
	}

	/**
	 * Setting the value of the flag.
	 */
	public Region setFlag(Flags flagName, boolean value, String source, String target) {
		setFlag(flagName.toString(), value, source, target);
		return this;
	}

	/**
	 * Setting the value of the flag.
	 */
	public Region setFlag(String flagName, boolean value, String source, String target) {
		FlagValue flagValue = new FlagValue(value, source, target);
		removeFlag(flagName, flagValue);
		if(!flagValues.containsKey(flagName)) flagValues.put(flagName, new ArrayList<>());
		flagValues.get(flagName).add(flagValue);
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public Region removeFlag(Flags flagName) {
		removeFlag(flagName.toString());
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public Region removeFlag(String flagName) {
		if(flagValues.containsKey(flagName)) flagValues.remove(flagName);
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public Region removeFlag(Flags flagName, String source, String target) {
		removeFlag(flagName.toString(), new FlagValue(flat, source, target));
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public Region removeFlag(String flagName, String source, String target) {
		removeFlag(flagName.toString(), new FlagValue(flat, source, target));
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public Region removeFlag(Flags flagName, FlagValue flagValue) {
		removeFlag(flagName.toString(), flagValue);
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public Region removeFlag(String flagName, FlagValue flagValue) {
		while(flagValues.containsKey(flagName) && flagValues.get(flagName).contains(flagValue)) flagValues.get(flagName).remove(flagValue);
		return this;
	}

	/**
	 * Get the region creation time in unix format.
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * Getting region join message.<br>
	 * If the specified localization is not found, the default localization will be checked.<br>
	 * If no default localization is found, the first value found will be returned or an empty optional value if the list is empty.
	 * 
	 * @param locale - checking locale
	 */
	public Optional<Component> getJoinMessage(Locale locale) {
		return joinMessages.isEmpty() ? Optional.empty() : joinMessages.containsKey(locale.toLanguageTag()) ? Optional.of(deserialize(joinMessages.get(locale.toLanguageTag()))) : joinMessages.containsKey(Locales.DEFAULT.toLanguageTag()) ? Optional.of(deserialize(joinMessages.get(Locales.DEFAULT.toLanguageTag()))) : joinMessages.values().stream().map(this::deserialize).findFirst();
	}

	/**
	 * Setting region join message.<br>
	 * 
	 * @param message - setting message
	 * @param locale - setting locale
	 */
	public Region setJoinMessage(String message, Locale locale) {
		if(joinMessages.containsKey(locale.toLanguageTag())) joinMessages.remove(locale.toLanguageTag());
		if(message != null) joinMessages.put(locale.toLanguageTag(), message);
		return this;
	}


	/**
	 * Getting region exit message.<br>
	 * If the specified localization is not found, the default localization will be checked.<br>
	 * If no default localization is found, the first value found will be returned or an empty optional value if the list is empty.
	 * 
	 * @param locale - checking locale
	 */
	public Optional<Component> getExitMessage(Locale locale) {
		return exitMessages.isEmpty() ? Optional.empty() : exitMessages.containsKey(locale.toLanguageTag()) ? Optional.of(deserialize(exitMessages.get(locale.toLanguageTag()))) : exitMessages.containsKey(Locales.DEFAULT.toLanguageTag()) ? Optional.of(deserialize(joinMessages.get(Locales.DEFAULT.toLanguageTag()))) : exitMessages.values().stream().map(this::deserialize).findFirst();
	}

	/**
	 * Setting region exit message.<br>
	 * 
	 * @param message - setting message
	 * @param locale - setting locale
	 */
	public Region setExitMessage(String message, Locale locale) {
		if(exitMessages.containsKey(locale.toLanguageTag())) exitMessages.remove(locale.toLanguageTag());
		if(message != null) exitMessages.put(locale.toLanguageTag(), message);
		return this;
	}

	/**
	 * Not tested.
	 */
	public Optional<AdditionalData> getAdditionalData(PluginContainer container) {
		return additionalData.containsKey(container.metadata().id()) ? Optional.ofNullable(additionalData.get(container.metadata().id())) : Optional.empty();
	}

	/**
	 * Not tested.
	 */
	public void setAdditionalData(PluginContainer container, AdditionalData additionalData) {
		removeAdditionalData(container, additionalData);
		this.additionalData.put(regionType, additionalData);
	}

	/**
	 * Not tested.
	 */
	public void removeAdditionalData(PluginContainer container, AdditionalData additionalData) {
		if(this.additionalData.containsKey(container.metadata().id())) this.additionalData.remove(container.metadata().id());
	}

	/**
	 * Checking whether the position belongs to the region.
	 * 
	 * @param serverWorld - checkable World
	 * @param vector3i - checkable position
	 */
	public boolean isIntersectsWith(ServerWorld serverWorld, Vector3i vector3i) {
		return isIntersectsWith(serverWorld.key(), vector3i);
	}

	/**
	 * Checking whether the position belongs to the region.
	 * 
	 * @param worldkey - checkable World
	 * @param vector3i - checkable position
	 */
	public boolean isIntersectsWith(ResourceKey worldkey, Vector3i vector3i) {
		return world.equals(worldkey.asString()) && getCuboid().containsIntersectsPosition(vector3i);
	}

	/**
	 * Getting a list of loaded chunks.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public List<WorldChunk> getLoadedChunks() {
		if(!getServerWorld().isPresent()) return new ArrayList<>();
		ServerWorld serverWorld = getServerWorld().get();
		List<WorldChunk> chunks = new ArrayList<WorldChunk>();
		if(serverWorld.isLoaded()) {
			for(ChunkNumber chunkNumber : getChunkNumbers()) {
				Vector3i vector = chunkNumber.chunkPosition();
				if(serverWorld.isChunkLoaded(Vector3i.from(vector.x(), serverWorld.min().y(), vector.y()), false)) {
					WorldChunk worldChunk = serverWorld.chunkAtBlock(vector.x(), serverWorld.min().y(), vector.y());
					if(!chunks.contains(worldChunk)) chunks.add(worldChunk);
				}
			}
		}
		return chunks;
	}

	/**
	 * Getting a list of blocks in the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public List<BlockState> getBlocks() {
		if(!getServerWorld().isPresent() || !getServerWorld().get().isLoaded()) return new ArrayList<>();
		return getServerWorld().get().blockStateStream(cuboid.getMin().toInt(), cuboid.getMax().toInt(), StreamOptions.builder().setLoadingStyle(LoadingStyle.NONE).build()).toStream().map(VolumeElement::type).collect(Collectors.toList());
	}

	/**
	 * Getting a list of block entities in the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public List<BlockEntity> getBlockEntities() {
		if(!getServerWorld().isPresent() || !getServerWorld().get().isLoaded()) return new ArrayList<>();
		return getServerWorld().get().blockEntityStream(cuboid.getMin().toInt(), cuboid.getMax().toInt(), StreamOptions.builder().setLoadingStyle(LoadingStyle.NONE).build()).toStream().map(VolumeElement::type).collect(Collectors.toList());
	}

	/**
	 * Getting a list of entities in the region.<br>
	 * This list will not contain players.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public List<Entity> getEntities() {
		if(!getServerWorld().isPresent() || !getServerWorld().get().isLoaded()) return new ArrayList<>();
		return getServerWorld().get().entities(cuboid.getAABB()).stream().filter(entity -> (!(entity instanceof ServerPlayer))).collect(Collectors.toList());
	}

	/**
	 * Getting a list of players in the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public List<ServerPlayer> getPlayers() {
		if(!getServerWorld().isPresent() || !getServerWorld().get().isLoaded()) return new ArrayList<>();
		return getServerWorld().get().players().parallelStream().filter(player -> cuboid.containsIntersectsPosition(player.position().toInt())).collect(Collectors.toList());
	}

	/**
	 * Getting a list of the chunk numbers that the region occupies.
	 */
	public List<ChunkNumber> getChunkNumbers() {
		List<ChunkNumber> chunkNumbers = new ArrayList<ChunkNumber>();
		ChunkNumber min = new ChunkNumber(cuboid.getMin());
		ChunkNumber max = new ChunkNumber(cuboid.getMax());
		for(int x = min.x; x <= max.x; x++) {
			for(int z = min.z; z <= max.z; z++) {
				ChunkNumber chunkNumber = new ChunkNumber(x, z);
				if(!chunkNumbers.contains(chunkNumber)) chunkNumbers.add(chunkNumber);
			}
		}
		return chunkNumbers;
	}

	/**
	 * Create a Schematic from the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public Optional<Schematic> getSchematic(String schematicName, String altAuthor) {
		Schematic schematic = null;
		if(getServerWorld().isPresent() && getServerWorld().get().isLoaded() && !isGlobal()) {
			ArchetypeVolume archetypeVolume = getServerWorld().get()
					.createArchetypeVolume(
							cuboid.getMin(),
							cuboid.getMax(),
							cuboid.getAABB().center().toInt()
							);
			if(altAuthor != null && archetypeVolume != null) {
				schematic = Schematic.builder()
						.volume(archetypeVolume)
						.metaValue(Schematic.METADATA_AUTHOR, altAuthor)
						.metaValue(Schematic.METADATA_DATE, Instant.ofEpochMilli(creationTime).toString())
						.metaValue(Schematic.METADATA_NAME, schematicName)
						.build();
			} else {
				schematic = Schematic.builder()
						.volume(archetypeVolume)
						.metaValue(Schematic.METADATA_AUTHOR, getOwnerData().getName())
						.metaValue(Schematic.METADATA_DATE, Instant.ofEpochMilli(creationTime).toString())
						.metaValue(Schematic.METADATA_NAME, schematicName)
						.build();
			}
		}
		return Optional.ofNullable(schematic);
	}

	/**
	 * Insert Schematic into the region.<br><br>
	 * This operation can work overload the server.
	 * 
	 * @param schematic
	 * @param heigt - the height at which the insertion will be made
	 */
	public boolean putSchematic(Schematic schematic, int heigt) {
		if(getServerWorld().isPresent()) {
			schematic.applyToWorld(getServerWorld().get(), Vector3i.from(cuboid.getAABB().center().floorX(), heigt, cuboid.getAABB().center().floorY()), SpawnTypes.PLUGIN);
			return true;
		}
		return false;
	}


	/**
	 * Insert Schematic into the region.<br><br>
	 * This operation can work overload the server.
	 * 
	 * @param schematic
	 * @param vector3i - central position
	 */
	public boolean putSchematic(Schematic schematic, Vector3i vector3i) {
		if(getServerWorld().isPresent()) {
			schematic.applyToWorld(getServerWorld().get(), vector3i, SpawnTypes.PLUGIN);
			return true;
		}
		return false;
	}

	/**
	 * Territory regeneration in the region.
	 * 
	 * @param async - TODO
	 */
	public boolean regen(boolean async) {
		if(!getServerWorld().isPresent() || !getServerWorld().get().isLoaded() || cuboid == null) return false;
		ServerWorld world = getServerWorld().get();
		final String id = "tempworld_" + world.key().value();

		WorldGenerationConfig baseConfig = world.asTemplate().generationConfig();

		WorldTemplate tempWorldProperties = world.asTemplate().asBuilder()
			.key(ResourceKey.of("regionguard", id))
			.loadOnStartup(true)
			.serializationBehavior(SerializationBehavior.NONE)
			.generationConfig(baseConfig)
			.build();


		try {
			ServerWorld tempWorld = Sponge.server().worldManager().loadWorld(tempWorldProperties).get();
			// Pre-gen all the chunks
			// We need to also pull one more chunk in every direction
			for (ChunkNumber chunkNumber : getChunkNumbers()) {
				tempWorld.loadChunk(chunkNumber.chunkPosition(), true);
			}
			for(ChunkNumber chunkNumber : getChunkNumbers()) if(!tempWorld.isChunkLoaded(chunkNumber.chunkPosition(), true)) tempWorld.loadChunk(chunkNumber.chunkPosition(), true);
			
			for(Vector3i vector3i : getCuboid().getAllPositions()) {
				world.setBlock(vector3i, tempWorld.block(vector3i));
			}
		
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		} finally {
			removeWorld(tempWorldProperties);
		}
		return true;
	}

	private void removeWorld(WorldTemplate template) {
		Sponge.server().worldManager().unloadWorld(template.key()).thenRun(() -> Sponge.server().worldManager().deleteWorld(template.key()));
	}

	private Component deserialize(String string) {
		try {
			return GsonComponentSerializer.gson().deserialize(string);
		} catch (Exception e) {
			return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		Region other = (Region) obj;
		return Objects.equals(regionUUID, other.regionUUID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(regionUUID);
	}

	@Override
	public String toString() {
		return "RegionUUID: " + regionUUID +
				" Region names: " + names.toString() +
				" OwnerUUID: " + getOwnerUUID() +
				" Owner name: " + getOwnerName() +
				" Region names: " + names.toString();
	}
}
