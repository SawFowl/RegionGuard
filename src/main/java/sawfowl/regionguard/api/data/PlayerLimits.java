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

	static PlayerLimits of(long blocks, long claims, long subdivisions, long membersPerRegion) {
		return builder().setBlocks(blocks).setClaims(claims).setSubdivisions(subdivisions).setMembersPerRegion(membersPerRegion).build();
	}

	static PlayerLimits zero() {
		return builder().build();
	}

	long getBlocks();

	long getClaims();

	long getSubdivisions();

	long getMembersPerRegion();

	PlayerLimits setBlocks(long limit);

	PlayerLimits setClaims(long limit);

	PlayerLimits setSubdivisions(long limit);

	PlayerLimits setMembersPerRegion(long limit);

	interface Builder extends AbstractBuilder<PlayerLimits>, org.spongepowered.api.util.Builder<PlayerLimits, Builder> {

		Builder setBlocks(long limit);

		Builder setClaims(long limit);

		Builder setSubdivisions(long limit);

		Builder setMembersPerRegion(long limit);

	}

}
