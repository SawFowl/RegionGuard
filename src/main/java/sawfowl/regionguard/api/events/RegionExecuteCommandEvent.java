package sawfowl.regionguard.api.events;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.command.ExecuteCommandEvent;

import sawfowl.regionguard.api.data.Region;

public interface RegionExecuteCommandEvent extends Event, RegionMessageEvent, Cancellable {

	/**
	 * Get the {@link Region} where the event occurred.
	 */
	public Region getRegion();

	/**
	 * Getting a {@link ServerPlayer}.
	 */
	public ServerPlayer getPlayer();

	/**
	 * Get protect result.
	 */
	public boolean isAllow();

	/**
	 * Checks if the player is in a fight with another player.
	 */
	public boolean isPvP();

	/**
	 * Getting a {@link ExecuteCommandEvent.Pre}.
	 */
	public ExecuteCommandEvent.Pre spongeEvent();

}
