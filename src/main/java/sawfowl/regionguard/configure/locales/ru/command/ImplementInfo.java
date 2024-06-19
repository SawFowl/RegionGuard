package sawfowl.regionguard.configure.locales.ru.command;

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
	private Component description = deserialize("&6Информация о регионе.");
	@Setting("Header")
	private Component header = deserialize(" &bИнформация о регионе ");
	@Setting("Padding")
	private Component padding = deserialize("&b=");
	@Setting("UUID")
	private Component uuid = deserialize("&eUUID региона&f: &2" + PlaceholderKeys.UUID);
	@Setting("Name")
	private Component name = deserialize("&eИмя региона&f: &2" + PlaceholderKeys.NAME);
	@Setting("Type")
	private Component type = deserialize("&eТип региона&f: &2" + PlaceholderKeys.TYPE);
	@Setting("Created")
	private Component created = deserialize("&eДата создания&f: &2" + PlaceholderKeys.DATE);
	@Setting("Owner")
	private Component owner = deserialize("&eВладелец&f: &2" + PlaceholderKeys.NAME);
	@Setting("OwnerUUID")
	private Component ownerUUID = deserialize("&eUUID владельца&f: &2" + PlaceholderKeys.UUID);
	@Setting("Members")
	private Component members = deserialize("&eУчастников&f: &2" + PlaceholderKeys.SIZE);
	@Setting("Min")
	private Component min = deserialize("&eМинимальная позиция&f: &2" + PlaceholderKeys.POS);
	@Setting("Max")
	private Component max = deserialize("&eМаксимальная позиция&f: &2" + PlaceholderKeys.POS);
	@Setting("Selector")
	private Component selector = deserialize("&eтип выделения&f: &2" + PlaceholderKeys.TYPE);

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
