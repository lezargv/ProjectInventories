package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.settings.InventorySettings;
import com.gmail.trentech.pji.service.settings.PermissionSettings;

public class CMDList implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		InventorySettings inventorySettings = inventoryService.getInventorySettings();
		PermissionSettings permissionSettings = inventoryService.getPermissionSettings();

		for (String name : inventorySettings.all()) {
			Text text = Text.of(TextColors.YELLOW, " - ", name);
			
			Optional<String> optionalPermission = permissionSettings.get(name);
			
			if(optionalPermission.isPresent()) {
				text = Text.join(text, Text.of(TextColors.WHITE, " ", optionalPermission.get()));
			}

			list.add(text);
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Inventories")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			src.sendMessage(Text.of(TextColors.GREEN, "Inventories:"));
			
			for (Text text : list) {
				src.sendMessage(text);
			}
		}

		return CommandResult.success();
	}

}
