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

import com.gmail.trentech.helpme.Help;
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
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true), @Dependency(id = "helpme", optional = true) })
public class Main {

	@Inject @ConfigDir(sharedRoot = false)
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
		ConfigManager.init();

		Sponge.getEventManager().registerListeners(this, new EventManager());
		Sponge.getCommandManager().register(this, new CommandManager().cmdInventory, "inventory", "inv");
		Sponge.getDataManager().registerBuilder(Inventory.class, new InventoryBuilder());

		SQLUtils.createSettings();

		SQLInventory.createInventory("default");
		
		Help invCreate = new Help("inventory create", "create", "Create a new inventory")
				.setPermission("pji.cmd.inventory.create")
				.addUsage("/inventory create <name>")
				.addUsage("/inv c <name>")
				.addExample("/inventory create nether");
		
		Help invDelete = new Help("inventory delete", "delete", "Delete an existing inventory. WARNING: This cannot be undone.")
				.setPermission("pji.cmd.inventory.delete")
				.addUsage("/inventory delete <name>")
				.addUsage("/inv d <name>")
				.addExample("/inventory delete nether");
		
		Help invInfo = new Help("inventory info", "info", "Lists the worlds and their assigned inventories")
				.setPermission("pji.cmd.inventory.info")
				.addUsage("/inventory info")
				.addUsage("/inv i");
		
		Help invList = new Help("inventory list", "list", "List all inventories")
				.setPermission("pji.cmd.inventory.list")
				.addUsage("/inventory list")
				.addUsage("/inv l");
		
		Help invSet = new Help("inventory set", "set", "Set an inventory for the specified world")
				.setPermission("pji.cmd.inventory.set")
				.addUsage("/inventory set <world> <inventory>")
				.addUsage("/inv s <world> <inventory>")
				.addExample("/inventory set DIM-1 nether");
		
		
		Help inv = new Help("inventory", "inventory", "Base Project Inventories command")
				.setPermission("pji.cmd.inventory")
				.addChild(invSet)
				.addChild(invList)
				.addChild(invInfo)
				.addChild(invDelete)
				.addChild(invCreate);
		
		Help.register(inv);
	}

	@Listener
	public void onReloadEvent(GameReloadEvent event) {
		ConfigManager.init();
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