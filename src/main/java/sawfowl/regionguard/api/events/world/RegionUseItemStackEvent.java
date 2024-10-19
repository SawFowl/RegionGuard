package sawfowl.regionguard.api.events.world;

import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public interface RegionUseItemStackEvent extends RegionWorldEvent, RegionMessageEvent {

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
	@SuppressWarnings("unchecked")
	public Optional<ServerPlayer> getPlayer();

	/**
	 * Get the {@link ItemStack} that is the target of the event.
	 * 
	 * @return item
	 */
	public ItemStack getItemStack();

	/**
	 * Get protect result.
	 */
	public boolean isAllow();

	@SuppressWarnings("unchecked")
	public UseItemStackEvent.Start getSpongeEvent();

}
