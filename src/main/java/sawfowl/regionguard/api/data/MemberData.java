package sawfowl.regionguard.api.data;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.TrustTypes;

@ConfigSerializable
public interface MemberData extends DataSerializable {

	static Builder builder() {
		return Sponge.game().builderProvider().provide(Builder.class);
	}

	static MemberData of(ServerPlayer player, TrustTypes trustType) {
		return builder().setPlayer(player, trustType).build();
	}

	static MemberData of(GameProfile player, TrustTypes trustType) {
		return builder().setPlayer(player, trustType).build();
	}

	static MemberData forServer() {
		return builder().setServer();
	}

	/**
	 * Getting member name
	 */
	String getName();

	/**
	 * Getting member {@link UUID}
	 */
	UUID getUniqueId();

	/**
	 * Getting member name as kyori component
	 */
	Component asComponent();

	/**
	 * Getting member name as kyori component
	 */
	Component asComponent(ServerPlayer joiner);

	/**
	 * Checking if the owner is a player <br>
	 * The check is performed by the name of the owner.
	 */
	boolean isPlayer();

	/**
	 * Getting a player object
	 * 
	 * @return - player if a player is found <br>
	 * - empty if a player is not found
	 */
	Optional<ServerPlayer> getPlayer();

	/**
	 * Obtaining a region member's trust type
	 */
	TrustTypes getTrustType();

	/**
	 * Setting the type of trust for a region member
	 */
	void setTrustType(TrustTypes level);

	boolean isReplaceNameInTitle();

	void setReplaceNameInTitle(boolean replaceNameInTitle);

	interface Builder extends AbstractBuilder<MemberData>, org.spongepowered.api.util.Builder<MemberData, Builder> {

		Builder setPlayer(ServerPlayer player, TrustTypes type);

		Builder setPlayer(GameProfile player, TrustTypes trustType);

		Builder setTrustType(TrustTypes type);

		MemberData setServer();

		MemberData from(MemberData data);

	}

}
