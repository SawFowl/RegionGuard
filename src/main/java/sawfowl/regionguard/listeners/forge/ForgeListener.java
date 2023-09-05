package sawfowl.regionguard.listeners.forge;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.registry.RegistryTypes;

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
		return entity.getEncodeId();
	}

	private String getEntityCategory(Entity entity) {
		String id = getEntityId(entity);
		if(id == null) return "all";
		Optional<EntityType<org.spongepowered.api.entity.Entity>> optType = EntityTypes.registry().findValue(ResourceKey.resolve(id));
		return optType.isPresent() ? Sponge.game().registry(RegistryTypes.ENTITY_CATEGORY).valueKey(optType.get().category()).asString() : "all";
	}

}
