package sawfowl.regionguard.configure.locales.ru.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Sell;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementSell implements Sell {

	public ImplementSell() {}

	@Setting("Description")
	private Component description = deserialize("&6Продажа ранее купленных лимитов.");
	@Setting("Blocks")
	private ImplementLimit blocks = new ImplementLimit("&6Продажа лимита блоков.", "&aВы уменьшили свой лимит блоков на &b" + Placeholders.SIZE + "&a. Ваш текущий лимит блоков: &b" + Placeholders.VOLUME + "&a.");
	@Setting("Claims")
	private ImplementLimit claims = new ImplementLimit("&6Продажа лимита приватов.", "&aВы уменьшили свой лимит приватов на &b" + Placeholders.SIZE + "&a. Ваш текущий лимит приватов: &b" + Placeholders.VOLUME + "&a.");
	@Setting("Subdivisions")
	private ImplementLimit subdivisions = new ImplementLimit("&6Продажа лимита дочерних регионов.", "&aВы уменьшили свой лимит дочерних регионов на &b" + Placeholders.SIZE + "&a. Ваш текущий лимит дочерних регионов: &b" + Placeholders.VOLUME + "&a.");
	@Setting("Members")
	private ImplementLimit members = new ImplementLimit("&6Продажа лимита участников региона.", "&aВы уменьшили свой лимит участников региона на &b" + Placeholders.SIZE + "&a. Ваш текущий лимит участников: &b" + Placeholders.VOLUME + "&a.");

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
