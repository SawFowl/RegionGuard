package sawfowl.regionguard.api;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.RegistryTypes;

import sawfowl.regionguard.api.data.FlagSettings;

public enum Flags {

	INTERACT_BLOCK_PRIMARY {
		@Override
		public String toString() {
			return "Interact-Block-Primary";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	INTERACT_BLOCK_SECONDARY {
		@Override
		public String toString() {
			return "Interact-Block-Secondary";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	BLOCK_BREAK {
		@Override
		public String toString() {
			return "Block-Break";
		}
		@Override
		public FlagSettings getFlagSettings() {
			// TODO Автоматически созданная заглушка метода
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	BLOCK_PLACE {
		@Override
		public String toString() {
			return "Block-Place";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	BLOCK_DECAY {
		@Override
		public String toString() {
			return "Block-Decay";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	BLOCK_GROWTH {
		@Override
		public String toString() {
			return "Block-Growth";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceEntity() {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	PISTON {
		@Override
		public String toString() {
			return "Piston";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceEntity() {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	PISTON_GRIEF {
		@Override
		public String toString() {
			return "Piston-Grief";
		}
		@Override
		public FlagSettings getFlagSettings() {
			// TODO Автоматически созданная заглушка метода
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceEntity() {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	LIQUID_FLOW {
		@Override
		public String toString() {
			return "Liquid-Flow";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceEntity() {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	EXPLOSION {
		@Override
		public String toString() {
			return "Explosion";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	EXPLOSION_SURFACE {
		@Override
		public String toString() {
			return "Explosion-Surface";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	FIRE_SPREAD {
		@Override
		public String toString() {
			return "Fire-Spread";
		}

		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
				
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
				
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				
				@Override
				public boolean isAllowSourceEntity() {
					return false;
				}
				
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				
				@Override
				public boolean isAllowSource(String source) {
					return false;
				}
				
				@Override
				public boolean isAllowArgs() {
					return false;
				}
			};
		}
	},
	ENTER_CLAIM {
		@Override
		public String toString() {
			return "Enter-Claim";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	EXIT_CLAIM {
		@Override
		public String toString() {
			return "Exit-Claim";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	ALLOW_FLY {
		@Override
		public String toString() {
			return "Allow-Fly";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	PVP {
		@Override
		public String toString() {
			return "PvP";
		}

		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {

				@Override
				public boolean isAllowArgs() {
					return false;
				}

				@Override
				public boolean isAllowSource(String source) {
					return false;
				}

				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}

				@Override
				public boolean isAllowSourceEntity() {
					return false;
				}

				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}

				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}

				@Override
				public boolean isAllowTargetItem() {
					return false;
				}

				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
				
			};
		}
	},
	KEEP_INVENTORY {
		@Override
		public String toString() {
			return "Keep-Inventory";
		}

		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
				
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
				
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				
				@Override
				public boolean isAllowSourceEntity() {
					return false;
				}
				
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				
				@Override
				public boolean isAllowSource(String source) {
					return false;
				}
				
				@Override
				public boolean isAllowArgs() {
					return false;
				}
			};
		}
	},
	KEEP_EXP {
		@Override
		public String toString() {
			return "Keep-Exp";
		}

		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
				
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
				
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				
				@Override
				public boolean isAllowSourceEntity() {
					return false;
				}
				
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				
				@Override
				public boolean isAllowSource(String source) {
					return false;
				}
				
				@Override
				public boolean isAllowArgs() {
					return false;
				}
			};
		}
	},
	COMMAND_EXECUTE {
		@Override
		public String toString() {
			return "Command-Execute";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceEntity() {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	COMMAND_EXECUTE_PVP {
		@Override
		public String toString() {
			return "Command-Execute-PvP";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceEntity() {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	INTERACT_ENTITY_PRIMARY {
		@Override
		public String toString() {
			return "Interact-Entity-Primary";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	INTERACT_ENTITY_SECONDARY {
		@Override
		public String toString() {
			return "Interact-Entity-Secondary";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	COLLIDE_ENTITY {
		@Override
		public String toString() {
			return "Collide-Entity";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	ENTITY_DAMAGE {
		@Override
		public String toString() {
			return "Entity-Damage";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	ETITY_RIDING {
		@Override
		public String toString() {
			return "Entity-Riding";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	ENTITY_SPAWN {
		@Override
		public String toString() {
			return "Entity-Spawn";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	ENTITY_TELEPORT_FROM {
		@Override
		public String toString() {
			return "Entity-Teleport-From";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
			};
		}
	},
	ENTITY_TELEPORT_TO {
		@Override
		public String toString() {
			return "Entity-Teleport-To";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	INTERACT_ITEM {
		@Override
		public String toString() {
			return "Interact-Item";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
					return true;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	ITEM_DROP {
		@Override
		public String toString() {
			return "Item-Drop";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
					return true;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	ITEM_PICKUP {
		@Override
		public String toString() {
			return "Item-Pickup";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				return true;
			}
			@Override
			public boolean isAllowSourceDamageType() {
				return false;
			}
			@Override
			public boolean isAllowTargetEntity() {
				return false;
			}
			@Override
			public boolean isAllowTargetBlock() {
				return false;
			}
			};
		}
	},
	ITEM_SPAWN {
		@Override
		public String toString() {
			return "Item-Spawn";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
					return true;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	EXP_SPAWN {
		@Override
		public String toString() {
			return "Exp-Spawn";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	ITEM_USE {
		@Override
		public String toString() {
			return "Item-Use";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
					return true;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	PROJECTILE_IMPACT_BLOCK {
		@Override
		public String toString() {
			return "Projectile-Impact-Block";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
			};
		}
	},
	PROJECTILE_IMPACT_ENTITY {
		@Override
		public String toString() {
			return "Projectile-Impact-Entity";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	},
	PORTAL_USE {
		@Override
		public String toString() {
			return "Portal-Use";
		}
		@Override
		public FlagSettings getFlagSettings() {
			return new FlagSettings() {
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
				@Override
				public boolean isAllowTarget(String target) {
					return false;
				}
				@Override
				public boolean isAllowSourceDamageType() {
					return false;
				}
				@Override
				public boolean isAllowTargetEntity() {
					return false;
				}
				@Override
				public boolean isAllowTargetItem() {
					return false;
				}
				@Override
				public boolean isAllowTargetBlock() {
					return false;
				}
			};
		}
	};

	public abstract FlagSettings getFlagSettings();

	public static Flags valueOfName(String string) {
		for(Flags type : Flags.values()) {
			if(type.toString().equalsIgnoreCase(string)) return type;
		}
		return null;
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
