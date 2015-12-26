package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class CMDCreate implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("inv")) {
			src.sendMessage(Texts.of(TextColors.YELLOW, "/inv create <inventory>"));
			return CommandResult.empty();
		}
		String invName = args.<String>getOne("inv").get();
		
		return CommandResult.success();
	}

}
