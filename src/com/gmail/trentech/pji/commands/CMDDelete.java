package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CMDDelete implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("inv")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/inv delete <inventory>"));
			return CommandResult.empty();
		}
		String invName = args.<String>getOne("inv").get();
		
		return CommandResult.success();
	}

}
