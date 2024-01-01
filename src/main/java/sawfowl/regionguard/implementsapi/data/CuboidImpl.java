package sawfowl.regionguard.implementsapi.data;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.api.SelectorTypes;
import sawfowl.regionguard.api.data.Cuboid;

@ConfigSerializable
public class CuboidImpl implements Cuboid {

	public CuboidImpl(){}

	public Cuboid.Builder builder() {
		return new Builder() {

			@Override
			public @NotNull Cuboid build() {
				return CuboidImpl.this;
			}

			@Override
			public Builder setSelectorType(SelectorTypes type) {
				selectorType = type;
				return this;
			}

			@Override
			public Builder setFirstPosition(Vector3i vector3i) {
				min = vector3i;
				return this;
			}

			@Override
			public Builder setSecondPosition(Vector3i vector3i) {
				max = vector3i;
				return this;
			}

			@Override
			public Builder setPosition(Vector3i vector3i, boolean first) {
				return this;
			}
		};
	}

	private Vector3i min;
	private Vector3i max;
	private SelectorTypes selectorType;
	private AABB aabb;

	/**
	 * Getting all positions in the cuboid.
	 */
	public List<Vector3i> getAllPositions() {
		List<Vector3i> allPositions = new ArrayList<Vector3i>();
		boolean cuboid = selectorType == SelectorTypes.CUBOID;
		for(int x = (int) getAABB().min().x(); x <= (int) getAABB().max().x(); x++) {
			for(int z = (int) getAABB().min().z(); z <= (int) getAABB().max().z(); z++) {
				for(int y = (int) getAABB().min().y(); cuboid ? y <= (int) getAABB().max().y() : y < (int) getAABB().max().y(); y++) {
					allPositions.add(Vector3i.from(x, y, z));
				}
			}
		}
		return allPositions;
	}

	/**
	 * Getting all positions in the cuboid. Excludes the Y-axis.
	 */
	public List<Vector2i> getPositionsXZ() {
		List<Vector2i> allPositions = new ArrayList<Vector2i>();
		for(int x = (int) getAABB().min().x(); x <= (int) getAABB().max().x(); x++) {
			for(int z = (int) getAABB().min().z(); z <= (int) getAABB().max().x(); z++) {
				allPositions.add(Vector2i.from(x, z));
			}
		}
		return allPositions;
	}

	/**
	 * Setting positions at the cuboid.
	 * 
	 * @param position1 - first position
	 * @param position2 - second position
	 * @param selectorType - flat or cuboid
	 */
	public Cuboid setPositions(Vector3i position1, Vector3i position2, SelectorTypes selectorType, ServerWorld world) {
		this.selectorType = selectorType;
		if(position1.x() == position2.x()) position1 = Vector3i.from(position1.x() + 1, position1.y(), position1.z());
		if(position1.y() == position2.y() && !selectorType.equals(SelectorTypes.FLAT)) position1 = Vector3i.from(position1.x(), position1.y() + 1, position1.z());
		if(position1.z() == position2.z()) position1 = Vector3i.from(position1.x(), position1.y(), position1.z() + 1);
		if(selectorType.equals(SelectorTypes.FLAT)) {
			if(world == null) {
				this.aabb = AABB.of(position1.x(), position1.y(), position1.z(), position2.x(), position1.y() + 1, position2.z());
			} else this.aabb = AABB.of(position1.x(), world.min().y(), position1.z(), position2.x(), world.height(), position2.z());
		} else if(selectorType.equals(SelectorTypes.CUBOID)) {
			this.aabb = AABB.of(position1.x(), position1.y(), position1.z(), position2.x(), position2.y(), position2.z());;
		} else {
			return this;
		}
		this.min = aabb.min().toInt();
		this.max = aabb.max().toInt();
		return this;
	}

	/**
	 * Get the type of area selection.
	 */
	public SelectorTypes getSelectorType() {
		return selectorType;
	}

	/**
	 * Set the type of area selection.
	 */
	public void setSelectorType(SelectorTypes selectorType) {
		this.selectorType = selectorType;
	}

	/**
	 * Get AABB
	 */
	public AABB getAABB() {
		if(aabb == null) aabb = AABB.of(min.toInt(), max.toInt());
		return aabb;
	}

	/**
	 * Get the center position of the selection.
	 */
	public Vector3d getCenter() {
		return getAABB().center();
	}

	/**
	 * Get the sizes of the area by three coordinates
	 */
	public Vector3d getSizeXYZ() {
		return getAABB().size();
	}

	/**
	 * Get the sizes of the area by two coordinates
	 */
	public int[] getSizeXZ() {
		int x = 0;
		int z = 0;
		Vector3i min = getMin();
		Vector3i max = getMax();
		for (int minX = min.x(); minX <= max.x(); minX++) {
			x++;
		}
		for (int minZ = min.z(); minZ <= max.z(); minZ++) {
			z++;
		}
		int[] size = {x,z};
		return size;
	}

	/**
	 * Obtaining the volume of the area, taking into account all three coordinate axes.
	 */
	public long getSize3D() {
		return (max.toInt().x() - min.toInt().x() + 1L) * (max.toInt().y() - min.toInt().y() + 1L) * (max.toInt().z() - min.toInt().z() + 1L);
	}


	/**
	 * Obtaining the volume of the area with only XY coordinates.
	 */
	public long getSize2D() {
		return (max.toInt().x() - min.toInt().x() + 1L) * (max.toInt().z() - min.toInt().z() + 1L);
	}


	/**
	 * Obtaining the volume of an area depending on its type.
	 */
	public long getSize() {
		return selectorType == SelectorTypes.FLAT ? getSize2D() : getSize3D();
	}

	/**
	 * Expanding area
	 */
	public void expand(int x, int y, int z) {
		aabb = getAABB().expand(x, y, z);
		min = getAABB().min().toInt();
		max = getAABB().max().toInt();
	}

	/**
	 * Decreasing area
	 */
	public void contract(int x, int y, int z) {
		aabb = getAABB().offset(x, y, z);
		min = getAABB().min().toInt();
		max = getAABB().max().toInt();
	}


	/**
	 * Checking for a position in the area
	 * 
	 * @param vector3i - Checkable position.
	 * @return - true if contains <br>
	 * - false if not contains
	 */
	public boolean containsIntersectsPosition(Vector3i vector3i) {
		return getAABB().contains(vector3i);
	}


	/**
	 * Getting the minimum position
	 */
	public Vector3i getMin() {
		return min;
	}


	/**
	 * Getting the maximum position
	 */
	public Vector3i getMax() {
		return max;
	}

	/**
	 * Obtaining the opposite corner of area.
	 * 
	 * @param vector3i - Position for the search
	 * @return - position for the search if position is not corner <br>
	 * - position in the opposite corner according to the type of area
	 */
	public Vector3i getOppositeCorner(Vector3i vector3i) {
		if(!isCorner(vector3i)) return vector3i;
		Vector3i max = getMax().toInt();
		Vector3i min = getMin().toInt();
		if(vector3i == max) return min;
		if(vector3i == min) return max;
		int x = vector3i.x();
		int y = vector3i.y();
		int z = vector3i.z();
		int minX = min.x();
		int minY = min.y();
		int minZ = min.z();
		return Vector3i.from(minX == x ? max.x() : minX, selectorType == SelectorTypes.CUBOID && minY == y ? max.y() : minY, minZ == z ? max.z() : minZ);
	}


	/**
	 * Getting all corner positions of the area
	 */
	public List<Vector3i> getAllCorners() {
		List<Vector3i> corners = new ArrayList<Vector3i>();
		corners.add(min.toInt());
		
		corners.add(Vector3i.from(max.toInt().x(), min.toInt().y(), min.toInt().z()));
		corners.add(Vector3i.from(min.toInt().x(), min.toInt().y(), max.toInt().z()));
		corners.add(Vector3i.from(max.toInt().x(), min.toInt().y(), max.toInt().z()));
		
		corners.add(Vector3i.from(min.toInt().x(), max.toInt().y(), max.toInt().z()));
		corners.add(Vector3i.from(max.toInt().x(), max.toInt().y(), min.toInt().z()));
		corners.add(Vector3i.from(min.toInt().x(), max.toInt().y(), min.toInt().z()));
		
		corners.add(max.toInt());
		return corners;
	}

	/**
	 * Checking if the position is a corner position
	 * 
	 * @param vector3i - checked position
	 * @return - true if position is corner <br>
	 * - false if position isn't corner
	 */
	public boolean isCorner(Vector3i vector3i) {
		if(min == null && max == null) return false;
		if(selectorType == SelectorTypes.FLAT) {
			for(Vector3i corner : getAllCorners()) {
				if(corner.x() == vector3i.x() && corner.z() == vector3i.z()) return true;
			}
		} else {
			return getAllCorners().contains(vector3i);
		}
		return false;
	}

	@Override
	public Cuboid toFlat(ServerWorld world) {
		return setPositions(getMin(), getMax(), SelectorTypes.FLAT, world);
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
