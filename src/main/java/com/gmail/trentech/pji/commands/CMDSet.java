package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.settings.WorldData;

public class CMDSet implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();
		WorldData worldData = WorldData.get(properties);
		
		if (!args.hasAny("inv")) {
			List<Text> list = new ArrayList<>();

			list.add(Text.of(TextColors.GREEN, "Current Inventory: ", TextColors.WHITE, worldData.getInventory()));
			list.add(Text.of(TextColors.GREEN, "Command: ", TextColors.YELLOW, "/inventory set <world> <inventory>"));

			if (src instanceof Player) {
				PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

				pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Inventory")).build());

				pages.contents(list);

				pages.sendTo(src);
			} else {
				for (Text text : list) {
					src.sendMessage(text);
				}
			}

			return CommandResult.success();
		}
		String newInv = args.<String> getOne("inv").get().toUpperCase();
		String oldInv = worldData.getInventory();
		
		worldData.setInventory(newInv).save();

		Sponge.getServer().getWorld(properties.getWorldName()).ifPresent(world -> {
			Predicate<Entity> filter = new Predicate<Entity>() {

				@Override
				public boolean test(Entity entity) {
					return entity instanceof Player;
				}
			};
			
			for(Entity entity: world.getEntities(filter)) {
				Player player = (Player) entity;
				
				new PlayerData(player, oldInv).save();
				
				Optional<PlayerData> optionalPlayerData = PlayerData.get(player, newInv);
				
				if(optionalPlayerData.isPresent()) {
					optionalPlayerData.get().set();
				} else {
					player.getInventory().clear();
					new PlayerData(player, newInv).save();
				}
				
				player.sendMessage(Text.of(TextColors.RED, "[PJI] ", TextColors.YELLOW, "The inventory for this world has been changed by an admin"));
			}
		});
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set inventory for ", properties.getWorldName(), " to ", newInv));

		return CommandResult.success();
	}
}
