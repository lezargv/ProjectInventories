package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.Main;

public class CMDSet implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("world")) {
			src.sendMessage(Texts.of(TextColors.YELLOW, "/inv set <world> <inventory>"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("world").get();
		
		if(worldName.equalsIgnoreCase("@w")){
			if(src instanceof Player){
				worldName = ((Player) src).getWorld().getName();
			}
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}

		if(!args.hasAny("inv")) {
			src.sendMessage(Texts.of(TextColors.YELLOW, "/inv set <world> <inventory>"));
			return CommandResult.empty();
		}
		String invName = args.<String>getOne("inv").get();
		
		return CommandResult.success();
		
		
	}

}
