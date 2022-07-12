package sawfowl.regionguard.api.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

@ConfigSerializable
public class Position {

	public Position() {}

	public Position(Vector3i vector3i) {
		x = vector3i.x();
		y = vector3i.y();
		z = vector3i.z();
	}

	public Position(Vector3d vector3d) {
		x = vector3d.toInt().x();
		y = vector3d.toInt().y();
		z = vector3d.toInt().z();
	}

	@Setting("X")
	private int x;
	@Setting("Y")
	private int y;
	@Setting("Z")
	private int z;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public Vector3i getVector3i() {
		return Vector3i.from(x, y, z);
	}
}
