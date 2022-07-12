package sawfowl.regionguard.api.events;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.api.data.Region;

public interface RegionChangeEntityWorldEvent extends RegionMessageEvent, Cancellable {

	/**
     * Get the {@link ChangeEntityWorldEvent.Reposition}.
     */
	public ChangeEntityWorldEvent.Reposition spongeEvent();

	/**
	 * Get protect result.
	 */
	public boolean isAllowFrom();

	/**
	 * Get protect result.
	 */
	public boolean isAllowTo();

	/**
     * Get the {@link Region} where the event occurred.
     */
	public Region fromRegion();

	/**
     * Get the {@link Region} where the event occurred.
     */
	public Region toRegion();

	/**
	 * Get the {@link ServerPlayer} that is the target of the event.
	 * 
	 * @return entity
	 */
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Get fly protect result.
	 */
	public boolean isAllowFly();

	/**
	 * Set fly protect result.
	 */
	public void setAllowFly(boolean allow);

	/**
	 * Receiving a message that will be sent to the player if flights are forbidden in the region.
	 */
	public Optional<Component> getStopFlyingMessage();

	/**
	 * Set the message that will be sent to the player if the region is forbidden to fly.
	 */
	public void setStopFlyingMessage(Component component);

}
