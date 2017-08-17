package com.gmail.trentech.pji.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.WorldData;
import com.gmail.trentech.pji.settings.WorldSettings;

public class CMDAdd implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("inventory add").get();
		
		if (args.hasAny("help")) {		
			help.execute(src);
			return CommandResult.empty();
		}
		
		if (!args.hasAny("world")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties>getOne("world").get();
		
		if (!args.hasAny("inv")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		InventoryData inventoryData = args.<InventoryData>getOne("inv").get();

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		WorldSettings worldSetting = inventoryService.getWorldSettings();
		
		boolean isDefault = false;

		if (args.hasAny("true|false")) {
			isDefault = args.<Boolean>getOne("true|false").get();
		}

		if (isDefault && inventoryData.getPermission().isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "WARNING: ", TextColors.RED, "Permission will be ignored when changing worlds, while inventory is set to default"));
		}

		WorldData worldData = worldSetting.get(properties);
		
		worldData.add(inventoryData.getName(), isDefault);
		
		worldSetting.save(worldData);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Added inventory " + inventoryData.getName() + " to ", properties.getWorldName()));

		return CommandResult.success();
	}
}
