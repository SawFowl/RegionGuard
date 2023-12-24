package sawfowl.regionguard.configure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.data.FlagValueImpl;

@ConfigSerializable
public class DefaultFlags {

	public DefaultFlags(){}

	@Setting("ClaimFlags")
	private Map<String, Set<FlagValueImpl>> claimFlags = claimDefaultFlags();

	@Setting("ArenaFlags")
	private Map<String, Set<FlagValueImpl>> arenaFlags = claimDefaultFlags();

	@Setting("AdminFlags")
	private Map<String, Set<FlagValueImpl>> adminFlags = claimDefaultFlags();

	@Setting("GlobalFlags")
	private Map<String, Set<FlagValueImpl>> globalFlags = globalDefaultFlags();

	private Consumer<DefaultFlags> save;

	public Map<String, Set<FlagValue>> getClaimFlags() {
		return convert(claimFlags);
	}

	public Map<String, Set<FlagValue>> getArenaFlags() {
		return convert(arenaFlags);
	}

	public Map<String, Set<FlagValue>> getAdminFlags() {
		return convert(adminFlags);
	}

	public Map<String, Set<FlagValue>> getGlobalFlags() {
		return convert(globalFlags);
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

	private Map<String, Set<FlagValueImpl>> claimDefaultFlags() {
		Map<String, Set<FlagValueImpl>> claimFlags = new HashMap<String, Set<FlagValueImpl>>();
		claimFlags.put(Flags.BLOCK_BREAK.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.BLOCK_PLACE.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.PISTON_GRIEF.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.ENTITY_DAMAGE.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.ETITY_RIDING.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.INTERACT_BLOCK_PRIMARY.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.INTERACT_BLOCK_SECONDARY.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.INTERACT_ENTITY_PRIMARY.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.INTERACT_ENTITY_SECONDARY.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.INTERACT_ITEM.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.ITEM_PICKUP.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.ITEM_USE.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.PROJECTILE_IMPACT_BLOCK.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.PROJECTILE_IMPACT_ENTITY.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		claimFlags.put(Flags.PORTAL_USE.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(false).build())));
		for(Flags flag : Flags.values()) if(!claimFlags.containsKey(flag.toString())) claimFlags.put(flag.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(true).build())));
		return claimFlags;
	}

	private void setClaimFlags(Map<String, Set<FlagValue>> claimFlags) {
		this.claimFlags = convert2(claimFlags);
	}

	private void setArenaFlags(Map<String, Set<FlagValue>> arenaFlags) {
		this.arenaFlags = convert2(arenaFlags);
	}

	private void setAdminFlags(Map<String, Set<FlagValue>> adminFlags) {
		this.adminFlags = convert2(adminFlags);
	}

	private void setGlobalFlags(Map<String, Set<FlagValue>> globalFlags) {
		this.globalFlags = convert2(globalFlags);
	}

	private Map<String, Set<FlagValueImpl>> globalDefaultFlags() {
		Map<String, Set<FlagValueImpl>> worldFlags = new HashMap<String, Set<FlagValueImpl>>();
		for(Flags flag : Flags.values()) worldFlags.put(flag.toString(), new HashSet<FlagValueImpl>(Arrays.asList((FlagValueImpl) new FlagValueImpl().builder().setValue(true).build())));
		return worldFlags;
	}

	private Map<String, Set<FlagValue>> convert(Map<String, Set<FlagValueImpl>> map) {
		return map.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().stream().map(value -> (FlagValue) value).collect(Collectors.toSet())));
	}

	private Map<String, Set<FlagValueImpl>> convert2(Map<String, Set<FlagValue>> map) {
		return map.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().stream().map(value -> (FlagValueImpl) value).collect(Collectors.toSet())));
	}

}
