package sawfowl.regionguard.utils.worldedit.cuiusers;

import java.util.UUID;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

import sawfowl.regionguard.utils.worldedit.StringUtil;
import sawfowl.regionguard.utils.worldedit.cuievents.CUIEvent;

public class VanillaUser extends CUIUser {

	public VanillaUser(ServerPlayer player) {
		super(player);
	}

	public VanillaUser(UUID uuid) {
		super(uuid);
	}

	@Override
	public void dispatchCUIEvent(CUIEvent event) {
		if(!getPlayer().isPresent()) return;
		String[] params = event.getParameters();
		String send = event.getTypeId();
		if(params.length > 0) send = send + "|" + StringUtil.joinString(params, "|");
		ResourceLocation loc = new ResourceLocation(CUI_PLUGIN_CHANNEL);
		net.minecraft.server.level.ServerPlayer player = (net.minecraft.server.level.ServerPlayer) getPlayer().get();
		FriendlyByteBuf fbuf = new FriendlyByteBuf(Unpooled.copiedBuffer(send.getBytes(UTF_8_CHARSET)));
		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(loc, fbuf);
		player.connection.send(packet);
	}

}
