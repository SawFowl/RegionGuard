package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Info;
import sawfowl.regionguard.configure.locales.def.command.info.ImplementButtons;
import sawfowl.regionguard.utils.Placeholders;

@ConfigSerializable
public class ImplementInfo implements Info {

	public ImplementInfo() {}

	@Setting("Buttons")
	private ImplementButtons buttons = new ImplementButtons();
	@Setting("Description")
	private Component description = deserialize("&6Information about the region.");
	@Setting("Header")
	private Component header = deserialize(" &bRegion Info ");
	@Setting("Padding")
	private Component padding = deserialize("&b=");
	@Setting("UUID")
	private Component uuid = deserialize("&eRegion UUID&f: &2" + Placeholders.UUID);
	@Setting("Name")
	private Component name = deserialize("&eRegion name&f: &2" + Placeholders.NAME);
	@Setting("Type")
	private Component type = deserialize("&eRegion type&f: &2" + Placeholders.TYPE);
	@Setting("Created")
	private Component created = deserialize("&eCreated&f: &2" + Placeholders.DATE);
	@Setting("Owner")
	private Component owner = deserialize("&eOwner&f: &2" + Placeholders.NAME);
	@Setting("OwnerUUID")
	private Component ownerUUID = deserialize("&eOwner UUID&f: &2" + Placeholders.UUID);
	@Setting("Members")
	private Component members = deserialize("&eMembers&f: &2" + Placeholders.SIZE);
	@Setting("Min")
	private Component min = deserialize("&eMinimal position&f: &2" + Placeholders.POS);
	@Setting("Max")
	private Component max = deserialize("&eMaximal position&f: &2" + Placeholders.POS);
	@Setting("Selector")
	private Component selector = deserialize("&eSelection type&f: &2" + Placeholders.TYPE);

	@Override
	public Buttons getButtons() {
		return buttons;
	}

	@Override
	public Component getDescription() {
		return description;
	}

	@Override
	public Component getHeader() {
		return header;
	}

	@Override
	public Component getPadding() {
		return padding;
	}

	@Override
	public Component getUUID(Region region) {
		return replace(uuid, Placeholders.UUID, region.getUniqueId().toString());
	}

	@Override
	public Component getName(Component name) {
		return replace(this.name, Placeholders.NAME, name);
	}

	@Override
	public Component getType(Region region) {
		return replace(type, Placeholders.TYPE, region.getType());
	}

	@Override
	public Component getCreated(Component created) {
		return replace(created, Placeholders.DATE, created);
	}

	@Override
	public Component getOwner(Region region) {
		return replace(owner, Placeholders.NAME, region.getOwnerName());
	}

	@Override
	public Component getOwnerUUID(Region region) {
		return replace(ownerUUID, Placeholders.UUID, region.getOwnerUUID());
	}

	@Override
	public Component getMembers(Region region) {
		return replace(members, Placeholders.SIZE, region.getTotalMembers());
	}

	@Override
	public Component getMin(Region region) {
		return replace(min, Placeholders.POS, region.getCuboid() == null ? "-" : region.getCuboid().getMin().toString());
	}

	@Override
	public Component getMax(Region region) {
		return replace(max, Placeholders.POS, region.getCuboid() == null ? "-" : region.getCuboid().getMax().toString());
	}

	@Override
	public Component getSelector(Region region) {
		return replace(selector, Placeholders.TYPE, region.getCuboid() == null ? "" :region.getCuboid().getSelectorType().toString());
	}

}
