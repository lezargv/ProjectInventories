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
import com.gmail.trentech.pji.service.settings.PermissionSettings;

public class CMDAdd implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties>getOne("world").get();
		String name = args.<String>getOne("inv").get().toUpperCase();

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		PermissionSettings permissionSettings = inventoryService.getPermissionSettings();

		boolean isDefault = false;

		if (args.hasAny("true|false")) {
			isDefault = args.<Boolean>getOne("true|false").get();
		}

		if (isDefault && permissionSettings.get(name).isPresent()) {
			src.sendMessage(Text.of(TextColors.RED, name, "WARNING: Permission will be ignored when changing worlds, while inventory is set to default"));
		}

		inventoryService.getWorldSettings().add(properties, name, isDefault);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Added inventory " + name + " to ", properties.getWorldName()));

		return CommandResult.success();
	}
}
