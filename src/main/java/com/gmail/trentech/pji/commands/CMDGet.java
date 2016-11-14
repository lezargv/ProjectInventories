package com.gmail.trentech.pji.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.settings.WorldData;

public class CMDGet implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;

		String name = args.<String> getOne("inv").get();

		new PlayerData(player, WorldData.get(player.getWorld().getProperties()).getInventory()).save();
		
		Optional<PlayerData> optionalPlayerData = PlayerData.get(player, name);
		
		if(optionalPlayerData.isPresent()) {
			optionalPlayerData.get().set();
		} else {
			player.getInventory().clear();
			new PlayerData(player, name).save();
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set inventory to ", name));

		return CommandResult.success();
	}

}
