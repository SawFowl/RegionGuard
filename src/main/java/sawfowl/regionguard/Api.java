package sawfowl.regionguard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.NumberUtils;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.localeapi.serializetools.TypeTokens;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.RegionAPI;
import sawfowl.regionguard.api.RegionTypes;
import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.api.WorldEditCUIAPI;
import sawfowl.regionguard.api.data.ChunkNumber;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.utils.worldedit.WorldEditAPI;

class Api implements RegionAPI {

	private final RegionGuard plugin;
	private final WorldEditCUIAPI cuiapi;
	Api(RegionGuard plugin) {
		this.plugin = plugin;
		cuiapi = new WorldEditAPI(plugin);
		flags = Stream.of(Flags.values()).map(Flags::toString).collect(Collectors.toList());
	}

	private List<String> flags = new ArrayList<String>();
	private Region defaultGlobal;
	private Map<UUID, Region> regionsByUUID = new HashMap<UUID, Region>();
	private Map<UUID, Region> tempRegions = new HashMap<UUID, Region>();
	private Map<UUID, List<Region>> playersRegions = new HashMap<UUID, List<Region>>();
	private Map<ResourceKey, Region> globalRegionsPerWorlds = new HashMap<ResourceKey, Region>();
	private Map<ResourceKey, Map<ChunkNumber, ArrayList<Region>>> regionsPerWorld = new HashMap<ResourceKey, Map<ChunkNumber, ArrayList<Region>>>();
	private Map<UUID, SelectorTypes> selectorsPerPlayer = new HashMap<UUID, SelectorTypes>();
	private Map<UUID, RegionTypes> selectedRegionTypes = new HashMap<UUID, RegionTypes>();
	private ItemStack wandItem;

	void generateGlobalRegions() {
		Map<ResourceKey, Region> toUpdate = new HashMap<ResourceKey, Region>();
		defaultGlobal = new Region(new UUID(0, 0), Sponge.server().worldManager().defaultWorld(), null, null, null);
		Sponge.server().worldManager().worlds().forEach(world -> {
			Region region = plugin.getConfigs().getWorldRegion(world);
			region.setRegionType(RegionTypes.GLOBAL);
			toUpdate.put(world.key(), region);
		});
		globalRegionsPerWorlds.clear();
		globalRegionsPerWorlds.putAll(toUpdate);
	}

	@Override
	public List<String> getFlags() {
		return flags;
	}

	@Override
	public void updateGlobalRegionData(ServerWorld serverWorld, Region region) {
		if(globalRegionsPerWorlds.containsKey(serverWorld.key())) {
			globalRegionsPerWorlds.remove(serverWorld.key());
		}
		globalRegionsPerWorlds.put(serverWorld.key(), region);
	}

	@Override
	public void addTempRegion(Region region) {
		removeTempRegion(region.getOwnerUUID());
		tempRegions.put(region.getOwnerUUID(), region);
	}

	@Override
	public void removeTempRegion(Region region) {
		if(tempRegions.containsKey(region.getOwnerUUID())) tempRegions.remove(region.getOwnerUUID());
	}

	@Override
	public void removeTempRegion(UUID uuid) {
		if(tempRegions.containsKey(uuid)) tempRegions.remove(uuid);
	}

	@Override
	public Optional<Region> getTempRegion(UUID uuid) {
		return tempRegions.containsKey(uuid) ? Optional.ofNullable(tempRegions.get(uuid)) : Optional.empty();
	}

	@Override
	public SelectorTypes getSelectorType(UUID uuid) {
		return selectorsPerPlayer.getOrDefault(selectorsPerPlayer.get(uuid), plugin.getConfigs().getDefaultSelectorType());
	}

	@Override
	public void setSelectorType(ServerPlayer player, SelectorTypes selectorType) {
		setSelectorType(player.uniqueId(), selectorType);
	}

	@Override
	public void setSelectorType(UUID uuid, SelectorTypes selectorType) {
		if(selectorsPerPlayer.containsKey(uuid)) selectorsPerPlayer.remove(uuid);
		selectorsPerPlayer.put(uuid, selectorType);
	}

	@Override
	public RegionTypes getSelectRegionType(ServerPlayer player) {
		return getSelectRegionType(player.uniqueId());
	}

	@Override
	public RegionTypes getSelectRegionType(UUID uuid) {
		return selectedRegionTypes.containsKey(uuid) ? selectedRegionTypes.get(uuid) : RegionTypes.CLAIM;
	}

	@Override
	public void setCreatingRegionType(ServerPlayer player, RegionTypes regionType) {
		setCreatingRegionType(player.uniqueId(), regionType);
	}

	@Override
	public void setCreatingRegionType(UUID uuid, RegionTypes regionType) {
		if(regionType == RegionTypes.SUBDIVISION || regionType == RegionTypes.GLOBAL) return;
		if(selectedRegionTypes.containsKey(uuid)) selectedRegionTypes.remove(uuid);
		if(regionType == RegionTypes.UNSET) regionType = RegionTypes.CLAIM;
		selectedRegionTypes.put(uuid, regionType);
	}

	@Override
	public Region getGlobalRegion(ServerWorld serverWorld) {
		return getGlobalRegion(serverWorld.key());
	}

	@Override
	public Region getGlobalRegion(ResourceKey worldkey) {
		return globalRegionsPerWorlds.getOrDefault(worldkey, defaultGlobal);
	}

	@Override
	public Collection<Region> getRegions() {
		return regionsByUUID.values();
	}

	@Override
	public Map<ResourceKey, Map<ChunkNumber, ArrayList<Region>>> getRegionsPerWorld() {
		return regionsPerWorld;
	}

	@Override
	public void registerRegion(Region region) {
		ResourceKey worldKey = region.getServerWorldKey();
		if(!regionsPerWorld.containsKey(worldKey)) regionsPerWorld.put(worldKey, new HashMap<ChunkNumber, ArrayList<Region>>());
		for(ChunkNumber chunkNumber : region.getChunkNumbers()) {
			if(!regionsPerWorld.get(worldKey).containsKey(chunkNumber)) {
				ArrayList<Region> regions = new ArrayList<Region>();
				regions.add(region);
				regionsPerWorld.get(worldKey).put(chunkNumber, regions);
			} else {
				regionsPerWorld.get(worldKey).get(chunkNumber).add(region);
			}
		}
		if(!regionsByUUID.containsKey(region.getUniqueId())) {
			regionsByUUID.put(region.getUniqueId(), region);
		}
		if(playersRegions.containsKey(region.getOwnerUUID())) {
			playersRegions.get(region.getOwnerUUID()).add(region);
		} else {
			List<Region> playerRegions = new ArrayList<Region>();
			playerRegions.add(region);
			playersRegions.put(region.getOwnerUUID(), playerRegions);
		}
		removeTempRegion(region);
	}

	@Override
	public void registerRegionAsync(Region region) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			registerRegion(region);
		});
	}

	@Override
	public void saveRegion(Region region) {
		plugin.getConfigs().saveRegion(region);
	}

	@Override
	public void deleteRegion(Region region) {
		plugin.getConfigs().deleteRegion(region);
		ResourceKey worldKey = region.getServerWorldKey();
		for(ChunkNumber chunkNumber : region.getChunkNumbers()) if(regionsPerWorld.get(worldKey).containsKey(chunkNumber) && regionsPerWorld.get(worldKey).get(chunkNumber).contains(region)) regionsPerWorld.get(worldKey).get(chunkNumber).remove(region);
		if(playersRegions.containsKey(region.getOwnerUUID()) && playersRegions.get(region.getOwnerUUID()).contains(region)) playersRegions.get(region.getOwnerUUID()).remove(region);
		if(regionsByUUID.containsKey(region.getUniqueId())) regionsByUUID.remove(region.getUniqueId());
	}

	@Override
	public Region findRegion(ServerWorld world, Vector3i position) {
		return findRegion(world.key(), position);
	}

	@Override
	public Region findRegion(ResourceKey worldkey, Vector3i position) {
		ChunkNumber chunkNumber = new ChunkNumber(position);
		if(!regionsPerWorld.containsKey(worldkey) || !regionsPerWorld.get(worldkey).containsKey(chunkNumber)) return getGlobalRegion(worldkey);
		Stream<Region> stream = regionsPerWorld.get(worldkey).get(chunkNumber).size() > 10 ? regionsPerWorld.get(worldkey).get(chunkNumber).parallelStream() : regionsPerWorld.get(worldkey).get(chunkNumber).stream();
		Region region = getGlobalRegion(worldkey);
		Iterator<Region> iterator = stream.filter(rg -> (rg.isIntersectsWith(worldkey, position))).iterator();
		if(iterator.hasNext()) {
			region = iterator.next();
		}
		//Region region = stream.filter(rg -> (rg.isIntersectsWith(worldkey, position))).findAny().orElse(getGlobalRegion(worldkey));
		if(region.containsChilds()) {
			Optional<Region> child = region.getAllChilds().parallelStream().filter(rg -> (rg.isIntersectsWith(worldkey, position))).findFirst();
			if(child.isPresent()) return child.get();
		}
		return region;
	}

	@Override
	public List<Region> getPlayerRegions(ServerPlayer player) {
		return getPlayerRegions(player.uniqueId());
	}

	@Override
	public List<Region> getPlayerRegions(UUID playerUUID) {
		return playersRegions.containsKey(playerUUID) ? playersRegions.get(playerUUID) : new ArrayList<>();
	}

	@Override
	public ItemStack getWandItem() {
		if(wandItem == null) updateWandItem();
		return wandItem.copy();
	}

	@Override
	public int getMinimalRegionSize(SelectorTypes selectorType) {
		return plugin.getConfigs().getMinimalRegionSize(selectorType);
	}

	@Override
	public long getClaimedBlocks(ServerPlayer player) {
		int blocks = 0;
		if(getClaimedRegions(player) > 0) for(Region region : playersRegions.get(player.uniqueId())) blocks += region.getCuboid().getSize();
		return blocks;
	}

	@Override
	public long getClaimedRegions(ServerPlayer player) {
		return playersRegions.containsKey(player.uniqueId()) ? playersRegions.get(player.uniqueId()).size() : 0;
	}

	@Override
	public long getLimitBlocks(ServerPlayer player) {
		return player.user().subjectData().allOptions().values().stream().findFirst().isPresent() && player.user().subjectData().allOptions().values().stream().findFirst().get().containsKey(Permissions.LIMIT_BLOCKS) && NumberUtils.isCreatable(player.user().subjectData().allOptions().values().stream().findFirst().get().get(Permissions.LIMIT_BLOCKS)) ? Integer.valueOf(player.user().subjectData().allOptions().values().stream().findFirst().get().get(Permissions.LIMIT_BLOCKS)) : 0;
	}

	@Override
	public long getLimitClaims(ServerPlayer player) {
		return player.user().subjectData().allOptions().values().stream().findFirst().isPresent() && player.user().subjectData().allOptions().values().stream().findFirst().get().containsKey(Permissions.LIMIT_CLAIMS) && NumberUtils.isCreatable(player.user().subjectData().allOptions().values().stream().findFirst().get().get(Permissions.LIMIT_CLAIMS)) ? Integer.valueOf(player.user().subjectData().allOptions().values().stream().findFirst().get().get(Permissions.LIMIT_CLAIMS)) : 0;
	}

	@Override
	public long getLimitSubdivisions(ServerPlayer player) {
		return player.user().subjectData().allOptions().values().stream().findFirst().isPresent() && player.user().subjectData().allOptions().values().stream().findFirst().get().containsKey(Permissions.LIMIT_SUBDIVISIONS) && NumberUtils.isCreatable(player.user().subjectData().allOptions().values().stream().findFirst().get().get(Permissions.LIMIT_SUBDIVISIONS)) ? Integer.valueOf(player.user().subjectData().allOptions().values().stream().findFirst().get().get(Permissions.LIMIT_SUBDIVISIONS)) : 0;
	}

	void updateWandItem() {
		wandItem = setNBT(getWandItemFromConfig());
	}

	private ItemStack setNBT(ItemStack itemStack) {
		String nbt = "{\"WandItem\":1}";
		try {
			itemStack = ItemStack.builder().fromContainer(itemStack.toContainer().set(DataQuery.of("UnsafeData"), DataFormats.JSON.get().read(nbt))).build();
		} catch (InvalidDataException | IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		return itemStack.copy();
	}

	private ItemStack getWandItemFromConfig() {
		try {
			return setNBT(plugin.getRootNode().node("Items", "Wand").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack());
		} catch (SerializationException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
			return ItemStack.of(ItemTypes.STONE_AXE);
		}
	}

	@Override
	public WorldEditCUIAPI getWorldEditCUIAPI() {
		return cuiapi;
	}

}
