package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.sql.SQLInventory;
import com.gmail.trentech.pji.sql.SQLSettings;
import com.gmail.trentech.pji.utils.ConfigManager;
import com.gmail.trentech.pji.utils.Help;

public class CMDCreate implements CommandExecutor {

	public CMDCreate() {
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
		String name = args.<String>getOne("inv").get();

		if(SQLSettings.getInventory(name)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " already exists"));
			return CommandResult.empty();
        }
        
		SQLSettings.saveInventory(name);
		SQLInventory.createInventory(name);
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Created inventory ", name));
		
		return CommandResult.success();
	}

}
