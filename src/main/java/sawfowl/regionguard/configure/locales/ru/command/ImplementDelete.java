package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Delete;

@ConfigSerializable
public class ImplementDelete implements Delete {

	public ImplementDelete() {}

	@Setting("Description")
	private Component description = deserialize("&6Удаление региона.");
	@Setting("ConfirmRequest")
	private Component confirmRequest = deserialize("&eВы уверены, что хотите удалить регион в своем местоположении? Нажмите на это сообщение, чтобы подтвердить.");
	@Setting("Regen")
	private Component regen = deserialize("&eВНИМАНИЕ!!! Территория в регионе будет восстановлена до первоначального состояния!");
	@Setting("SuccesWhithChilds")
	private Component succesWhithChilds = deserialize("&aРегион удален. Содержащиеся в нем дочерние регионы также были удалены.");
	@Setting("SuccesChild")
	private Component succesChild = deserialize("&aДочерний регион был удален.");
	@Setting("Success")
	private Component success = deserialize("&aРегион был удален.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getConfirmRequest() {
		return confirmRequest;
	}

	@Override
	public Component getRegen() {
		return regen;
	}

	@Override
	public Component getSuccesWhithChilds() {
		return succesWhithChilds;
	}

	@Override
	public Component getSuccesChild() {
		return succesChild;
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
