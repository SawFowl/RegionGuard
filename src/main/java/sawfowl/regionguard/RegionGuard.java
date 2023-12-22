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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.impl.AbstractEvent;
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
import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.api.event.LocaleServiseEvent;
import sawfowl.localeapi.api.serializetools.SerializeOptions;
import sawfowl.regionguard.api.RegionAPI;
import sawfowl.regionguard.api.data.ChunkNumber;
import sawfowl.regionguard.api.data.ClaimedByPlayer;
import sawfowl.regionguard.api.data.Cuboid;
import sawfowl.regionguard.api.data.FlagConfig;
import sawfowl.regionguard.api.data.FlagValue;
import sawfowl.regionguard.api.data.MemberData;
import sawfowl.regionguard.api.data.PlayerData;
import sawfowl.regionguard.api.data.PlayerLimits;
import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionAPIPostEvent;
import sawfowl.regionguard.configure.CuiConfig;
import sawfowl.regionguard.configure.DefaultFlags;
import sawfowl.regionguard.configure.Locales;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.configure.MainConfig;
import sawfowl.regionguard.configure.MySQL;
import sawfowl.regionguard.configure.WorkConfigs;
import sawfowl.regionguard.configure.WorkData;
import sawfowl.regionguard.configure.WorkTables;
import sawfowl.regionguard.data.ChunkNumberImpl;
import sawfowl.regionguard.data.ClaimedByPlayerImpl;
import sawfowl.regionguard.data.CuboidImpl;
import sawfowl.regionguard.data.FlagConfigImpl;
import sawfowl.regionguard.data.FlagValueImpl;
import sawfowl.regionguard.data.MemberDataImpl;
import sawfowl.regionguard.data.PlayerDataImpl;
import sawfowl.regionguard.data.PlayerLimitsImpl;
import sawfowl.regionguard.data.RegionImpl;
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
import sawfowl.regionguard.listeners.forge.ForgeExplosionListener;
import sawfowl.regionguard.utils.Economy;
import sawfowl.regionguard.utils.RegenUtil;
import sawfowl.regionguard.utils.worldedit.WorldEditAPI;
import sawfowl.regionguard.utils.worldedit.cuihandle.SpongeCUIChannelHandler;

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

	public RegionAPI getAPI() {
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
		return flagsConfig.get();
	}

	public CuiConfig getCuiConfig() {
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

	@Inject
	public RegionGuard(PluginContainer pluginContainer, @ConfigDir(sharedRoot = false) Path configDirectory) {
		instance = this;
		logger = LogManager.getLogger("RegionGuard");
		this.pluginContainer = pluginContainer;
		configDir = configDirectory;
		configDirectory.toFile();
		api = new Api(instance);
		regenUtil = new RegenUtil(instance);
	}

	@Listener
	public void onConstruct(LocaleServiseEvent.Construct event) {
		localeService = event.getLocaleService();
		if(!getConfigDir().resolve("Worlds").toFile().exists()) {
			getConfigDir().resolve("Worlds").toFile().mkdir();
		}
		try {
			configurationReference = SerializeOptions.createHoconConfigurationLoader(2).path(configDir.resolve("Config.conf")).build().loadToReference();
			this.mainConfig = configurationReference.referenceTo(MainConfig.class);
			configurationReference.save();
			
			flagsConfigurationReference = SerializeOptions.createHoconConfigurationLoader(2).path(configDir.resolve("DefaultFlags.conf")).build().loadToReference();
			this.flagsConfig = flagsConfigurationReference.referenceTo(DefaultFlags.class);
			flagsConfigurationReference.save();
			
			cuiConfigurationReference = SerializeOptions.createHoconConfigurationLoader(2).path(configDir.resolve("CuiSettings.conf")).build().loadToReference();
			this.cuiConfig = cuiConfigurationReference.referenceTo(CuiConfig.class);
			cuiConfigurationReference.save();
		} catch (ConfigurateException e) {
			logger.warn(e.getLocalizedMessage());
		}
		locales = new Locales(localeService, getConfig().isLocaleJsonSerialize());
		((WorldEditAPI) api.getWorldEditCUIAPI()).updateCuiDataMaps();
		Sponge.game().eventManager().registerListeners(
				pluginContainer,
				new SpongeCUIChannelHandler.RegistrationHandler(instance)
			);
		class PostAPIEvent extends AbstractEvent implements RegionAPIPostEvent.PostAPI {
			@Override
			public Cause cause() {
				return Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, pluginContainer).build(), pluginContainer);
			}
			@Override
			public RegionAPI getAPI() {
				return api;
			}
		}
		PostAPIEvent toPost = new PostAPIEvent();
		Sponge.eventManager().post(toPost);
	}

	@Listener
	public void getCommandPackAPI(CommandPack.PostAPI event) {
		commandPack = event.getAPI();
	}

	@Listener(order = Order.LAST)
	public void onStart(StartedEngineEvent<Server> event) {
		if(localeService == null) return;
		boolean mysql = getConfig().getMySQLConfig().isEnable();
		if(mysql) mySQL = new MySQL(instance, getConfig().getMySQLConfig().getHost(), getConfig().getMySQLConfig().getPort(), getConfig().getMySQLConfig().getDatabase(), getConfig().getMySQLConfig().getUser(), getConfig().getMySQLConfig().getPassword(), getConfig().getMySQLConfig().getSSL());
		if(!mysql) {
			playersDataWork = regionsDataWork = new WorkConfigs(instance);
		} else if(mysql && getConfig().getSplitStorage().isEnable()) {
			if(getConfig().getSplitStorage().isPlayers() && getConfig().getSplitStorage().isRegions()) {
				playersDataWork = regionsDataWork = new WorkTables(instance);
				((WorkTables) regionsDataWork).createWorldsTables();
				((WorkTables) playersDataWork).createTableForPlayers();
			} else if(getConfig().getSplitStorage().isPlayers() && !getConfig().getSplitStorage().isRegions()) {
				playersDataWork = new WorkTables(instance);
				((WorkTables) playersDataWork).createTableForPlayers();
				regionsDataWork = new WorkConfigs(instance);
			} else if(!getConfig().getSplitStorage().isPlayers() && getConfig().getSplitStorage().isRegions()) {
				regionsDataWork = new WorkTables(instance);
				((WorkTables) regionsDataWork).createWorldsTables();
				playersDataWork = new WorkConfigs(instance);
			} else {
				playersDataWork = regionsDataWork = new WorkConfigs(instance);
			}
		} else if(mysql) {
			playersDataWork = regionsDataWork = new WorkTables(instance);
			((WorkTables) regionsDataWork).createWorldsTables();
			((WorkTables) playersDataWork).createTableForPlayers();
		} else {
			playersDataWork = regionsDataWork = new WorkConfigs(instance);
		}
		api.updateWandItem();
		if(Sponge.server().serviceProvider().economyService().isPresent()) {
			economyService  = Sponge.server().serviceProvider().economyService().get();
			economy = new Economy(instance);
		} else {
			logger.warn(locales.getText(Sponge.server().locale(), LocalesPaths.ECONOMY_NOT_FOUND));
		}
		regionsDataWork.createDataForWorlds();
		playersDataWork.loadDataOfPlayers();
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
			class PostEvent extends AbstractEvent implements RegionAPIPostEvent.CompleteLoadRegions {
				@Override
				public Cause cause() {
					return Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, pluginContainer).build(), pluginContainer);
				}
				@Override
				public long getTotalLoaded() {
					return api.getRegions().size();
				}
			}
			PostEvent toPost = new PostEvent();
			Sponge.eventManager().post(toPost);
			mainCommand.genEconomyCommands();
		});
	}

	@Listener
	public void onRegisterRawSpongeCommand(final RegisterCommandEvent<Command.Raw> event) {
		mainCommand = new sawfowl.regionguard.commands.Region(instance);
		mainCommand.register(event);
	}

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

}