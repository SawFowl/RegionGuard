package sawfowl.regionguard.configure.locales.ru.events.region;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.locales.abstractlocale.Events.RegionEvents.Create;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementCreate implements Create {

	public ImplementCreate() {}

	@Setting("NoAdminPerm")
	private Component noAdminPerm = deserialize("&cУ вас нет разрешения на создание дочерних регионов в административном регионе.");
	@Setting("PositionLocked")
	private Component positionLocked = deserialize("&cЭта позиция уже принадлежит другому игроку.");
	@Setting("LimitRegions")
	private Component limitRegions = deserialize("&cВы достигли предела доступных вам регионов. Ваш лимит: &b" + Placeholders.SIZE + "&c. Максимум регионов: &b" + Placeholders.MAX + "&c.");
	@Setting("LimitSubdivisions")
	private Component limitSubdivisions = deserialize("&cВы достигли лимита дочерних регионов в текущем регионе. Ваш лимит: &b" + Placeholders.SIZE + "&c. Максимум дочерних регионов: &b" + Placeholders.MAX + "&c.");
	@Setting("LimitBlocks")
	private Component limitBlocks = deserialize("&cВы выбрали слишком большой объем: &b" + Placeholders.SELECTED + "&c. Выберите меньшую область, чтобы создать регион. Доступно блоков &b" + Placeholders.MAX + " &c.");
	@Setting("SmallVolume")
	private Component smallVolume = deserialize("&cВыбранный объем слишком мал. Чтобы завершить операцию, выберите область больше &b" + Placeholders.VOLUME + "&c блоков.");
	@Setting("IncorrectCords")
	private Component incorrectCords = deserialize("&cТочки имеют совпадение по одной из координат. Для кубоидов недопустимо совпадение по координатам XYZ. Для плоских областей недопустимо совпадение по координатам XZ.");
	@Setting("Intersect")
	private Component intersect = deserialize("&cСоздаваемая область частично или полностью перекрывает существующую область. Выберите другие позиции.");
	@Setting("Cancel")
	private Component cancel = deserialize("&cСоздание региона было отменено.");
	@Setting("WrongSubdivisionPositions")
	private Component wrongSubdivisionPositions = deserialize("&cДочерний регион не может быть создан, поскольку он перекрывает границы родительского региона. Базовые регионы также не могут перекрываться.");
	@Setting("SetPos")
	private Component setPos = deserialize("&dПозиция &b" + Placeholders.NUMBER + " &d установленна по координатам &b" + Placeholders.POS + "&d.");
	@Setting("Success")
	private Component success = deserialize("&aРегион создан. Объем: &b" + Placeholders.VOLUME + "&a. Введите /rg claim для привата территории.");
	@Setting("SuccessSubdivision")
	private Component successSubdivision = deserialize("&aДочерний регион создан. Объем: &b" + Placeholders.VOLUME + "&a.");

	@Override
	public Component getNoAdminPerm() {
		return noAdminPerm;
	}

	@Override
	public Component getPositionLocked() {
		return positionLocked;
	}

	@Override
	public Component getLimitRegions(long size, long limit) {
		return replace(limitRegions, array(Placeholders.SIZE, Placeholders.MAX), size, limit);
	}

	@Override
	public Component getLimitSubdivisions(long size, long limit) {
		return replace(limitSubdivisions, array(Placeholders.SIZE, Placeholders.MAX), size, limit);
	}

	@Override
	public Component getLimitBlocks(long size, long limit) {
		return replace(limitBlocks, array(Placeholders.SELECTED, Placeholders.MAX), size, limit);
	}

	@Override
	public Component getSmallVolume(int size) {
		return replace(smallVolume, Placeholders.VOLUME, size);
	}

	@Override
	public Component getIncorrectCords() {
		return incorrectCords;
	}

	@Override
	public Component getIntersect() {
		return intersect;
	}

	@Override
	public Component getCancel() {
		return cancel;
	}

	@Override
	public Component getWrongSubdivisionPositions() {
		return wrongSubdivisionPositions;
	}

	@Override
	public Component getSetPos(int pos, Vector3i cords) {
		return replace(setPos, array(Placeholders.NUMBER, Placeholders.POS), pos, cords);
	}

	@Override
	public Component getSuccess(Region region) {
		return replace(success, Placeholders.VOLUME, region.getCuboid().getSize());
	}

	@Override
	public Component getSuccessSubdivision(Region region) {
		return replace(successSubdivision, Placeholders.VOLUME, region.getCuboid().getSize());
	}

}
