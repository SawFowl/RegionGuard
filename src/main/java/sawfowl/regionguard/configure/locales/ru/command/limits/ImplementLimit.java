package sawfowl.regionguard.configure.locales.ru.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Transaction.Limit;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementLimit implements Limit {

	public ImplementLimit() {}

	public ImplementLimit(String description, String success) {
		this.description = deserialize(description);
		this.success = deserialize(success);
	}

	@Setting("Description")
	private Component description = deserialize("&6Увеличение лимита за игровую валюту.");
	@Setting("Success")
	private Component success = deserialize("&aВы увеличили свой лимит на &b" + PlaceholderKeys.SIZE + "&a. Ваш текущий лимит: &b" + PlaceholderKeys.VOLUME + "&a.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getSuccess(long size, long limit) {
		return replace(success, array(PlaceholderKeys.SIZE, PlaceholderKeys.VOLUME), size, limit);
	}

}