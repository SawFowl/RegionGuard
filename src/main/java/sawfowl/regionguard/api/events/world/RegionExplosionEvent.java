package sawfowl.regionguard.api.events.world;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.event.block.ChangeBlockEvent.All;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.world.explosion.Explosion;

public interface RegionExplosionEvent extends RegionWorldEvent {

	interface EntityDamage extends RegionExplosionEvent {

		@SuppressWarnings("unchecked")
		ExplosionEvent.Pre getSpongeEvent();

	}

	interface Surface extends RegionExplosionEvent {


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

		@SuppressWarnings("unchecked")
		All getSpongeEvent();

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
