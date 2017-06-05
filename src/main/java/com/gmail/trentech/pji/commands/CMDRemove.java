package com.gmail.trentech.pji.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.data.WorldData;
import com.gmail.trentech.pji.settings.InventorySettings;
import com.gmail.trentech.pji.settings.PlayerSettings;
import com.gmail.trentech.pji.settings.WorldSettings;
import com.gmail.trentech.pji.sql.PlayerDB;

public class CMDRemove implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("world")) {
			Help help = Help.get("inventory remove").get();
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties>getOne("world").get();
		
		if (!args.hasAny("inv")) {
			Help help = Help.get("inventory remove").get();
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		InventoryData inventoryData = args.<InventoryData>getOne("inv").get();

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		WorldSettings worldSettings = inventoryService.getWorldSettings();
		WorldData worldData = worldSettings.get(properties);
		
		if (!worldData.contains(inventoryData.getName())) {
			throw new CommandException(Text.of(TextColors.RED, inventoryData.getName(), " is not assigned to ", properties.getWorldName()), false);
		}

		if (worldData.getInventories().size() == 1) {
			throw new CommandException(Text.of(TextColors.RED, "World must contain at least one inventory. Add another inventory before removing ", inventoryData.getName(), false));
		}

		if(worldData.getDefault().equalsIgnoreCase(inventoryData.getName())) {
			throw new CommandException(Text.of(TextColors.RED, "Cannot remove the default inventory. Set another inventory as default before removing this inventory", false));
		}

		worldData.remove(inventoryData.getName());

		PlayerSettings playerSettings = inventoryService.getPlayerSettings();
		InventorySettings inventorySettings = inventoryService.getInventorySettings();
		
		Sponge.getServer().getWorld(properties.getWorldName()).ifPresent(world -> {
			for(Player player : world.getPlayers()) {
				PlayerData playerData = PlayerDB.get(player.getUniqueId());
				
				if (playerData.getInventoryName().equals(inventoryData.getName())) {
					playerSettings.set(player, inventorySettings.get(worldData.getDefault()).get(), false);

					player.sendMessage(Text.of(TextColors.RED, "[PJI] ", TextColors.YELLOW, "The inventory for this world has been removed by an admin. Changing to default inventory"));
				}
			}
		});

		worldSettings.save(worldData);
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Removed inventory " + inventoryData.getName() + " from ", properties.getWorldName()));

		return CommandResult.success();
	}
}
