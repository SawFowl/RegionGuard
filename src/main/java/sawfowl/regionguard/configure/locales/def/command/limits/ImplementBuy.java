package sawfowl.regionguard.configure.locales.def.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Buy;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementBuy implements Buy {

	public ImplementBuy() {}

	@Setting("Description")
	private Component description = deserialize("&6Payment in game currency to increase the limits.");
	@Setting("Blocks")
	private ImplementLimit blocks = new ImplementLimit("&6Payment in game currency to increase the limit of blocks.", "&aYou have increased your blocks limit by &b" + PlaceholderKeys.SIZE + "&a. Your current blocks limit: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Claims")
	private ImplementLimit claims = new ImplementLimit("&6Payment in game currency to increase the limit of claims.", "&aYou have increased your claims limit by &b" + PlaceholderKeys.SIZE + "&a. Your current claims limit: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Subdivisions")
	private ImplementLimit subdivisions = new ImplementLimit("&6Payment in game currency to increase the limit of subdivisions.", "&aYou have increased your subdivisions limit by &b" + PlaceholderKeys.SIZE + "&a. Your current subdivisions limit: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Members")
	private ImplementLimit members = new ImplementLimit("&6Payment in game currency to increase the limit of region members.", "&aYou have increased your region members limit by &b" + PlaceholderKeys.SIZE + "&a. Your current members limit: &b" + PlaceholderKeys.VOLUME + "&a.");

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
