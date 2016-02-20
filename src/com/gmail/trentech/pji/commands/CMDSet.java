package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.sql.SQLSettings;
import com.gmail.trentech.pji.utils.ConfigManager;
import com.gmail.trentech.pji.utils.Help;

public class CMDSet implements CommandExecutor {

	public CMDSet(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "inventory").getString();
		
		Help help = new Help("set", "set", " Set an inventory for the specified world");
		help.setSyntax(" /inventory set <world> <inventory>\n /" + alias + " s <world> <inventory>");
		help.setExample(" /inventory set DIM-1 nether");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("world")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/inventory set <world> <inventory>"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("world").get();
		
		if(worldName.equalsIgnoreCase("@w")){
			if(src instanceof Player){
				worldName = ((Player) src).getWorld().getName();
			}
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		if(!args.hasAny("inv")) {
			PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
			
			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Inventory")).build());
			
			List<Text> list = new ArrayList<>();
			list.add(Text.of(TextColors.GREEN, "Current Inventory: ", TextColors.WHITE, SQLSettings.getWorld(world)));
			list.add(Text.of(TextColors.GREEN, "Command: ", TextColors.YELLOW, "/inventory set <world> <inventory>"));
			
			pages.contents(list);
			
			pages.sendTo(src);
			
			return CommandResult.empty();
		}
		String name = args.<String>getOne("inv").get();

		if(!SQLSettings.getInventory(name) && !name.equalsIgnoreCase("default")){
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " does not exist"));
			return CommandResult.empty();
        }
		
		SQLSettings.updateWorld(world, name);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set inventory for ", worldName, " to ", name));
		
		return CommandResult.success();	
	}
}
