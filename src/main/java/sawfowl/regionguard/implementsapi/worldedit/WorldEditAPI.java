package sawfowl.regionguard.implementsapi.worldedit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.blockray.RayTrace;
import org.spongepowered.api.util.blockray.RayTraceResult;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3i;

import com.google.common.collect.Maps;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.worldedit.CUIUser;
import sawfowl.regionguard.api.worldedit.WorldEditCUIAPI;
import sawfowl.regionguard.configure.CuiConfigPaths;
import sawfowl.regionguard.implementsapi.worldedit.cui.events.MultiSelectionClearEvent;
import sawfowl.regionguard.implementsapi.worldedit.cui.events.MultiSelectionColorEvent;
import sawfowl.regionguard.implementsapi.worldedit.cui.events.MultiSelectionCuboidEvent;
import sawfowl.regionguard.implementsapi.worldedit.cui.events.MultiSelectionGridEvent;
import sawfowl.regionguard.implementsapi.worldedit.cui.events.MultiSelectionPointEvent;

public class WorldEditAPI extends Thread implements WorldEditCUIAPI {

	private final RegionGuard plugin;
	private Map<UUID, CUIUser> worldEditPlayers = Maps.newHashMap();
	private Map<String, String[]> cuiColors = new HashMap<String, String[]>();
	private Map<String, Integer> cuiSpaces = new HashMap<String, Integer>();
	private String sendMethodName;
	public WorldEditAPI(RegionGuard plugin) {
		this.plugin = plugin;
		Sponge.asyncScheduler().submit(Task.builder().interval(10, TimeUnit.SECONDS).plugin(plugin.getPluginContainer()).execute(() -> {
			for(CUIUser user : worldEditPlayers.values()) if(!user.isDrag() && user.getPlayer().isPresent() && System.currentTimeMillis() - user.getLastTimeSendBorders() > 30000) revertVisuals(user.getPlayer().get(), null);
		}).build());
	}

	public void updateCuiDataMaps() {
		Map<String, Map<String, String>> colors = plugin.getCuiConfig().getColors();
		if(colors.isEmpty()) cuiColors.put(CuiConfigPaths.DEFAULT, new String[]{MultiSelectionColors.RED, MultiSelectionColors.RED, MultiSelectionColors.RED, MultiSelectionColors.RED});
		for(Entry<String, Map<String, String>> entry : colors.entrySet()) {
			Map<String, String> value = entry.getValue();
			cuiColors.put(entry.getKey(), new String[]{
					value.containsKey(CuiConfigPaths.EDGE) ? value.get(CuiConfigPaths.EDGE) : MultiSelectionColors.RED,
					value.containsKey(CuiConfigPaths.GRID) ? value.get(CuiConfigPaths.GRID) : MultiSelectionColors.RED,
					value.containsKey(CuiConfigPaths.FIRST_POSITION) ? value.get(CuiConfigPaths.FIRST_POSITION) : MultiSelectionColors.RED,
					value.containsKey(CuiConfigPaths.SECOND_POSITION) ? value.get(CuiConfigPaths.SECOND_POSITION) : MultiSelectionColors.RED,
					});
		}
		cuiSpaces = plugin.getCuiConfig().getSpaces();
		if(cuiSpaces.isEmpty()) cuiSpaces.put(CuiConfigPaths.DEFAULT, 5);
	}

	@Override
	public CUIUser getOrCreateUser(ServerPlayer player) {
		if (worldEditPlayers.containsKey(player.uniqueId())) return worldEditPlayers.get(player.uniqueId());
		final CUIUser user = new CUIUserImpl(player);
		worldEditPlayers.put(player.uniqueId(), user);
		return getOrCreateUser(player.uniqueId());
	}

	@Override
	public CUIUser getOrCreateUser(UUID uuid) {
		if (worldEditPlayers.containsKey(uuid)) return worldEditPlayers.get(uuid);
		final CUIUser user = new CUIUserImpl(uuid);
		worldEditPlayers.put(uuid, user);
		return user;
	}

	@Override
	public void removeUser(ServerPlayer player) {
		if(worldEditPlayers.containsKey(player.uniqueId())) worldEditPlayers.remove(player.uniqueId());
	}

	@Override
	public void visualizeRegion(Region region, ServerPlayer player, boolean investigating, boolean tempRegion) {
		CUIUser user = getOrCreateUser(player);
		user.setLastTimeSendBorders(System.currentTimeMillis());
		visualizeRegion(region, region.getCuboid().getMin(), region.getCuboid().getMax(), player, user, investigating, tempRegion);
	}

	@Override
	public void visualizeRegion(Region region, Vector3i pos1, Vector3i pos2, ServerPlayer player, CUIUser user, boolean investigating, boolean tempRegion) {
		// revert any current visuals if investigating
		if(investigating) revertVisuals(player, null);
		Cuboid cuboid = Cuboid.of(pos1, pos2);
		user.dispatchCUIEvent(new MultiSelectionCuboidEvent(region.getUniqueId()));
		user.dispatchCUIEvent(new MultiSelectionPointEvent(0, pos1, cuboid.getSize3D()));
		if (user.getClaimResizing() != null) {
			user.dispatchCUIEvent(new MultiSelectionPointEvent(1));
		} else user.dispatchCUIEvent(new MultiSelectionPointEvent(1, pos2, cuboid.getSize3D()));
		if(investigating || user.getLastWandLocation() == null) user.dispatchCUIEvent(new MultiSelectionColorEvent(MultiSelectionColors.getColors(cuiColors, tempRegion ? CuiConfigPaths.TEMP : region.getType().toString())));
		user.dispatchCUIEvent(new MultiSelectionGridEvent(getSpaces(region.getType().toString())));
	}

	@Override
	public void visualizeRegions(List<Region> regions, ServerPlayer player, boolean investigating) {
		CUIUser user = getOrCreateUser(player);
		for (Region region : regions) {
			long size = region.getCuboid().getSize3D();
			user.dispatchCUIEvent(new MultiSelectionCuboidEvent(region.getUniqueId()));
			user.dispatchCUIEvent(new MultiSelectionPointEvent(0, region.getCuboid().getMin(), size));
			if (user.getClaimResizing() != null) {
				user.dispatchCUIEvent(new MultiSelectionPointEvent(1));
			} else user.dispatchCUIEvent(new MultiSelectionPointEvent(1, region.getCuboid().getMax(), size));
			if(investigating) user.dispatchCUIEvent(new MultiSelectionColorEvent(MultiSelectionColors.getColors(cuiColors, region.getType().toString())));
			user.dispatchCUIEvent(new MultiSelectionGridEvent(getSpaces(region.getType().toString())));
		}
	}

	@Override
	public void revertVisuals(ServerPlayer player, UUID regionUniqueId) {
		CUIUser user = getOrCreateUser(player);
		if (regionUniqueId != null) {
			user.dispatchCUIEvent(new MultiSelectionClearEvent(regionUniqueId));
		} else user.dispatchCUIEvent(new MultiSelectionClearEvent());
	}

	@Override
	public void stopVisualDrag(ServerPlayer player) {
		CUIUser user = getOrCreateUser(player);
		user.setLastWandLocation(null);
		user.setDrag(false);
		user.dispatchCUIEvent(new MultiSelectionClearEvent(player.uniqueId()));
	}

	@Override
	public void sendVisualDrag(ServerPlayer player, Vector3i pos) {
		CUIUser user = getOrCreateUser(player);
		final ServerLocation location = getTargetBlock(player, 50).orElse(null);
		Vector3i point1 = null;
		if (user.getLastWandLocation() != null) {
			point1 = user.getLastWandLocation();
		} else point1 = pos;
		Vector3i point2 = null;
		if (location == null) {
			point2 = player.blockPosition();
		} else point2 = location.blockPosition();
		//user.setLastTimeSendBorders(System.currentTimeMillis());
		//Cuboid cuboid = new Cuboid(point1, point2);
		user.dispatchCUIEvent(new MultiSelectionCuboidEvent(player.uniqueId()));
		user.dispatchCUIEvent(new MultiSelectionPointEvent(0, point1, (point2.x() - point1.x() + 1L) * (point2.y() - point1.y() + 1L) * (point2.z() - point1.z() + 1L)));
		user.dispatchCUIEvent(new MultiSelectionPointEvent(1, point2));
		user.dispatchCUIEvent(new MultiSelectionColorEvent(MultiSelectionColors.getColors(cuiColors, CuiConfigPaths.DRAG)));
		user.dispatchCUIEvent(new MultiSelectionGridEvent(getSpaces(CuiConfigPaths.DRAG)));
	}

	@Override
	public Optional<ServerLocation> getTargetBlock(ServerPlayer player, int maxDistance) {
		Optional<RayTraceResult<LocatableBlock>> blockRay = Optional.empty();
		blockRay = RayTrace.block()
				.world(player.world())
				.sourceEyePosition(player)
				.limit(maxDistance)
				.direction(player)
				.select(RayTrace.nonAir())
				.execute();
		if(blockRay.isPresent()) return Optional.ofNullable(blockRay.get().selectedObject().serverLocation());
		return Optional.empty();
	}

	private int getSpaces(String type) {
		if(cuiSpaces.containsKey(type)) return cuiSpaces.get(type);
		return 5;
	}

	String getSendMethodName() {
		return sendMethodName;
	}

	void setSendMethodName(String sendMethodName) {
		this.sendMethodName = sendMethodName;
	}

}
