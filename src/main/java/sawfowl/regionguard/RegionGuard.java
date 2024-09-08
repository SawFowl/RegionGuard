/*
 * RegionGuard - A plugin to protect territories..
 * Copyright (C) 2022 SawFowl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * You can copy any part of the plugin and modify it to your liking,
 * as well as make and publish forks with modified code.
 *
 * RegionGuard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package sawfowl.regionguard;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterBuilderEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.google.inject.Inject;

import sawfowl.commandpack.api.CommandPack;
import sawfowl.commandpack.utils.StorageType;
import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.api.event.LocaleServiseEvent;
import sawfowl.localeapi.api.serializetools.SerializeOptions;
import sawfowl.regionguard.api.RegionAPI;
import sawfowl.regionguard.api.RegionSerializerCollection;
import sawfowl.regionguard.api.data.ChunkNumber;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.FlagConfig;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.commands.child.limits.Buy;
import sawfowl.regionguard.commands.child.limits.Sell;
import sawfowl.regionguard.configure.Locales;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.configure.MySQL;
import sawfowl.regionguard.configure.WorkData;
import sawfowl.regionguard.configure.configs.CuiConfig;
import sawfowl.regionguard.configure.configs.DefaultFlags;
import sawfowl.regionguard.configure.configs.MainConfig;
import sawfowl.regionguard.configure.storage.FileStorage;
import sawfowl.regionguard.configure.storage.H2Storage;
import sawfowl.regionguard.configure.storage.MySqlStorage;
import sawfowl.regionguard.implementsapi.Api;
import sawfowl.regionguard.implementsapi.data.ChunkNumberImpl;
import sawfowl.regionguard.implementsapi.data.ClaimedByPlayerImpl;
import sawfowl.regionguard.implementsapi.data.CuboidImpl;
import sawfowl.regionguard.implementsapi.data.FlagConfigImpl;
import sawfowl.regionguard.implementsapi.data.FlagValueImpl;
import sawfowl.regionguard.implementsapi.data.MemberDataImpl;
import sawfowl.regionguard.implementsapi.data.PlayerDataImpl;
import sawfowl.regionguard.implementsapi.data.PlayerLimitsImpl;
import sawfowl.regionguard.implementsapi.data.RegionImpl;
import sawfowl.regionguard.implementsapi.worldedit.WorldEditAPI;
import sawfowl.regionguard.implementsapi.worldedit.cui.handle.SpongeCUIChannelHandler;
import sawfowl.regionguard.implementsapi.worldedit.cui.handle.SpongeCUIChannelHandler.RegistrationHandler;
import sawfowl.regionguard.listeners.BlockAndWorldChangeListener;
import sawfowl.regionguard.listeners.ChunkListener;
import sawfowl.regionguard.listeners.ClientConnectionListener;
import sawfowl.regionguard.listeners.ImpactListener;
import sawfowl.regionguard.listeners.DamageEntityAndCommandListener;
import sawfowl.regionguard.listeners.DeathListener;
import sawfowl.regionguard.listeners.PickupDropItemListener;
import sawfowl.regionguard.listeners.EntityMoveListener;
import sawfowl.regionguard.listeners.ExplosionListener;
import sawfowl.regionguard.listeners.InteractEntityListener;
import sawfowl.regionguard.listeners.InteractItemListener;
import sawfowl.regionguard.listeners.ItemUseListener;
import sawfowl.regionguard.listeners.SpawnEntityListener;
import sawfowl.regionguard.listeners.BlockAndWorldChangeListener.PlayerPositions;
import sawfowl.regionguard.listeners.forge.ForgeExplosionListener;
import sawfowl.regionguard.utils.Economy;
import sawfowl.regionguard.utils.Logger;
import sawfowl.regionguard.utils.RegenUtil;

@Plugin("regionguard")
public class RegionGuard {

	private Logger logger;
	private LocaleService localeService;

	private static RegionGuard instance;
	private Locales locales;
	private PluginContainer pluginContainer;
	private Path configDir;
	private ConfigurationReference<CommentedConfigurationNode> configurationReference;
	private ValueReference<MainConfig, CommentedConfigurationNode> mainConfig;
	private ConfigurationReference<CommentedConfigurationNode> flagsConfigurationReference;
	private ValueReference<DefaultFlags, CommentedConfigurationNode> flagsConfig;
	private ConfigurationReference<CommentedConfigurationNode> cuiConfigurationReference;
	private ValueReference<CuiConfig, CommentedConfigurationNode> cuiConfig;
	private EconomyService economyService;
	private Api api;
	private sawfowl.regionguard.commands.Region mainCommand;
	private MySQL mySQL;
	private WorkData playersDataWork;
	private WorkData regionsDataWork;
	private RegenUtil regenUtil;
	private Economy economy;
	private CommandPack commandPack;
	private RegistrationHandler handler;
	private Map<UUID, PlayerPositions> selectedPositions = new HashMap<>();

	public static RegionGuard getInstance() {
		return instance;
	}

	public Logger getLogger() {
		return logger;
	}

	public Path getConfigDir() {
		return configDir;
	}

	public Locales getLocales() {
		return locales;
	}

	public PluginContainer getPluginContainer() {
		return pluginContainer;
	}

	public Api getAPI() {
		return api;
	}

	public MySQL getMySQL() {
		return mySQL;
	}

	public WorkData getPlayersDataWork() {
		return playersDataWork;
	}

	public WorkData getRegionsDataWork() {
		return regionsDataWork;
	}

	public RegenUtil getRegenUtil() {
		return regenUtil;
	}

	public MainConfig getConfig() {
		return mainConfig.get();
	}

	public DefaultFlags getDefaultFlagsConfig() {
		if(flagsConfig == null) saveConfigs();
		return flagsConfig.get();
	}

	public CuiConfig getCuiConfig() {
		if(cuiConfig == null) saveConfigs();
		return cuiConfig.get();
	}

	public EconomyService getEconomyService() {
		return economyService;
	}

	public Economy getEconomy() {
		return economy;
	}

	public CommandPack getCommandPack() {
		return commandPack;
	}

	public void addPlayerPositions(ServerPlayer player, PlayerPositions positions) {
		if(playerPositionsExist(player)) selectedPositions.remove(player.uniqueId());
		selectedPositions.put(player.uniqueId(), positions);
	}

	public void removePlayerPositions(ServerPlayer player) {
		if(playerPositionsExist(player)) selectedPositions.remove(player.uniqueId());
	}

	public Optional<PlayerPositions> getPlayerPositions(ServerPlayer player) {
		return Optional.ofNullable(playerPositionsExist(player) ? selectedPositions.get(player.uniqueId()) : null);
	}

	public boolean playerPositionsExist(ServerPlayer player) {
		return selectedPositions.containsKey(player.uniqueId());
	}

	@Inject
	public RegionGuard(PluginContainer pluginContainer, @ConfigDir(sharedRoot = false) Path configDirectory) {
		instance = this;
		logger = new Logger();
		this.pluginContainer = pluginContainer;
		configDir = configDirectory;
		configDirectory.toFile();
	}

	@Listener
	public void onConstruct(LocaleServiseEvent.Construct event) {
		localeService = event.getLocaleService();
		try {
			configurationReference = SerializeOptions.createHoconConfigurationLoader(2).path(configDir.resolve("Config.conf")).build().loadToReference();
			this.mainConfig = configurationReference.referenceTo(MainConfig.class);
			configurationReference.save();
			locales = new Locales(localeService, getConfig().isLocaleJsonSerialize());
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
		Sponge.game().eventManager().registerListeners(
			pluginContainer,
			handler = new SpongeCUIChannelHandler.RegistrationHandler(instance)
		);
	}

	@Listener
	public void getCommandPackAPI(CommandPack.PostAPI event) {
		commandPack = event.getAPI();
		if(commandPack.isForgeServer()) {
			if(handler != null) {
				Sponge.eventManager().unregisterListeners(handler);
				handler = null;
			}
			Sponge.game().eventManager().registerListeners(
				pluginContainer,
				new SpongeCUIChannelHandler.ForgeCuiListener(instance)
			);
		}
	}

	@Listener(order = Order.LAST)
	public void onStart(StartedEngineEvent<Server> event) {
		if(localeService == null) return;
		api = new Api(instance);
		regenUtil = new RegenUtil(instance);
		if(getConfig().getMySQLConfig().isEnable()) {
			mySQL = new MySQL(instance, getConfig().getMySQLConfig());
		}
		((WorldEditAPI) api.getWorldEditCUIAPI()).updateCuiDataMaps();
		boolean h2 = Sponge.pluginManager().plugin("h2driver").isPresent();
		boolean mysql = Sponge.pluginManager().plugin("mysqldriver").isPresent() && mySQL != null && mySQL.checkConnection();
		if(getConfig().getMySQLConfig().isEnable()) {
			if(getConfig().getSplitStorage().isEnable()) {
				switch (getConfig().getSplitStorage().getPlayers()) {
				case FILE: {
					playersDataWork = new FileStorage(instance);
					if(getConfig().getSplitStorage().getRegions() == StorageType.FILE) {
						regionsDataWork = playersDataWork;
					} else if(mysql && getConfig().getSplitStorage().getRegions() == StorageType.MYSQL) {
						regionsDataWork = new MySqlStorage(instance);
					} else if(h2 && getConfig().getSplitStorage().getRegions() == StorageType.H2) {
						regionsDataWork = new H2Storage(instance);
					} else regionsDataWork = playersDataWork;
					break;
				}
				case MYSQL: {
					if(mysql) {
						playersDataWork = new MySqlStorage(instance);
						if(getConfig().getSplitStorage().getRegions() == StorageType.FILE) {
							regionsDataWork = new FileStorage(instance);
						} else if(getConfig().getSplitStorage().getRegions() == StorageType.MYSQL) {
							regionsDataWork = playersDataWork;
						} else if(h2 && getConfig().getSplitStorage().getRegions() == StorageType.H2) {
							regionsDataWork = new H2Storage(instance);
						} else regionsDataWork = playersDataWork;
					} else if(h2) {
						if(getConfig().getSplitStorage().getRegions() == StorageType.FILE) {
							regionsDataWork = new FileStorage(instance);
						} else if(getConfig().getSplitStorage().getRegions() == StorageType.H2) {
							regionsDataWork = new H2Storage(instance);
						} else regionsDataWork = playersDataWork;
					} else regionsDataWork = playersDataWork = new FileStorage(instance);
					break;
				}
				case H2: {
					if(h2) {
						playersDataWork = new H2Storage(instance);
						if(getConfig().getSplitStorage().getRegions() == StorageType.FILE) {
							regionsDataWork = new FileStorage(instance);
						} else if(mysql && getConfig().getSplitStorage().getRegions() == StorageType.MYSQL) {
							regionsDataWork = new MySqlStorage(instance);
						} else if(getConfig().getSplitStorage().getRegions() == StorageType.H2) {
							regionsDataWork = playersDataWork;
						} else regionsDataWork = playersDataWork = new FileStorage(instance);
					} else {
						playersDataWork = new H2Storage(instance);
						if(getConfig().getSplitStorage().getRegions() == StorageType.FILE) {
							regionsDataWork = new FileStorage(instance);
						} else if(mysql && getConfig().getSplitStorage().getRegions() == StorageType.MYSQL) {
							regionsDataWork = new MySqlStorage(instance);
						} else regionsDataWork = playersDataWork = new FileStorage(instance);
					}
					break;
				}
				default:
					playersDataWork = regionsDataWork = new MySqlStorage(instance);
					break;
				}
			} else playersDataWork = regionsDataWork = new MySqlStorage(instance);
		} else {
			if(getConfig().getSplitStorage().isEnable()) {
				switch (getConfig().getSplitStorage().getPlayers()) {
				case FILE: {
					playersDataWork = new FileStorage(instance);
					if(h2 && getConfig().getSplitStorage().getRegions() == StorageType.H2) {
						regionsDataWork = new H2Storage(instance);
					} else regionsDataWork = playersDataWork;
					break;
				}
				case MYSQL: {
					playersDataWork = new FileStorage(instance);
					if(h2 && getConfig().getSplitStorage().getRegions() == StorageType.H2) {
						regionsDataWork = new H2Storage(instance);
					} else regionsDataWork = playersDataWork;
					break;
				}
				case H2: {
					playersDataWork = new H2Storage(instance);
					if(getConfig().getSplitStorage().getRegions() == StorageType.FILE) {
						regionsDataWork = new FileStorage(instance);
					} else regionsDataWork = playersDataWork;
					break;
				}
				default:
					playersDataWork = regionsDataWork = new FileStorage(instance);
					break;
				}
			} else playersDataWork = regionsDataWork = new FileStorage(instance);
		}
		api.updateWandItem();
		if(Sponge.server().serviceProvider().economyService().isPresent()) {
			economyService  = Sponge.server().serviceProvider().economyService().get();
			economy = new Economy(instance);
			mainCommand.getChildExecutors().get("limits").getChildExecutors().put("buy", new Buy(instance));
			mainCommand.getChildExecutors().get("limits").getChildExecutors().put("sell", new Sell(instance));
		} else {
			logger.warn(locales.getComponent(Sponge.server().locale(), LocalesPaths.ECONOMY_NOT_FOUND));
		}
		api.generateDefaultGlobalRegion();
		if(getConfig().isUnloadRegions()) Sponge.eventManager().registerListeners(pluginContainer, new ChunkListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new ClientConnectionListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new BlockAndWorldChangeListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new ExplosionListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new InteractEntityListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new EntityMoveListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new DeathListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new DamageEntityAndCommandListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new ImpactListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new SpawnEntityListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new PickupDropItemListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new InteractItemListener(instance));
		Sponge.eventManager().registerListeners(pluginContainer, new ItemUseListener(instance));
		if(getConfig().isRegisterForgeListeners() && commandPack.isForgeServer()) new ForgeExplosionListener(instance);
		Sponge.asyncScheduler().executor(pluginContainer).submit(() -> {
			long time = System.currentTimeMillis();
			regionsDataWork.loadRegions();
			logger.info("Loaded claims: " + api.getRegions().size() + " in " + (System.currentTimeMillis() - time) + "ms");
			Sponge.eventManager().post(new RegionAPI.PostAPI() {
				@Override
				public Cause cause() {
					return Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, pluginContainer).build(), pluginContainer);
				}
				@Override
				public RegionAPI getAPI() {
					return api;
				}
			});
		});
	}

	@Listener
	public void onRegisterRawSpongeCommand(final RegisterCommandEvent<Command.Raw> event) {
		mainCommand = new sawfowl.regionguard.commands.Region(instance);
		mainCommand.register(event);
		mainCommand.getChildExecutors().get("wand").register(event);
		saveConfigs();
	}

	@Listener
	public void registerBuilders(RegisterBuilderEvent event) {
		event.register(ChunkNumber.Builder.class, () -> new ChunkNumberImpl().builder());
		event.register(ClaimedByPlayer.Builder.class, () -> new ClaimedByPlayerImpl().builder());
		event.register(Cuboid.Builder.class, () -> new CuboidImpl().builder());
		event.register(FlagValue.Builder.class, () -> new FlagValueImpl().builder());
		event.register(MemberData.Builder.class, () -> new MemberDataImpl().builder());
		event.register(PlayerData.Builder.class, () -> new PlayerDataImpl().builder());
		event.register(PlayerLimits.Builder.class, () -> new PlayerLimitsImpl().builder());
		event.register(Region.Builder.class, () -> new RegionImpl().builder());
		event.register(FlagConfig.Builder.class, () -> new FlagConfigImpl().builder());
	}

	@Listener
	public void onRefresh(RefreshGameEvent event) {
		if(playersDataWork instanceof MySqlStorage) {
			((MySqlStorage) playersDataWork).updateSync();
		} else if (regionsDataWork instanceof MySqlStorage) {
			((MySqlStorage) regionsDataWork).updateSync();
		}
	}

	private void saveConfigs() {
		try {
			flagsConfigurationReference = SerializeOptions.createHoconConfigurationLoader(2).defaultOptions(options -> options.serializers(serializers -> serializers.register(FlagValue.class, RegionSerializerCollection.COLLETCTION.get(FlagValue.class)))).path(configDir.resolve("DefaultFlags.conf")).build().loadToReference();
			this.flagsConfig = flagsConfigurationReference.referenceTo(DefaultFlags.class);
			flagsConfigurationReference.save();
			flagsConfig.get().setSaveConsumer(consumer -> flagsConfig.setAndSave(flagsConfig.get()));
			
			cuiConfigurationReference = SerializeOptions.createHoconConfigurationLoader(2).path(configDir.resolve("CuiSettings.conf")).build().loadToReference();
			this.cuiConfig = cuiConfigurationReference.referenceTo(CuiConfig.class);
			cuiConfigurationReference.save();
		} catch (ConfigurateException e) {
			logger.warn(e.getLocalizedMessage());
		}
	}

}