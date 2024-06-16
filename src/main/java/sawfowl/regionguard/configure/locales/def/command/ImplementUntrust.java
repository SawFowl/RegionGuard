package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Untrust;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementUntrust implements Untrust {

	public ImplementUntrust() {}
	@Setting("Description")
	private Component description = deserialize("&6Remove a player from the region.");
	@Setting("LowTrust")
	private Component lowTrust = deserialize("&cYou do not owner the region and cannot exclude another manager from it.");
	@Setting("PlayerIsOwner")
	private Component playerIsOwner = deserialize("&cYou cannot exclude the owner of a region. You must first specify another player as the owner.");
	@Setting("NotOwner")
	private Component notOwner = deserialize("&cYou are not the owner or manager of this region and cannot exclude other players from it.");
	@Setting("Success")
	private Component success = deserialize("&aYou have excluded a player from the region &b" + Placeholders.PLAYER + "&a.");
	@Setting("SuccessTarget")
	private Component successTarget = deserialize("&b" + Placeholders.PLAYER + " &eexcludes you from the region in the world &b" + Placeholders.WORLD + " &eas the boundaries from &e" + Placeholders.MIN + " &ato &e" + Placeholders.MAX + "&a.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getLowTrust() {
		return lowTrust;
	}

	@Override
	public Component getPlayerIsOwner() {
		return playerIsOwner;
	}

	@Override
	public Component getNotOwner() {
		return notOwner;
	}

	@Override
	public Component getSuccess(GameProfile profile) {
		return replace(success, Placeholders.PLAYER, profile.name().orElse(profile.examinableName()));
	}

	@Override
	public Component getSuccessTarget(ServerPlayer player, Region region) {
		return replace(successTarget, array(Placeholders.PLAYER, Placeholders.WORLD, Placeholders.MIN, Placeholders.MAX), player.name(), region.getWorldKey().asString(), region.getCuboid().getMin(), region.getCuboid().getMax());
	}

}
