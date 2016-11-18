package com.gmail.trentech.pji.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.settings.GamemodeSettings;
import com.gmail.trentech.pji.service.settings.InventorySettings;
import com.gmail.trentech.pji.service.settings.PermissionSettings;

public class CMDCreate implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String name = args.<String>getOne("inv").get().toUpperCase();

		if (name.equalsIgnoreCase("DEFAULT")) {
			throw new CommandException(Text.of(TextColors.RED, name, " cannot be altered or deleted"), false);
		}

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		InventorySettings inventorySettings = inventoryService.getInventorySettings();
		PermissionSettings permissionSettings = inventoryService.getPermissionSettings();
		GamemodeSettings gamemodeSettings = inventoryService.getGamemodeSettings();
		
		if (inventorySettings.exists(name)) {
			if (args.hasAny("permission")) {
				String permission = args.<String>getOne("permission").get().toUpperCase();

				permissionSettings.set(name, permission);

				src.sendMessage(Text.of(TextColors.DARK_GREEN, "Altered " + name + ", set permission to ", permission));
			} else {
				permissionSettings.remove(name);
			}
			
			if (args.hasAny("gamemode")) {
				GameMode gamemode = args.<GameMode>getOne("gamemode").get();

				gamemodeSettings.set(name, gamemode);

				src.sendMessage(Text.of(TextColors.DARK_GREEN, "Altered " + name + ", set gamemode to ", gamemode));
			}else {
				gamemodeSettings.remove(name);
			}
			
			return CommandResult.success();
		}

		inventorySettings.create(name);

		if (args.hasAny("permission")) {
			String permission = args.<String>getOne("permission").get().toUpperCase();

			permissionSettings.set(name, permission);
		}
		
		if (args.hasAny("gamemode")) {
			GameMode gamemode = args.<GameMode>getOne("gamemode").get();

			gamemodeSettings.set(name, gamemode);
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Created new inventory ", name));

		return CommandResult.success();
	}

}
