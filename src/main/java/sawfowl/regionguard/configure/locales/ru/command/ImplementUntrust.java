package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Untrust;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementUntrust implements Untrust {

	public ImplementUntrust() {}
	@Setting("Description")
	private Component description = deserialize("&6Исключение игрока из региона.");
	@Setting("LowTrust")
	private Component lowTrust = deserialize("&cВы не являетесь владельцем региона и не можете исключить из него другого менеджера.");
	@Setting("PlayerIsOwner")
	private Component playerIsOwner = deserialize("&cВы не можете исключить владельца региона. Сначала необходимо указать другого игрока в качестве владельца.");
	@Setting("NotOwner")
	private Component notOwner = deserialize("&cВы не являетесь владельцем или управляющим этого региона и не можете исключить из него других игроков.");
	@Setting("Success")
	private Component success = deserialize("&aВы исключили из региона игрока &b" + PlaceholderKeys.PLAYER + "&a.");
	@Setting("SuccessTarget")
	private Component successTarget = deserialize("&b" + PlaceholderKeys.PLAYER + " &eисключает вас из региона в мире &b" + PlaceholderKeys.WORLD + " &eс границами от &e" + PlaceholderKeys.MIN + " &aдо &e" + PlaceholderKeys.MAX + "&a.");

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
		return replace(success, PlaceholderKeys.PLAYER, profile.name().orElse(profile.examinableName()));
	}

	@Override
	public Component getSuccessTarget(ServerPlayer player, Region region) {
		return replace(successTarget, array(PlaceholderKeys.PLAYER, PlaceholderKeys.WORLD, PlaceholderKeys.MIN, PlaceholderKeys.MAX), player.name(), region.getWorldKey().asString(), region.getCuboid().getMin(), region.getCuboid().getMax());
	}

}
