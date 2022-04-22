package sawfowl.regionguard.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.block.transaction.Operation;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.Queries;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.common.data.MemoryDataView;
import org.spongepowered.math.vector.Vector3i;

public class ListenerUtils {

	public static ServerWorld getWorld(ResourceKey key) {
		return Sponge.server().worldManager().world(key).get();
	}

	public static String entityId(Entity entity) {
		return Sponge.game().registry(RegistryTypes.ENTITY_TYPE).valueKey(entity.type()).asString();
	}

	public static String entityCategory(Entity entity) {
		return Sponge.game().registry(RegistryTypes.ENTITY_CATEGORY).valueKey(entity.type().category()).asString();
	}

	public static String blockID(BlockSnapshot block) {
		return Sponge.game().registry(RegistryTypes.BLOCK_TYPE).valueKey(block.state().type()).asString();
	}

	public static String blockID(BlockState block) {
		return Sponge.game().registry(RegistryTypes.BLOCK_TYPE).valueKey(block.type()).asString();
	}

	public static String damageTypeId(DamageType damageType) {
		return Sponge.game().registry(RegistryTypes.DAMAGE_TYPE).valueKey(damageType).asString();
	}

	public static String itemId(ItemStack itemStack) {
		return Sponge.game().registry(RegistryTypes.ITEM_TYPE).valueKey(itemStack.type()).asString();
	}

	public static BlockTransaction getTransaction(List<BlockTransaction> transactions, Operation operation) {
		return transactions.stream().filter(transaction -> (transaction.operation() == operation)).findFirst().get();
	}

	public static Optional<BlockTransaction> pistonMovedTransaction(List<BlockTransaction> transactions) {
		return transactions.stream().filter(transaction -> (!blockID(transaction.original()).contains("air") && !blockID(transaction.original()).contains("piston") && (blockID(transaction.defaultReplacement()).contains("air") || blockID(transaction.defaultReplacement()).contains("piston")))).findFirst();
	}

	public static List<String> flagEntityArgs(Entity entity) {
		return Arrays.asList(entityId(entity), entityCategory(entity), "all");
	}

	public static List<String> flagEntitiesArgs(List<Entity> entities) {
		List<String> list = new ArrayList<String>();
		entities.forEach(entity -> {
			if(!list.contains(entityId(entity))) list.add(entityId(entity));
			if(!list.contains(entityCategory(entity))) list.add(entityCategory(entity));
		});
		list.add("all");
		return list;
	}

	public static List<String> flagItemsArgs(List<ItemStackSnapshot> snapshots) {
		List<String> list = new ArrayList<String>();
		snapshots.forEach(snapshot -> {
			if(!list.contains(itemId(snapshot.createStack()))) list.add(itemId(snapshot.createStack()));
		});
		list.add("all");
		return list;
	}

	public static List<String> flagItemArgs(ItemStack itemStack) {
		return Arrays.asList(itemId(itemStack), "all");
	}

	public static List<String> flagDamageSourceArgs(DamageSource damageSource) {
		return Arrays.asList(damageTypeId(damageSource.type()), "all");
	}

	public static List<String> flagBlockArgs(BlockSnapshot snapshot) {
		return flagBlockArgs(snapshot.state());
	}

	public static List<String> flagBlockArgs(BlockState snapshot) {
		return Arrays.asList(blockID(snapshot), "all");
	}

	public static WorldChunk getChunk(ServerWorld world, Vector3i vector3i) {
		return world.chunkAtBlock(vector3i);
	}

	public static boolean findEntity(Entity entity) {
		return entity != null && Sponge.game().registry(RegistryTypes.ENTITY_TYPE).findValueKey(entity.type()).isPresent();
	}

	public static boolean postEvent(Event regionEvent) {
		return Sponge.eventManager().post(regionEvent);
	}

	public static boolean nonReplacement(List<BlockTransaction> transactions) {
		return transactions.isEmpty() || !transactions.stream().filter(transaction -> (transaction.toContainer().contains(Queries.DEFAULT_REPLACEMENT))).findFirst().isPresent();
	}

	public static boolean nonReplacement(BlockTransaction transaction) {
		return !transaction.toContainer().contains(Queries.DEFAULT_REPLACEMENT);
	}

	public static boolean isContainer(BlockSnapshot block) {
		return (block.toContainer().get(DataQuery.of("UnsafeData")).isPresent() && block.toContainer().get(DataQuery.of("UnsafeData")).get().toString().contains("Items")) || blockID(block).equals("minecraft:crafting_table");
	}

	public static boolean isBedBlock(BlockSnapshot block) {
		String id = blockID(block);
		return id.contains("minecraft:") && id.contains("minecraft:_bed");
	}

	public static boolean isPistonOperation(List<BlockTransaction> transactions) {
		return transactions.stream().filter(transaction -> (blockID(transaction.defaultReplacement()).contains("piston"))).findFirst().isPresent();
	}

	public static boolean isDestructBlock(List<BlockTransaction> transactions) {
		return transactions.stream().filter(transaction -> (transaction.operation() == Operations.BREAK.get())).findFirst().isPresent();
	}

	public static boolean isPlaceBlock(List<BlockTransaction> transactions) {
		return transactions.stream().filter(transaction -> (transaction.operation() == Operations.PLACE.get())).findFirst().isPresent();
	}

	public static boolean isLiquidFlow(List<BlockTransaction> transactions) {
		return transactions.stream().filter(transaction -> (transaction.operation() == Operations.LIQUID_SPREAD.get())).findFirst().isPresent();
	}

	public static boolean isModify(List<BlockTransaction> transactions) {
		return transactions.stream().filter(transaction -> (transaction.operation() == Operations.MODIFY.get())).findFirst().isPresent();
	}

	public static boolean isGrowth(List<BlockTransaction> transactions) {
		return transactions.stream().filter(transaction -> (transaction.operation() == Operations.GROWTH.get())).findFirst().isPresent();
	}

	public static boolean isDecay(List<BlockTransaction> transactions) {
		return transactions.stream().filter(transaction -> (transaction.operation() == Operations.DECAY.get())).findFirst().isPresent();
	}

	public static boolean isExplosion(Object object) {
		return object instanceof Explosion;
	}

	public static int getLiquidFlowLevel(BlockTransaction transaction) {
		if(nonReplacement(transaction) || !((MemoryDataView) transaction.toContainer().get(Queries.DEFAULT_REPLACEMENT).get()).values(true).containsKey(DataQuery.of("BlockState", "BlockState"))) return 0;
		String check = ((MemoryDataView) transaction.toContainer().get(Queries.DEFAULT_REPLACEMENT).get()).values(true).get(DataQuery.of("BlockState", "BlockState")).toString();
		if(!check.contains("level=")) return 0;
		check = check.split("level=")[1].replace("]", "");
		return NumberUtils.isCreatable(check) ? NumberUtils.createInteger(check) : 0;
	}

}
