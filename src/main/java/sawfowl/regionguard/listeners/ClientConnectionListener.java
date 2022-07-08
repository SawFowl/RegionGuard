package sawfowl.regionguard.listeners;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;

public class ClientConnectionListener {

	private final RegionGuard plugin;
	public ClientConnectionListener(RegionGuard plugin) {
		this.plugin = plugin;
	}

	@Listener
	public void onJoin(ServerSideConnectionEvent.Join event) {
		if(plugin.getAPI().getPlayerData(event.player().uniqueId()).isPresent()) return;
		ServerPlayer player = event.player();
		plugin.getAPI().setPlayerData(player.uniqueId(), new PlayerData(new PlayerLimits(plugin.getAPI().getLimitBlocks(player), plugin.getAPI().getLimitClaims(player), plugin.getAPI().getLimitSubdivisions(player), plugin.getAPI().getLimitMembers(player)), new ClaimedByPlayer(plugin.getAPI().getClaimedBlocks(player), plugin.getAPI().getClaimedRegions(player))));
	}

}
