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
import com.gmail.trentech.pji.data.inventory.Inventory;
import com.gmail.trentech.pji.data.inventory.InventoryBuilder;
import com.gmail.trentech.pji.sql.SQLInventory;
import com.gmail.trentech.pji.sql.SQLUtils;
import com.gmail.trentech.pji.utils.ConfigManager;
import com.gmail.trentech.pji.utils.Resource;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	@Inject @ConfigDir(sharedRoot = false)
    private Path path;

	@Inject 
	private PluginContainer plugin;
	
	@Inject
	private Logger log;

	private static Main instance;
	
	@Listener
	public void onPreInitializationEvent(GamePreInitializationEvent event) {
		instance = this;
		
		try {			
			Files.createDirectories(path);		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Listener
	public void onInitializationEvent(GameInitializationEvent event) {
		ConfigManager.init();

		Sponge.getEventManager().registerListeners(this, new EventManager());
		Sponge.getCommandManager().register(this, new CommandManager().cmdInventory, "inventory", "inv");
		Sponge.getDataManager().registerBuilder(Inventory.class, new InventoryBuilder());

		SQLUtils.createSettings();

		SQLInventory.createInventory("default");
	}

	@Listener
	public void onReloadEvent(GameReloadEvent event) {
		ConfigManager.init();
	}
	
	public Logger getLog() {
		return log;
	}

	public PluginContainer getPlugin() {
		return plugin;
	}
	
	public Path getPath() {
		return path;
	}
	
	public static Main instance() {
		return instance;
	}
}