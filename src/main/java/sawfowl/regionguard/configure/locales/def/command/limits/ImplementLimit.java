package sawfowl.regionguard.configure.locales.def.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Transaction.Limit;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementLimit implements Limit {

	public ImplementLimit() {}

	public ImplementLimit(String description, String success) {
		this.description = deserialize(description);
		this.success = deserialize(success);
	}

	@Setting("Description")
	private Component description = deserialize("&6Payment in game currency to increase the limit.");
	@Setting("Success")
	private Component success = deserialize("&aYou have increased your limit by &b" + Placeholders.SIZE + "&a. Your current limit: &b" + Placeholders.VOLUME + "&a.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getSuccess(long size, long limit) {
		return replace(success, array(Placeholders.SIZE, Placeholders.VOLUME), size, limit);
	}

}