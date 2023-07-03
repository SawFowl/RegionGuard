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
import java.util.concurrent.TimeUnit;

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
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.google.inject.Inject;

import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.event.LocaleServiseEvent;
import sawfowl.regionguard.api.RegionAPI;
import sawfowl.regionguard.api.events.RegionAPIPostEvent;
import sawfowl.regionguard.commands.RegionCommand;
import sawfowl.regionguard.configure.CuiConfig;
import sawfowl.regionguard.configure.DefaultFlags;
import sawfowl.regionguard.configure.Locales;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.configure.MainConfig;
import sawfowl.regionguard.configure.MySQL;
import sawfowl.regionguard.configure.WorkConfigs;
import sawfowl.regionguard.configure.WorkData;
import sawfowl.regionguard.configure.WorkTables;
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
	private RegionCommand mainCommand;
	private MySQL mySQL;
	private WorkData playersDataWork;
	private WorkData regionsDataWork;
	private RegenUtil regenUtil;
	private Economy economy;

	public static RegionGuard getInstance() {
		return instance;
	}

	public Logger getLogger() {
		return logger;
	}

	public Path getConfigDir() {
		return configDir;
	}

	public ConfigurationOptions getConfigurationOptions() {
		return localeService.getConfigurationOptions();
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

	@Inject
	public RegionGuard(PluginContainer pluginContainer, @ConfigDir(sharedRoot = false) Path configDirectory) {
		instance = this;
		logger = LogManager.getLogger("RegionGuard");
		this.pluginContainer = pluginContainer;
		configDir = configDirectory;
		configDirectory.toFile();
		api = new Api(instance, isForgePlatform());
		regenUtil = new RegenUtil(instance);
	}

	@Listener
	public void onConstruct(LocaleServiseEvent.Construct event) {
		localeService = event.getLocaleService();
		if(!getConfigDir().resolve("Worlds").toFile().exists()) {
			getConfigDir().resolve("Worlds").toFile().mkdir();
		}
		try {
			configurationReference = HoconConfigurationLoader.builder().defaultOptions(getConfigurationOptions()).path(configDir.resolve("Config.conf")).build().loadToReference();
			this.mainConfig = configurationReference.referenceTo(MainConfig.class);
			configurationReference.save();
			
			flagsConfigurationReference = HoconConfigurationLoader.builder().defaultOptions(getConfigurationOptions()).path(configDir.resolve("DefaultFlags.conf")).build().loadToReference();
			this.flagsConfig = flagsConfigurationReference.referenceTo(DefaultFlags.class);
			flagsConfigurationReference.save();
			
			cuiConfigurationReference = HoconConfigurationLoader.builder().defaultOptions(getConfigurationOptions()).path(configDir.resolve("CuiSettings.conf")).build().loadToReference();
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
		Sponge.asyncScheduler().executor(pluginContainer).submit(() -> {
			long time = System.currentTimeMillis();
			regionsDataWork.loadRegions();
			logger.info("Loaded claims: " + api.getRegions().size() + " in " + (System.currentTimeMillis() - time) + "ms");
			mainCommand.getFlagCommand().updateCompletions();
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
		mainCommand = new RegionCommand(instance, event);
		event.register(pluginContainer, mainCommand, "regionguard", "region", "rg");
	}

	private boolean isForgePlatform() {
		try {
			Class.forName("net.minecraft.entity.player.ServerPlayerEntity");
			Class.forName("net.minecraft.network.play.client.CCustomPayloadPacket");
			Class.forName("net.minecraft.network.PacketBuffer");
			return true;
		}  catch (ClassNotFoundException e) {
			return false;
		}
	}

}