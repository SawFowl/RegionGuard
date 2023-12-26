package sawfowl.regionguard.data;

import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.MemberData;

@ConfigSerializable
public class MemberDataImpl implements MemberData {
	
	public MemberDataImpl(){}
	public MemberDataImpl(String name, UUID uuid, TrustTypes type) {
		memberName = name;
		this.uuid = uuid;
		trustLevel = type;
	}

	public MemberData.Builder builder() {
		return new Builder() {

			@Override
			public @NotNull MemberData build() {
				if(memberName == null) memberName = "n/a";
				return MemberDataImpl.this;
			}

			@Override
			public Builder setTrustType(TrustTypes type) {
				trustLevel = type;
				return this;
			}

			@Override
			public MemberData setServer() {
				trustLevel = TrustTypes.OWNER;
				memberName = "Server";
				uuid = new UUID(0,0);
				replaceNameInTitle = true;
				return build();
			}

			@Override
			public Builder setPlayer(ServerPlayer player, TrustTypes type) {
				return setPlayer(player.profile(), type);
			}

			@Override
			public Builder setPlayer(GameProfile player, TrustTypes trustType) {
				memberName = player.name().orElse(player.examinableName());
				uuid = player.uniqueId();
				return setTrustType(trustType);
			}

			@Override
			public MemberData from(MemberData data) {
				memberName = data.getName();
				uuid = data.getUniqueId();
				trustLevel = data.getTrustType();
				return MemberDataImpl.this;
			}
		};
	}

	@Setting("Name")
	private String memberName;
	@Setting("UUID")
	private UUID uuid;
	@Setting("TrustLevel")
	private TrustTypes trustLevel;
	@Setting("ReplaceNameInTitle")
	private boolean replaceNameInTitle = false;

	/**
	 * Getting member name
	 */
	public String getName() {
		return memberName;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	/**
	 * Getting member name as kyori component
	 */
	public Component asComponent() {
		return Component.text(memberName);
	}

	/**
	 * Getting member name as kyori component
	 */
	public Component asComponent(ServerPlayer joiner) {
		return replaceNameInTitle ? Component.text(joiner.name()) : asComponent();
	}

	/**
	 * Checking if the owner is a player <br>
	 * The check is performed by the name of the owner.
	 */
	public boolean isPlayer() {
		return !memberName.equals("Server");
	}

	/**
	 * Getting a player object
	 * 
	 * @return - player if a player is found <br>
	 * - empty if a player is not found
	 */
	public Optional<ServerPlayer> getPlayer() {
		return isPlayer() ? Sponge.server().player(memberName) : Optional.empty();
	}

	/**
	 * Obtaining a region member's trust type
	 */
	public TrustTypes getTrustType() {
		return trustLevel;
	}

	/**
	 * Setting the type of trust for a region member
	 */
	public void setTrustType(TrustTypes level) {
		trustLevel = level;
	}

	public boolean isReplaceNameInTitle() {
		return replaceNameInTitle;
	}

	public void setReplaceNameInTitle(boolean replaceNameInTitle) {
		this.replaceNameInTitle = replaceNameInTitle;
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
