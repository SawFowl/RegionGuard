package sawfowl.regionguard.implementsapi.data;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.api.data.ChunkNumber;

public class ChunkNumberImpl implements ChunkNumber {

	public ChunkNumberImpl(){}

	public ChunkNumberImpl(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public ChunkNumber.Builder builder() {
		return new Builder() {

			@Override
			public @NotNull ChunkNumber build() {
				return ChunkNumberImpl.this;
			}

			@Override
			public ChunkNumber from(Vector3d vector3d) {
				return from(vector3d.toInt());
			}

			@Override
			public ChunkNumber from(Vector3i vector3i) {
				x = vector3i.x() / 16;
				z = vector3i.z() / 16;
				return build();
			}
		};
	}

	int x;
	int z;

	/**
	 * Getting a chunk position.
	 */
	public Vector3i chunkPosition() {
		return Vector3i.from(x * 16, 0, z * 16);
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public boolean equalsTo(Vector3i vector3i) {
		return x == vector3i.x() / 16 && z == vector3i.z() / 16;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		ChunkNumberImpl other = (ChunkNumberImpl) obj;
		return x == other.x && z == other.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, z);
	}

	@Override
	public String toString() {
		return "ChunkNumberImpl(" + x + ", " + z + ")";
	}

	@Override
	public int contentVersion() {
		return 0;
	}

	@Override
	public DataContainer toContainer() {
		return null;
	}

}
