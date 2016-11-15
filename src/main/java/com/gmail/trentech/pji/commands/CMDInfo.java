package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;

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
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.service.InventoryService;

public class CMDInfo implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();
		
		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		if(!args.hasAny("world")) {

			for(WorldProperties properties : Sponge.getServer().getAllWorldProperties()) {
				list.addAll(get(inventoryService, properties));
			}
		} else {
			WorldProperties properties = args.<WorldProperties> getOne("world").get();
			
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
		
		for(Entry<String, Boolean> entry : inventoryService.getWorldSettings().all(properties).entrySet()) {
			Text text = Text.of(TextColors.YELLOW, "  - ", entry.getKey());
			
			if(entry.getValue()) {
				text = Text.join(text, Text.of(TextColors.GOLD, " [Default]"));
			} else {
				Optional<String> optionalPermission = inventoryService.getPermissionSettings().get(entry.getKey());
				
				if(optionalPermission.isPresent()) {
					text = Text.join(text, Text.of(TextColors.WHITE, " ", optionalPermission.get()));
				}
			}

			list.add(text);
		}
		
		return list;
	}
}