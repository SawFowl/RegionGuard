package sawfowl.regionguard.api.events.world;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.block.ChangeBlockEvent.All;

public interface RegionChangeBlockEvent extends RegionWorldEvent {

	interface LiquidFlow extends RegionChangeBlockEvent {

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

	interface Break extends RegionChangeBlockEvent, RegionMessageEvent {

		/**
		 * Get the entity that is the source of the event.
		 * 
		 * @return entity
		 */
		public Entity getEntity();

		/**
		 * Getting the player if he is the cause of the event.
		 */
		@SuppressWarnings("unchecked")
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

	interface Place extends RegionChangeBlockEvent, RegionMessageEvent {

		/**
		 * Get the entity that is the source of the event.
		 * 
		 * @return entity
		 */
		public Entity getEntity();

		/**
		 * Getting the {@link ServerPlayer} if he is the cause of the event.
		 */
		@SuppressWarnings("unchecked")
		public Optional<ServerPlayer> getPlayer();

		/**
		 * Getting protect result.
		 */
		public boolean isAllowPlace();

		/**
		 * Getting a list of the {@link Transaction}s for this event. If a
		 * transaction is requested to be marked as "invalid",
		 * {@link Transaction#setValid(boolean)} can be used.
		 *
		 * @return The unmodifiable list of transactions
		 */
		public List<BlockTransaction> getTransactions();

		/**
		 * Getting default {@link Transaction} for this event. If a
		 * transaction is requested to be marked as "invalid",
		 * {@link Transaction#setValid(boolean)} can be used.
		 *
		 * @return transaction
		 */
		public BlockTransaction getDefaultTransaction();

		/**
		 * Getting the default replacement snapshot.
		 *
		 * @return The default replacement
		 */
		public BlockSnapshot afterTransaction();

		/**
		 * Getting the original snapshot.
		 * 
		 * @return The original snapshot
		 */
		public BlockSnapshot beforeTransaction();

	}

	@SuppressWarnings("unchecked")
	All getSpongeEvent();

	/**
	 * Get plugin {@link Cause}.
	 */
	public Cause cause();

}
