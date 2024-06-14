package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.configure.locales.abstractlocale.Command.Limits;
import sawfowl.regionguard.configure.locales.def.command.limits.ImplementBuy;
import sawfowl.regionguard.configure.locales.def.command.limits.ImplementSell;
import sawfowl.regionguard.configure.locales.def.command.limits.ImplementSet;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementLimits implements Limits {

	public ImplementLimits() {}

	@Setting("Buy")
	private ImplementBuy buy = new ImplementBuy();
	@Setting("Sell")
	private ImplementSell sell = new ImplementSell();
	@Setting("Set")
	private ImplementSet set = new ImplementSet();
	@Setting("Description")
	private Component description = deserialize("&6Information about your limits.");
	@Setting("Title")
	private Component title = deserialize("&6" + Placeholders.PLAYER + " &3limits");
	@Setting("Padding")
	private Component padding = deserialize("&3=");
	@Setting("Blocks")
	private Component blocks = deserialize("&eBlocks&f: &2" + Placeholders.CURRENT + "&f/&2" + Placeholders.SIZE + "&f/&2" + Placeholders.MAX);
	@Setting("Claims")
	private Component claims = deserialize("&eClaims&f: &2" + Placeholders.CURRENT + "&f/&2" + Placeholders.SIZE + "&f/&2" + Placeholders.MAX);
	@Setting("Subdivisions")
	private Component subdivisions = deserialize("&eSubdivisions&f: &2" + Placeholders.CURRENT + "&f/&2" + Placeholders.SIZE + "&f/&2" + Placeholders.MAX);
	@Setting("Members")
	private Component members = deserialize("&eMembers&f: &2" + Placeholders.CURRENT + "&f/&2" + Placeholders.SIZE + "&f/&2" + Placeholders.MAX);

	@Override
	public Buy getBuy() {
		return buy;
	}

	@Override
	public Sell getSell() {
		return sell;
	}

	@Override
	public Set getSet() {
		return set;
	}

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getTitle(String player) {
		return replace(title, Placeholders.PLAYER, player);
	}

	@Override
	public Component getPadding() {
		return padding;
	}

	@Override
	public Component getBlocks(long claimed, long limit, long max) {
		return replace(blocks, array(Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX), claimed, limit, max);
	}

	@Override
	public Component getClaims(long claimed, long limit, long max) {
		return replace(claims, array(Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX), claimed, limit, max);
	}

	@Override
	public Component getSubdivisions(long claimed, long limit, long max) {
		return replace(subdivisions, array(Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX), claimed, limit, max);
	}

	@Override
	public Component getMembers(long claimed, long limit, long max) {
		return replace(members, array(Placeholders.CURRENT, Placeholders.SIZE, Placeholders.MAX), claimed, limit, max);
	}

}
