package sawfowl.regionguard.api.events;

import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.server.ServerWorld;

import sawfowl.regionguard.api.data.Region;

public interface RegionUseItemStackEvent extends RegionMessageEvent, Cancellable {

	/**
	 * Get the {@link Entity} that is the source of the event.
	 * 
	 * @return entity
	 */
	public Entity getEntity();

	/**
	 * Get the {@link ServerPlayer} that is the source of the event.
	 * 
	 * @return player
	 */
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Get the {@link ItemStack} that is the target of the event.
	 * 
	 * @return item
	 */
	public ItemStack getItemStack();

	/**
	 * Get event {@link ServerWorld}
	 */
	public Region getRegion();

	/**
	 * Get protect result.
	 */
	public boolean isAllow();

	/**
	 * Get event {@link UseItemStackEvent.Start}
	 */
	public UseItemStackEvent.Start spongeEvent();

}
