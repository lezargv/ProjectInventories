package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.data.inventory.extra.InventoryHelper;
import com.gmail.trentech.pji.sql.SQLSettings;

public class CMDTest implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.YELLOW, "/inventory test <inventory>"));
		}
		Player player = (Player) src;

		String name = args.<String> getOne("inv").get();

		if (!SQLSettings.getInventory(name)) {
			throw new CommandException(Text.of(TextColors.RED, name, " does not exist"));
		}

		InventoryHelper.saveInventory(player, name);
		InventoryHelper.setInventory(player, name);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set inventory ", name));

		return CommandResult.success();
	}

}
