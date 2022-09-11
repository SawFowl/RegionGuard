package sawfowl.regionguard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
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
import sawfowl.regionguard.api.data.FlagSettings;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.utils.worldedit.WorldEditAPI;

class Api implements RegionAPI {

	private final RegionGuard plugin;
	private final WorldEditCUIAPI cuiapi;
	private final boolean isForgePlatform;
	Api(RegionGuard plugin, boolean isForgePlatform) {
		this.plugin = plugin;
		this.isForgePlatform = isForgePlatform;
		if(isForgePlatform) {
			plugin.getLogger().info("The plugin is running in Forge compatibility mode.");
		} else {
			plugin.getLogger().info("The plugin is running in Vanilla compatibility mode.");
		}
		cuiapi = new WorldEditAPI(plugin, isForgePlatform);
		for(Flags flag : Flags.values()) {
			registeredFlags.put(flag.toString(), flag.getFlagSettings());
		}
	}

	private SortedMap<String, FlagSettings> registeredFlags = new TreeMap<String, FlagSettings>();
	private Region defaultGlobal;
	private Map<UUID, Region> regionsByUUID = new HashMap<UUID, Region>();
	private Map<UUID, Region> tempRegions = new HashMap<UUID, Region>();
	private Map<UUID, List<Region>> playersRegions = new HashMap<UUID, List<Region>>();
	private Map<ResourceKey, Region> globalRegionsPerWorlds = new HashMap<ResourceKey, Region>();
	private Map<ResourceKey, Map<ChunkNumber, ArrayList<Region>>> regionsPerWorld = new HashMap<ResourceKey, Map<ChunkNumber, ArrayList<Region>>>();
	private Map<UUID, SelectorTypes> selectorsPerPlayer = new HashMap<UUID, SelectorTypes>();
	private Map<UUID, RegionTypes> selectedRegionTypes = new HashMap<UUID, RegionTypes>();
	private Map<UUID, PlayerData> dataPlayers = new HashMap<UUID, PlayerData>();
	private ItemStack wandItem;

	void generateDefaultGlobalRegion() {
		defaultGlobal = new Region(new UUID(0, 0), Sponge.server().worldManager().defaultWorld(), null, null, null);
		defaultGlobal.setRegionType(RegionTypes.GLOBAL);
	}

	@Override
	public SortedMap<String, FlagSettings> getRegisteredFlags() {
		SortedMap<String, FlagSettings> copy = new TreeMap<String, FlagSettings>();
		copy.putAll(registeredFlags);
		return copy;
	}

	@Override
	public void registerFlag(String flagName, FlagSettings settings) {
		if(flagName == null) throw new NullPointerException("The name of the flag is not specified!");
		if(settings == null) throw new NullPointerException("The flag settings are not specified!");
		if(registeredFlags.containsKey(flagName)) throw new RuntimeException("The flag is already registered!");
		registeredFlags.put(flagName, settings);
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
		return selectorsPerPlayer.containsKey(uuid) ? selectorsPerPlayer.get(uuid) : plugin.getConfig().getDefaultSelectorType();
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
			updatePlayerData(region.getOwnerUUID());
		} else {
			List<Region> playerRegions = new ArrayList<Region>();
			playerRegions.add(region);
			playersRegions.put(region.getOwnerUUID(), playerRegions);
			updatePlayerData(region.getOwnerUUID());
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
		plugin.getRegionsDataWork().saveRegion(region);
	}

	@Override
	public void deleteRegion(Region region) {
		plugin.getRegionsDataWork().deleteRegion(region);
		ResourceKey worldKey = region.getServerWorldKey();
		for(ChunkNumber chunkNumber : region.getChunkNumbers()) if(regionsPerWorld.get(worldKey).containsKey(chunkNumber) && regionsPerWorld.get(worldKey).get(chunkNumber).contains(region)) regionsPerWorld.get(worldKey).get(chunkNumber).remove(region);
		if(playersRegions.containsKey(region.getOwnerUUID()) && playersRegions.get(region.getOwnerUUID()).contains(region)) {
			playersRegions.get(region.getOwnerUUID()).remove(region);
			updatePlayerData(region.getOwnerUUID());
		}
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
		return plugin.getConfig().getMinimalRegionSize(selectorType);
	}

	@Override
	public Map<String, List<FlagValue>> getDefaultFlags(RegionTypes regionType) {
		if(regionType == RegionTypes.ADMIN) return plugin.getConfig().getDefaultAdminFlags();
		if(regionType == RegionTypes.ARENA) return plugin.getConfig().getDefaultArenaFlags();
		if(regionType == RegionTypes.GLOBAL) return plugin.getConfig().getDefaultGlobalFlags();
		return plugin.getConfig().getDefaultClaimFlags();
	}

	@Override
	public long getClaimedBlocks(ServerPlayer player) {
		long blocks = dataPlayers.containsKey(player.uniqueId()) && dataPlayers.get(player.uniqueId()).getClaimed() != null && dataPlayers.get(player.uniqueId()).getClaimed().getBlocks() != null ? dataPlayers.get(player.uniqueId()).getClaimed().getBlocks() : 0;
		if(blocks == 0 && getClaimedRegions(player) > 0) for(Region region : playersRegions.get(player.uniqueId())) blocks += region.getCuboid().getSize();
		return blocks;
	}

	@Override
	public long getClaimedRegions(ServerPlayer player) {
		return containsLimits(player.uniqueId()) && dataPlayers.get(player.uniqueId()).getClaimed() != null && dataPlayers.get(player.uniqueId()).getClaimed().getRegions() != null ? dataPlayers.get(player.uniqueId()).getClaimed().getRegions() : playersRegions.containsKey(player.uniqueId()) ? playersRegions.get(player.uniqueId()).size() : 0;
	}

	@Override
	public long getLimitBlocks(ServerPlayer player) {
		return containsLimits(player.uniqueId()) && dataPlayers.get(player.uniqueId()).getLimits().getBlocks() != null ? dataPlayers.get(player.uniqueId()).getLimits().getBlocks() : getOptionLongValue(player, Permissions.LIMIT_BLOCKS);
	}

	@Override
	public long getLimitClaims(ServerPlayer player) {
		return containsLimits(player.uniqueId()) && dataPlayers.get(player.uniqueId()).getLimits().getClaims() != null ? dataPlayers.get(player.uniqueId()).getLimits().getClaims() : getOptionLongValue(player, Permissions.LIMIT_CLAIMS);
	}

	@Override
	public long getLimitSubdivisions(ServerPlayer player) {
		return containsLimits(player.uniqueId()) && dataPlayers.get(player.uniqueId()).getLimits().getSubdivisions() != null ? dataPlayers.get(player.uniqueId()).getLimits().getSubdivisions() : getOptionLongValue(player, Permissions.LIMIT_SUBDIVISIONS);
	}

	@Override
	public long getLimitMembers(ServerPlayer player) {
		return containsLimits(player.uniqueId()) && dataPlayers.get(player.uniqueId()).getLimits().getMembersPerRegion() != null ? dataPlayers.get(player.uniqueId()).getLimits().getMembersPerRegion() : getOptionLongValue(player, Permissions.LIMIT_MEMBERS);
	}

	@Override
	public long getLimitMembers(UUID player) {
		return containsLimits(player) && dataPlayers.get(player).getLimits().getMembersPerRegion() != null ? dataPlayers.get(player).getLimits().getMembersPerRegion() : 0;
	}

	@Override
	public long getLimitMaxBlocks(ServerPlayer player) {
		return getOptionLongValue(player, Permissions.LIMIT_MAX_BLOCKS);
	}

	@Override
	public long getLimitMaxClaims(ServerPlayer player) {
		return getOptionLongValue(player, Permissions.LIMIT_MAX_CLAIMS);
	}

	@Override
	public long getLimitMaxSubdivisions(ServerPlayer player) {
		return getOptionLongValue(player, Permissions.LIMIT_MAX_SUBDIVISIONS);
	}

	@Override
	public long getLimitMaxMembers(ServerPlayer player) {
		return getOptionLongValue(player, Permissions.LIMIT_MAX_MEMBERS);
	}

	@Override
	public void setLimitBlocks(ServerPlayer player, long limit) {
		if(!dataPlayers.containsKey(player.uniqueId())) dataPlayers.put(player.uniqueId(), new PlayerData());
		if(dataPlayers.get(player.uniqueId()).getLimits() == null) dataPlayers.get(player.uniqueId()).setLimits(new PlayerLimits());
		dataPlayers.get(player.uniqueId()).getLimits().setBlocks(limit);
		plugin.getPlayersDataWork().savePlayerData(player, dataPlayers.get(player.uniqueId()));
	}

	@Override
	public void setLimitClaims(ServerPlayer player, long limit) {
		if(!dataPlayers.containsKey(player.uniqueId())) dataPlayers.put(player.uniqueId(), new PlayerData());
		if(dataPlayers.get(player.uniqueId()).getLimits() == null) dataPlayers.get(player.uniqueId()).setLimits(new PlayerLimits());
		dataPlayers.get(player.uniqueId()).getLimits().setClaims(limit);
		plugin.getPlayersDataWork().savePlayerData(player, dataPlayers.get(player.uniqueId()));
	}

	@Override
	public void setLimitSubdivisions(ServerPlayer player, long limit) {
		if(!dataPlayers.containsKey(player.uniqueId())) dataPlayers.put(player.uniqueId(), new PlayerData());
		if(dataPlayers.get(player.uniqueId()).getLimits() == null) dataPlayers.get(player.uniqueId()).setLimits(new PlayerLimits());
		dataPlayers.get(player.uniqueId()).getLimits().setSubdivisions(limit);
		plugin.getPlayersDataWork().savePlayerData(player, dataPlayers.get(player.uniqueId()));
	}

	@Override
	public void setLimitMembers(ServerPlayer player, long limit) {
		if(!dataPlayers.containsKey(player.uniqueId())) dataPlayers.put(player.uniqueId(), new PlayerData());
		if(dataPlayers.get(player.uniqueId()).getLimits() == null) dataPlayers.get(player.uniqueId()).setLimits(new PlayerLimits());
		dataPlayers.get(player.uniqueId()).getLimits().setMembersPerRegion(limit);
		plugin.getPlayersDataWork().savePlayerData(player, dataPlayers.get(player.uniqueId()));
	}

	@Override
	public double getBuyBlockPrice(ServerPlayer player) {
		return getOptionDoubleValue(player, Permissions.BUY_BLOCK_PRICE);
	}

	@Override
	public double getBuyClaimPrice(ServerPlayer player) {
		return getOptionDoubleValue(player, Permissions.BUY_REGION_PRICE);
	}

	@Override
	public double getBuySubdivisionPrice(ServerPlayer player) {
		return getOptionDoubleValue(player, Permissions.BUY_SUBDIVISION_PRICE);
	}

	@Override
	public double getBuyMembersPrice(ServerPlayer player) {
		return getOptionDoubleValue(player, Permissions.BUY_MEMBERS_PRICE);
	}

	@Override
	public double getSellBlockPrice(ServerPlayer player) {
		return getOptionDoubleValue(player, Permissions.SELL_BLOCK_PRICE);
	}

	@Override
	public double getSellClaimPrice(ServerPlayer player) {
		return getOptionDoubleValue(player, Permissions.SELL_REGION_PRICE);
	}

	@Override
	public double getSellSubdivisionPrice(ServerPlayer player) {
		return getOptionDoubleValue(player, Permissions.SELL_SUBDIVISION_PRICE);
	}

	@Override
	public double getSellMembersPrice(ServerPlayer player) {
		return getOptionDoubleValue(player, Permissions.SELL_MEMBERS_PRICE);
	}

	@Override
	public String getCurrency(ServerPlayer player) {
		return optionIsPresent(player, Permissions.TRANSACRION_CURRENCY) ? player.user().subjectData().allOptions().values().stream().findFirst().get().get(Permissions.TRANSACRION_CURRENCY) : null;
	}

	@Override
	public void setPlayerData(ServerPlayer player, PlayerData playerData) {
		setPlayerData(player.uniqueId(), playerData);
	}

	@Override
	public void setPlayerData(UUID player, PlayerData playerData) {
		if(dataPlayers.containsKey(player)) dataPlayers.remove(player);
		dataPlayers.put(player, playerData);
		plugin.getPlayersDataWork().savePlayerData(player, playerData);
	}

	@Override
	public void updatePlayerData(ServerPlayer player) {
		updatePlayerData(player.uniqueId());
	}

	@Override
	public void updatePlayerData(UUID player) {
		if(containsLimits(player)) dataPlayers.get(player).getClaimed().setRegions(playersRegions.containsKey(player) ? (long) playersRegions.get(player).size() : 0);
		long blocks = 0;
		if(playersRegions.containsKey(player) && !playersRegions.get(player).isEmpty()) for(Region region1 : playersRegions.get(player)) blocks += region1.getCuboid().getSize();
		if(playersRegions.containsKey(player) && containsLimits(player)) dataPlayers.get(player).getClaimed().setBlocks(blocks);
		plugin.getPlayersDataWork().savePlayerData(player, dataPlayers.get(player));
	}

	@Override
	public Optional<PlayerData> getPlayerData(ServerPlayer player) {
		return getPlayerData(player.uniqueId());
	}

	@Override
	public Optional<PlayerData> getPlayerData(UUID player) {
		return dataPlayers.containsKey(player) ? Optional.ofNullable(dataPlayers.get(player)) : Optional.empty();
	}

	@Override
	public WorldEditCUIAPI getWorldEditCUIAPI() {
		return cuiapi;
	}

	@Override
	public boolean isForgePlatform() {
		return isForgePlatform;
	}

	void updateWandItem() {
		wandItem = setNBT(getWandItemFromConfig());
	}

	private ItemStack setNBT(ItemStack itemStack) {
		try {
			itemStack = ItemStack.builder().fromContainer(itemStack.toContainer().set(DataQuery.of("UnsafeData"), DataFormats.JSON.get().read("{\"WandItem\":1}"))).build();
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
			return setNBT(ItemStack.of(ItemTypes.STONE_AXE));
		}
	}

	private long getOptionLongValue(ServerPlayer player, String option) {
		return optionIsPresent(player, option) && NumberUtils.isCreatable(player.option(option).get()) ? NumberUtils.createLong(player.option(option).get()) : 0;
	}

	private double getOptionDoubleValue(ServerPlayer player, String option) {
		return optionIsPresent(player, option) && NumberUtils.isCreatable(player.option(option).get()) ? NumberUtils.createDouble(player.option(option).get()) : 0;
	}

	private boolean optionIsPresent(ServerPlayer player, String option) {
		return player.option(option).isPresent();
	}

	private boolean containsLimits(UUID player) {
		return dataPlayers.containsKey(player) && dataPlayers.get(player).getLimits() != null;
	}

}
