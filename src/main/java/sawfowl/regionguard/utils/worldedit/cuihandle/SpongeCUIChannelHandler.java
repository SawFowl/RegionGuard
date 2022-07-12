package sawfowl.regionguard.utils.worldedit.cuihandle;

import java.nio.charset.StandardCharsets;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterChannelEvent;
import org.spongepowered.api.network.ServerPlayerConnection;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.api.network.channel.raw.play.RawPlayDataHandler;

import sawfowl.regionguard.Permissions;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.utils.worldedit.cuihandle.utils.SimpleLifecycled;
import sawfowl.regionguard.utils.worldedit.cuiusers.CUIUser;

public class SpongeCUIChannelHandler implements RawPlayDataHandler<ServerPlayerConnection>{

    public static final ResourceKey CUI_PLUGIN_CHANNEL = ResourceKey.of("worldedit", "cui");
    private static final SimpleLifecycled<RawDataChannel> CHANNEL = SimpleLifecycled.invalid();
    static RegionGuard plugin;

    public static final class RegistrationHandler {
    	
    	public RegistrationHandler(RegionGuard regionGuard) {
    		plugin = regionGuard;
    	}
    	
        @Listener
        public void onChannelRegistration(RegisterChannelEvent event) {
            RawDataChannel channel = event.register(CUI_PLUGIN_CHANNEL, RawDataChannel.class);
            channel.play().addHandler(ServerPlayerConnection.class, new SpongeCUIChannelHandler());
            CHANNEL.newValue(channel);
        }
    }

    public static RawDataChannel channel() {
        return CHANNEL.valueOrThrow();
    }

    @Override
    public void handlePayload(ChannelBuf data, ServerPlayerConnection connection) {
        ServerPlayer player = connection.player();
        if(!player.hasPermission(Permissions.CUI_COMMAND)) return;
        String packetData = new String(data.readBytes(data.available()), StandardCharsets.UTF_8);
        CUIUser user = plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(player);
        user.handleCUIInitializationMessage(packetData);
    }

}
