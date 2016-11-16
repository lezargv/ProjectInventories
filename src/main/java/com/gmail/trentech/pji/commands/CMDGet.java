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
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.settings.PermissionSettings;
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
		PermissionSettings permissionSettings = inventoryService.getPermissionSettings();

		if (!args.hasAny("inv")) {
			List<Text> list = new ArrayList<>();

			for (Entry<String, Boolean> entry : worldSettings.all(player.getWorld().getProperties()).entrySet()) {
				Text text = Text.of(TextColors.YELLOW, " - ", entry.getKey());

				if (entry.getValue()) {
					text = Text.join(text, Text.of(TextColors.GOLD, " [Default]"));
				} else {
					Optional<String> optionalPermission = permissionSettings.get(entry.getKey());

					if (optionalPermission.isPresent()) {
						text = Text.join(text, Text.of(TextColors.WHITE, " ", optionalPermission.get()));
					}
				}

				if (playerSettings.get(player).equals(entry.getKey())) {
					text = Text.join(text, Text.of(TextColors.GREEN, " [Current]"));
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
		String name = args.<String>getOne("inv").get().toUpperCase();

		if (!worldSettings.contains(player.getWorld().getProperties(), name)) {
			throw new CommandException(Text.of(TextColors.RED, "This inventory is not assigned to this world"), false);
		}

		Optional<String> optionalPermission = permissionSettings.get(name);

		if (optionalPermission.isPresent() && !src.hasPermission(optionalPermission.get())) {
			throw new CommandException(Text.of(TextColors.RED, "You do not have permission to get this inventory"), false);
		}

		inventoryService.save(new PlayerData(player));

		playerSettings.set(player, name, false);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set inventory to ", name));

		return CommandResult.success();
	}

}
