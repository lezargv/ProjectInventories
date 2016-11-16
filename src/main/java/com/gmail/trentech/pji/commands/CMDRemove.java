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

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.settings.WorldSettings;

public class CMDRemove implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties>getOne("world").get();
		String name = args.<String>getOne("inv").get().toUpperCase();

		WorldSettings worldSettings = Sponge.getServiceManager().provideUnchecked(InventoryService.class).getWorldSettings();

		if (!worldSettings.contains(properties, name)) {
			throw new CommandException(Text.of(TextColors.RED, name, " is not assigned to ", properties.getWorldName()), false);
		}

		if (worldSettings.all(properties).size() == 1) {
			throw new CommandException(Text.of(TextColors.RED, "World must contain at least one inventory. Add another inventory before removing ", name, false));
		}

		worldSettings.remove(properties, name);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Removed inventory " + name + " from ", properties.getWorldName()));

		return CommandResult.success();
	}
}
