package com.gmail.trentech.pji.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.WorldData;
import com.gmail.trentech.pji.data.sql.SQLUtils;
import com.gmail.trentech.pji.utils.ConfigManager;
import com.gmail.trentech.pji.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDDelete implements CommandExecutor {

	public CMDDelete(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "inventory").getString();
		
		Help help = new Help("delete", "delete", " Delete an existing inventory. WARNING: This cannot be undone.");
		help.setSyntax(" /inventory delete <name>\n /" + alias + " d <name>");
		help.setExample(" /inventory delete nether");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("inv")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/inventory delete <inventory>"));
			return CommandResult.empty();
		}
		String invName = args.<String>getOne("inv").get();

		ConfigManager configManager = new ConfigManager();
		ConfigurationNode config = configManager.getConfig();
		
		List<String> inventories = config.getNode("inventories").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
		
        if(!inventories.contains(invName)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, invName, " does not exist"));
			return CommandResult.empty();
        }
        
        if(invName.equalsIgnoreCase("default")){
			src.sendMessage(Text.of(TextColors.DARK_RED, invName, " inventory cannot be deleted"));
			return CommandResult.empty();
        }
        
        inventories.remove(invName);
        
        config.getNode("inventories").setValue(inventories);
        configManager.save();

		for(World world : Main.getGame().getServer().getWorlds()){
			WorldData worldData = WorldData.get(world).get();
			if(worldData.getInventory().equalsIgnoreCase(invName)){
				WorldData.get(world).get().setInventory("default");
			}		
		}

		SQLUtils.deleteInventory(invName);
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Deleted inventory ", invName));
		
		return CommandResult.success();
	}
}
