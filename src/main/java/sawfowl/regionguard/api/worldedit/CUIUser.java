package sawfowl.regionguard.api.worldedit;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.implementsapi.worldedit.cui.events.CUIEvent;

public interface CUIUser {

	/**
	 * Activate the event of displaying the selection boundaries.
	 */
	void dispatchCUIEvent(CUIEvent event);

	/**
	 * Getting player
	 */
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Getting the time when the player was shown the boundaries
	 */
	public long getLastTimeSendBorders();

	/**
	 * Setting the last time of the boundaries display
	 */
	public void setLastTimeSendBorders(long lastTimeSendBorders);

	/**
	 * Checking whether the player is using dynamic selection.
	 */
	public boolean isDrag();

	/**
	 * Setting the status of the dynamic selection.
	 */
	public void setDrag(boolean isDrag);

	/**
	 * Getting a cuboid of dynamic selection.
	 */
	public Cuboid getDragCuboid();

	/**
	 * Setting cuboid for dynamic selection.
	 */
	public void setDragCuboid(Cuboid dragCuboid);

	/**
	 * Getting a region that is resized. It can be zero.
	 * 
	 * @return {@link Region} or null
	 */
	public Region getClaimResizing();

	/**
	 * Specifies the region that will be resized.
	 */
	public void setClaimResizing(Region claimResizing);

	/**
	 * Getting the last position that was selected by the wand tool.
	 * 
	 * @return {@link Vector3i}
	 */
	public Vector3i getLastWandLocation();

	/**
	 * Getting the last position that was selected by the wand tool.
	 * 
	 * @param lastWandLocation - {@link Vector3i}
	 */
	public void setLastWandLocation(Vector3i lastWandLocation);

	/**
	 * Getting the UUID of the region whose borders were shown to the player.
	 * 
	 * @return - {@link UUID}
	 */
	public UUID getVisualClaimId();

	/**
	 * Setting the UUID of the region, the boundaries of which will be shown to the player.
	 * 
	 * @param visualClaimId - {@link UUID}
	 */
	public void setVisualClaimId(UUID visualClaimId);

	/**
	 * Checking if the player will be sent packets to display the boundaries by the WECui mod.
	 * 
	 */
	public boolean isSupportCUI();

	/**
	 * If sets this to false, the player will not receive packets for display the boundaries by the WECui mod.
	 * If a player has the right to see boundaries, when the player logs into the server, plugin will attempt to verify that the player has the WECui mod.
	 */
	public void setSupportCUI(boolean cuiSupport);

	/**
	 * You don't have to use that.
	 */
	public void handleCUIInitializationMessage(String text);

}
