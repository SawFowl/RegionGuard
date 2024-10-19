package sawfowl.regionguard.configure.locales.ru.command;

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
	private Component description = deserialize("&6Смена владельца региона");
	@Setting("OnlyOwner")
	private Component onlyOwner = deserialize("&cВы не являетесь владельцем этого региона.");
	@Setting("AdminClaim")
	private Component adminClaim = deserialize("&cВы не можете назначить игроку административный регион.");
	@Setting("AlreadyOwner")
	private Component alreadyOwner = deserialize("&cВы уже владеете этим регионом.");
	@Setting("AlreadyOwnerStaff")
	private Component alreadyOwnerStaff = deserialize("&b" + PlaceholderKeys.PLAYER + " &cуже является владельцем региона.");
	@Setting("ConfirmRequest")
	private Component confirmRequest = deserialize("&eВы действительно хотите изменить владельца региона? Нажмите на это сообщение, чтобы подтвердить.");
	@Setting("SuccessFromStaff")
	private Component successFromStaff = deserialize("&eПредставитель администрации понижает вас до менеджера региона в мире &b" + PlaceholderKeys.WORLD + "&a с границами от &b" + PlaceholderKeys.MIN + "&a до &b" + PlaceholderKeys.MAX + "&a.");
	@Setting("Success")
	private Component success = deserialize("&aВы передали регион в мире &b" + PlaceholderKeys.WORLD + "&a с границами от &b" + PlaceholderKeys.MIN + "&a до &b" + PlaceholderKeys.MAX + " игроку &b" + PlaceholderKeys.PLAYER + "&a.");
	@Setting("SuccessNewOwner")
	private Component successNewOwner = deserialize("&b" + PlaceholderKeys.PLAYER + " &aпередает вам права владельца региона в мире &b" + PlaceholderKeys.WORLD + "&a с границами от &b" + PlaceholderKeys.MIN + " до &b" + PlaceholderKeys.MAX + "&a.");

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
