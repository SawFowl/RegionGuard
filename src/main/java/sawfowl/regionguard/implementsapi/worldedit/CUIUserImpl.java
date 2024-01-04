package sawfowl.regionguard.implementsapi.worldedit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.math.vector.Vector3i;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
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
	private WorldEditAPI api;
	public CUIUserImpl(ServerPlayer player, WorldEditAPI api) {
		playerUUID = player.uniqueId();
		this.api = api;
	}

	public CUIUserImpl(UUID uuid, WorldEditAPI api) {
		playerUUID = uuid;
		this.api = api;
	}

	public void dispatchCUIEvent(CUIEvent event) {
		if(!getPlayer().isPresent()) return;
		getPlayer().get().connection();
		String[] params = event.getParameters();
		String send = event.getTypeId();
		if(params.length > 0) send = send + "|" + StringUtil.joinString(params, "|");
		ResourceLocation loc = new ResourceLocation(CUI_PLUGIN_CHANNEL);
		net.minecraft.server.level.ServerPlayer player = (net.minecraft.server.level.ServerPlayer) getPlayer().get();
		FriendlyByteBuf fbuf = new FriendlyByteBuf(Unpooled.copiedBuffer(send.getBytes(UTF_8_CHARSET)));
		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(loc, fbuf);
		// Forge obfuscates the fields and methods of a class. I had to use reflection.
		ServerPlayerConnection connection = getConnection(player);
		try {
			getSendMethod(connection).invoke(connection, packet);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		connection = null;
		//player.connection.send(packet);
	}

	private ServerPlayerConnection getConnection(net.minecraft.server.level.ServerPlayer player) {
		return getPlayer().get().connection() instanceof ServerPlayerConnection ? (ServerPlayerConnection) getPlayer().get().connection() : sawfowl.regionguard.utils.ReflectionUtil.findField(player, ServerGamePacketListenerImpl.class).orElse(null);
	}

	private Method getSendMethod(ServerPlayerConnection connection) throws NoSuchMethodException, SecurityException, IllegalArgumentException {
		if(api.getSendMethodName() == null) {
			for(Method method : connection.getClass().getMethods())
				if(method.getGenericParameterTypes().length == 1 && method.getReturnType().toString().equals("void") && method.getGenericParameterTypes()[0].toString().equals("net.minecraft.network.protocol.Packet<?>")) {
					api.setSendMethodName(method.getName());
					return method;
				}
		}
		return connection.getClass().getMethod(api.getSendMethodName(), Packet.class);
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
