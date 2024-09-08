package sawfowl.regionguard.api.events.world;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.command.ExecuteCommandEvent;

import sawfowl.regionguard.api.data.Region;

public interface RegionExecuteCommandEvent extends RegionWorldEvent, RegionMessageEvent {

	/**
	 * Get the {@link Region} where the event occurred.
	 */
	public Region getRegion();

	/**
	 * Getting a {@link ServerPlayer}.
	 */
	@SuppressWarnings("unchecked")
	public ServerPlayer getPlayer();

	/**
	 * Get protect result.
	 */
	public boolean isAllow();

	/**
	 * Checks if the player is in a fight with another player.
	 */
	public boolean isPvP();

	@SuppressWarnings("unchecked")
	public ExecuteCommandEvent.Pre getSpongeEvent();

}
