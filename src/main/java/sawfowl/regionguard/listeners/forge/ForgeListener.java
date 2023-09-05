package sawfowl.regionguard.listeners.forge;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;

import sawfowl.regionguard.RegionGuard;

public class ForgeListener {

	protected final RegionGuard plugin;
	public ForgeListener(RegionGuard plugin) {
		this.plugin = plugin;
		MinecraftForge.EVENT_BUS.register(this);
	}

	protected List<String> flagEntityArgs(Entity entity) {
		return Arrays.asList(getEntityId(entity), getEntityCategory(entity), "all");
	}

	private String getEntityId(Entity entity) {
		return entity.getType() == null || entity.getType().getCategory() == null || entity.getType().getCategory().getSerializedName() == null ? "all" : entity.getType().getRegistryName().toString();
	}

	private String getEntityCategory(Entity entity) {
		return entity.getType() == null || entity.getType().getCategory() == null || entity.getType().getCategory().getSerializedName() == null ? "all" : "sponge:" + entity.getType().getCategory().getSerializedName();
	}

}
