package sawfowl.regionguard.api.data;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.regionguard.api.TrustTypes;

@ConfigSerializable
public class MemberData {
	
	public MemberData() {}
	
	public MemberData(ServerPlayer player, TrustTypes level) {
		memberName = player.name();
		setTrustType(level);
	}

	public MemberData(TrustTypes level) {
		memberName = "Server";
		setTrustType(level);
	}

	@Setting("MemberName")
	private String memberName;
	@Setting("TrustLevel")
	private String trustLevel;
	@Setting("ReplaceNameInTitle")
	private boolean replaceNameInTitle = false;

	/**
	 * Getting member name
	 */
	public String getName() {
		return memberName;
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
		return TrustTypes.checkType(trustLevel);
	}

	/**
	 * Setting the type of trust for a region member
	 */
	public void setTrustType(TrustTypes level) {
		trustLevel = level.toString();
	}

	public boolean isReplaceNameInTitle() {
		return replaceNameInTitle;
	}

	public void setReplaceNameInTitle(boolean replaceNameInTitle) {
		this.replaceNameInTitle = replaceNameInTitle;
	}

}
