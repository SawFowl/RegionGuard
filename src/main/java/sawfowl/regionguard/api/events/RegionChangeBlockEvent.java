package sawfowl.regionguard.api.events;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.server.ServerWorld;

import sawfowl.regionguard.api.data.Region;

public interface RegionChangeBlockEvent extends Event {

	interface Explode extends RegionChangeBlockEvent, Cancellable {

		interface Surface extends Explode {

			/**
	         * Gets a list of the {@link Transaction}s for this event. If a
	         * transaction is requested to be marked as "invalid",
	         * {@link Transaction#setValid(boolean)} can be used.
	         *
	         * @return The unmodifiable list of transactions
	         */
			public List<BlockTransaction> getTransactions();

			/**
	         * Get default {@link Transaction} for this event. If a
	         * transaction is requested to be marked as "invalid",
	         * {@link Transaction#setValid(boolean)} can be used.
	         *
	         * @return transaction
	         */
			public BlockTransaction getDefaultTransaction();

			/**
		     * Gets the default replacement snapshot.
		     *
		     * @return The default replacement
		     */
			public BlockSnapshot afterTransaction();

			/**
			 * Gets the original snapshot.
			 * 
			 * @return The original snapshot
			 */
			public BlockSnapshot beforeTransaction();
		}

		/**
	     * Gets the {@link Explosion}.
	     *
	     * @return The explosion
	     */
		public Explosion getExplosion();

		/**
	     * Set the {@link Explosion}.
		 */
		public void setExplosion(Explosion explosion);

		/**
	     * Gets the {@link Explosive}.
	     *
	     * @return The explosive
	     */
		public Optional<Explosive> getExplosive();

		/**
		 * Get protect result.
		 */
		public boolean isAllowExplosion();

	}

	interface LiquidFlow extends RegionChangeBlockEvent, Cancellable {

		/**
		 * Get protect result.
		 */
		public boolean isAllowFlow();

		/**
		 * Get liquid flow level.
		 */
		public int getFlowLevel();
		/**
         * Gets a list of the {@link Transaction}s for this event. If a
         * transaction is requested to be marked as "invalid",
         * {@link Transaction#setValid(boolean)} can be used.
         *
         * @return The unmodifiable list of transactions
         */
		public List<BlockTransaction> getTransactions();

		/**
         * Get default {@link Transaction} for this event. If a
         * transaction is requested to be marked as "invalid",
         * {@link Transaction#setValid(boolean)} can be used.
         *
         * @return transaction
         */
		public BlockTransaction getDefaultTransaction();

		/**
	     * Gets the default replacement snapshot.
	     *
	     * @return The default replacement
	     */
		public BlockSnapshot afterTransaction();

		/**
		 * Gets the original snapshot.
		 * 
		 * @return The original snapshot
		 */
		public BlockSnapshot beforeTransaction();

	}

	interface Break extends RegionChangeBlockEvent, RegionMessageEvent, Cancellable {

		/**
		 * Get the entity that is the source of the event.
		 * 
		 * @return entity
		 */
		public Entity getEntity();

		/**
		 * Getting the player if he is the cause of the event.
		 */
		public Optional<ServerPlayer> getPlayer();

		/**
		 * Get protect result.
		 */
		public boolean isAllowBreak();
		/**
         * Gets a list of the {@link Transaction}s for this event. If a
         * transaction is requested to be marked as "invalid",
         * {@link Transaction#setValid(boolean)} can be used.
         *
         * @return The unmodifiable list of transactions
         */
		public List<BlockTransaction> getTransactions();

		/**
         * Get default {@link Transaction} for this event. If a
         * transaction is requested to be marked as "invalid",
         * {@link Transaction#setValid(boolean)} can be used.
         *
         * @return transaction
         */
		public BlockTransaction getDefaultTransaction();

		/**
	     * Gets the default replacement snapshot.
	     *
	     * @return The default replacement
	     */
		public BlockSnapshot afterTransaction();

		/**
		 * Gets the original snapshot.
		 * 
		 * @return The original snapshot
		 */
		public BlockSnapshot beforeTransaction();

	}

	interface Place extends RegionChangeBlockEvent, RegionMessageEvent, Cancellable {

		/**
		 * Get the entity that is the source of the event.
		 * 
		 * @return entity
		 */
		public Entity getEntity();

		/**
		 * Getting the {@link ServerPlayer} if he is the cause of the event.
		 */
		public Optional<ServerPlayer> getPlayer();

		/**
		 * Get protect result.
		 */
		public boolean isAllowPlace();
		/**
         * Gets a list of the {@link Transaction}s for this event. If a
         * transaction is requested to be marked as "invalid",
         * {@link Transaction#setValid(boolean)} can be used.
         *
         * @return The unmodifiable list of transactions
         */
		public List<BlockTransaction> getTransactions();

		/**
         * Get default {@link Transaction} for this event. If a
         * transaction is requested to be marked as "invalid",
         * {@link Transaction#setValid(boolean)} can be used.
         *
         * @return transaction
         */
		public BlockTransaction getDefaultTransaction();

		/**
	     * Gets the default replacement snapshot.
	     *
	     * @return The default replacement
	     */
		public BlockSnapshot afterTransaction();

		/**
		 * Gets the original snapshot.
		 * 
		 * @return The original snapshot
		 */
		public BlockSnapshot beforeTransaction();

	}

	/**
	 * Get plugin {@link Cause}.
	 */
	public Cause cause();

	/**
	 * Get sponge event {@link Cause}.
	 */
	public Cause spongeCause();

	/**
	 * Get event source.
	 */
	public Object source();

	/**
	 * Get event {@link ServerWorld}
	 */
	public ServerWorld getWorld();

	/**
     * Get the {@link Region} where the event occurred.
     */
	public Region getRegion();

}
