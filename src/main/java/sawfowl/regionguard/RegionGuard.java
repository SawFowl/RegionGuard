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

import java.io.IOException;
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
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.google.inject.Inject;

import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.event.LocaleServiseEvent;
import sawfowl.regionguard.api.RegionAPI;
import sawfowl.regionguard.api.events.RegionAPIPostEvent;
import sawfowl.regionguard.commands.RegionCommand;
import sawfowl.regionguard.configure.Config;
import sawfowl.regionguard.configure.Locales;
import sawfowl.regionguard.configure.LocalesPaths;
import sawfowl.regionguard.configure.MySQL;
import sawfowl.regionguard.configure.WorkConfigs;
import sawfowl.regionguard.configure.WorkData;
import sawfowl.regionguard.configure.WorkTables;
import sawfowl.regionguard.listeners.BlockAndWorldChangeListener;
import sawfowl.regionguard.listeners.ChunkListener;
import sawfowl.regionguard.listeners.ClientConnectionListener;
import sawfowl.regionguard.listeners.ConnectionListener;
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
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private CommentedConfigurationNode rootNode;
	private ConfigurationLoader<CommentedConfigurationNode> flagsConfigLoader;
	private CommentedConfigurationNode flagsNode;
	private ConfigurationLoader<CommentedConfigurationNode> cuiConfigLoader;
	private CommentedConfigurationNode cuiNode;
	private EconomyService economyService;
	private Api api;
	private Config config;
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

	public CommentedConfigurationNode getRootNode() {
		return rootNode;
	}

	public CommentedConfigurationNode getFlagsNode() {
		return flagsNode;
	}

	public CommentedConfigurationNode getCuiNode() {
		return cuiNode;
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

	public Config getConfig() {
		return config;
	}

	public EconomyService getEconomyService() {
		return economyService;
	}

	public Economy getEconomy() {
		return economy;
	}

	public void loadConfigs() {
		try {
			rootNode = configLoader.load();
			flagsNode = flagsConfigLoader.load();
			cuiNode = cuiConfigLoader.load();
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	public void saveConfigs() {
		try {
			configLoader.save(rootNode);
			flagsConfigLoader.save(flagsNode);
			cuiConfigLoader.save(cuiNode);
		} catch (ConfigurateException e) {
			logger.error(e.getLocalizedMessage());
		}
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
		configLoader = HoconConfigurationLoader.builder().defaultOptions(getConfigurationOptions()).path(configDir.resolve("Config.conf")).build();
		flagsConfigLoader = HoconConfigurationLoader.builder().defaultOptions(getConfigurationOptions()).path(configDir.resolve("DefaultFlags.conf")).build();
		cuiConfigLoader = HoconConfigurationLoader.builder().defaultOptions(getConfigurationOptions()).path(configDir.resolve("CuiSettings.conf")).build();
		loadConfigs();
		config = new Config(instance);
		locales = new Locales(localeService, rootNode.node("LocaleJsonSerialize").getBoolean());
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
	public void onStart(StartedEngineEvent<Server> event) {
		if(localeService == null) return;
		boolean mysql = rootNode.node("MySQL", "Enable").getBoolean();
		if(mysql) mySQL = new MySQL(instance, rootNode.node("MySQL", "Host").getString(), rootNode.node("MySQL", "Port").getString(), rootNode.node("MySQL", "DataBase").getString(), rootNode.node("MySQL", "User").getString(), rootNode.node("MySQL", "Password").getString(), rootNode.node("MySQL", "SSL").getString());
		if(!mysql) {
			playersDataWork = regionsDataWork = new WorkConfigs(instance);
		} else if(mysql && rootNode.node("SplitStorage", "Enable").getBoolean()) {
			if(rootNode.node("SplitStorage", "Players").getBoolean() && rootNode.node("SplitStorage", "Regions").getBoolean()) {
				playersDataWork = regionsDataWork = new WorkTables(instance);
				((WorkTables) regionsDataWork).createWorldsTables();
				((WorkTables) playersDataWork).createTableForPlayers();
			} else if(rootNode.node("SplitStorage", "Players").getBoolean() && !rootNode.node("SplitStorage", "Regions").getBoolean()) {
				playersDataWork = new WorkTables(instance);
				((WorkTables) playersDataWork).createTableForPlayers();
				regionsDataWork = new WorkConfigs(instance);
			} else if(!rootNode.node("SplitStorage", "Players").getBoolean() && rootNode.node("SplitStorage", "Regions").getBoolean()) {
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
		config.writeDefaultWandItem();
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
		if(config.unloadRegions()) Sponge.eventManager().registerListeners(pluginContainer, new ChunkListener(instance));
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
		Sponge.eventManager().registerListeners(pluginContainer, new ConnectionListener(instance));
		Sponge.asyncScheduler().executor(pluginContainer).execute(() -> {
			long time = System.currentTimeMillis();
			regionsDataWork.loadRegions();
			logger.info("Loaded claims: " + api.getRegions().size() + " in " + (System.currentTimeMillis() - time) + "ms");
		});
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