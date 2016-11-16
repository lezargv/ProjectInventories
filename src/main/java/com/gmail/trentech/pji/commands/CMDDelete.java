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
import org.spongepowered.api.text.format.TextStyles;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.settings.InventorySettings;

public class CMDDelete implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String name = args.<String>getOne("inv").get().toUpperCase();

		InventorySettings inventorySettings = Sponge.getServiceManager().provideUnchecked(InventoryService.class).getInventorySettings();

		if (!inventorySettings.exists(name)) {
			throw new CommandException(Text.of(TextColors.RED, name, " does not exist"), false);
		}

		if (name.equalsIgnoreCase("DEFAULT")) {
			throw new CommandException(Text.of(TextColors.RED, name, " inventory be altered or deleted"), false);
		}

		src.sendMessage(Text.builder().color(TextColors.RED).append(Text.of(TextColors.RED, "[WARNING] ", TextColors.YELLOW, "This will delete players inventories and cannot be undone. Confirm? ")).onClick(TextActions.runCommand("/pji:inventory delete yes")).append(Text.of(TextColors.DARK_PURPLE, TextStyles.UNDERLINE, "/inventory delete yes")).build());

		CMDYes.confirm.put(src, name);

		return CommandResult.success();
	}
}
