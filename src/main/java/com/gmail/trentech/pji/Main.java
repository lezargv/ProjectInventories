package com.gmail.trentech.pji;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.gmail.trentech.pji.commands.CommandManager;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.KitData;
import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.data.PlayerInventoryData;
import com.gmail.trentech.pji.data.WorldData;
import com.gmail.trentech.pji.init.Common;
import com.gmail.trentech.pji.sql.InitDB;
import com.gmail.trentech.pji.utils.Resource;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true), @Dependency(id = "pjc", optional = false) })
public class Main {

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path path;

	@Inject
	private Logger log;

	private static PluginContainer plugin;
	private static Main instance;

	@Listener
	public void onPreInitializationEvent(GamePreInitializationEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		instance = this;

		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Listener
	public void onInitializationEvent(GameInitializationEvent event) {
		Common.initConfig();

		Sponge.getEventManager().registerListeners(this, new EventManager());
		
		Sponge.getCommandManager().register(this, new CommandManager().cmdInventory, "inventory", "inv");
		
		Sponge.getDataManager().registerBuilder(PlayerInventoryData.class, new PlayerInventoryData.Builder());
		Sponge.getDataManager().registerBuilder(PlayerData.class, new PlayerData.Builder());
		Sponge.getDataManager().registerBuilder(KitData.class, new KitData.Builder());
		Sponge.getDataManager().registerBuilder(InventoryData.class, new InventoryData.Builder());
		Sponge.getDataManager().registerBuilder(WorldData.class, new WorldData.Builder());
		
		InitDB.createSettings();

		Common.initHelp();

		Sponge.getServiceManager().setProvider(getPlugin(), InventoryService.class, new InventoryService());

		Sponge.getServiceManager().provideUnchecked(InventoryService.class).getInventorySettings().save(new InventoryData("DEFAULT"));
	}

	@Listener
	public void onReloadEvent(GameReloadEvent event) {
		Common.initConfig();
	}

	public Logger getLog() {
		return log;
	}

	public Path getPath() {
		return path;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}

	public static Main instance() {
		return instance;
	}
}