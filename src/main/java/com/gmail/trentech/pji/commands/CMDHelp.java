package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.gmail.trentech.pji.utils.Help;

public class CMDHelp implements CommandExecutor {

	public CMDHelp() {
		new Help("inventory help", "help", "Get help with all commands in Project Inventories", false)
			.setPermission("pjw.pji.inventory")
			.setUsage("/inventory help <rawCommand>")
			.setExample("/inventory help inventory create")
			.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = args.<Help>getOne("rawCommand").get();
		help.execute(src);

		return CommandResult.success();
	}
}
