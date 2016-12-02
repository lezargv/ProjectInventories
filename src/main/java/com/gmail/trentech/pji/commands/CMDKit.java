package com.gmail.trentech.pji.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.KitData;

public class CMDKit implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;

		InventoryData inventoryData = args.<InventoryData>getOne("inv").get();
		
		inventoryData.setKitData(new KitData(player));
		
		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		inventoryService.getInventorySettings().save(inventoryData);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Added kit to ", inventoryData.getName()));

		return CommandResult.success();
	}

}
