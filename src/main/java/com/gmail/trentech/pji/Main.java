package com.gmail.trentech.pji;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
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

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = "ProjectInventories", repoOwner = "TrenTech", version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, authors = Resource.AUTHOR, url = Resource.URL, dependencies = {@Dependency(id = "Updatifier", optional = true)})
public class Main {

	private static Game game;
	private static Logger log;
	private static PluginContainer plugin;

	@Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
    	new ConfigManager().init();
    	
    	getGame().getEventManager().registerListeners(this, new EventManager());
    	
    	getGame().getCommandManager().register(this, new CommandManager().cmdInventory, "inventory", "inv");
    	
    	getGame().getDataManager().registerBuilder(Inventory.class, new InventoryBuilder());
    	
    	SQLUtils.createSettings();
    	
    	SQLInventory.createInventory("default");
    }

    public static Logger getLog() {
        return log;
    }
    
	public static Game getGame() {
		return game;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}
}