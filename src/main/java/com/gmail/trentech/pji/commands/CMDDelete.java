package com.gmail.trentech.pji.commands;

import java.util.HashMap;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.inventory.extra.InventoryHelper;
import com.gmail.trentech.pji.sql.SQLInventory;
import com.gmail.trentech.pji.sql.SQLSettings;
import com.gmail.trentech.pji.utils.Help;

public class CMDDelete implements CommandExecutor {

	private static HashMap<CommandSource, String> confirm = new HashMap<>();

	public CMDDelete() {
		Help help = new Help("delete", "delete", " Delete an existing inventory. WARNING: This cannot be undone.");
		help.setSyntax(" /inventory delete <name>\n /inv d <name>");
		help.setExample(" /inventory delete nether");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("inv")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/inventory delete <inventory>"));
			return CommandResult.empty();
		}
		String name = args.<String> getOne("inv").get();

		if (name.equalsIgnoreCase("yes")) {
			if (confirm.containsKey(src)) {
				String inv = confirm.get(src);
				for (World world : Main.getGame().getServer().getWorlds()) {
					String oldInv = SQLSettings.getWorld(world).get();
					if (oldInv.equalsIgnoreCase(inv)) {
						SQLSettings.updateWorld(world, oldInv, "default");

						for (Entity entity : world.getEntities()) {
							if (entity instanceof Player) {
								Player player = (Player) entity;
								InventoryHelper.setInventory(player, "default");
								player.sendMessage(Text.of(TextColors.RED, "[PJP] ", TextColors.YELLOW, "Admin deleted your inventory for this world"));
							}
						}
					}
				}

				SQLInventory.deleteTable(inv);
				SQLSettings.deleteInventory(inv);

				src.sendMessage(Text.of(TextColors.DARK_GREEN, "Deleted inventory ", inv));

				confirm.remove(src);
			}
			return CommandResult.success();
		}

		if (!SQLSettings.getInventory(name)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " does not exist"));
			return CommandResult.empty();
		}

		if (name.equalsIgnoreCase("default")) {
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " inventory cannot be deleted"));
			return CommandResult.empty();
		}

		src.sendMessage(Text.builder().color(TextColors.RED).append(Text.of(TextColors.RED, "[WARNING] ", TextColors.YELLOW, "This will delete players inventories and cannot be undone. Confirm? ")).onClick(TextActions.runCommand("/pji:inventory delete yes")).append(Text.of(TextColors.DARK_PURPLE, TextStyles.UNDERLINE, "/inventory delete yes")).build());

		confirm.put(src, name);

		return CommandResult.success();
	}
}
