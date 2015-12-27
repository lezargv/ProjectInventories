package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.Main;

public class CMDHelp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("command")) {
			Text t1 = Texts.of(TextColors.YELLOW, "/inv help ");
			Text t2 = Texts.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Texts.of("Enter the command you need help with"))).append(Texts.of("<command> ")).build();
			src.sendMessage(Texts.of(t1,t2));
			return CommandResult.empty();
		}
		String command = args.<String>getOne("command").get().toUpperCase();
		String description = null;
		String syntax = null;
		String example = null;
		
		switch(command.toLowerCase()){
			case "create":
				description = " Allows you to create new inventory table.";
				syntax = " /inventory create <inventory>\n"
						+ " /inv c <inventory>";
				example = " /inventory create NewInventory";
				break;
			case "delete":
				description = " Allows you to delete existing inventory table.";
				syntax = " /inventory delete <inventory>\n"
						+ " /inv d <inventory>";
				example = " /inventory delete OldInventory";
				break;
			case "set":
				description = " Allows you to set a an inventory for a world.";
				syntax = " /inventory set <world> <inventory>\n"
						+ " /inv s <world> <inventory>";
				example = " /inventory set world NewInventory";
				break;
			default:
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Not a valid command"));
				return CommandResult.empty();
		}
		
		help(command, description, syntax, example).sendTo(src);
		return CommandResult.success();
	}
	
	private PaginationBuilder help(String command, String description, String syntax, String example){
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		pages.title(Texts.builder().color(TextColors.DARK_GREEN).append(Texts.of(TextColors.AQUA, command)).build());
		
		List<Text> list = new ArrayList<>();

		list.add(Texts.of(TextColors.AQUA, "Description:"));
		list.add(Texts.of(TextColors.GREEN, description));
		list.add(Texts.of(TextColors.AQUA, "Syntax:"));
		list.add(Texts.of(TextColors.GREEN, syntax));
		list.add(Texts.of(TextColors.AQUA, "Example:"));
		list.add(Texts.of(TextColors.GREEN,  example, TextColors.DARK_GREEN));
		
		pages.contents(list);
		
		return pages;
	}

}
