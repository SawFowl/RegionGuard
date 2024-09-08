package sawfowl.regionguard.api.events.world;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.entity.CollideEntityEvent;

import sawfowl.regionguard.api.data.Region;

public interface RegionCollideEntityEvent extends RegionWorldEvent {

	/**
	 * Get the {@link Region} where the event occurred.
	 *
	 * @return The world encompassing these block changes
	 */
	public Region getRegion();

	/**
	 * Get the sponge {@link CollideEntityEvent}
	 */
	@SuppressWarnings("unchecked")
	public CollideEntityEvent getSpongeEvent();

	/**
	 * Get protect result.
	 */
	public boolean isAllow();

	/**
	 * Get the entity that is the source of the event.
	 * 
	 * @return entity
	 */
	public Entity getSource();

	/**
	 * Get the entity that is the target of the event.
	 * 
	 * @return entity
	 */
	public Entity getTarget();

}
