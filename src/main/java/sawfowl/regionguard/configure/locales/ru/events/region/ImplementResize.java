package sawfowl.regionguard.configure.locales.ru.events.region;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Events.RegionEvents.Resize;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementResize implements Resize {

	public ImplementResize() {}

	@Setting("IncorrectCords")
	private Component incorrectCords = deserialize("&cОдна из координат XYZ(3D)/XZ(2D) новой точки совпадает с такой же координатой противоположного угла области. Выберите другую позицию.");
	@Setting("SmallVolume")
	private Component smallVolume = deserialize("&cВыбранный объем слишком мал. Чтобы завершить операцию, выберите область больше &b" + PlaceholderKeys.VOLUME + "&c.");
	@Setting("LimitBlocks")
	private Component limitBlocks = deserialize("&cНовый размер слишком велик. Вы выбрали блоков: &b" + PlaceholderKeys.SELECTED + "&c.\nДоступно блоков: &b" + PlaceholderKeys.VOLUME + "&c.");
	@Setting("Intersect")
	private Component intersect = deserialize("&cНевозможно изменить размер региона, так как это приведет к ее пересечению с другим регионом.");
	@Setting("ChildOut")
	private Component childOut = deserialize("&cИзменить размер региона невозможно, так как это приведет к тому, что дочерний регион окажется за границами родительского.");
	@Setting("Start")
	private Component start = deserialize("&dНажмите еще раз в другом месте, чтобы изменить размер.");
	@Setting("Finish")
	private Component finish = deserialize("&dРазмер региона был изменен.");

	@Override
	public Component getIncorrectCords() {
		return incorrectCords;
	}

	@Override
	public Component getSmallVolume(int size) {
		return replace(smallVolume, PlaceholderKeys.VOLUME, size);
	}

	@Override
	public Component getLimitBlocks(long size, long limit) {
		return replace(smallVolume, array(PlaceholderKeys.SELECTED, PlaceholderKeys.VOLUME), size, limit);
	}

	@Override
	public Component getIntersect() {
		return intersect;
	}

	@Override
	public Component getChildOut() {
		return childOut;
	}

	@Override
	public Component getStart() {
		return start;
	}

	@Override
	public Component getFinish() {
		return finish;
	}

}
