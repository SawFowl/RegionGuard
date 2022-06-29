package sawfowl.regionguard.api.data;

import java.util.Objects;

import org.spongepowered.math.vector.Vector3i;

public class ChunkNumber {

	public ChunkNumber(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public ChunkNumber(Vector3i vector3i) {
		x = vector3i.x() / 16;
		z = vector3i.z() / 16;
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

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		ChunkNumber other = (ChunkNumber) obj;
		return x == other.x && z == other.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, z);
	}

	@Override
	public String toString() {
		return "ChunkNumber(" + x + ", " + z + ")";
	}

}
