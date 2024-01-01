package sawfowl.regionguard.implementsapi.worldedit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.math.vector.Vector3i;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.worldedit.CUIUser;
import sawfowl.regionguard.implementsapi.worldedit.cui.events.CUIEvent;

public class CUIUserImpl implements CUIUser {

	static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");
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

	/**
	 * Activate the event of displaying the selection boundaries.
	 */
	public void dispatchCUIEvent(CUIEvent event) {
		if(!getPlayer().isPresent()) return;
		String[] params = event.getParameters();
		String send = event.getTypeId();
		if(params.length > 0) send = send + "|" + StringUtil.joinString(params, "|");
		ResourceLocation loc = new ResourceLocation(CUI_PLUGIN_CHANNEL);
		net.minecraft.server.level.ServerPlayer player = (net.minecraft.server.level.ServerPlayer) getPlayer().get();
		FriendlyByteBuf fbuf = new FriendlyByteBuf(Unpooled.copiedBuffer(send.getBytes(UTF_8_CHARSET)));
		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(loc, fbuf);
		// Forge obfuscates the fields and methods of a class. I had to use reflection.
		for(Field field : player.getClass().getDeclaredFields()) if(field.getType().equals(ServerGamePacketListenerImpl.class)) try {
			ServerGamePacketListenerImpl connection = ((ServerGamePacketListenerImpl) ReflectionUtil.getFieldValue(field, player));
			for(Method method : connection.getClass().getMethods())
				if(method.getGenericParameterTypes().length == 1 && method.getReturnType().toString().equals("void") && method.getGenericParameterTypes()[0].toString().equals("net.minecraft.network.protocol.Packet<?>")) 
					method.invoke(connection, packet);
			connection = null;
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		//player.connection.send(packet);
	}

	/**
	 * Getting player
	 */
	public Optional<ServerPlayer> getPlayer() {
		return Sponge.server().player(playerUUID);
	}

	/**
	 * Getting the time when the player was shown the boundaries
	 */
	public long getLastTimeSendBorders() {
		return lastTimeSendBorders;
	}

	/**
	 * Setting the last time of the boundaries display
	 */
	public void setLastTimeSendBorders(long lastTimeSendBorders) {
		this.lastTimeSendBorders = lastTimeSendBorders;
	}

	/**
	 * Checking whether the player is using dynamic selection.
	 */
	public boolean isDrag() {
		return isDrag;
	}

	/**
	 * Setting the status of the dynamic selection.
	 */
	public void setDrag(boolean isDrag) {
		this.isDrag = isDrag;
	}

	/**
	 */
	public Cuboid getDragCuboid() {
		return dragCuboid;
	}

	/**
	 */
	public void setDragCuboid(Cuboid dragCuboid) {
		this.dragCuboid = dragCuboid;
	}

	/**
	 * Getting a region that is resized. It can be zero.
	 * 
	 * @return {@link Region} or null
	 */
	public Region getClaimResizing() {
		return claimResizing;
	}

	/**
	 * Specifies the region that will be resized.
	 */
	public void setClaimResizing(Region claimResizing) {
		this.claimResizing = claimResizing;
	}

	/**
	 * Getting the last position that was selected by the wand tool.
	 * 
	 * @return {@link Vector3i}
	 */
	public Vector3i getLastWandLocation() {
		return lastWandLocation;
	}

	/**
	 * Getting the last position that was selected by the wand tool.
	 * 
	 * @param lastWandLocation - {@link Vector3i}
	 */
	public void setLastWandLocation(Vector3i lastWandLocation) {
		this.lastWandLocation = lastWandLocation;
	}

	/**
	 * Getting the UUID of the region whose borders were shown to the player.
	 * 
	 * @return - {@link UUID}
	 */
	public UUID getVisualClaimId() {
		return visualClaimId;
	}

	/**
	 * Setting the UUID of the region, the boundaries of which will be shown to the player.
	 * 
	 * @param visualClaimId - {@link UUID}
	 */
	public void setVisualClaimId(UUID visualClaimId) {
		this.visualClaimId = visualClaimId;
	}

	/**
	 * Checking if the player will be sent packets to display the boundaries by the WECui mod.
	 * 
	 */
	public boolean isSupportCUI() {
		return cuiSupport;
	}

	/**
	 * If sets this to false, the player will not receive packets for display the boundaries by the WECui mod.
	 * If a player has the right to see boundaries, when the player logs into the server, plugin will attempt to verify that the player has the WECui mod.
	 */
	public void setSupportCUI(boolean cuiSupport) {
		this.cuiSupport = cuiSupport;
	}

	/**
	 * You don't have to use that.
	 */
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
