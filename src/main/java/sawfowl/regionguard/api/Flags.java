package sawfowl.regionguard.api;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.RegistryTypes;

public enum Flags {

	INTERACT_BLOCK_PRIMARY {
		@Override
		public String toString() {
			return "Interact-Block-Primary";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredBlockType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetBlock() {
			return true;
		}
	},
	INTERACT_BLOCK_SECONDARY {
		@Override
		public String toString() {
			return "Interact-Block-Secondary";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredBlockType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetBlock() {
			return true;
		}
	},
	BLOCK_BREAK {
		@Override
		public String toString() {
			return "Block-Break";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredBlockType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetBlock() {
			return true;
		}
	},
	BLOCK_PLACE {
		@Override
		public String toString() {
			return "Block-Place";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredBlockType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetBlock() {
			return true;
		}
	},
	BLOCK_DECAY {
		@Override
		public String toString() {
			return "Block-Decay";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredBlockType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetBlock() {
			return true;
		}
	},
	BLOCK_GROWTH {
		@Override
		public String toString() {
			return "Block-Growth";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredBlockType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowTargetBlock() {
			return true;
		}
	},
	PISTON {
		@Override
		public String toString() {
			return "Piston";
		}
	},
	PISTON_GRIEF {
		@Override
		public String toString() {
			return "Piston-Grief";
		}
	},
	LIQUID_FLOW {
		@Override
		public String toString() {
			return "Liquid-Flow";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredBlockType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowTargetBlock() {
			return false;
		}
	},
	EXPLOSION {
		@Override
		public String toString() {
			return "Explosion";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
	},
	EXPLOSION_SURFACE {
		@Override
		public String toString() {
			return "Explosion-Surface";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredBlockType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetBlock() {
			return true;
		}
	},
	FIRE_SPREAD {
		@Override
		public String toString() {
			return "Fire-Spread";
		}
	},
	ENTER_CLAIM {
		@Override
		public String toString() {
			return "Enter-Claim";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
	},
	EXIT_CLAIM {
		@Override
		public String toString() {
			return "Exit-Claim";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
	},
	ALLOW_FLY {
		@Override
		public String toString() {
			return "Allow-Fly";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
	},
	PVP {
		@Override
		public String toString() {
			return "PvP";
		}
	},
	KEEP_INVENTORY {
		@Override
		public String toString() {
			return "Keep-Inventory";
		}
	},
	KEEP_EXP {
		@Override
		public String toString() {
			return "Keep-Exp";
		}
	},
	COMMAND_EXECUTE {
		@Override
		public String toString() {
			return "Command-Execute";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			String[] targets = target.split(";");
			for(String command : targets) {
				if(Sponge.server().commandManager().knownAliases().contains(command)) return true;
			}
			return Sponge.server().commandManager().knownAliases().contains(target) || target.equals("all");
		}
	},
	COMMAND_EXECUTE_PVP {
		@Override
		public String toString() {
			return "Command-Execute-PvP";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return Sponge.server().commandManager().knownAliases().contains(target) || target.equals("all");
		}
	},
	INTERACT_ENTITY_PRIMARY {
		@Override
		public String toString() {
			return "Interact-Entity-Primary";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredEntityCategory(target) || registeredEntityType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetEntity() {
			return false;
		}
	},
	INTERACT_ENTITY_SECONDARY {
		@Override
		public String toString() {
			return "Interact-Entity-Secondary";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredEntityCategory(target) || registeredEntityType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetEntity() {
			return false;
		}
	},
	COLLIDE_ENTITY {
		@Override
		public String toString() {
			return "Collide-Entity";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredEntityCategory(target) || registeredEntityType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetEntity() {
			return false;
		}
	},
	ENTITY_DAMAGE {
		@Override
		public String toString() {
			return "Entity-Damage";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || registeredDamageType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredEntityCategory(target) || registeredEntityType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowSourceDamageType() {
			return true;
		}
		@Override
		public boolean isAllowTargetEntity() {
			return false;
		}
	},
	ETITY_RIDING {
		@Override
		public String toString() {
			return "Entity-Riding";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredEntityCategory(target) || registeredEntityType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetEntity() {
			return false;
		}
	},
	ENTITY_SPAWN {
		@Override
		public String toString() {
			return "Entity-Spawn";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredEntityCategory(target) || registeredEntityType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetEntity() {
			return true;
		}
	},
	ENTITY_TELEPORT_FROM {
		@Override
		public String toString() {
			return "Entity-Teleport-From";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTargetEntity() {
			return false;
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetItem() {
			return false;
		}
		@Override
		public boolean isAllowTargetBlock() {
			return false;
		}
	},
	ENTITY_TELEPORT_TO {
		@Override
		public String toString() {
			return "Entity-Teleport-To";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
	},
	INTERACT_ITEM {
		@Override
		public String toString() {
			return "Interact-Item";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredItemType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetItem() {
			return false;
		}
	},
	ITEM_DROP {
		@Override
		public String toString() {
			return "Item-Drop";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredItemType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetItem() {
			return false;
		}
	},
	ITEM_PICKUP {
		@Override
		public String toString() {
			return "Item-Pickup";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredItemType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetItem() {
			return false;
		}
	},
	ITEM_SPAWN {
		@Override
		public String toString() {
			return "Item-Spawn";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredItemType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetItem() {
			return false;
		}
	},
	EXP_SPAWN {
		@Override
		public String toString() {
			return "Exp-Spawn";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
	},
	ITEM_USE {
		@Override
		public String toString() {
			return "Item-Use";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredItemType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetItem() {
			return false;
		}
	},
	PROJECTILE_IMPACT_BLOCK {
		@Override
		public String toString() {
			return "Projectile-Impact-Block";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredBlockType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetBlock() {
			return false;
		}
	},
	PROJECTILE_IMPACT_ENTITY {
		@Override
		public String toString() {
			return "Projectile-Impact-Entity";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowTarget(String target) {
			return registeredEntityCategory(target) || registeredEntityType(target) || target.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
		@Override
		public boolean isAllowTargetEntity() {
			return false;
		}
	},
	PORTAL_USE {
		@Override
		public String toString() {
			return "Portal-Use";
		}
		@Override
		public boolean isAllowArgs() {
			return true;
		}
		@Override
		public boolean isAllowSource(String source) {
			return registeredEntityCategory(source) || registeredEntityType(source) || source.equals("all");
		}
		@Override
		public boolean isAllowSourceEntity() {
			return true;
		}
	};

	public static Flags valueOfName(String string) {
		for(Flags type : Flags.values()) {
			if(type.toString().equalsIgnoreCase(string)) return type;
		}
		return null;
	}

	public boolean isAllowArgs() {
		return false;
	}

	public boolean isAllowSource(String source) {
		return false;
	}

	public boolean isAllowTarget(String target) {
		return false;
	}

	public boolean isAllowSourceEntity() {
		return false;
	}

	public boolean isAllowSourceDamageType() {
		return false;
	}

	public boolean isAllowTargetEntity() {
		return false;
	}

	public boolean isAllowTargetItem() {
		return false;
	}

	public boolean isAllowTargetBlock() {
		return false;
	}

	public static boolean registeredItemType(String string) {
		return string != null && Sponge.game().registry(RegistryTypes.ITEM_TYPE).findValue(ResourceKey.resolve(string)).isPresent();
	}

	public static boolean registeredEntityType(String string) {
		return string != null && Sponge.game().registry(RegistryTypes.ENTITY_TYPE).findValue(ResourceKey.resolve(string)).isPresent();
	}

	public static boolean registeredEntityCategory(String string) {
		return string != null && Sponge.game().registry(RegistryTypes.ENTITY_CATEGORY).findValue(ResourceKey.resolve(string)).isPresent();
	}

	public static boolean registeredBlockType(String string) {
		return string != null && Sponge.game().registry(RegistryTypes.BLOCK_TYPE).findValue(ResourceKey.resolve(string)).isPresent();
	}

	public static boolean registeredDamageType(String string) {
		return string != null && Sponge.game().registry(RegistryTypes.DAMAGE_TYPE).findValue(ResourceKey.resolve(string)).isPresent();
	}

}
