package sawfowl.regionguard.api.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.chunk.Chunk;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.builder.AbstractBuilder;

public interface ChunkNumber extends DataSerializable {

	private static Builder builder() {
		return Sponge.game().builderProvider().provide(Builder.class);
	}

	static ChunkNumber of(Vector3i vector3i) {
		return builder().from(vector3i);
	}

	static ChunkNumber of(Vector3d vector3d) {
		return of(vector3d.toInt());
	}

	static ChunkNumber from(Chunk<?> chunk) {
		return of(chunk.chunkPosition());
	}

	static ChunkNumber from(Entity entity) {
		return of(entity.blockPosition());
	}

	/**
	 * Getting a chunk position.
	 */
	Vector3i chunkPosition();

	public int getX();

	public int getZ();

	public boolean equalsTo(Vector3i vector3i);

	interface Builder extends AbstractBuilder<ChunkNumber>, org.spongepowered.api.util.Builder<ChunkNumber, Builder> {

		ChunkNumber from(Vector3i vector3i);

		ChunkNumber from(Vector3d vector3d);

	}

}
