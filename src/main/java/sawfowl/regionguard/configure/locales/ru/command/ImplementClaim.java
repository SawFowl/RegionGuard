package sawfowl.regionguard.configure.locales.ru.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Claim;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementClaim implements Claim {

	public ImplementClaim() {}

	@Setting("Description")
	private Component description = deserialize("&6Заприватить созданный регион.");
	@Setting("RegionNotFound")
	private Component regionNotFound = deserialize("&cНет региона доступного для создания привата.");
	@Setting("WorldNotFound")
	private Component worldNotFound = deserialize("&cУ региона не загружен или удален мир: &b" + Placeholders.WORLD);
	@Setting("Intersect")
	private Component intersect = deserialize("&cРегион касается другого уже существующего региона с границами от &b" + Placeholders.MIN + " &c до &b" + Placeholders.MAX + "&c. Выделите другой регион.");
	@Setting("Success")
	private Component success = deserialize("&aВы успешно заприватили регион.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getRegionNotFound() {
		return regionNotFound;
	}

	@Override
	public Component getWorldNotFound(String world) {
		return replace(worldNotFound, Placeholders.WORLD, world);
	}

	@Override
	public Component getIntersect(Vector3i min, Vector3i max) {
		return replace(intersect, array(Placeholders.MIN, Placeholders.MAX), min, max);
	}

	@Override
	public Component getSuccess() {
		return success;
	}

}
