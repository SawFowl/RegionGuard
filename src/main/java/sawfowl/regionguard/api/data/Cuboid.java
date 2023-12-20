package sawfowl.regionguard.api.data;

import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.builder.AbstractBuilder;

import sawfowl.regionguard.api.SelectorTypes;

@ConfigSerializable
public interface Cuboid extends DataSerializable {

	static Builder builder() {
		return Sponge.game().builderProvider().provide(Builder.class);
	}

	static Builder builder(SelectorTypes type) {
		return builder().setSelectorType(type);
	}

	static Cuboid of(SelectorTypes type, Vector3i first, Vector3i second) {
		return builder(type).setFirstPosition(first).setSecondPosition(second).build();
	}

	static Cuboid of(Vector3i first, Vector3i second) {
		return builder(SelectorTypes.CUBOID).setFirstPosition(first).setSecondPosition(second).build();
	}

	static Cuboid of(AABB aabb) {
		return builder(SelectorTypes.CUBOID).setFirstPosition(aabb.min().toInt()).setSecondPosition(aabb.max().toInt()).build();
	}

	static Cuboid of(SelectorTypes type, AABB aabb) {
		return builder(type).setFirstPosition(aabb.min().toInt()).setSecondPosition(aabb.max().toInt()).build();
	}

	/**
	 * Getting all positions in the cuboid.
	 */
	List<Vector3i> getAllPositions();

	/**
	 * Getting all positions in the cuboid. Excludes the Y-axis.
	 */
	List<Vector2i> getPositionsXZ();

	/**
	 * Setting positions at the cuboid.
	 * 
	 * @param position1 - first position
	 * @param position2 - second position
	 * @param selectorType - flat or cuboid
	 * @param serverWorld - world
	 */
	Cuboid setPositions(Vector3i position1, Vector3i position2 , SelectorTypes selectorType);

	/**
	 * Get the type of area selection.
	 */
	SelectorTypes getSelectorType();

	/**
	 * Set the type of area selection.
	 */
	void setSelectorType(SelectorTypes selectorType);

	/**
	 * Get AABB
	 */
	AABB getAABB();

	/**
	 * Get the center position of the selection.
	 */
	Vector3d getCenter();

	/**
	 * Get the sizes of the area by three coordinates
	 */
	Vector3d getSizeXYZ();

	/**
	 * Get the sizes of the area by two coordinates
	 */
	int[] getSizeXZ();

	/**
	 * Obtaining the volume of the area, taking into account all three coordinate axes.
	 */
	long getSize3D();


	/**
	 * Obtaining the volume of the area with only XY coordinates.
	 */
	long getSize2D();


	/**
	 * Obtaining the volume of an area depending on its type.
	 */
	long getSize();

	/**
	 * Expanding area
	 */
	void expand(int x, int y, int z);

	/**
	 * Decreasing area
	 */
	void contract(int x, int y, int z);


	/**
	 * Checking for a position in the area
	 * 
	 * @param vector3i - Checkable position.
	 * @return - true if contains <br>
	 * - false if not contains
	 */
	boolean containsIntersectsPosition(Vector3i vector3i);


	/**
	 * Getting the minimum position
	 */
	Vector3i getMin();


	/**
	 * Getting the maximum position
	 */
	Vector3i getMax();

	/**
	 * Obtaining the opposite corner of area.
	 * 
	 * @param vector3i - Position for the search
	 * @return - position for the search if position is not corner <br>
	 * - position in the opposite corner according to the type of area
	 */
	Vector3i getOppositeCorner(Vector3i vector3i);


	/**
	 * Getting all corner positions of the area
	 */
	List<Vector3i> getAllCorners();

	/**
	 * Checking if the position is a corner position
	 * 
	 * @param vector3i - checked position
	 * @return - true if position is corner <br>
	 * - false if position isn't corner
	 */
	boolean isCorner(Vector3i vector3i);

	/**
	 * Changing the positions of the cuboid according to the height of the world.
	 */
	Cuboid toFlat(ServerWorld world);

	interface Builder extends AbstractBuilder<Cuboid>, org.spongepowered.api.util.Builder<Cuboid, Builder> {

		Builder setPosition(Vector3i vector3i, boolean first);

		Builder setFirstPosition(Vector3i vector3i);

		Builder setSecondPosition(Vector3i vector3i);

		Builder setSelectorType(SelectorTypes type);

	}

}
