package sawfowl.regionguard.api.events.world;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public interface RegionDamageEntityEvent extends RegionWorldEvent, RegionMessageEvent {

	/**
	 * Getting the {@link ServerPlayer} if he is the cause of the event.
	 */
	@SuppressWarnings("unchecked")
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Get protect result.
	 */
	public boolean isAllowDamage();

}
