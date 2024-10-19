package sawfowl.regionguard.configure.locales.ru.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Set.Limit;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementSetLimit implements Limit {

	public ImplementSetLimit() {}

	public ImplementSetLimit(String description, String success, String successStaff) {
		this.description = deserialize(description);
		this.success = deserialize(success);
		this.successStaff = deserialize(successStaff);
	}

	@Setting("Description")
	private Component description = deserialize("&6Изменение лимита игрока.");
	@Setting("Success")
	private Component success = deserialize("&aВаш лимит изменен и теперь равен: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("SuccessStaff")
	private Component successStaff = deserialize("&aВы изменили лимит игрока " + PlaceholderKeys.PLAYER + " и установили его в значении: &b" + PlaceholderKeys.VOLUME + "&a.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getSuccess(long limit) {
		return replace(success, PlaceholderKeys.VOLUME, limit);
	}

	@Override
	public Component getSuccessStaff(String player, long limit) {
		return replace(success, array(PlaceholderKeys.PLAYER, PlaceholderKeys.VOLUME), player, limit);
	}

}
