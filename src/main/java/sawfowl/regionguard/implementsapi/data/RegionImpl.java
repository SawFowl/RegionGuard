package sawfowl.regionguard.implementsapi.data;

import java.io.BufferedWriter;
import java.io.StringWriter;
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
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.math.vector.Vector3i;
import org.spongepowered.plugin.PluginContainer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import sawfowl.localeapi.api.TextUtils;
import sawfowl.localeapi.api.serializetools.SerializeOptions;
import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.AdditionalData;
import sawfowl.regionguard.api.data.ChunkNumber;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.SelectorTypes;

public class RegionImpl implements Region {

	public RegionImpl() {}
	public Region.Builder builder() {
		return new Builder() {
			
			@Override
			public @NotNull Region build() {
				if(creationTime == 0) setCreationTime(System.currentTimeMillis());
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
				regionType = type;
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
				RegionImpl.this.cuboid = cuboid;
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
			public Builder addAdditionalData(Map<String, Map<String, JsonObject>> dataMap) {
				if(dataMap == null) return this;
				if(additionalDataMap == null) additionalDataMap = dataMap;
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

	private Map<String, Component> names = new HashMap<String, Component>();
	private UUID regionUUID = UUID.randomUUID();
	private String world = DefaultWorldKeys.DEFAULT.asString();
	private Cuboid cuboid;
	private Set<Region> childs = new HashSet<Region>();
	private Map<String, Set<FlagValue>> flagValues = new HashMap<String, Set<FlagValue>>();
	private Map<String, Set<FlagValue>> tempFlags = new HashMap<String, Set<FlagValue>>();
	private Set<MemberData> members = new HashSet<MemberData>();
	private RegionTypes regionType;
	private long creationTime = 0;
	private Map<String, Component> joinMessages = new HashMap<String, Component>();
	private Map<String, Component> exitMessages = new HashMap<String, Component>();
	private Map<String, Map<String, JsonObject>> additionalDataMap = null;
	private Region parrent;

	@Override
	public Optional<String> getPlainName(Locale locale) {
		return names.isEmpty() ? Optional.empty() : names.containsKey(locale.toLanguageTag()) ? Optional.of(toPlain(names.get(locale.toLanguageTag()))) : names.containsKey(Locales.DEFAULT.toLanguageTag()) ? Optional.of(toPlain(names.get(Locales.DEFAULT.toLanguageTag()))) : names.values().stream().findFirst().map(this::toPlain);
	}

	@Override
	public Component getName(Locale locale) {
		Optional<String> name = getPlainName(locale);
		return name.isPresent() ? TextUtils.deserialize(name.get()) : Component.empty();
	}

	@Override
	public Region setName(Component name, Locale locale) {
		if(names.containsKey(locale.toLanguageTag())) names.remove(locale.toLanguageTag());
		if(name != null) names.put(locale.toLanguageTag(), name);
		return this;
	}

	@Override
	public Map<String, Component> getNames() {
		return names;
	}

	@Override
	public Optional<ServerPlayer> getOwner() {
		return !getOwnerData().isPlayer() || getOwnerUUID().equals(new UUID(0, 0)) ? Optional.empty() : Sponge.server().player(getOwnerUUID());
	}

	@Override
	public UUID getOwnerUUID() {
		Optional<MemberData> optOwnerData = members.stream().filter(member -> member.getTrustType() == TrustTypes.OWNER).findFirst();
		UUID uuid = optOwnerData.isPresent() ? optOwnerData.get().getUniqueId() : parrent != null ? parrent.getOwnerUUID() : new UUID(0, 0);
		return uuid;
	}

	@Override
	public String getOwnerName() {
		return getOwnerData().getName();
	}

	@Override
	public Region setOwner(ServerPlayer owner) {
		return setTrustType(owner, TrustTypes.OWNER);
	}

	@Override
	public Region setOwner(GameProfile owner) {
		return setTrustType(owner, TrustTypes.OWNER);
	}

	@Override
	public Region setTrustType(ServerPlayer player, TrustTypes type) {
		return setTrustType(player.profile(), type);
	}

	@Override
	public Region setTrustType(GameProfile player, TrustTypes type) {
		untrust(player.uniqueId());
		if(regionType == RegionTypes.ADMIN) return this;
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

	@Override
	public MemberData getOwnerData() {
		return (members.size() > 10000 ? members.parallelStream() : members.stream()).filter(member -> (member.getTrustType() == TrustTypes.OWNER)).findFirst().orElse(parrent != null ? parrent.getOwnerData() : MemberData.forServer());
	}

	@Override
	public List<MemberData> getMembers() {
		return members.stream().collect(Collectors.toUnmodifiableList());
	}

	@Override
	public Optional<MemberData> getMemberData(ServerPlayer player) {
		return getMemberData(player.uniqueId());
	}

	@Override
	public Optional<MemberData> getMemberData(UUID uuid) {
		return Optional.ofNullable(members.stream().filter(member -> member.getUniqueId().equals(uuid)).findFirst().orElse(parrent != null ? parrent.getMemberData(uuid).map(m -> m).orElse(null) : null));
	}

	@Override
	public int getTotalMembers() {
		return members.size();
	}

	@Override
	public boolean isCurrentTrustType(ServerPlayer player, TrustTypes level) {
		return isCurrentTrustType(player.uniqueId(), level);
	}

	@Override
	public boolean isCurrentTrustType(UUID uuid, TrustTypes level) {
		return !isAdmin() && !isGlobal() && (getTrustType(uuid) == level || getTrustType(uuid).equals(level));
	}

	@Override
	public TrustTypes getTrustType(ServerPlayer player) {
		return getTrustType(player.uniqueId());
	}

	@Override
	public TrustTypes getTrustType(UUID uuid) {
		return getMemberData(uuid).map(member -> member.getTrustType()).orElse(TrustTypes.WITHOUT_TRUST);
	}

	@Override
	public void untrust(ServerPlayer player) {
		untrust(player.uniqueId());
	}

	@Override
	public void untrust(UUID uuid) {
		members.removeIf(member -> member.getUniqueId().equals(uuid));
		if(parrent != null) parrent.untrust(uuid);
	}

	@Override
	public boolean isTrusted(ServerPlayer player) {
		return isTrusted(player.uniqueId());
	}

	@Override
	public boolean isTrusted(UUID uuid) {
		return members.stream().filter(member -> member.getUniqueId().equals(uuid)).findFirst().isPresent() || (parrent != null ? parrent.isTrusted(uuid) : false);
	}

	@Override
	public UUID getUniqueId() {
		return regionUUID;
	}

	@Override
	public Optional<ServerWorld> getWorld() {
		return Sponge.server().worldManager().world(ResourceKey.resolve(world));
	}

	@Override
	public ResourceKey getWorldKey() {
		try {
			return ResourceKey.resolve(world);
		} catch (Exception e) {
			RegionGuard.getInstance().getLogger().error("Error in region " + regionUUID + " configuration. Failed to parse world key " + world + "\n" + e.getLocalizedMessage());
		}
		return null;
	}

	@Override
	public Cuboid getCuboid() {
		if(cuboid != null && cuboid.getSelectorType() == SelectorTypes.FLAT && getWorld().isPresent() && (getWorld().get().min().y() != cuboid.getMin().y() || getWorld().get().max().y() != cuboid.getMax().y())) {
			cuboid.setPositions(cuboid.getMin(), cuboid.getMax(), cuboid.getSelectorType(), getWorld().orElse(null));
		}
		return cuboid;
	}

	@Override
	public RegionTypes getType() {
		return regionType;
	}

	@Override
	public boolean setRegionType(RegionTypes type) {
		if(type == RegionTypes.UNSET) return false;
		if(type == RegionTypes.ADMIN) {
			members.clear();
			members.add(MemberData.forServer());
		}
		regionType = type;
		return true;
	}

	@Override
	public boolean isGlobal() {
		return regionType == null || regionType == RegionTypes.GLOBAL;
	}

	@Override
	public boolean isAdmin() {
		return regionType == RegionTypes.ADMIN;
	}

	@Override
	public boolean isArena() {
		return regionType == RegionTypes.ARENA;
	}

	@Override
	public boolean isBasicClaim() {
		return regionType == RegionTypes.CLAIM;
	}

	@Override
	public boolean isSubdivision() {
		return regionType == RegionTypes.SUBDIVISION;
	}

	@Override
	public Region setCuboid(Vector3i first, Vector3i second, SelectorTypes selectorType) {
		if(selectorType == null) return this;
		cuboid = Cuboid.builder(selectorType).setFirstPosition(first).setSecondPosition(second).build();
		if(selectorType.equals(SelectorTypes.FLAT)) {
			first = Vector3i.from(first.x(), Sponge.server().worldManager().world(DefaultWorldKeys.DEFAULT).get().min().y(), first.z());
			second = Vector3i.from(second.x(), Sponge.server().worldManager().world(DefaultWorldKeys.DEFAULT).get().max().y(), second.z());
		}
		if(getWorld().isPresent() && first != null && second != null) cuboid.setPositions(first, second, selectorType, getWorld().orElse(null));
		return this;
	}

	@Override
	public Region setCuboid(Cuboid cuboid) {
		this.cuboid = cuboid;
		return this;
	}

	@Override
	public Optional<Region> getParrent() {
		return Optional.ofNullable(parrent);
	}

	@Override
	public Region getPrimaryParent() {
		return parrent == null ? this : parrent.getPrimaryParent();
	}

	@Override
	public Region setParrent(Region region) {
		regionType = RegionTypes.SUBDIVISION;
		parrent = (RegionImpl) region;
		return this;
	}

	@Override
	public boolean containsChilds() {
		return !childs.isEmpty();
	}

	@Override
	public Region getChild(Vector3i position) {
		return containsChilds() ? (childs.size() > 10000 ? childs.parallelStream() : childs.stream()).filter(child -> child.isIntersectsWith(position)).findFirst().orElse(this) : this;
	}

	@Override
	public Region addChild(Region region) {
		childs.add((RegionImpl) region);
		return this;
	}

	@Override
	public Region removeChild(Region region) {
		if(childs.contains(region)) childs.remove(region);
		return this;
	}

	@Override
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

	@Override
	public List<Region> getChilds() {
		return childs.stream().map(child -> (Region) child).toList();
	}

	@Override
	public Map<String, Set<FlagValue>> getFlags() {
		if(!getParrent().isPresent()) return flagValues;
		Map<String, Set<FlagValue>> flagValues = new HashMap<String, Set<FlagValue>>();
		flagValues.putAll(getParrent().get().getFlags());
		for(Entry<String, Set<FlagValue>> entry : this.flagValues.entrySet()) flagValues.replace(entry.getKey(), entry.getValue());
		return flagValues;
	}

	@Override
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

	@Override
	public Set<FlagValue> getFlagValues(Flags flagName) {
		return getFlagValues(flagName.toString());
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<FlagValue> getFlagValues(String flagName) {
		return containsFlag(flagName) ? (Set) flagValues.get(flagName) : parrent != null ? parrent.getFlagValues(flagName) : new HashSet<>();
	}

	@Override
	public boolean containsFlag(Flags flagName) {
		return containsFlag(flagName.toString());
	}

	@Override
	public boolean containsFlag(String flagName) {
		return flagValues.keySet().parallelStream().filter(flag -> (flag.contains(flagName))).findFirst().isPresent();
	}

	@Override
	public Tristate getFlagResult(Flags flag, String source, String target) {
		return getFlagResult(flag.toString(), source, target);
	}

	@Override
	public Tristate getFlagResult(String flag, String source, String target) {
		Tristate result = getFlagResultWhithoutParrents(flag, source, target);
		return result != Tristate.UNDEFINED ?  result : (parrent != null ? parrent.getFlagResult(flag, source, target) : Tristate.UNDEFINED);
	}

	@Override
	public Tristate getFlagResultWhithoutParrents(Flags flag, String source, String target) {
		return getFlagResult(flag.toString(), source, target);
	}

	@Override
	public Tristate getFlagResultWhithoutParrents(String flag, String source, String target) {
		return tempFlags.containsKey(flag) ?
				tempFlags.get(flag).stream().filter(
					value -> value.equalsTo(source, target)
				).findFirst().map(
					value -> value.asTristate()
				).orElse(
					flagValues.containsKey(flag) ?
						flagValues.get(flag).stream().filter(value -> value.equalsTo(source, target)).findFirst().map(value -> value.asTristate()).orElse(Tristate.UNDEFINED) :
						Tristate.UNDEFINED) :
		flagValues.containsKey(flag) ?
			flagValues.get(flag).stream().filter(value -> value.equalsTo(source, target)).findFirst().map(value -> value.asTristate()).orElse(Tristate.UNDEFINED) :
			Tristate.UNDEFINED;
	}

	@Override
	public Region setFlag(Flags flagName, boolean value) {
		setFlag(flagName.toString(), value);
		return this;
	}

	@Override
	public Region setFlag(String flagName, boolean value) {
		setFlag(flagName.toString(), value, null, null);
		return this;
	}

	@Override
	public Region setFlag(Flags flagName, boolean value, String source, String target) {
		setFlag(flagName.toString(), value, source, target);
		return this;
	}

	@Override
	public Region setTempFlag(Flags flagName, boolean value, String source, String target) {
		return setTempFlag(flagName.toString(), value, source, target);
	}

	@Override
	public Region setTempFlag(String flagName, boolean value, String source, String target) {
		FlagValue flagValue = FlagValue.of(value, source, target);
		removeFlag(flagName, flagValue);
		if(!tempFlags.containsKey(flagName)) tempFlags.put(flagName, new HashSet<>());
		tempFlags.get(flagName).add(flagValue);
		return this;
	}

	@Override
	public Region setFlag(String flagName, boolean value, String source, String target) {
		removeFlag(flagName, source, target);
		if(!flagValues.containsKey(flagName)) flagValues.put(flagName, new HashSet<>());
		flagValues.get(flagName).add(FlagValue.of(value, source, target));
		return this;
	}

	@Override
	public RegionImpl setFlags(Map<String, Set<FlagValue>> flags) {
		flagValues = flags;
		return this;
	}

	@Override
	public Region removeFlag(Flags flagName) {
		removeFlag(flagName.toString());
		return this;
	}

	@Override
	public Region removeFlag(String flagName) {
		if(flagValues.containsKey(flagName)) flagValues.remove(flagName);
		return this;
	}

	@Override
	public Region removeFlag(Flags flagName, String source, String target) {
		removeFlag(flagName.toString(), source, target);
		return this;
	}

	@Override
	public Region removeFlag(String flagName, String source, String target) {
		if(flagValues.containsKey(flagName)) {
			flagValues.get(flagName).removeIf(value -> value.getSource().equals(source == null ? "all" : source) && value.getTarget().equals(target == null ? "all" : target));
			if(flagValues.get(flagName).isEmpty()) flagValues.remove(flagName);
		}
		return this;
	}

	@Override
	public Region removeTempFlag(Flags flagName, String source, String target) {
		return removeTempFlag(flagName.name(), source, target);
	}

	@Override
	public Region removeTempFlag(String flagName, String source, String target) {
		if(tempFlags.containsKey(flagName)) {
			tempFlags.get(flagName).removeIf(value -> value.getSource().equals(source == null ? "all" : source) && value.getTarget().equals(target == null ? "all" : target));
			if(tempFlags.get(flagName).isEmpty()) tempFlags.remove(flagName);
		}
		return this;
	}

	@Override
	public Region removeFlag(Flags flagName, FlagValue flagValue) {
		removeFlag(flagName.toString(), flagValue);
		return this;
	}

	@Override
	public Region removeFlag(String flagName, FlagValue flagValue) {
		return removeFlag(flagName, flagValue.getSource(), flagValue.getTarget());
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	@Override
	public Optional<Component> getJoinMessage(Locale locale) {
		return joinMessages.isEmpty() ? Optional.empty() : joinMessages.containsKey(locale.toLanguageTag()) ? Optional.of(joinMessages.get(locale.toLanguageTag())) : joinMessages.containsKey(Locales.DEFAULT.toLanguageTag()) ? Optional.of(joinMessages.get(Locales.DEFAULT.toLanguageTag())) : joinMessages.values().stream().findFirst();
	}

	@Override
	public Region setJoinMessage(Component message, Locale locale) {
		if(joinMessages.containsKey(locale.toLanguageTag())) joinMessages.remove(locale.toLanguageTag());
		if(message != null) joinMessages.put(locale.toLanguageTag(), message);
		return this;
	}

	@Override
	public Map<String, Component> getJoinMessages() {
		return joinMessages;
	}

	@Override
	public Optional<Component> getExitMessage(Locale locale) {
		return exitMessages.isEmpty() ? Optional.empty() : exitMessages.containsKey(locale.toLanguageTag()) ? Optional.of(exitMessages.get(locale.toLanguageTag())) : exitMessages.containsKey(Locales.DEFAULT.toLanguageTag()) ? Optional.of(exitMessages.get(Locales.DEFAULT.toLanguageTag())) : exitMessages.values().stream().findFirst();
	}

	@Override
	public Region setExitMessage(Component message, Locale locale) {
		if(exitMessages.containsKey(locale.toLanguageTag())) exitMessages.remove(locale.toLanguageTag());
		if(message != null) exitMessages.put(locale.toLanguageTag(), message);
		return this;
	}

	@Override
	public Map<String, Component> getExitMessages() {
		return exitMessages;
	}

	@Override
	public <T extends AdditionalData> Optional<T> getAdditionalData(PluginContainer container, String dataName, Class<T> clazz) {
		if(additionalDataMap.containsKey(container.metadata().id()) && additionalDataMap.get(container.metadata().id()).containsKey(dataName)) {
			BasicConfigurationNode node = BasicConfigurationNode.root(options -> options.options().serializers(serializers -> serializers.registerAll(SerializeOptions.selectSerializersCollection(2))));
			try {
				node.set(JsonObject.class, additionalDataMap.get(container.metadata().id()).get(dataName));
				return Optional.ofNullable(node.get(clazz));
			} catch (SerializationException e) {
				e.printStackTrace();
			}
		}
		return Optional.empty();
	}

	@Override
	public <T extends AdditionalData> Region setAdditionalData(PluginContainer container, String dataName, T additionalData) {
		if(additionalData == null) return this;
		if(this.additionalDataMap == null) this.additionalDataMap = new HashMap<>();
		if(!additionalDataMap.containsKey(container.metadata().id())) additionalDataMap.put(container.metadata().id(), new HashMap<>());
		if(additionalData.toJsonObject() == null) {
			StringWriter sink = new StringWriter();
			GsonConfigurationLoader loader = createWriter(sink);
			BasicConfigurationNode tempNode = BasicConfigurationNode.root(options -> options.options().serializers(serializers -> serializers.registerAll(SerializeOptions.SERIALIZER_COLLECTION_VARIANT_2)));
			try {
				tempNode.set(additionalData.getClass(), additionalData);
				loader.save(tempNode);
				if(sink.toString() != null) additionalDataMap.get(container.metadata().id()).put(dataName, JsonParser.parseString(sink.toString()).getAsJsonObject());
			} catch (ConfigurateException e) {
				e.printStackTrace();
			}
			sink = null;
			loader = null;
			tempNode = null;
		} else additionalDataMap.get(container.metadata().id()).put(dataName, additionalData.toJsonObject());
		return this;
	}

	@Override
	public Region removeAdditionalData(PluginContainer container, String dataName) {
		if(this.additionalDataMap.containsKey(container.metadata().id()) && this.additionalDataMap.get(container.metadata().id()).containsKey(dataName)) this.additionalDataMap.get(container.metadata().id()).remove(dataName);
		return this;
	}

	@Override
	public Map<String, Map<String, JsonObject>> getAllAdditionalData() {
		return additionalDataMap == null ? null : new HashMap<>(additionalDataMap);
	}

	@Override
	public boolean isIntersectsWith(ServerWorld serverWorld, Vector3i vector3i) {
		return isIntersectsWith(serverWorld.key(), vector3i);
	}

	@Override
	public boolean isIntersectsWith(ResourceKey worldkey, Vector3i vector3i) {
		return world.equals(worldkey.asString()) && getCuboid().containsIntersectsPosition(vector3i);
	}

	@Override
	public boolean isIntersectsWith(Vector3i vector3i) {
		return getCuboid().containsIntersectsPosition(vector3i);
	}

	@Override
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

	@Override
	public List<BlockState> getBlocks() {
		if(!getWorld().isPresent() || !getWorld().get().isLoaded()) return new ArrayList<>();
		return getWorld().get().blockStateStream(cuboid.getMin().toInt(), cuboid.getMax().toInt(), StreamOptions.builder().setLoadingStyle(LoadingStyle.NONE).build()).toStream().map(VolumeElement::type).collect(Collectors.toList());
	}

	@Override
	public List<BlockEntity> getBlockEntities() {
		if(!getWorld().isPresent() || !getWorld().get().isLoaded()) return new ArrayList<>();
		return getWorld().get().blockEntityStream(cuboid.getMin().toInt(), cuboid.getMax().toInt(), StreamOptions.builder().setLoadingStyle(LoadingStyle.NONE).build()).toStream().map(VolumeElement::type).collect(Collectors.toList());
	}

	@Override
	public List<Entity> getEntities() {
		if(!getWorld().isPresent() || !getWorld().get().isLoaded()) return new ArrayList<>();
		return getWorld().get().entities(cuboid.getAABB()).stream().filter(entity -> (!(entity instanceof ServerPlayer))).collect(Collectors.toList());
	}

	@Override
	public List<ServerPlayer> getPlayers() {
		if(!getWorld().isPresent() || !getWorld().get().isLoaded()) return new ArrayList<>();
		return getWorld().get().players().parallelStream().filter(player -> cuboid.containsIntersectsPosition(player.position().toInt())).collect(Collectors.toList());
	}

	@Override
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

	@Override
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

	@Override
	public boolean putSchematic(Schematic schematic, int heigt) {
		if(getWorld().isPresent()) {
			schematic.applyToWorld(getWorld().get(), Vector3i.from(cuboid.getAABB().center().floorX(), heigt, cuboid.getAABB().center().floorY()), SpawnTypes.PLUGIN);
			return true;
		}
		return false;
	}

	@Override
	public boolean putSchematic(Schematic schematic, Vector3i vector3i) {
		if(getWorld().isPresent()) {
			schematic.applyToWorld(getWorld().get(), vector3i, SpawnTypes.PLUGIN);
			return true;
		}
		return false;
	}

	@Override
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

	@Override
	public boolean equalsOwners(Region region) {
		return Objects.equals(getOwnerUUID(), region.getOwnerUUID());
	}

	@Override
	public Region copy() {
		RegionImpl region = new RegionImpl();
		region.childs = childs;
		region.creationTime = creationTime;
		region.cuboid = cuboid;
		region.additionalDataMap = additionalDataMap;
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

	private GsonConfigurationLoader createWriter(StringWriter sink) {
		return GsonConfigurationLoader.builder().defaultOptions(SerializeOptions.selectOptions(2)).sink(() -> new BufferedWriter(sink)).build();
	}

}
