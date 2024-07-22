package sawfowl.regionguard.implementsapi.data;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.TrustTypes;
import sawfowl.regionguard.api.data.MemberData;

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
			public MemberData build() {
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

			@Override
			public Builder setName(String name) {
				memberName = name;
				return this;
			}

			@Override
			public Builder setUUID(UUID uuid) {
				MemberDataImpl.this.uuid = uuid;
				return this;
			}
		};
	}

	private String memberName;
	private UUID uuid;
	private TrustTypes trustLevel;
	private boolean replaceNameInTitle = false;

	@Override
	public String getName() {
		return memberName;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public Component asComponent() {
		return Component.text(memberName);
	}

	@Override
	public Component asComponent(ServerPlayer joiner) {
		return replaceNameInTitle ? Component.text(joiner.name()) : asComponent();
	}

	@Override
	public boolean isPlayer() {
		return !memberName.equals("Server");
	}

	@Override
	public Optional<ServerPlayer> getPlayer() {
		return isPlayer() ? Sponge.server().player(uuid) : Optional.empty();
	}

	@Override
	public TrustTypes getTrustType() {
		return trustLevel;
	}

	@Override
	public void setTrustType(TrustTypes level) {
		trustLevel = level;
	}

	@Override
	public boolean isReplaceNameInTitle() {
		return replaceNameInTitle;
	}

	@Override
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
