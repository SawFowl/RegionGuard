package sawfowl.regionguard.api.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import net.kyori.adventure.builder.AbstractBuilder;

@ConfigSerializable
public interface PlayerLimits extends DataSerializable {

	static Builder builder() {
		return Sponge.game().builderProvider().provide(Builder.class);
	}

	static PlayerLimits of(long blocks, long regions, long subdivisions, long membersPerRegion) {
		return builder().setBlocks(blocks).setRegions(regions).setSubdivisions(subdivisions).setMembersPerRegion(membersPerRegion).build();
	}

	static PlayerLimits zero() {
		return builder().build();
	}

	long getBlocks();

	long getRegions();

	long getSubdivisions();

	long getMembers();

	PlayerLimits setBlocks(long limit);

	PlayerLimits setClaims(long limit);

	PlayerLimits setSubdivisions(long limit);

	PlayerLimits setMembersPerRegion(long limit);

	interface Builder extends AbstractBuilder<PlayerLimits>, org.spongepowered.api.util.Builder<PlayerLimits, Builder> {

		Builder setBlocks(long limit);

		Builder setRegions(long limit);

		Builder setSubdivisions(long limit);

		Builder setMembersPerRegion(long limit);

	}

}
