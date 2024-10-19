package sawfowl.regionguard.configure.locales.ru.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Sell;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementSell implements Sell {

	public ImplementSell() {}

	@Setting("Description")
	private Component description = deserialize("&6Продажа ранее купленных лимитов.");
	@Setting("Blocks")
	private ImplementLimit blocks = new ImplementLimit("&6Продажа лимита блоков.", "&aВы уменьшили свой лимит блоков на &b" + PlaceholderKeys.SIZE + "&a. Ваш текущий лимит блоков: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Claims")
	private ImplementLimit claims = new ImplementLimit("&6Продажа лимита приватов.", "&aВы уменьшили свой лимит приватов на &b" + PlaceholderKeys.SIZE + "&a. Ваш текущий лимит приватов: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Subdivisions")
	private ImplementLimit subdivisions = new ImplementLimit("&6Продажа лимита дочерних регионов.", "&aВы уменьшили свой лимит дочерних регионов на &b" + PlaceholderKeys.SIZE + "&a. Ваш текущий лимит дочерних регионов: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Members")
	private ImplementLimit members = new ImplementLimit("&6Продажа лимита участников региона.", "&aВы уменьшили свой лимит участников региона на &b" + PlaceholderKeys.SIZE + "&a. Ваш текущий лимит участников: &b" + PlaceholderKeys.VOLUME + "&a.");

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

}
