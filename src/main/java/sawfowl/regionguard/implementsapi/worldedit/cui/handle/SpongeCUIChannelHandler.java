package sawfowl.regionguard.implementsapi.worldedit.cui.handle;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.RegisterChannelEvent;
import org.spongepowered.api.network.ServerConnectionState.Game;
import org.spongepowered.api.network.channel.Channel;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.api.network.channel.raw.play.RawPlayDataHandler;

import sawfowl.commandpack.api.events.RecievePacketEvent;
import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.worldedit.CUIUser;
import sawfowl.regionguard.implementsapi.worldedit.cui.handle.utils.SimpleLifecycled;

public class SpongeCUIChannelHandler implements RawPlayDataHandler<Game>{

	public static final ResourceKey CUI_PLUGIN_CHANNEL = ResourceKey.of("worldedit", "cui");
	private static final SimpleLifecycled<RawDataChannel> CHANNEL = SimpleLifecycled.invalid();
	static RegionGuard plugin;

	public static final class RegistrationHandler {
		
		public RegistrationHandler(RegionGuard regionGuard) {
			plugin = regionGuard;
		}
		
		@Listener(order = Order.LAST)
		public void onChannelRegistration(RegisterChannelEvent event) {
			Optional<Channel> cuiChanel = Sponge.channelManager().get(CUI_PLUGIN_CHANNEL);
			RawDataChannel channel = cuiChanel.isPresent() && cuiChanel.get() instanceof RawDataChannel ? (RawDataChannel) cuiChanel.get() : event.register(CUI_PLUGIN_CHANNEL, RawDataChannel.class);
			channel.play().addHandler(Game.class, new SpongeCUIChannelHandler());
			CHANNEL.newValue(channel);
		}

	}

	public static class ForgeCuiListener {

		public ForgeCuiListener(RegionGuard regionGuard) {
			plugin = regionGuard;
		}

		@Listener
		public void onRecievePacket(RecievePacketEvent event) {
			if(!event.isReadable() || !event.getPacketName().equals(CUI_PLUGIN_CHANNEL.asString())) return;
			event.getMixinPlayer().ifPresent(player -> plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player).handleCUIInitializationMessage(event.getDataAsString()));
		}

	}

	public static RawDataChannel channel() {
		return CHANNEL.valueOrThrow();
	}

	@Override
	public void handlePayload(ChannelBuf data, Game connection) {
		ServerPlayer player = connection.player();
		if(!player.hasPermission(Permissions.CUI_COMMAND)) return;
		String packetData = new String(data.readBytes(data.available()), StandardCharsets.UTF_8);
		CUIUser user = plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player);
		user.handleCUIInitializationMessage(packetData);
	}

}
