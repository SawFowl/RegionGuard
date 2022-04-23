package sawfowl.regionguard.utils.worldedit;

import java.util.List;
import java.util.Map;
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
import sawfowl.regionguard.api.WorldEditCUIAPI;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.utils.worldedit.cuievents.MultiSelectionClearEvent;
import sawfowl.regionguard.utils.worldedit.cuievents.MultiSelectionColorEvent;
import sawfowl.regionguard.utils.worldedit.cuievents.MultiSelectionCuboidEvent;
import sawfowl.regionguard.utils.worldedit.cuievents.MultiSelectionGridEvent;
import sawfowl.regionguard.utils.worldedit.cuievents.MultiSelectionPointEvent;
import sawfowl.regionguard.utils.worldedit.cuiusers.CUIUser;
import sawfowl.regionguard.utils.worldedit.cuiusers.ForgeUser;
import sawfowl.regionguard.utils.worldedit.cuiusers.VanillaUser;

public class WorldEditAPI extends Thread implements WorldEditCUIAPI {

	private boolean isForgePlatform;
	public WorldEditAPI(RegionGuard plugin) {
		isForgePlatform = isForgePlatform();
		if(isForgePlatform) {
			plugin.getLogger().info("WECui support is running in Forge compatibility mode.");
		} else {
			plugin.getLogger().info("WECui support is running in Vanilla compatibility mode.");
		}
		Sponge.asyncScheduler().submit(Task.builder().interval(10, TimeUnit.SECONDS).plugin(plugin.getPluginContainer()).execute(() -> {
			for(CUIUser user : worldEditPlayers.values()) {
				if(!user.isDrag() && user.getPlayer().isPresent() && System.currentTimeMillis() - user.getLastTimeSendBorders() > 30000) {
					revertVisuals(user.getPlayer().get(), null);
				}
			}
		}).build());
	}

	private Map<UUID, CUIUser> worldEditPlayers = Maps.newHashMap();

	@Override
	public CUIUser getOrCreateUser(ServerPlayer player) {
		if (worldEditPlayers.containsKey(player.uniqueId())) return worldEditPlayers.get(player.uniqueId());
		final CUIUser user = isForgePlatform ? new ForgeUser(player) : new VanillaUser(player);
		worldEditPlayers.put(player.uniqueId(), user);
		return getOrCreateUser(player.uniqueId());
	}

	@Override
	public CUIUser getOrCreateUser(UUID uuid) {
		if (worldEditPlayers.containsKey(uuid)) return worldEditPlayers.get(uuid);
		final CUIUser user = isForgePlatform ? new ForgeUser(uuid) : new VanillaUser(uuid);
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
		if(!user.isSupportCUI()) return;
		user.setLastTimeSendBorders(System.currentTimeMillis());
		visualizeRegion(region, region.getCuboid().getMin(), region.getCuboid().getMax(), player, user, investigating, tempRegion);
	}

	@Override
	public void visualizeRegion(Region region, Vector3i pos1, Vector3i pos2, ServerPlayer player, CUIUser user, boolean investigating, boolean tempRegion) {
		if(!user.isSupportCUI()) return;
		// revert any current visuals if investigating
		if (investigating) {
			revertVisuals(player, null);
		}
		Cuboid cuboid = new Cuboid(pos1, pos2);
		user.dispatchCUIEvent(new MultiSelectionCuboidEvent(region.getUniqueId()));
		user.dispatchCUIEvent(new MultiSelectionPointEvent(0, pos1, cuboid.getSize3D()));
		if (user.getClaimResizing() != null) {
			user.dispatchCUIEvent(new MultiSelectionPointEvent(1));
		} else {
			user.dispatchCUIEvent(new MultiSelectionPointEvent(1, pos2, cuboid.getSize3D()));
		}
		if (investigating || user.getLastWandLocation() == null) {
			user.dispatchCUIEvent(new MultiSelectionColorEvent(MultiSelectionColors.RED, tempRegion ? MultiSelectionColors.PURPLE : MultiSelectionColors.getClaimColor(region), MultiSelectionColors.BLUE, MultiSelectionColors.ORANGE));
		}
		user.dispatchCUIEvent(new MultiSelectionGridEvent(3));
	}

	@Override
	public void visualizeRegions(List<Region> regions, ServerPlayer player, boolean investigating) {
		CUIUser user = getOrCreateUser(player);
		if(!user.isSupportCUI()) return;
		for (Region region : regions) {
			/*if (region.getChilds().size() > 0) {
				visualizeClaims(region.getChilds(), player, investigating);
			}*/
			long size = region.getCuboid().getSize3D();
			user.dispatchCUIEvent(new MultiSelectionCuboidEvent(region.getUniqueId()));
			user.dispatchCUIEvent(new MultiSelectionPointEvent(0, region.getCuboid().getMin(), size));
			if (user.getClaimResizing() != null) {
				user.dispatchCUIEvent(new MultiSelectionPointEvent(1));
			} else {
				user.dispatchCUIEvent(new MultiSelectionPointEvent(1, region.getCuboid().getMax(), size));
			}
			if (investigating) {
				user.dispatchCUIEvent(new MultiSelectionColorEvent(MultiSelectionColors.RED, MultiSelectionColors.getClaimColor(region), MultiSelectionColors.BLUE, MultiSelectionColors.ORANGE));
			}
			user.dispatchCUIEvent(new MultiSelectionGridEvent(3));
		}
	}

	@Override
	public void revertVisuals(ServerPlayer player, UUID regionUniqueId) {
		CUIUser user = getOrCreateUser(player);
		if(!user.isSupportCUI()) return;
		if (regionUniqueId != null) {
			user.dispatchCUIEvent(new MultiSelectionClearEvent(regionUniqueId));
		} else {
			user.dispatchCUIEvent(new MultiSelectionClearEvent());
		}
	}

	@Override
	public void stopVisualDrag(ServerPlayer player) {
		final CUIUser user = getOrCreateUser(player);
		if(!user.isSupportCUI()) return;
		user.dispatchCUIEvent(new MultiSelectionClearEvent(player.uniqueId()));
	}

	@Override
	public void sendVisualDrag(ServerPlayer player, Vector3i pos) {
		CUIUser user = getOrCreateUser(player);
		if(!user.isSupportCUI()) return;
		final ServerLocation location = getTargetBlock(player, user, 50).orElse(null);
		Vector3i point1 = null;
		if (user.getLastWandLocation() != null) {
			point1 = user.getLastWandLocation();
		} else {
			point1 = pos;
		}
		Vector3i point2 = null;
		if (location == null) {
			point2 = player.blockPosition();
		} else {
			point2 = location.blockPosition();
		}
		//user.setLastTimeSendBorders(System.currentTimeMillis());
		//Cuboid cuboid = new Cuboid(point1, point2);
		user.dispatchCUIEvent(new MultiSelectionCuboidEvent(player.uniqueId()));
		user.dispatchCUIEvent(new MultiSelectionPointEvent(0, point1, (point2.x() - point1.x() + 1L) * (point2.y() - point1.y() + 1L) * (point2.z() - point1.z() + 1L)));
		user.dispatchCUIEvent(new MultiSelectionPointEvent(1, point2));
		user.dispatchCUIEvent(new MultiSelectionColorEvent(MultiSelectionColors.BLUE, MultiSelectionColors.YELLOW, MultiSelectionColors.GRAY, MultiSelectionColors.ORANGE));
		user.dispatchCUIEvent(new MultiSelectionGridEvent(3));
	}

	@Override
	public Optional<ServerLocation> getTargetBlock(ServerPlayer player, CUIUser user, int maxDistance) {
		Optional<RayTraceResult<LocatableBlock>> blockRay = Optional.empty();
		blockRay = RayTrace.block()
				.world(player.world())
				.sourceEyePosition(player)
				.limit(maxDistance)
				.direction(player)
				.select(RayTrace.nonAir())
				.execute();
		if(blockRay.isPresent()) {
			return Optional.ofNullable(blockRay.get().selectedObject().serverLocation());
		}
		return Optional.empty();
	}

	private boolean isForgePlatform() {
		try {
	        Class.forName("net.minecraft.entity.player.ServerPlayerEntity");
	        Class.forName("net.minecraft.network.play.client.CCustomPayloadPacket");
	        Class.forName("net.minecraft.network.PacketBuffer");
	        return true;
	    }  catch (ClassNotFoundException e) {
	        return false;
	    }
	}

}