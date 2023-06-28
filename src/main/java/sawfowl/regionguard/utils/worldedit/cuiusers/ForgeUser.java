package sawfowl.regionguard.utils.worldedit.cuiusers;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import io.netty.buffer.Unpooled;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLConnectionData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import sawfowl.regionguard.utils.worldedit.StringUtil;
import sawfowl.regionguard.utils.worldedit.cuievents.CUIEvent;

public class ForgeUser extends CUIUser {

	public ForgeUser(ServerPlayer player) {
		super(player);
		List<String> mods = getModList(player);
		if(mods != null) setSupportCUI(getModList(player).contains("worldeditcuife3"));
	}

	public ForgeUser(UUID uuid) {
		super(uuid);
		Sponge.server().player(uuid).ifPresent(player -> {
			List<String> mods = getModList(player);
			if(mods != null) setSupportCUI(getModList(player).contains("worldeditcuife3"));
		});
	}

	@Override
	public void dispatchCUIEvent(CUIEvent event) {
		if(!getPlayer().isPresent()) return;
		String[] params = event.getParameters();
		String send = event.getTypeId();
		if(params.length > 0) send = send + "|" + StringUtil.joinString(params, "|");
		ResourceLocation loc = new ResourceLocation(CUI_PLUGIN_CHANNEL);
		ServerPlayerEntity player = (ServerPlayerEntity) getPlayer().get();
		PacketBuffer buffer = new PacketBuffer(Unpooled.copiedBuffer(send.getBytes(UTF_8_CHARSET)));
		SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(loc, buffer);
		PacketDistributor.PLAYER.with(new Supplier<ServerPlayerEntity>() {
			@Override
			public ServerPlayerEntity get() {
				return player;
			}
		}).send(packet);
	}

	private List<String> getModList(ServerPlayer player) {
		FMLConnectionData data = NetworkHooks.getConnectionData(((ServerPlayerEntity) player).connection.connection);
		return data == null ? null : NetworkHooks.getConnectionData(((ServerPlayerEntity) player).connection.connection).getModList();
	}

}
