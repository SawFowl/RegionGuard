package sawfowl.regionguard.api.events.world;

import java.util.Optional;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.math.vector.Vector3d;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.data.Region;

public interface RegionMoveEntityEvent extends RegionWorldEvent {

	public interface ChangeRegion extends RegionMoveEntityEvent, RegionMessageEvent {

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
		public Region toRegion();

		/**
		 * Get the {@link ServerPlayer} that is the target of the event.
		 * 
		 * @return entity
		 */
		@SuppressWarnings("unchecked")
		public Optional<ServerPlayer> getPlayer();

	}

	/**
	 * Get the {@link MoveEntityEvent}.
	 */
	@SuppressWarnings("unchecked")
	public MoveEntityEvent getSpongeEvent();

	/**
	 * Get the {@link Entity} that is the target of the event.
	 * 
	 * @return entity
	 */
	public Entity getEntity();

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
	 * Setting the position to which the entity will be moved.
	 */
	public void setDestinationPosition(Vector3d vector3d);

	/**
	 * The distance between the first and second position.
	 */
	public double getDistanceSquared();

	/**
	 * Get the {@link Region} where the event occurred.
	 */
	public Region fromRegion();

	/**
	 * Receiving a message that will be sent to the player if flights are forbidden in the region.
	 */
	public Optional<Component> getStopFlyingMessage();

	/**
	 * Set the message that will be sent to the player if the region is forbidden to fly.
	 */
	public void setStopFlyingMessage(Component component);

	/**
	 * Receiving a message that will be sent to the player if riding are forbidden in the region.
	 */
	public Optional<Component> getStopRidingMessage();

	/**
	 * Set the message that will be sent to the player if riding are forbidden in region.
	 */
	public void setStopRidingMessage(Component component);

	/**
	 * Whether the entity is riding anything.
	 */
	public boolean isRiding();

	/**
	 * Does the entity use a portal.
	 */
	public boolean isPortal();

	/**
	 * Get riding protect result.
	 */
	public boolean isAllowRiding();

	/**
	 * Set riding protect result.
	 */
	public void setAllowRiding(boolean allow);

	/**
	 * Getting an entity with another entity riding on it.
	 */
	public Optional<Entity> getRidingEntity();

	default Region getRegion() {
		return fromRegion();
	}

}
