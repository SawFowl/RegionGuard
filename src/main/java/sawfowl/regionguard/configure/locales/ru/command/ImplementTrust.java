package sawfowl.regionguard.configure.locales.ru.command;

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
	private Component description = deserialize("&6Добавление игрока в регион и указывание его прав в регионе.");
	@Setting("AdminClaim")
	private Component adminClaim = deserialize("&cВы не можете добавлять игроков в административный регион.");
	@Setting("LowTrust")
	private Component lowTrust = deserialize("&cТолько менеджеры и владельцы регионов могут добавлять в него игроков.");
	@Setting("LimitReached")
	private Component limitReached = deserialize("&cЛимит участников в этом регионе исчерпан.");
	@Setting("NotOwner")
	private Component notOwner = deserialize("&cТолько владелец региона может назначать менеджеров.");
	@Setting("Success")
	private Component success = deserialize("&aВы назначили тип доверия &b" + Placeholders.TRUST_TYPE + "&a игроку &b" + Placeholders.PLAYER + "&a.");
	@Setting("SuccessTarget")
	private Component successTarget = deserialize("&b" + Placeholders.PLAYER + " &aназначает вам тип доверия &b" + Placeholders.TRUST_TYPE + " &aв регионе в мире &b" + Placeholders.WORLD + " &a с границами от &b" + Placeholders.MIN + " &aдо &b" + Placeholders.MAX + "&a.");

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
