package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.SetOwner;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementSetOwner implements SetOwner {

	public ImplementSetOwner() {}

	@Setting("Description")
	private Component description = deserialize("&6Set the owner of the region.");
	@Setting("OnlyOwner")
	private Component onlyOwner = deserialize("&cYou do not owner this region.");
	@Setting("AdminClaim")
	private Component adminClaim = deserialize("&cYou cannot assign an administrative region to a player.");
	@Setting("AlreadyOwner")
	private Component alreadyOwner = deserialize("&cYou already own this region.");
	@Setting("AlreadyOwnerStaff")
	private Component alreadyOwnerStaff = deserialize("&b" + PlaceholderKeys.PLAYER + " &cis already the owner of the region.");
	@Setting("ConfirmRequest")
	private Component confirmRequest = deserialize("&eDo you really want to change the region owner? Click on this message to confirm.");
	@Setting("SuccessFromStaff")
	private Component successFromStaff = deserialize("&eThe administration representative demotes you to manager of a region in the &b" + PlaceholderKeys.WORLD + "&a borders from &b" + PlaceholderKeys.MIN + "&a to &b" + PlaceholderKeys.MAX + "&a.");
	@Setting("Success")
	private Component success = deserialize("&aYou have appointed &b" + PlaceholderKeys.PLAYER + "&a as the new owner of a region in &b" + PlaceholderKeys.WORLD + "&a within the boundaries of &b" + PlaceholderKeys.MIN + " to &b" + PlaceholderKeys.MAX + "&a.");
	@Setting("SuccessNewOwner")
	private Component successNewOwner = deserialize("&b" + PlaceholderKeys.PLAYER + " &apoints you as the new owner of a region in the &b" + PlaceholderKeys.WORLD + "&a the boundaries from &b" + PlaceholderKeys.MIN + " to &b" + PlaceholderKeys.MAX + "&a.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getOnlyOwner() {
		return onlyOwner;
	}

	@Override
	public Component getAdminClaim() {
		return adminClaim;
	}

	@Override
	public Component getAlreadyOwner() {
		return alreadyOwner;
	}

	@Override
	public Component getAlreadyOwnerStaff(GameProfile profile) {
		return replace(alreadyOwnerStaff, PlaceholderKeys.PLAYER, profile.name().orElse(profile.examinableName()));
	}

	@Override
	public Component getConfirmRequest(GameProfile profile) {
		return replace(confirmRequest, PlaceholderKeys.PLAYER, profile.name().orElse(profile.examinableName()));
	}

	@Override
	public Component getSuccessFromStaff(Region region) {
		return replace(successFromStaff, array(PlaceholderKeys.WORLD, PlaceholderKeys.MIN, PlaceholderKeys.MAX), region.getWorldKey().asString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString());
	}

	@Override
	public Component getSuccess(Region region, GameProfile newOwner) {
		return replace(success, array(PlaceholderKeys.PLAYER, PlaceholderKeys.WORLD, PlaceholderKeys.MIN, PlaceholderKeys.MAX), newOwner.name().orElse(newOwner.examinableName()), region.getWorldKey().asString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString());
	}

	@Override
	public Component getSuccessNewOwner(Region region, GameProfile old) {
		return replace(successNewOwner, array(PlaceholderKeys.PLAYER, PlaceholderKeys.WORLD, PlaceholderKeys.MIN, PlaceholderKeys.MAX), old.name().orElse(old.examinableName()), region.getWorldKey().asString(), region.getCuboid().getMin().toString(), region.getCuboid().getMax().toString());
	}

}
