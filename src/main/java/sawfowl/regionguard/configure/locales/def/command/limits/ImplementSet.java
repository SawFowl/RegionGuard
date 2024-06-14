package sawfowl.regionguard.configure.locales.def.command.limits;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits.Set;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementSet implements Set {

	public ImplementSet() {}

	@Setting("Description")
	private Component description = deserialize("&6Changing player limits.");
	@Setting("LessThanZero")
	private Component lessThanZero = deserialize("&cYou entered a number less than zero.");
	@Setting("Blocks")
	private ImplementSetLimit blocks = new ImplementSetLimit("&6Change the blocks limit of the player.", "&aYour blocks limit has been changed to: &b" + Placeholders.VOLUME + "&a.", "&aYou have changed the blocks limit of player &b" + Placeholders.PLAYER + "&a to: &b" + Placeholders.VOLUME + "&a.");
	@Setting("Claims")
	private ImplementSetLimit claims = new ImplementSetLimit("&6Change the claims limit of the player.", "&aYour claims limit has been changed to: &b" + Placeholders.VOLUME + "&a.", "&aYou have changed the claims limit of player &b" + Placeholders.PLAYER + "&a to: &b" + Placeholders.VOLUME + "&a.");
	@Setting("Subdivisions")
	private ImplementSetLimit subdivisions = new ImplementSetLimit("&6Change the subdivisions limit of the player.", "&aYour subdivisions limit has been changed to: &b" + Placeholders.VOLUME + "&a.", "&aYou have changed the subdivisions limit of player &b" + Placeholders.PLAYER + "&a to: &b" + Placeholders.VOLUME + "&a.");
	@Setting("Members")
	private ImplementSetLimit members = new ImplementSetLimit("&6Changing the limit of members in the player regions.", "&aYour members limit has been changed to: &b" + Placeholders.VOLUME + "&a.", "&aYou have changed the members limit of a player's &b" + Placeholders.PLAYER + "&a region to: &b" + Placeholders.VOLUME + "&a.");

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
