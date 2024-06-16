package sawfowl.regionguard.configure.locales.def.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Set.Limit;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementSetLimit implements Limit {

	public ImplementSetLimit() {}

	public ImplementSetLimit(String description, String success, String successStaff) {
		this.description = deserialize(description);
		this.success = deserialize(success);
		this.successStaff = deserialize(successStaff);
	}

	@Setting("Description")
	private Component description = deserialize("&6Change the limit of the player.");
	@Setting("Success")
	private Component success = deserialize("&aYour limit has been changed to: &b" + Placeholders.VOLUME + "&a.");
	@Setting("SuccessStaff")
	private Component successStaff = deserialize("&aYou have changed the limit of player " + Placeholders.PLAYER + " to: &b" + Placeholders.VOLUME + "&a.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getSuccess(long limit) {
		return replace(success, Placeholders.VOLUME, limit);
	}

	@Override
	public Component getSuccessStaff(String player, long limit) {
		return replace(success, array(Placeholders.PLAYER, Placeholders.VOLUME), player, limit);
	}

}
