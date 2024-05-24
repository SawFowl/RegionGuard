package sawfowl.regionguard.listeners.forge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.block.ChangeBlockEvent.All;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.explosion.Explosion.Builder;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.world.RegionExplosionEvent;
import sawfowl.regionguard.utils.ListenerUtils;

public class ForgeExplosionListener extends ForgeListener {

	public ForgeExplosionListener(RegionGuard plugin) {
		super(plugin);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onExplosion(ExplosionEvent.Start event) {
		if(event.isCanceled()) return;
		Explosion explosion = event.getExplosion();
		ResourceKey worldKey = ResourceKey.resolve(event.getLevel().dimension().location().toString());
		Vector3i position = Vector3i.from((int) explosion.center().x, (int) explosion.center().y, (int) explosion.center().z);
		Region region = plugin.getAPI().findRegion(worldKey, position);
		List<String> sources = new ArrayList<String>();
		if(explosion.getDirectSourceEntity() != null) sources.addAll(flagEntityArgs(explosion.getDirectSourceEntity()));
		if(explosion.getIndirectSourceEntity() != null) sources.addAll(flagEntityArgs(explosion.getIndirectSourceEntity()));
		boolean allow = isAllowExplosion(region, sources, region.getWorld().get().block(position));
		List<BlockTransaction> transactions = new ArrayList<BlockTransaction>();
		RegionExplosionEvent.Surface rgEvent = new RegionExplosionEvent.Surface() {

			org.spongepowered.api.world.explosion.Explosion explosion;
			boolean cancellded;
			@Override
			public Cause cause() {
				return Cause.builder().insert(0, explosion).build();
			}

			@Override
			public org.spongepowered.api.world.explosion.Explosion getExplosion() {
				return this.explosion;
			}

			@Override
			public void setExplosion(org.spongepowered.api.world.explosion.Explosion explosion) {
				this.explosion = explosion;
				
			}

			@Override
			public Optional<Explosive> getExplosive() {
				return explosion.sourceExplosive();
			}

			@Override
			public boolean isAllowExplosion() {
				return allow;
			}

			@Override
			public Object source() {
				return explosion;
			}

			@Override
			public ServerWorld getWorld() {
				return region.getWorld().get();
			}

			@Override
			public Region getRegion() {
				return region;
			}

			@Override
			public boolean isCancelled() {
				return cancellded;
			}

			@Override
			public void setCancelled(boolean cancel) {
				this.cancellded = cancel;
			}

			@Override
			public List<BlockTransaction> getTransactions() {
				return transactions;
			}

			@Override
			public BlockTransaction getDefaultTransaction() {
				return transactions.stream().filter(t -> t.original().position().equals(position)).findFirst().orElse(transactions.size() > 0 ? transactions.get(0) : null);
			}

			@Override
			public BlockSnapshot afterTransaction() {
				return getDefaultTransaction().finalReplacement();
			}

			@Override
			public BlockSnapshot beforeTransaction() {
				return getDefaultTransaction().original();
			}

			@SuppressWarnings("unchecked")
			@Override
			public All getSpongeEvent() {
				return null;
			}
			
		};
		rgEvent.setCancelled(!allow);
		List<BlockPos> positions = new ArrayList<BlockPos>(explosion.getToBlow());
		Builder builder = org.spongepowered.api.world.explosion.Explosion.builder().location(region.getWorld().get().location(position)).shouldBreakBlocks(positions.size() > 0).shouldDamageEntities(explosion.getHitPlayers().size() > 0);
		if(explosion.getDirectSourceEntity() != null) {
			Optional<Entity> optSource = EntityTypes.registry().findValue(ResourceKey.resolve(explosion.getDirectSourceEntity().getEncodeId())).map(type -> Sponge.server().worldManager().world(worldKey).get().createEntity(type, position));
			if(optSource.isPresent() && optSource.get() instanceof Explosive) {
				builder = builder.sourceExplosive((@Nullable Explosive) optSource.get());
			}
		}
		rgEvent.setExplosion(builder.build());
		ListenerUtils.postEvent(rgEvent);
		if(rgEvent.isCancelled()) {
			event.setCanceled(true);
			return;
		}
		positions.forEach(pos -> {
			Vector3i blockPos = Vector3i.from(pos.getX(), pos.getY(), pos.getZ());
			Region find = plugin.getAPI().findRegion(worldKey, blockPos);
			if(!isAllowExplosion(find, sources, find.getWorld().get().block(position))) explosion.getToBlow().remove(pos);
		});
	}

	private boolean isAllowExplosion(Region region, List<String> sources, BlockState blockState) {
		if(!sources.isEmpty()) {
			for(String source : sources) {
				for(String target : Arrays.asList(ListenerUtils.blockID(blockState), "all")) {
					Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, source, target);
					if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
				}
			}
		} else {
			Tristate flagResult = region.getFlagResult(Flags.EXPLOSION_SURFACE, null, null);
			if(flagResult != Tristate.UNDEFINED) return flagResult.asBoolean();
		}
		return region.isGlobal() ? true : isAllowExplosion(plugin.getAPI().getGlobalRegion(region.getWorldKey()), sources, blockState);
	}

}
