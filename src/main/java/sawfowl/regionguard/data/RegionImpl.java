package sawfowl.regionguard.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.cause.entity.SpawnTypes;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.api.world.DefaultWorldKeys;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.api.world.schematic.Schematic;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.volume.archetype.ArchetypeVolume;
import org.spongepowered.api.world.volume.stream.StreamOptions;
import org.spongepowered.api.world.volume.stream.StreamOptions.LoadingStyle;
import org.spongepowered.api.world.volume.stream.VolumeElement;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.math.vector.Vector3i;
import org.spongepowered.plugin.PluginContainer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.AdditionalData;
import sawfowl.regionguard.api.data.AdditionalDataMap;
import sawfowl.regionguard.api.data.ChunkNumber;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.SelectorTypes;

@ConfigSerializable
public class RegionImpl implements Region {

	public RegionImpl() {}
	public Region.Builder builder() {
		return new Builder() {
			
			@Override
			public @NotNull Region build() {
				setCreationTime(System.currentTimeMillis());
				return RegionImpl.this;
			}

			@Override
			public Builder setUniqueId(UUID uuid) {
				if(uuid == null) return this;
				regionUUID = uuid;
				return this;
			}

			@Override
			public Builder setCreationTime(long time) {
				creationTime = time;;
				return this;
			}

			@Override
			public Builder setWorld(ResourceKey worldKey) {
				if(worldKey == null) worldKey = DefaultWorldKeys.DEFAULT;
				world = worldKey.asString();
				return this;
			}

			@Override
			public Builder setWorld(ServerWorld serverWorld) {
				return setWorld(serverWorld.key());
			}

			@Override
			public Builder setType(RegionTypes type) {
				if(type == null) type = RegionTypes.CLAIM;
				regionType = type.toString();
				return this;
			}

			@Override
			public Builder setServerOwner() {
				if(!members.isEmpty()) members.removeIf(member -> member.getTrustType() == TrustTypes.OWNER);
				members.add(MemberData.forServer());
				return this;
			}

			@Override
			public Builder setParrent(Region region) {
				if(region == null) return this;
				parrent = (RegionImpl) region;
				return this;
			}

			@Override
			public Builder setOwner(ServerPlayer player) {
				if(!members.isEmpty()) members.removeIf(member -> member.getTrustType() == TrustTypes.OWNER);
				setTrustType(player, TrustTypes.OWNER);
				return this;
			}

			@Override
			public Builder setName(Locale locale, Component name) {
				RegionImpl.this.setName(name, locale);
				return this;
			}

			@Override
			public Builder setFlags(Map<String, Set<FlagValue>> flags) {
				if(flags == null) return this;
				RegionImpl.this.setFlags(flags);
				return this;
			}

			@Override
			public Builder setCuboid(Cuboid cuboid) {
				if(cuboid == null) return this;
				RegionImpl.this.cuboid = (CuboidImpl) cuboid;
				return this;
			}

			@Override
			public Builder addMembers(Collection<MemberData> members) {
				if(members == null) return this;
				RegionImpl.this.members.addAll(members);
				return this;
			}

			@Override
			public Builder addJoinMessages(Map<String, Component> messages) {
				if(messages == null) return this;
				joinMessages.putAll(messages);
				return this;
			}

			@Override
			public Builder addExitMessages(Map<String, Component> messages) {
				if(messages == null) return this;
				exitMessages.putAll(messages);
				return this;
			}

			@Override
			public Builder addNames(Map<String, Component> names) {
				if(names == null) return this;
				RegionImpl.this.names.putAll(names);
				return this;
			}

			@Override
			public <T extends AdditionalData> Builder addAdditionalData(Map<String, Map<String, T>> dataMap) {
				if(dataMap == null) return this;
				if(additionalData == null) additionalData = new AdditionalDataHashMap<T>().from(dataMap);
				return this;
			}

			@Override
			public Builder addChilds(Collection<Region> regions) {
				if(regions == null) return this;
				childs.addAll(regions);
				childs.forEach(child -> {
					child.setParrent(RegionImpl.this);
				});
				return this;
			}
		};
	}

	@Setting("Name")
	private Map<String, Component> names = new HashMap<String, Component>();
	@Setting("UUID")
	private UUID regionUUID = UUID.randomUUID();
	@Setting("World")
	private String world = DefaultWorldKeys.DEFAULT.asString();
	@Setting("Cuboid")
	private Cuboid cuboid;
	@Setting("Childs")
	private Set<Region> childs = new HashSet<Region>();
	@Setting("Flags")
	private Map<String, Set<FlagValue>> flagValues = new HashMap<String, Set<FlagValue>>();
	@Setting("Members")
	private Set<MemberData> members = new HashSet<MemberData>();
	@Setting("RegionType")
	private String regionType;
	@Setting("Created")
	private long creationTime = 0;
	@Setting("JoinMessage")
	private Map<String, Component> joinMessages = new HashMap<String, Component>();
	@Setting("ExitMessage")
	private Map<String, Component> exitMessages = new HashMap<String, Component>();
	@Setting("AdditionalData")
	private AdditionalDataMap<? extends AdditionalData> additionalData = null;
	private Region parrent;

	/**
	 * Getting the region name.
	 *
	 * @param locale - language to be checked
	 */
	public Optional<String> getPlainName(Locale locale) {
		return names.isEmpty() ? Optional.empty() : names.containsKey(locale.toLanguageTag()) ? Optional.of(toPlain(names.get(locale.toLanguageTag()))) : names.containsKey(Locales.DEFAULT.toLanguageTag()) ? Optional.of(toPlain(names.get(Locales.DEFAULT.toLanguageTag()))) : names.values().stream().findFirst().map(this::toPlain);
	}

	/**
	 * Getting the region name as kyori component.
	 *
	 * @param locale - language to be checked
	 */
	public Component getName(Locale locale) {
		Optional<String> name = getPlainName(locale);
		return name.isPresent() ? TextUtils.deserialize(name.get()) : Component.empty();
	}

	/**
	 * Set region name.
	 */
	public RegionImpl setName(Component name, Locale locale) {
		if(names.containsKey(locale.toLanguageTag())) names.remove(locale.toLanguageTag());
		if(name != null) names.put(locale.toLanguageTag(), name);
		return this;
	}

	@Override
	public Map<String, Component> getNames() {
		return names;
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
		Optional<MemberData> optOwnerData = members.stream().filter(member -> member.getTrustType() == TrustTypes.OWNER).findFirst();
		UUID uuid = optOwnerData.isPresent() ? optOwnerData.get().getUniqueId() : parrent != null ? parrent.getOwnerUUID() : new UUID(0, 0);
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
		return setTrustType(owner, TrustTypes.OWNER);
	}

	/**
	 * Make the player the new owner of the region.
	 *
	 * @param owner - new owner
	 */
	@Override
	public Region setOwner(GameProfile owner) {
		return setTrustType(owner, TrustTypes.OWNER);
	}

	/**
	 * Adding a player to a region.
	 *
	 * @param player - addable player
	 * @param type - assignable trust type
	 */
	public Region setTrustType(ServerPlayer player, TrustTypes type) {
		return setTrustType(player.profile(), type);
	}

	/**
	 * Adding a player to a region.
	 *
	 * @param player - addable player
	 * @param type - assignable trust type
	 */
	@Override
	public Region setTrustType(GameProfile player, TrustTypes type) {
		untrust(player.uniqueId());
		if(regionType.equals("Admin")) return this;
		if(type == TrustTypes.OWNER) members.removeIf(member -> member.getTrustType() == TrustTypes.OWNER);
		members.add(MemberData.of(player, type));
		return this;
	}

	@Override
	public Region setTrustType(UUID uuid, TrustTypes type) {
		getMemberData(uuid).ifPresent(member -> {
			member.setTrustType(type);
		});
		return this;
	}

	/**
	 * Getting the data of the region owner
	 */
	public MemberData getOwnerData() {
		return (members.size() > 10000 ? members.parallelStream() : members.stream()).filter(member -> (member.getTrustType() == TrustTypes.OWNER)).findFirst().orElse(parrent != null ? parrent.getOwnerData() : MemberData.forServer());
	}

	@Override
	public List<MemberData> getMembers() {
		return members.stream().collect(Collectors.toUnmodifiableList());
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
		return Optional.ofNullable(members.stream().filter(member -> member.getUniqueId().equals(uuid)).findFirst().orElse(parrent != null ? parrent.getMemberData(uuid).map(m -> m).orElse(null) : null));
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
		return !isAdmin() && !isGlobal() && (getTrustType(uuid) == level || getTrustType(uuid).equals(level));
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
		return getMemberData(uuid).map(member -> member.getTrustType()).orElse(TrustTypes.WITHOUT_TRUST);
	}

	/**
	 * Removing a player from a region.
	 */
	public void untrust(ServerPlayer player) {
		untrust(player.uniqueId());
	}


	/**
	 * Removing a player or entity from a region.
	 */
	public void untrust(UUID uuid) {
		members.removeIf(member -> member.getUniqueId().equals(uuid));
		if(parrent != null) parrent.untrust(uuid);
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
		return members.stream().filter(member -> member.getUniqueId().equals(uuid)).findFirst().isPresent() || (parrent != null ? parrent.isTrusted(uuid) : false);
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
	public Optional<ServerWorld> getWorld() {
		return Sponge.server().worldManager().world(ResourceKey.resolve(world));
	}

	/**
	 * Getting the world key.
	 */
	public ResourceKey getWorldKey() {
		try {
			return ResourceKey.resolve(world);
		} catch (Exception e) {
			RegionGuard.getInstance().getLogger().error("Error in region " + regionUUID + " configuration. Failed to parse world key " + world + "\n" + e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * Getting the region cuboid.
	 */
	public Cuboid getCuboid() {
		if(cuboid != null && cuboid.getSelectorType() == SelectorTypes.FLAT && getWorld().isPresent() && (getWorld().get().min().y() != cuboid.getAABB().min().y() || getWorld().get().max().y() != cuboid.getAABB().max().y())) {
			if(!((CuboidImpl) cuboid).getWorld().isPresent()) ((CuboidImpl) cuboid).setSupplier(getWorld());
			cuboid.setPositions(cuboid.getMin(), cuboid.getMax(), cuboid.getSelectorType());
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
		if(type == RegionTypes.UNSET) return false;
		if(type == RegionTypes.ADMIN) {
			members.clear();
			members.add(MemberData.forServer());
		}
		regionType = type.toString();
		return true;
	}

	/**
	 * Check if the region is global.
	 */
	public boolean isGlobal() {
		return regionType == null || regionType.equals(RegionTypes.GLOBAL.toString());
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
	public RegionImpl setCuboid(Vector3i first, Vector3i second, SelectorTypes selectorType) {
		if(selectorType == null) return this;
		cuboid = (CuboidImpl) Cuboid.builder(selectorType).setFirstPosition(first).setSecondPosition(second).build();
		if(!((CuboidImpl) cuboid).getWorld().isPresent()) ((CuboidImpl) cuboid).setSupplier(getWorld());
		if(selectorType.equals(SelectorTypes.FLAT)) {
			first = Vector3i.from(first.x(), Sponge.server().worldManager().world(DefaultWorldKeys.DEFAULT).get().min().y(), first.z());
			second = Vector3i.from(second.x(), Sponge.server().worldManager().world(DefaultWorldKeys.DEFAULT).get().max().y(), second.z());
		}
		if(getWorld().isPresent() && first != null && second != null) cuboid.setPositions(first, second, selectorType);
		return this;
	}

	public RegionImpl setCuboid(Cuboid cuboid) {
		this.cuboid = (CuboidImpl) cuboid;
		if(!((CuboidImpl) this.cuboid).getWorld().isPresent()) ((CuboidImpl) this.cuboid).setSupplier(getWorld());
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
		parrent = (RegionImpl) region;
		return this;
	}

	/**
	 * Checking if there are child regions in the region.
	 */
	public boolean containsChilds() {
		return !childs.isEmpty();
	}

	/**
	 * Searching for a child region.<br>
	 * If no region is found, it will return the current region.
	 */
	public Region getChild(Vector3i position) {
		return containsChilds() ? (childs.size() > 10000 ? childs.parallelStream() : childs.stream()).filter(child -> child.isIntersectsWith(position)).findFirst().orElse(this) : this;
	}

	/**
	 * Adding a child region.
	 */
	public Region addChild(Region region) {
		childs.add((RegionImpl) region);
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
		return childs.stream().map(child -> (Region) child).toList();
	}

	/**
	 * Recursively search and list flagValues from the current region to the oldest parent.
	 */
	public Map<String, Set<FlagValue>> getFlags() {
		if(!getParrent().isPresent()) return flagValues;
		Map<String, Set<FlagValue>> flagValues = new HashMap<String, Set<FlagValue>>();
		flagValues.putAll(getParrent().get().getFlags());
		for(Entry<String, Set<FlagValue>> entry : this.flagValues.entrySet()) flagValues.replace(entry.getKey(), entry.getValue());
		return flagValues;
	}

	/**
	 * Recursive search and enumeration of flagValues with parameters from the current region to the oldest parent.
	 */
	public Map<String, Set<FlagValue>> getCustomFlags() {
		Map<String, Set<FlagValue>> customFlags = new HashMap<String, Set<FlagValue>>();
		FlagValue defaultValue = FlagValue.simple(true);
		flagValues.entrySet().parallelStream().forEach(entry -> {
			Set<FlagValue> custom = new HashSet<>();
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
	public Set<FlagValue> getFlagValues(Flags flagName) {
		return getFlagValues(flagName.toString());
	}

	/**
	 * Getting the values of the flag in the region. <br>
	 * If there is no flag, an attempt will be made to check it in the parent region.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<FlagValue> getFlagValues(String flagName) {
		return containsFlag(flagName) ? (Set) flagValues.get(flagName) : parrent != null ? parrent.getFlagValues(flagName) : new HashSet<>();
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
		return flagValues.containsKey(flag) ? flagValues.get(flag).stream().filter(value -> value.equalsTo(source, target)).findFirst().map(value -> value.asTristate()).orElse(Tristate.UNDEFINED) : Tristate.UNDEFINED;
	}

	/**
	 * Setting the value of the flag.
	 */
	public RegionImpl setFlag(Flags flagName, boolean value) {
		setFlag(flagName.toString(), value);
		return this;
	}

	/**
	 * Setting the value of the flag.
	 */
	public RegionImpl setFlag(String flagName, boolean value) {
		setFlag(flagName.toString(), value, null, null);
		return this;
	}

	/**
	 * Setting the value of the flag.
	 */
	public RegionImpl setFlag(Flags flagName, boolean value, String source, String target) {
		setFlag(flagName.toString(), value, source, target);
		return this;
	}

	/**
	 * Setting the value of the flag.
	 */
	public RegionImpl setFlag(String flagName, boolean value, String source, String target) {
		FlagValueImpl flagValue = (FlagValueImpl) FlagValue.of(value, source, target);
		removeFlag(flagName, flagValue);
		if(!flagValues.containsKey(flagName)) flagValues.put(flagName, new HashSet<>());
		flagValues.get(flagName).add(flagValue);
		return this;
	}

	/**
	 * Set the values of a set of flags.
	 *
	 * @param flags - Map with flags and their values.
	 */
	public RegionImpl setFlags(Map<String, Set<FlagValue>> flags) {
		flags.entrySet().forEach(entry -> {
			if(flagValues.containsKey(entry.getKey())) flagValues.remove(entry.getKey());
			flagValues.put(entry.getKey(), entry.getValue().stream().map(value -> (FlagValueImpl) value).collect(Collectors.toSet()));
		});
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public RegionImpl removeFlag(Flags flagName) {
		removeFlag(flagName.toString());
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public RegionImpl removeFlag(String flagName) {
		if(flagValues.containsKey(flagName)) flagValues.remove(flagName);
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public RegionImpl removeFlag(Flags flagName, String source, String target) {
		removeFlag(flagName.toString(), FlagValue.of(true, source, target));
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public RegionImpl removeFlag(String flagName, String source, String target) {
		removeFlag(flagName.toString(), FlagValue.of(true, source, target));
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public RegionImpl removeFlag(Flags flagName, FlagValue flagValue) {
		removeFlag(flagName.toString(), flagValue);
		return this;
	}

	/**
	 * Removing the flag from the region.
	 */
	public RegionImpl removeFlag(String flagName, FlagValue flagValue) {
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
		return joinMessages.isEmpty() ? Optional.empty() : joinMessages.containsKey(locale.toLanguageTag()) ? Optional.of(joinMessages.get(locale.toLanguageTag())) : joinMessages.containsKey(Locales.DEFAULT.toLanguageTag()) ? Optional.of(joinMessages.get(Locales.DEFAULT.toLanguageTag())) : joinMessages.values().stream().findFirst();
	}

	/**
	 * Setting region join message.<br>
	 *
	 * @param message - setting message
	 * @param locale - setting locale
	 */
	public Region setJoinMessage(Component message, Locale locale) {
		if(joinMessages.containsKey(locale.toLanguageTag())) joinMessages.remove(locale.toLanguageTag());
		if(message != null) joinMessages.put(locale.toLanguageTag(), message);
		return this;
	}

	public Map<String, Component> getJoinMessages() {
		return joinMessages;
	}

	/**
	 * Getting region exit message.<br>
	 * If the specified localization is not found, the default localization will be checked.<br>
	 * If no default localization is found, the first value found will be returned or an empty optional value if the list is empty.
	 *
	 * @param locale - checking locale
	 */
	public Optional<Component> getExitMessage(Locale locale) {
		return exitMessages.isEmpty() ? Optional.empty() : exitMessages.containsKey(locale.toLanguageTag()) ? Optional.of(exitMessages.get(locale.toLanguageTag())) : exitMessages.containsKey(Locales.DEFAULT.toLanguageTag()) ? Optional.of(exitMessages.get(Locales.DEFAULT.toLanguageTag())) : exitMessages.values().stream().findFirst();
	}

	/**
	 * Setting region exit message.<br>
	 *
	 * @param message - setting message
	 * @param locale - setting locale
	 */
	public RegionImpl setExitMessage(Component message, Locale locale) {
		if(exitMessages.containsKey(locale.toLanguageTag())) exitMessages.remove(locale.toLanguageTag());
		if(message != null) exitMessages.put(locale.toLanguageTag(), message);
		return this;
	}

	public Map<String, Component> getExitMessages() {
		return exitMessages;
	}

	/**
	 * Getting additional data that is created by other plugins.<br>
	 * After getting the data, they must be converted to the desired type.<br>
	 * Exemple: YourDataClass yourDataClass = (YourDataClass) additionalData;
	 */
	@SuppressWarnings("unchecked")
	public <T extends AdditionalData> Optional<T> getAdditionalData(PluginContainer container, String dataName, Class<T> clazz) {
		return additionalData.getData(container, dataName, clazz).map(data -> (T) data);
	}

	/**
	 * Write additional data created by another plugin.
	 */
	public void setAdditionalData(PluginContainer container, String dataName, AdditionalData additionalData) {
		if(this.additionalData == null) this.additionalData = new AdditionalDataHashMap<>();
		this.additionalData.put(container, dataName, additionalData);
	}

	/**
	 * Deleting additional data created by another plugin.
	 */
	public void removeAdditionalData(PluginContainer container, String dataName) {
		if(this.additionalData.containsKey(container.metadata().id()) && this.additionalData.get(container.metadata().id()).containsKey(dataName)) this.additionalData.get(container.metadata().id()).remove(dataName);
	}

	public AdditionalDataMap<? extends AdditionalData> getAllAdditionalData() {
		return additionalData;
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
	 * Checking whether the position belongs to the region.
	 * This method should only be applied to child regions, as it does not perform a world matching check.
	 *
	 * @param vector3i - checkable position
	 */
	public boolean isIntersectsWith(Vector3i vector3i) {
		return getCuboid().containsIntersectsPosition(vector3i);
	}

	/**
	 * Getting a list of loaded chunks.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public List<WorldChunk> getLoadedChunks() {
		if(!getWorld().isPresent()) return new ArrayList<>();
		ServerWorld serverWorld = getWorld().get();
		List<WorldChunk> chunks = new ArrayList<WorldChunk>();
		if(serverWorld.isLoaded()) {
			for(ChunkNumber chunkNumberImpl : getChunkNumbers()) {
				Vector3i vector = chunkNumberImpl.chunkPosition();
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
		if(!getWorld().isPresent() || !getWorld().get().isLoaded()) return new ArrayList<>();
		return getWorld().get().blockStateStream(cuboid.getMin().toInt(), cuboid.getMax().toInt(), StreamOptions.builder().setLoadingStyle(LoadingStyle.NONE).build()).toStream().map(VolumeElement::type).collect(Collectors.toList());
	}

	/**
	 * Getting a list of block entities in the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public List<BlockEntity> getBlockEntities() {
		if(!getWorld().isPresent() || !getWorld().get().isLoaded()) return new ArrayList<>();
		return getWorld().get().blockEntityStream(cuboid.getMin().toInt(), cuboid.getMax().toInt(), StreamOptions.builder().setLoadingStyle(LoadingStyle.NONE).build()).toStream().map(VolumeElement::type).collect(Collectors.toList());
	}

	/**
	 * Getting a list of entities in the region.<br>
	 * This list will not contain players.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public List<Entity> getEntities() {
		if(!getWorld().isPresent() || !getWorld().get().isLoaded()) return new ArrayList<>();
		return getWorld().get().entities(cuboid.getAABB()).stream().filter(entity -> (!(entity instanceof ServerPlayer))).collect(Collectors.toList());
	}

	/**
	 * Getting a list of players in the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public List<ServerPlayer> getPlayers() {
		if(!getWorld().isPresent() || !getWorld().get().isLoaded()) return new ArrayList<>();
		return getWorld().get().players().parallelStream().filter(player -> cuboid.containsIntersectsPosition(player.position().toInt())).collect(Collectors.toList());
	}

	/**
	 * Getting a list of the chunk numbers that the region occupies.
	 */
	public List<ChunkNumber> getChunkNumbers() {
		List<ChunkNumber> chunkNumberImpls = new ArrayList<ChunkNumber>();
		ChunkNumber min = ChunkNumber.of(cuboid.getMin());
		ChunkNumber max = ChunkNumber.of(cuboid.getMax());
		for(int x = min.getX(); x <= max.getX(); x++) {
			for(int z = min.getZ(); z <= max.getZ(); z++) {
				ChunkNumber chunkNumberImpl = new ChunkNumberImpl(x, z);
				if(!chunkNumberImpls.contains(chunkNumberImpl)) chunkNumberImpls.add(chunkNumberImpl);
			}
		}
		return chunkNumberImpls;
	}

	/**
	 * Create a Schematic from the region.<br>
	 * This operation can work overload the server. It is recommended to use in asynchronous mode.
	 */
	public Optional<Schematic> getSchematic(String schematicName, String altAuthor) {
		Schematic schematic = null;
		if(getWorld().isPresent() && getWorld().get().isLoaded() && !isGlobal()) {
			ArchetypeVolume archetypeVolume = getWorld().get()
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
		if(getWorld().isPresent()) {
			schematic.applyToWorld(getWorld().get(), Vector3i.from(cuboid.getAABB().center().floorX(), heigt, cuboid.getAABB().center().floorY()), SpawnTypes.PLUGIN);
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
		if(getWorld().isPresent()) {
			schematic.applyToWorld(getWorld().get(), vector3i, SpawnTypes.PLUGIN);
			return true;
		}
		return false;
	}


	/**
	 * Territory regeneration in the region.
	 *
	 * @param async - regen in async mode
	 * @param delay - delay before regeneration in async mode
	 */
	public boolean regen(boolean async, int delay) {
		return async ? RegionGuard.getInstance().getRegenUtil().regenAsync(this, delay) : RegionGuard.getInstance().getRegenUtil().regenSync(this);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(this == obj) return true;
		if(getClass() != obj.getClass()) return false;
		return Objects.equals(regionUUID, ((RegionImpl) obj).regionUUID);
	}

	public boolean equalsOwners(Region region) {
		return Objects.equals(getOwnerUUID(), region.getOwnerUUID());
	}

	public Region copy() {
		RegionImpl region = new RegionImpl();
		region.childs = childs;
		region.creationTime = creationTime;
		region.cuboid = cuboid;
		region.additionalData = additionalData;
		region.exitMessages = exitMessages;
		region.flagValues = flagValues;
		region.joinMessages = joinMessages;
		region.members = members;
		region.names = names;
		region.parrent = parrent;
		region.regionType = regionType;
		region.regionUUID = regionUUID;
		region.world = world;
		return region;
	}

	@Override
	public int hashCode() {
		return Objects.hash(regionUUID);
	}

	@Override
	public String toString() {
		return "RegionImplUUID: " + regionUUID +
				" RegionImpl names: " + names.toString() +
				" OwnerUUID: " + getOwnerUUID() +
				" Owner name: " + getOwnerName() +
				" Region names: " + names.toString();
	}

	@Override
	public int contentVersion() {
		return 0;
	}

	@Override
	public DataContainer toContainer() {
		return null;
	}

	private String toPlain(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}
}
