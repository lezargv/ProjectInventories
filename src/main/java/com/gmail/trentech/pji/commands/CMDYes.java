package com.gmail.trentech.pji.commands;

import java.util.HashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.settings.InventorySettings;

public class CMDYes implements CommandExecutor {

	protected static HashMap<CommandSource, String> confirm = new HashMap<>();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (confirm.containsKey(src)) {
			String inventory = confirm.get(src);

			InventorySettings inventorySettings = Sponge.getServiceManager().provideUnchecked(InventoryService.class).getInventorySettings();

			inventorySettings.delete(inventory);

			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Deleted inventory ", inventory));

			confirm.remove(src);
		}

		return CommandResult.success();
	}
}
