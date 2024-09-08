package sawfowl.regionguard.configure.configs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.Region;

@ConfigSerializable
public class DefaultFlags {

	public DefaultFlags(){}

	@Setting("ClaimFlags")
	private Map<String, Set<FlagValue>> claimFlags = claimDefaultFlags();

	@Setting("ArenaFlags")
	private Map<String, Set<FlagValue>> arenaFlags = claimDefaultFlags();

	@Setting("AdminFlags")
	private Map<String, Set<FlagValue>> adminFlags = claimDefaultFlags();

	@Setting("GlobalFlags")
	private Map<String, Set<FlagValue>> globalFlags = globalDefaultFlags();

	private Consumer<DefaultFlags> save;

	public Map<String, Set<FlagValue>> getClaimFlags() {
		return claimFlags;
	}

	public Map<String, Set<FlagValue>> getArenaFlags() {
		return arenaFlags;
	}

	public Map<String, Set<FlagValue>> getAdminFlags() {
		return adminFlags;
	}

	public Map<String, Set<FlagValue>> getGlobalFlags() {
		return globalFlags;
	}

	public void setDefaultFlags(Region region) {
		if(region.isGlobal()) setGlobalFlags(region.getFlags());
		if(region.isArena()) setArenaFlags(region.getFlags());
		if(region.isBasicClaim()) setClaimFlags(region.getFlags());
		if(region.isAdmin()) setAdminFlags(region.getFlags());
		if(save != null) save.accept(this);
	}

	public void setSaveConsumer(Consumer<DefaultFlags> save) {
		this.save = save;
	}

	private Map<String, Set<FlagValue>> claimDefaultFlags() {
		Map<String, Set<FlagValue>> claimFlags = new HashMap<String, Set<FlagValue>>();
		claimFlags.put(Flags.BLOCK_BREAK.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.BLOCK_PLACE.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.PISTON_GRIEF.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.ENTITY_DAMAGE.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.ETITY_RIDING.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.INTERACT_BLOCK_PRIMARY.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.INTERACT_BLOCK_SECONDARY.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.INTERACT_ENTITY_PRIMARY.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.INTERACT_ENTITY_SECONDARY.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.INTERACT_ITEM.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.ITEM_PICKUP.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.ITEM_USE.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.PROJECTILE_IMPACT_BLOCK.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.PROJECTILE_IMPACT_ENTITY.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		claimFlags.put(Flags.PORTAL_USE.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(false).build())));
		for(Flags flag : Flags.values()) if(!claimFlags.containsKey(flag.toString())) claimFlags.put(flag.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(true).build())));
		return claimFlags;
	}

	private void setClaimFlags(Map<String, Set<FlagValue>> claimFlags) {
		this.claimFlags = claimFlags;
	}

	private void setArenaFlags(Map<String, Set<FlagValue>> arenaFlags) {
		this.arenaFlags = arenaFlags;
	}

	private void setAdminFlags(Map<String, Set<FlagValue>> adminFlags) {
		this.adminFlags = adminFlags;
	}

	private void setGlobalFlags(Map<String, Set<FlagValue>> globalFlags) {
		this.globalFlags = globalFlags;
	}

	private Map<String, Set<FlagValue>> globalDefaultFlags() {
		Map<String, Set<FlagValue>> worldFlags = new HashMap<String, Set<FlagValue>>();
		for(Flags flag : Flags.values()) worldFlags.put(flag.toString(), new HashSet<FlagValue>(Arrays.asList(FlagValue.builder().setValue(true).build())));
		return worldFlags;
	}

}
