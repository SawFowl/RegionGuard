package sawfowl.regionguard.configure;

import java.io.IOException;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class WandItem {

	public WandItem(){}

	@Setting("ItemQuantity")
	private int itemQuantity = 1;
	@Setting("ItemType")
	private String itemType = "minecraft:stone_axe";
	@Setting("Nbt")
	private String nbt = "{\"Damage\":131}";

	public ItemStack getWandItem() {
		ItemStack itemStack = ItemStack.of(RegistryTypes.ITEM_TYPE.get().findValue(ResourceKey.resolve(itemType)).orElse(ItemTypes.STONE_AXE.get()), itemQuantity);
		if(nbt != null && nbt.length() != 0) try {
			itemStack = ItemStack.builder().fromContainer(itemStack.toContainer().set(DataQuery.of("UnsafeData"), DataFormats.JSON.get().read(nbt))).build();
		} catch (InvalidDataException | IOException e) {
		}
		return itemStack;
	}

}
