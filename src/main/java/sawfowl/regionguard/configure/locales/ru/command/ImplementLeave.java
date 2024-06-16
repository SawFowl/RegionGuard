package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Leave;

@ConfigSerializable
public class ImplementLeave implements Leave {

	public ImplementLeave() {}

	@Setting("Description")
	private Component description = TextUtils.deserialize("&6Выход из региона.");
	@Setting("Owner")
	private Component owner = TextUtils.deserialize("&cВы владеете регионом и не можете его покинуть. Смените владельца или удалите регион.");
	@Setting("NotTrusted")
	private Component notTrusted = TextUtils.deserialize("&cВы не являетесь участником этого региона.");
	@Setting("ConfirmRequest")
	private Component confirmRequest = TextUtils.deserialize("&eВы уверены, что хотите покинуть этот регион? Нажмите на это сообщение, чтобы подтвердить.");
	@Setting("Success")
	private Component success = TextUtils.deserialize("&aВы покинули регион в своем текущем местоположении.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getOwner() {
		return owner;
	}

	@Override
	public Component getNotTrusted() {
		return notTrusted;
	}

	@Override
	public Component getConfirmRequest() {
		return confirmRequest;
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
