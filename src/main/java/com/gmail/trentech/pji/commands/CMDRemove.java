package com.gmail.trentech.pji.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.WorldData;
import com.gmail.trentech.pji.settings.WorldSettings;

public class CMDRemove implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties>getOne("world").get();
		InventoryData inventoryData = args.<InventoryData>getOne("inv").get();

		WorldSettings worldSettings = Sponge.getServiceManager().provideUnchecked(InventoryService.class).getWorldSettings();
		WorldData worldData = worldSettings.get(properties);
		
		if (!worldData.contains(inventoryData.getName())) {
			throw new CommandException(Text.of(TextColors.RED, inventoryData.getName(), " is not assigned to ", properties.getWorldName()), false);
		}

		if (worldData.getInventories().size() == 1) {
			throw new CommandException(Text.of(TextColors.RED, "World must contain at least one inventory. Add another inventory before removing ", inventoryData.getName(), false));
		}

		worldData.remove(inventoryData.getName());

		worldSettings.save(worldData);
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Removed inventory " + inventoryData.getName() + " from ", properties.getWorldName()));

		return CommandResult.success();
	}
}
