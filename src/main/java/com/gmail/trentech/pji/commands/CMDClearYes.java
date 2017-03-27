package com.gmail.trentech.pji.commands;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CMDClearYes implements CommandExecutor {

	protected static HashMap<CommandSource, UUID> confirm = new HashMap<>();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (confirm.containsKey(src)) {
			Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(confirm.get(src));
			
			if(optionalPlayer.isPresent()) {
				Player player = optionalPlayer.get();
				
				player.getInventory().clear();
				
				if(!(src instanceof Player) || !((Player) src).getUniqueId().equals(player.getUniqueId())) {
					player.sendMessage(Text.of(TextColors.GOLD, "Your inventory was cleared by", src.getName()));
				}		
			} else {
				throw new CommandException(Text.of(TextColors.RED, "Player no longer online"), false);
			}
		}

		return CommandResult.success();
	}
}
