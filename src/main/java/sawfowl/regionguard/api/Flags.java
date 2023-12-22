package sawfowl.regionguard.api;

import java.util.stream.Stream;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.entity.SpawnTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.RegistryTypes;

import sawfowl.regionguard.api.data.FlagConfig;
import sawfowl.regionguard.api.data.FlagSettings;

public enum Flags {

	INTERACT_BLOCK_PRIMARY {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Interact-Block-Primary", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	INTERACT_BLOCK_SECONDARY {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Interact-Block-Secondary", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	BLOCK_BREAK {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Block-Break", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	BLOCK_PLACE {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Block-Place", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	BLOCK_DECAY {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Block-Decay", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return streamOnlyOne();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	BLOCK_GROWTH {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Block-Growth", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	PISTON {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Piston", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	PISTON_GRIEF {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Piston-Grief", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	LIQUID_FLOW {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Liquid-Flow", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return streamOnlyOne();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	EXPLOSION {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Explosion", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getEntitiesStream();
				}
			});
		}
	},
	EXPLOSION_SURFACE {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Explosion-Surface", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	FIRE_SPREAD {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Fire-Spread", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return false;
				}
				@Override
				public Stream<String> getSources() {
					return streamOnlyOne();
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	},
	ENTER_CLAIM {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Enter-Claim", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	},
	EXIT_CLAIM {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Exit-Claim", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	},
	ALLOW_FLY {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Allow-Fly", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	},
	PVP {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("PvP", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return false;
				}
				@Override
				public Stream<String> getSources() {
					return streamOnlyOne();
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	},
	KEEP_INVENTORY {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Keep-Inventory", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return false;
				}
				@Override
				public Stream<String> getSources() {
					return streamOnlyOne();
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	},
	KEEP_EXP {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Keep-Exp", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return false;
				}
				@Override
				public Stream<String> getSources() {
					return streamOnlyOne();
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	},
	COMMAND_EXECUTE {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Command-Execute", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getCommandsStream();
				}
			});
		}
	},
	COMMAND_EXECUTE_PVP {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Command-Execute-PvP", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getCommandsStream();
				}
			});
		}
	},
	INTERACT_ENTITY_PRIMARY {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Interact-Entity-Primary", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getEntitiesStream();
				}
			});
		}
	},
	INTERACT_ENTITY_SECONDARY {
		@Override
		public String toString() {
			return "Interact-Entity-Secondary";
		}
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Interact-Entity-Secondary", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getEntitiesStream();
				}
			});
		}
	},
	COLLIDE_ENTITY {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Collide-Entity", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getEntitiesStream();
				}
			});
		}
	},
	ENTITY_DAMAGE {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Entity-Damage", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return Stream.concat(getDamageTypesStream(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return getEntitiesStream();
				}
			});
		}
	},
	ETITY_RIDING {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Entity-Riding", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getEntitiesStream();
				}
			});
		}
	},
	ENTITY_SPAWN {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Entity-Spawn", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return Stream.concat(getSpawnTypesStream(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return getEntitiesStream();
				}
			});
		}
	},
	ENTITY_TELEPORT_FROM {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Entity-Teleport-From", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	},
	ENTITY_TELEPORT_TO {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Entity-Teleport-To", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	},
	INTERACT_ITEM {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Interact-Item", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getItemsStream();
				}
			});
		}
	},
	ITEM_DROP {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Item-Drop", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getItemsStream();
				}
			});
		}
	},
	ITEM_PICKUP {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Item-Pickup", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getItemsStream();
				}
			});
		}
	},
	ITEM_SPAWN {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Item-Spawn", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return Stream.concat(getSpawnTypesStream(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return getItemsStream();
				}
			});
		}
	},
	EXP_SPAWN {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Exp-Spawn", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return Stream.concat(getSpawnTypesStream(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	},
	ITEM_USE {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Item-Use", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getItemsStream();
				}
			});
		}
	},
	PROJECTILE_IMPACT_BLOCK {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Projectile-Impact-Block", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getBlocksStream();
				}
			});
		}
	},
	PROJECTILE_IMPACT_ENTITY {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Projectile-Impact-Entity", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return getEntitiesStream();
				}
			});
		}
	},
	PORTAL_USE {
		@Override
		public FlagConfig getFlagConfig() {
			return FlagConfig.of("Portal-Use", new FlagSettings() {
				@Override
				public boolean isAllowArgs() {
					return true;
				}
				@Override
				public Stream<String> getSources() {
					return getEntitiesStream();
				}
				@Override
				public Stream<String> getTargets() {
					return streamOnlyOne();
				}
			});
		}
	};

	@Override
	public String toString() {
		return getFlagConfig().getName();
	}

	public abstract FlagConfig getFlagConfig();

	public static Flags valueOfName(String string) {
		for(Flags type : Flags.values()) {
			if(type.toString().equalsIgnoreCase(string)) return type;
		}
		return null;
	}

	public static boolean registeredItemType(String string) {
		return string != null && ItemTypes.registry().findValue(ResourceKey.resolve(string)).isPresent();
	}

	public static boolean registeredEntityType(String string) {
		return string != null && EntityTypes.registry().findValue(ResourceKey.resolve(string)).isPresent();
	}

	public static boolean registeredEntityCategory(String string) {
		return string != null && Sponge.game().registry(RegistryTypes.ENTITY_CATEGORY).findValue(ResourceKey.resolve(string)).isPresent();
	}

	public static boolean registeredBlockType(String string) {
		return string != null && BlockTypes.registry().findValue(ResourceKey.resolve(string)).isPresent();
	}

	public static boolean registeredDamageType(String string) {
		return string != null && DamageTypes.registry().findValue(ResourceKey.resolve(string)).isPresent();
	}

	private static Stream<String> getEntitiesStream() {
		return Stream.concat(Sponge.game().registry(RegistryTypes.ENTITY_CATEGORY).streamEntries().map(registry -> registry.key().asString()), EntityTypes.registry().streamEntries().map(registry -> registry.key().asString()));
	}

	private static Stream<String> getBlocksStream() {
		return BlockTypes.registry().streamEntries().map(registry -> registry.key().asString());
	}

	private static Stream<String> getItemsStream() {
		return ItemTypes.registry().streamEntries().map(registry -> registry.key().asString());
	}

	private static Stream<String> getDamageTypesStream() {
		return DamageTypes.registry().streamEntries().map(registry -> registry.key().asString());
	}

	private static Stream<String> getSpawnTypesStream() {
		return SpawnTypes.registry().streamEntries().map(registry -> registry.key().asString());
	}

	private static Stream<String> getCommandsStream() {
		return Sponge.server().commandManager().knownAliases().stream();
	}

	private static Stream<String> streamOnlyOne() {
		return Stream.of("all");
	}

}
