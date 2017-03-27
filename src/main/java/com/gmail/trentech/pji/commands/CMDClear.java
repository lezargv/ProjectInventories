package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class CMDClear implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player player;
		
		if (!args.hasAny("player")) {
			if (!(src instanceof Player)) {
				throw new CommandException(Text.of(TextColors.RED, "Must be a player, or specify another player"), false);
			}
			player = (Player) src;
		} else {
			player = args.<Player>getOne("player").get();	
		}

		if (src instanceof Player && !((Player) src).equals(player) && !((Player) src).hasPermission("pjp.cmd.inventory.clear.others")) {
			throw new CommandException(Text.of(TextColors.RED, "You do not have permission to clear other players inventory."), false);
		}
		
		src.sendMessage(Text.builder().color(TextColors.RED).append(Text.of(TextColors.RED, "[WARNING] ", TextColors.YELLOW, "This will delete all of your inventory. Confirm? ")).onClick(TextActions.runCommand("/pji:inventory clear yes")).append(Text.of(TextColors.DARK_PURPLE, TextStyles.UNDERLINE, "/inventory clear yes")).build());

		CMDClearYes.confirm.put(src, player.getUniqueId());

		return CommandResult.success();
	}
}
