package sawfowl.regionguard.implementsapi.worldedit;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.commandpack.api.mixin.network.CustomPacket;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.worldedit.CUIUser;
import sawfowl.regionguard.implementsapi.worldedit.cui.events.CUIEvent;

public class CUIUserImpl implements CUIUser {

	static final String CUI_PLUGIN_CHANNEL = "worldedit:cui";
	private Region claimResizing;
	private Vector3i lastWandLocation;
	private UUID visualClaimId;
	private UUID playerUUID;
	private long lastTimeSendBorders;
	private boolean cuiSupport = false;
	private boolean isDrag = false;
	private int failedCuiAttempts = 0;
	private Cuboid dragCuboid;
	public CUIUserImpl(ServerPlayer player) {
		playerUUID = player.uniqueId();
	}

	public CUIUserImpl(UUID uuid) {
		playerUUID = uuid;
	}

	public void dispatchCUIEvent(CUIEvent event) {
		if(!isSupportCUI()) return;
		getPlayer().ifPresent(player -> {
			if(event.getParameters().length > 0) CustomPacket.of(CUI_PLUGIN_CHANNEL, event.getTypeId() + "|" + StringUtil.joinString(event.getParameters(), "|")).sendTo(player);
		});
	}

	public Optional<ServerPlayer> getPlayer() {
		return Sponge.server().player(playerUUID);
	}

	public long getLastTimeSendBorders() {
		return lastTimeSendBorders;
	}

	public void setLastTimeSendBorders(long lastTimeSendBorders) {
		this.lastTimeSendBorders = lastTimeSendBorders;
	}

	public boolean isDrag() {
		return isDrag;
	}

	public void setDrag(boolean isDrag) {
		this.isDrag = isDrag;
	}

	public Cuboid getDragCuboid() {
		return dragCuboid;
	}

	public void setDragCuboid(Cuboid dragCuboid) {
		this.dragCuboid = dragCuboid;
	}

	public Region getClaimResizing() {
		return claimResizing;
	}

	public void setClaimResizing(Region claimResizing) {
		this.claimResizing = claimResizing;
	}

	public Vector3i getLastWandLocation() {
		return lastWandLocation;
	}

	public void setLastWandLocation(Vector3i lastWandLocation) {
		this.lastWandLocation = lastWandLocation;
	}

	public UUID getVisualClaimId() {
		return visualClaimId;
	}

	public void setVisualClaimId(UUID visualClaimId) {
		this.visualClaimId = visualClaimId;
	}

	public boolean isSupportCUI() {
		return cuiSupport;
	}

	public void setSupportCUI(boolean cuiSupport) {
		this.cuiSupport = cuiSupport;
	}

	public void handleCUIInitializationMessage(String text) {
		if (this.failedCuiAttempts > 3) {
			failedCuiAttempts = 0; // Test
			return;
		}
		String[] split = text.split("\\|", 2);
		if (split.length > 1 && split[0].equalsIgnoreCase("v")) { // enough fields and right message
			if (split[1].length() > 4) {
				this.failedCuiAttempts++;
				return;
			}

			try {
				Integer.parseInt(split[1]);
			} catch (NumberFormatException e) {
				RegionGuard.getInstance().getLogger().warn("Error while reading CUI init message: " + e.getMessage());
				this.failedCuiAttempts++;
				return;
			}
			setSupportCUI(true);
		}
	}

}
