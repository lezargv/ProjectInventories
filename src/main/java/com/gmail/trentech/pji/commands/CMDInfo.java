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
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.sql.SQLSettings;
import com.gmail.trentech.pji.utils.Help;

public class CMDInfo implements CommandExecutor {

	public CMDInfo() {
		Help help = new Help("info", "info", " Lists the worlds and their assigned inventories");
		help.setPermission("pji.cmd.inventory.info");
		help.setSyntax(" /inventory info\n /inv i");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();

		for(WorldProperties properties : Sponge.getServer().getAllWorldProperties()) {
			Optional<String> optionalInv = SQLSettings.getWorld(properties);
			
			if(optionalInv.isPresent()) {
				list.add(Text.of(TextColors.GREEN, properties.getWorldName(), " : ", TextColors.WHITE, " ", optionalInv.get()));
			} else {
				list.add(Text.of(TextColors.GREEN, properties.getWorldName(), " : ", TextColors.WHITE, " default"));
			}
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Inventories")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}

		return CommandResult.success();
	}

}