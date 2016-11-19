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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.data.InventoryData;
import com.gmail.trentech.pji.service.settings.PlayerSettings;
import com.gmail.trentech.pji.service.settings.WorldSettings;

public class CMDGet implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		WorldSettings worldSettings = inventoryService.getWorldSettings();
		PlayerSettings playerSettings = inventoryService.getPlayerSettings();

		if (!args.hasAny("inv")) {
			List<Text> list = new ArrayList<>();

			for (Entry<String, Boolean> entry : worldSettings.all(player.getWorld().getProperties()).entrySet()) {
				InventoryData inventoryData = inventoryService.getInventorySettings().get(entry.getKey()).get();

				Text text = Text.of(TextColors.YELLOW, " - ", inventoryData.getName());
				Text hover = Text.EMPTY;
				
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
		InventoryData inventoryData = args.<InventoryData>getOne("inv").get();

		if (!worldSettings.contains(player.getWorld().getProperties(), inventoryData.getName())) {
			throw new CommandException(Text.of(TextColors.RED, "This inventory is not assigned to this world"), false);
		}

		Optional<String> optionalPermission = inventoryData.getPermission();

		if (optionalPermission.isPresent() && !src.hasPermission(optionalPermission.get())) {
			throw new CommandException(Text.of(TextColors.RED, "You do not have permission to get this inventory"), false);
		}

		Optional<GameMode> optionalGamemode = inventoryData.getGamemode();

		if (optionalGamemode.isPresent() && !src.hasPermission("pji.override.gamemode")) {
			player.offer(Keys.GAME_MODE, optionalGamemode.get());
		}
		
		playerSettings.save(player, playerSettings.copy(player));

		playerSettings.set(player, inventoryData.getName(), false);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set inventory to ", inventoryData.getName()));

		return CommandResult.success();
	}

}
