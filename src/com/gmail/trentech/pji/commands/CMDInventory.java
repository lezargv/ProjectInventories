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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.utils.Help;

public class CMDInventory implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());
		
		List<Text> list = new ArrayList<>();
		
		if(src.hasPermission("pji.cmd.inventory.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("create"))).append(Text.of(" /inventory create")).build());
		}
		if(src.hasPermission("pji.cmd.inventory.delete")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("delete"))).append(Text.of(" /inventory delete")).build());
		}
		if(src.hasPermission("pji.cmd.inventory.set")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("set"))).append(Text.of(" /inventory set")).build());
		}
		if(src.hasPermission("pji.cmd.inventory.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information ")))
					.onClick(TextActions.executeCallback(Help.getHelp("list"))).append(Text.of(" /inventory list")).build());
		}
		
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
