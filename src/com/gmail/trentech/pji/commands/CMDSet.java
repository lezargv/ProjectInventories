package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.gmail.trentech.pji.data.WorldData;
import com.gmail.trentech.pji.utils.ConfigManager;
import com.gmail.trentech.pji.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDSet implements CommandExecutor {

	public CMDSet(){
		Help help = new Help("set", "set", " Set an inventory for the specified world");
		help.setSyntax(" /inventory set <world> <inventory>\n /inv s <world> <inventory>");
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
			list.add(Text.of(TextColors.GREEN, "Current Inventory: ", TextColors.WHITE, WorldData.get(world).get().getInventory()));
			list.add(Text.of(TextColors.GREEN, "Command: ", TextColors.YELLOW, "/inventory set <world> <inventory>"));
			
			pages.contents(list);
			
			pages.sendTo(src);
			
			return CommandResult.empty();
		}
		String invName = args.<String>getOne("inv").get();

		ConfigurationNode config =  new ConfigManager().getConfig();
		
		List<String> inventories = config.getNode("Inventories").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
		
        if(!inventories.contains(invName) && !invName.equalsIgnoreCase("default")) {
			src.sendMessage(Text.of(TextColors.DARK_RED, invName, " does not exist"));
			return CommandResult.empty();
        }
		
        WorldData.get(world).get().setInventory(invName);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set inventory for ", worldName, " to ", invName));
		
		return CommandResult.success();	
	}
}
