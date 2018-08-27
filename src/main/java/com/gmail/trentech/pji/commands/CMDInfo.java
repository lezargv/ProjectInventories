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
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.InventoryData;

public class CMDInfo implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("inventory info").get();
		
		if (args.hasAny("help")) {			
			help.execute(src);
			return CommandResult.empty();
		}
		
		List<Text> list = new ArrayList<>();

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);

		if (!args.hasAny("world")) {

			for (WorldProperties properties : Sponge.getServer().getAllWorldProperties()) {
				list.addAll(get(inventoryService, properties));
			}
		} else {
			WorldProperties properties = args.<WorldProperties>getOne("world").get();

			list.addAll(get(inventoryService, properties));
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Worlds")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			src.sendMessage(Text.of(TextColors.GREEN, "Worlds:"));

			for (Text text : list) {
				src.sendMessage(text);
			}
		}

		return CommandResult.success();
	}

	private List<Text> get(InventoryService inventoryService, WorldProperties properties) {
		List<Text> list = new ArrayList<>();

		list.add(Text.of(TextColors.GREEN, " ", properties.getWorldName(), ":"));
		
		for (Entry<String, Boolean> entry : inventoryService.getWorldSettings().get(properties).getInventories().entrySet()) {
			InventoryData inventoryData = inventoryService.getInventorySettings().get(entry.getKey()).get();

			Text text = Text.of(TextColors.YELLOW, " - ", inventoryData.getName());
			Text hover = Text.empty();
			
			if (entry.getValue()) {
				text = Text.join(text, Text.of(TextColors.GOLD, " [Default]"));
			} else {
				Optional<String> optionalPermission = inventoryData.getPermission();

				if (optionalPermission.isPresent()) {
					hover = Text.of(TextColors.BLUE, "Permission: ", TextColors.WHITE, optionalPermission.get());
				}
			}

			Optional<GameMode> optionalGamemode = inventoryData.getGamemode();

			if (optionalGamemode.isPresent()) {
				if(!hover.isEmpty()) {
					hover = Text.join(hover, Text.newLine());
				}
				hover = Text.join(hover, Text.of(TextColors.BLUE, "Gamemode: ", TextColors.WHITE, optionalGamemode.get().getTranslation()));
			}

			if(!hover.isEmpty()) {
				list.add(Text.builder().onHover(TextActions.showText(hover)).append(text).build());
			} else {
				list.add(text);
			}
		}

		return list;
	}
}