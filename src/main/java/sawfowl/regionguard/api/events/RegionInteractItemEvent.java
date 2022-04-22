package sawfowl.regionguard.api.events;

import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import sawfowl.regionguard.api.data.Region;

public interface RegionInteractItemEvent extends RegionMessageEvent, Cancellable {

	/**
	 * Get the entity that is the target of the event.
	 * 
	 * @return entity
	 */
	public Entity getSource();

	/**
	 * Get the {@link ItemStack} that is the target of the event.
	 */
	public ItemStack getItemStack();

	/**
     * Get the {@link Region} where the event occurred.
     */
	public Region getRegion();

	/**
	 * Get protect result.
	 */
	public boolean isAllow();

	/**
	 * Get the {@link ServerPlayer} that is the source of the event.
	 */
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Get the {@link InteractItemEvent.Secondary}.
	 */
	public InteractItemEvent.Secondary spongeEvent();

}
