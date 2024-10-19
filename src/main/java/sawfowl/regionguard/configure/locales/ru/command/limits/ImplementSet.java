package sawfowl.regionguard.configure.locales.ru.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Set;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementSet implements Set {

	public ImplementSet() {}

	@Setting("Description")
	private Component description = deserialize("&6Изменение лимитов игрока.");
	@Setting("LessThanZero")
	private Component lessThanZero = deserialize("&cНужно ввести значение больше или равное 0.");
	@Setting("Blocks")
	private ImplementSetLimit blocks = new ImplementSetLimit("&6Изменение лимита блоков у игрока.", "&aВаш лимит блоков был изменен и теперь равен: &b" + PlaceholderKeys.VOLUME + "&a.", "&aВы изменели лимит блоков у игрока &b" + PlaceholderKeys.PLAYER + "&a и установили его в значении: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Claims")
	private ImplementSetLimit claims = new ImplementSetLimit("&6Изменение лимита приватов у игрока.", "&aВаш лимит приватов был изменен и теперь равен: &b" + PlaceholderKeys.VOLUME + "&a.", "&aВы изменели лимит приватов у игрока &b" + PlaceholderKeys.PLAYER + "&a и установили его в значении: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Subdivisions")
	private ImplementSetLimit subdivisions = new ImplementSetLimit("&6Изменение лимита дочерних регионов у игрока.", "&aВаш лимит дочерних регионов был изменен и теперь равен: &b" + PlaceholderKeys.VOLUME + "&a.", "&aВы изменели лимит дочерних регионов у игрока &b" + PlaceholderKeys.PLAYER + "&a и установили его в значении: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Members")
	private ImplementSetLimit members = new ImplementSetLimit("&6Изменение лимита участников региона у игрока.", "&aВаш лимит участников региона был изменен и теперь равен: &b" + PlaceholderKeys.VOLUME + "&a.", "&aВы изменели лимит участников региона у игрока &b" + PlaceholderKeys.PLAYER + "&a и установили его в значении: &b" + PlaceholderKeys.VOLUME + "&a.");

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Limit getBlocksLimit() {
		return blocks;
	}

	@Override
	public Limit getClaimsLimit() {
		return claims;
	}

	@Override
	public Limit getSubdivisionsLimit() {
		return subdivisions;
	}

	@Override
	public Limit getMembersLimit() {
		return members;
	}

	@Override
	public Component getLessThanZero() {
		return lessThanZero;
	}

}
