package sawfowl.regionguard.configure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.Region;

@ConfigSerializable
public class DefaultFlags {

	@Setting("ClaimFlags")
	private Map<String, List<FlagValue>> claimFlags = claimDefaultFlags();

	@Setting("ArenaFlags")
	private Map<String, List<FlagValue>> arenaFlags = claimDefaultFlags();

	@Setting("AdminFlags")
	private Map<String, List<FlagValue>> adminFlags = claimDefaultFlags();

	@Setting("GlobalFlags")
	private Map<String, List<FlagValue>> globalFlags = globalDefaultFlags();

	public Map<String, List<FlagValue>> getClaimFlags() {
		return claimFlags;
	}

	public void setClaimFlags(Map<String, List<FlagValue>> claimFlags) {
		this.claimFlags = claimFlags;
	}

	public Map<String, List<FlagValue>> getArenaFlags() {
		return arenaFlags;
	}

	public void setArenaFlags(Map<String, List<FlagValue>> arenaFlags) {
		this.arenaFlags = arenaFlags;
	}

	public Map<String, List<FlagValue>> getAdminFlags() {
		return adminFlags;
	}

	public void setAdminFlags(Map<String, List<FlagValue>> adminFlags) {
		this.adminFlags = adminFlags;
	}

	public Map<String, List<FlagValue>> getGlobalFlags() {
		return globalFlags;
	}

	public void setGlobalFlags(Map<String, List<FlagValue>> globalFlags) {
		this.globalFlags = globalFlags;
	}

	public void setDefaultFlags(Region region) {
		if(region.isGlobal()) setGlobalFlags(region.getFlags());
		if(region.isArena()) setArenaFlags(region.getFlags());
		if(region.isBasicClaim()) setClaimFlags(region.getFlags());
		if(region.isAdmin()) setAdminFlags(region.getFlags());
	}

	private Map<String, List<FlagValue>> claimDefaultFlags() {
		Map<String, List<FlagValue>> claimFlags = new HashMap<String, List<FlagValue>>();
		claimFlags.put(Flags.BLOCK_BREAK.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.BLOCK_PLACE.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.PISTON_GRIEF.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.ENTITY_DAMAGE.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.ETITY_RIDING.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.INTERACT_BLOCK_PRIMARY.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.INTERACT_BLOCK_SECONDARY.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.INTERACT_ENTITY_PRIMARY.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.INTERACT_ENTITY_SECONDARY.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.INTERACT_ITEM.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.ITEM_PICKUP.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.ITEM_USE.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.PROJECTILE_IMPACT_BLOCK.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.PROJECTILE_IMPACT_ENTITY.toString(), Arrays.asList(new FlagValue(false, null, null)));
		claimFlags.put(Flags.PORTAL_USE.toString(), Arrays.asList(new FlagValue(false, null, null)));
		for(Flags flag : Flags.values()) if(!claimFlags.containsKey(flag.toString())) claimFlags.put(flag.toString(), Arrays.asList(new FlagValue(true, null, null)));
		return claimFlags;
	}

	private Map<String, List<FlagValue>> globalDefaultFlags() {
		Map<String, List<FlagValue>> worldFlags = new HashMap<String, List<FlagValue>>();
		for(Flags flag : Flags.values()) worldFlags.put(flag.toString(), Arrays.asList(new FlagValue(true, null, null)));
		return worldFlags;
	}

}
