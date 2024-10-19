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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return streamDefault();
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return streamDefault();
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getEntitiesStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return streamDefault();
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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
					return streamDefault();
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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
					return streamDefault();
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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
					return streamDefault();
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getCommandsStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getCommandsStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getEntitiesStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getEntitiesStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getEntitiesStream());
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
					return concat(streamDefault(), getDamageTypesStream(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getEntitiesStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getEntitiesStream());
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
					return concat(streamDefault(), getSpawnTypesStream(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getEntitiesStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getItemsStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getItemsStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getItemsStream());
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
					return concat(streamDefault(), getSpawnTypesStream(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getItemsStream());
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
					return concat(streamDefault(), getSpawnTypesStream(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getItemsStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getBlocksStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return concat(streamDefault(), getEntitiesStream());
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
					return concat(streamDefault(), getEntitiesStream());
				}
				@Override
				public Stream<String> getTargets() {
					return streamDefault();
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

	private static Stream<String> streamDefault() {
		return Stream.of("all");
	}

	private static Stream<String> concat(Stream<String> stream1, Stream<String> stream2) {
		return Stream.concat(stream1, stream2);
	}

	private static Stream<String> concat(Stream<String> stream1, Stream<String> stream2, Stream<String> stream3) {
		return concat(concat(stream1, stream2), stream3);
	}

}
