package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Trust;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementTrust implements Trust {

	public ImplementTrust() {}

	@Setting("Description")
	private Component description = deserialize("&6Add a player to the region and specify his rights in the region.");
	@Setting("AdminClaim")
	private Component adminClaim = deserialize("&cYou cannot add players to an administrative region.");
	@Setting("LowTrust")
	private Component lowTrust = deserialize("&cOnly managers and region owners can add players to it.");
	@Setting("LimitReached")
	private Component limitReached = deserialize("&cThe limit of members in this region has been reached.");
	@Setting("NotOwner")
	private Component notOwner = deserialize("&cOnly the owner of the region can appoint managers.");
	@Setting("Success")
	private Component success = deserialize("&aYou have assigned the trust type &b" + Placeholders.TRUST_TYPE + "&a to &b" + Placeholders.PLAYER + "&a.");
	@Setting("SuccessTarget")
	private Component successTarget = deserialize("&b" + Placeholders.PLAYER + " &aassigns you the trust type &b" + Placeholders.TRUST_TYPE + " &ain the region in the world &b" + Placeholders.WORLD + " &aas the boundaries from &b" + Placeholders.MIN + " &ato &b" + Placeholders.MAX + "&a.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getAdminClaim() {
		return adminClaim;
	}

	@Override
	public Component getLowTrust() {
		return lowTrust;
	}

	@Override
	public Component getLimitReached() {
		return limitReached;
	}

	@Override
	public Component getNotOwner() {
		return notOwner;
	}

	@Override
	public Component getSuccess(String trustlevel, GameProfile profile) {
		return replace(success, array(Placeholders.TRUST_TYPE, Placeholders.PLAYER), trustlevel, profile.name().orElse(profile.examinableName()));
	}

	@Override
	public Component getSuccessTarget(String trustlevel, ServerPlayer player, Region region) {
		return replace(successTarget, array(Placeholders.PLAYER, Placeholders.TRUST_TYPE, Placeholders.WORLD, Placeholders.MIN, Placeholders.MAX), player.name(), trustlevel, region.getWorldKey().asString(), region.getCuboid().getMin(), region.getCuboid().getMax());
	}

}
