package sawfowl.regionguard.configure.locales.def.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Sell;
import sawfowl.regionguard.utils.PlaceholderKeys;

@ConfigSerializable
public class ImplementSell implements Sell {

	public ImplementSell() {}

	@Setting("Description")
	private Component description = deserialize("&6Selling the limits for game currency.");
	@Setting("Blocks")
	private ImplementLimit blocks = new ImplementLimit("&6Selling the limit of blocks for game currency.", "&aYou have reduced your block limit by &b" + PlaceholderKeys.SIZE + "&a. Your current blocks limit: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Claims")
	private ImplementLimit claims = new ImplementLimit("&6Selling the limit of claims for game currency.", "&aYou have reduced your claims limit by &b" + PlaceholderKeys.SIZE + "&a. Your current claims limit: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Subdivisions")
	private ImplementLimit subdivisions = new ImplementLimit("&6Selling the limit of subdivisions for game currency.", "&aYou have reduced your subdivisions limit by &b" + PlaceholderKeys.SIZE + "&a. Your current subdivisions limit: &b" + PlaceholderKeys.VOLUME + "&a.");
	@Setting("Members")
	private ImplementLimit members = new ImplementLimit("&6Selling the limit of region members for game currency.", "&aYou have reduced your region members limit by &b" + PlaceholderKeys.SIZE + "&a. Your current members limit: &b" + PlaceholderKeys.VOLUME + "&a.");

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
