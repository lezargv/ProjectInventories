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

import com.gmail.trentech.pji.utils.ConfigManager;
import com.gmail.trentech.pji.utils.Help;
import com.gmail.trentech.pji.utils.SQLUtils;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDCreate implements CommandExecutor {

	public CMDCreate(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "inventory").getString();
		
		Help help = new Help("create", "create", " Create a new inventory");
		help.setSyntax(" /inventory create <name>\n /" + alias + " c <name>");
		help.setExample(" /inventory create nether");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("inv")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/inventory create <inventory>"));
			return CommandResult.empty();
		}
		String invName = args.<String>getOne("inv").get();

		ConfigManager configManager = new ConfigManager();
		ConfigurationNode config = configManager.getConfig();
		
		List<String> inventories = config.getNode("inventories").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
		
        if(inventories.contains(invName)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, invName, " already exists"));
			return CommandResult.empty();
        }
        
    	inventories.add(invName);
    	
    	config.getNode("inventories").setValue(inventories);
    	configManager.save();

    	SQLUtils.createInventory(invName);
    	
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Created inventory ", invName));
		
		return CommandResult.success();
	}

}
