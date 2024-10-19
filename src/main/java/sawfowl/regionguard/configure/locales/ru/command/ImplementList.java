package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.List;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementList implements List {

	public ImplementList() {}

	@Setting("Description")
	private Component description = deserialize("&6Показ списка регионов.");
	@Setting("Title")
	private Component title = deserialize("&3Регионы: " + PlaceholderKeys.PLAYER);
	@Setting("Padding")
	private Component padding = deserialize("&3=");
	@Setting("EmptyOther")
	private Component emptyOther = deserialize("&cУ игрока нет регионов.");
	@Setting("EmptySelf")
	private Component emptySelf = deserialize("&cУ вас нет ни одного региона.");
	@Setting("TeleportNotSafe")
	private Component teleportNotSafe = deserialize("&cЭто место небезопасно. Вы уверены, что хотите телепортироваться? Нажмите на это сообщение, чтобы подтвердить.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getTitle(String player) {
		return replace(title, PlaceholderKeys.PLAYER, player);
	}

	@Override
	public Component getPadding() {
		return padding;
	}

	@Override
	public Component getEmptyOther() {
		return emptyOther;
	}

	@Override
	public Component getEmptySelf() {
		return emptySelf;
	}

	@Override
	public Component getTeleportNotSafe() {
		return teleportNotSafe;
	}

}
