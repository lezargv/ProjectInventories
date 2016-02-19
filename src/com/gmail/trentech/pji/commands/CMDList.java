package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.utils.ConfigManager;
import com.gmail.trentech.pji.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDList implements CommandExecutor {

	public CMDList(){
		Help help = new Help("list", "list", " List all inventories");
		help.setSyntax(" /inventory list\n /inv l");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, "Inventories")).build());

		ConfigurationNode config =  new ConfigManager().getConfig();
		
		List<Text> list = new ArrayList<>();

		list.add(Text.of(TextColors.GREEN, " - default"));
		
		for(String inv : config.getNode("Inventories").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList())){
			list.add(Text.of(TextColors.GREEN, " -", inv));
		}

		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
