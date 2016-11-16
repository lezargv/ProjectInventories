package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.helpme.help.Help;

public class CMDInventory implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (Sponge.getPluginManager().isLoaded("helpme")) {
			Help.executeList(src, Help.get("inventory").get().getChildren());

			return CommandResult.success();
		}

		List<Text> list = new ArrayList<>();

		if (src.hasPermission("pji.cmd.inventory.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pji:inventory create")).append(Text.of(" /inventory create")).build());
		}
		if (src.hasPermission("pji.cmd.inventory.delete")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pji:inventory delete")).append(Text.of(" /inventory delete")).build());
		}
		if (src.hasPermission("pji.cmd.inventory.set")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pji:inventory set")).append(Text.of(" /inventory set")).build());
		}
		if (src.hasPermission("pji.cmd.inventory.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pji:inventory list")).append(Text.of(" /inventory list")).build());
		}
		if (src.hasPermission("pji.cmd.inventory.info")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pji:inventory into")).append(Text.of(" /inventory info")).build());
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}

		return CommandResult.success();
	}

}
