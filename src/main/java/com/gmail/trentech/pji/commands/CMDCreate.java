package com.gmail.trentech.pji.commands;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.settings.InventorySettings;

public class CMDCreate implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("inv")) {
			Help help = Help.get("inventory create").get();
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		String name = args.<String>getOne("inv").get().toUpperCase();

		if (name.equalsIgnoreCase("DEFAULT")) {
			throw new CommandException(Text.of(TextColors.RED, name, " cannot be altered or deleted"), false);
		}

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		InventorySettings inventorySettings = inventoryService.getInventorySettings();

		Optional<InventoryData> optionalInventoryData = inventorySettings.get(name);
		
		if (optionalInventoryData.isPresent()) {
			InventoryData inventoryData = optionalInventoryData.get();
			
			if (args.hasAny("permission")) {
				String permission = args.<String>getOne("permission").get().toUpperCase();

				inventoryData.setPermission(permission);

				src.sendMessage(Text.of(TextColors.DARK_GREEN, "Altered " + name + ", set permission to ", permission));
			} else {
				inventoryData.removePermission();
			}
			
			if (args.hasAny("gamemode")) {
				GameMode gamemode = args.<GameMode>getOne("gamemode").get();

				inventoryData.setGamemode(gamemode);

				src.sendMessage(Text.of(TextColors.DARK_GREEN, "Altered " + name + ", set gamemode to ", gamemode));
			}else {
				inventoryData.removeGamemode();
			}
			
			inventorySettings.save(inventoryData);
			
			return CommandResult.success();
		}

		InventoryData inventoryData = new InventoryData(name);

		if (args.hasAny("permission")) {
			String permission = args.<String>getOne("permission").get().toUpperCase();

			inventoryData.setPermission(permission);
		}
		
		if (args.hasAny("gamemode")) {
			GameMode gamemode = args.<GameMode>getOne("gamemode").get();

			inventoryData.setGamemode(gamemode);
		}

		inventorySettings.save(inventoryData);
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Created new inventory ", name));

		return CommandResult.success();
	}

}
