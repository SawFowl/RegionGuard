package sawfowl.regionguard.configure.locales.def.command;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.locales.abstractlocale.Command.Info;
import sawfowl.regionguard.configure.locales.def.command.info.ImplementButtons;
import sawfowl.regionguard.utils.PlaceholderKeys;

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
	private Component uuid = deserialize("&eRegion UUID&f: &2" + PlaceholderKeys.UUID);
	@Setting("Name")
	private Component name = deserialize("&eRegion name&f: &2" + PlaceholderKeys.NAME);
	@Setting("Type")
	private Component type = deserialize("&eRegion type&f: &2" + PlaceholderKeys.TYPE);
	@Setting("Created")
	private Component created = deserialize("&eCreated&f: &2" + PlaceholderKeys.DATE);
	@Setting("Owner")
	private Component owner = deserialize("&eOwner&f: &2" + PlaceholderKeys.NAME);
	@Setting("OwnerUUID")
	private Component ownerUUID = deserialize("&eOwner UUID&f: &2" + PlaceholderKeys.UUID);
	@Setting("Members")
	private Component members = deserialize("&eMembers&f: &2" + PlaceholderKeys.SIZE);
	@Setting("Min")
	private Component min = deserialize("&eMinimal position&f: &2" + PlaceholderKeys.POS);
	@Setting("Max")
	private Component max = deserialize("&eMaximal position&f: &2" + PlaceholderKeys.POS);
	@Setting("Selector")
	private Component selector = deserialize("&eSelection type&f: &2" + PlaceholderKeys.TYPE);

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
		return replace(uuid, PlaceholderKeys.UUID, region.getUniqueId().toString());
	}

	@Override
	public Component getName(Component name) {
		return replace(this.name, PlaceholderKeys.NAME, name);
	}

	@Override
	public Component getType(Region region) {
		return replace(type, PlaceholderKeys.TYPE, region.getType());
	}

	@Override
	public Component getCreated(String created) {
		return replace(this.created, PlaceholderKeys.DATE, created);
	}

	@Override
	public Component getOwner(Region region) {
		return replace(owner, PlaceholderKeys.NAME, region.getOwnerName());
	}

	@Override
	public Component getOwnerUUID(Region region) {
		return replace(ownerUUID, PlaceholderKeys.UUID, region.getOwnerUUID());
	}

	@Override
	public Component getMembers(Region region) {
		return replace(members, PlaceholderKeys.SIZE, region.getTotalMembers());
	}

	@Override
	public Component getMin(Region region) {
		return replace(min, PlaceholderKeys.POS, region.getCuboid() == null ? "-" : region.getCuboid().getMin().toString());
	}

	@Override
	public Component getMax(Region region) {
		return replace(max, PlaceholderKeys.POS, region.getCuboid() == null ? "-" : region.getCuboid().getMax().toString());
	}

	@Override
	public Component getSelector(Region region) {
		return replace(selector, PlaceholderKeys.TYPE, region.getCuboid() == null ? "" :region.getCuboid().getSelectorType().toString());
	}

}
