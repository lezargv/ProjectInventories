package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.settings.InventorySettings;

public class CMDList implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		InventorySettings inventorySettings = inventoryService.getInventorySettings();

		for (Entry<String, InventoryData> entry : inventorySettings.all().entrySet()) {
			InventoryData inventoryData = entry.getValue();
			
			Text text = Text.of(TextColors.YELLOW, " - ", inventoryData.getName());
			Text hover = Text.EMPTY;
			
			Optional<String> optionalPermission = inventoryData.getPermission();

			if (optionalPermission.isPresent()) {
				hover = Text.of(TextColors.BLUE, "Permission: ", TextColors.WHITE, optionalPermission.get());
			}
			
			Optional<GameMode> optionalGamemode = inventoryData.getGamemode();

			if (optionalGamemode.isPresent()) {
				hover = Text.join(hover, Text.NEW_LINE, Text.of(TextColors.BLUE, "Gamemode: ", TextColors.WHITE, optionalGamemode.get().getTranslation()));
			}

			if(!hover.isEmpty()) {
				list.add(Text.builder().onHover(TextActions.showText(hover)).append(text).build());
			} else {
				list.add(text);
			}			
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
