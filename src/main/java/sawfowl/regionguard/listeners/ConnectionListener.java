package sawfowl.regionguard.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import sawfowl.regionguard.RegionGuard;

public class ConnectionListener {

	private final RegionGuard plugin;
	public ConnectionListener(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Listener
	public void onConnect(ServerSideConnectionEvent.Join event) {
		plugin.getAPI().getWorldEditCUIAPI().stopVisualDrag(event.player());
		plugin.getAPI().getWorldEditCUIAPI().revertVisuals(event.player(), null);
	}

	@Listener
	public void onDisconnect(ServerSideConnectionEvent.Disconnect event) {
		plugin.getAPI().getWorldEditCUIAPI().removeUser(event.player());
	}

}
