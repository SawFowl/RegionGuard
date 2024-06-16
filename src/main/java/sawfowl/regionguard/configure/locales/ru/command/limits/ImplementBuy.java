package sawfowl.regionguard.configure.locales.ru.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Buy;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementBuy implements Buy {

	public ImplementBuy() {}

	@Setting("Description")
	private Component description = deserialize("&6Увеличение лимитов за игровую валюту.");
	@Setting("Blocks")
	private ImplementLimit blocks = new ImplementLimit("&6Покупка лимита блоков за игровую валюту.", "&aВы увеличичили свой лимит блоков на &b" + Placeholders.SIZE + "&a. Ваш текущий лимит блоков: &b" + Placeholders.VOLUME + "&a.");
	@Setting("Claims")
	private ImplementLimit claims = new ImplementLimit("&6Покупка лимита приватов за игровую валюту.", "&aВы увеличичили свой лимит приватов на &b" + Placeholders.SIZE + "&a. Ваш текущий лимит приватов: &b" + Placeholders.VOLUME + "&a.");
	@Setting("Subdivisions")
	private ImplementLimit subdivisions = new ImplementLimit("&6Покупка лимита дочерних регионов за игровую валюту.", "&aВы увеличичили свой лимит дочерних регионов на &b" + Placeholders.SIZE + "&a. Ваш текущий лимит дочерних регионов: &b" + Placeholders.VOLUME + "&a.");
	@Setting("Members")
	private ImplementLimit members = new ImplementLimit("&6Покупка лимита участников региона за игровую валюту.", "&aВы увеличичили свой лимит участников региона на &b" + Placeholders.SIZE + "&a. Ваш текущий лимит участников: &b" + Placeholders.VOLUME + "&a.");

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
