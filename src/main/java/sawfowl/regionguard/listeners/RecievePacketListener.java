package sawfowl.regionguard.listeners;

import org.spongepowered.api.event.Listener;

import sawfowl.commandpack.api.events.RecievePacketEvent;
import sawfowl.regionguard.RegionGuard;

public class RecievePacketListener {

	private final RegionGuard plugin;
	private final String cuiPacketId = "worldedit:cui";
	public RecievePacketListener(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Listener
	public void onRecievePacket(RecievePacketEvent event) {
		if(!event.isReadable() || !event.getPacketName().equals(cuiPacketId)) return;
		plugin.getAPI().getWorldEditCUIAPI().getOrCreateUser(event.getMixinPlayer()).handleCUIInitializationMessage(event.getDataAsString());
	}

}
