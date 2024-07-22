package sawfowl.regionguard.api.events.world;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.block.ChangeBlockEvent.All;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.api.data.Region;

public interface RegionExplosionEvent extends RegionWorldEvent {

	interface EntityDamage extends RegionExplosionEvent {

		@SuppressWarnings("unchecked")
		ExplosionEvent.Pre getSpongeEvent();

	}

	interface SurfaceMod extends RegionExplosionEvent {

		Region getRegion();

		ServerWorld getWorld();

		Collection<Vector3i> getBlockPositionsAffected();

		Map<Region, Map<Vector3i, BlockState>> getRegionsAffected();

		Map<Region, Map<Vector3i, Boolean>> getAllowBlockDestruction();

		void removeBlock(Vector3i vector3i);

		void removeBlocks(Collection<Vector3i> vectors3i);

		@Nullable
		Entity getDirectSourceEntity();

		@Nullable
		Living getIndirectSourceEntity();

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
