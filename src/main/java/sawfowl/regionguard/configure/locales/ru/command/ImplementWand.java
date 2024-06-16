package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Wand;

@ConfigSerializable
public class ImplementWand implements Wand {

	public ImplementWand() {}

	@Setting("Description")
	private Component description = deserialize("&6Получение предмета для создания регионов.");
	@Setting("Exist")
	private Component exist = deserialize("&cУ вас уже есть инструмент для создания регионов и получения информации о них.");
	@Setting("FullInventory")
	private Component fullInventory = deserialize("&cУ вас нет места в инвентаре. Освободите хотя бы 1 слот.");
	@Setting("Success")
	private Component success = deserialize("&dЛевой кнопкой мыши выделите две точки в мире, чтобы создать регион, затем введите команду '/rg claim', чтобы заприватить этот регион.\n&dДочерние регионы добавляются автоматически.\n&dЩелчок правой кнопкой мыши на блоке отобразит краткую информацию о регионе.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getExist() {
		return exist;
	}

	@Override
	public Component getFullInventory() {
		return fullInventory;
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
