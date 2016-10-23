package com.gmail.trentech.pji.commands;

import java.util.HashMap;

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
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.settings.Inventories;
import com.gmail.trentech.pji.settings.WorldData;

public class CMDDelete implements CommandExecutor {

	private static HashMap<CommandSource, String> confirm = new HashMap<>();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String name = args.<String> getOne("inv").get().toUpperCase();

		if (name.equalsIgnoreCase("yes")) {
			if (confirm.containsKey(src)) {
				String inv = confirm.get(src);
				
				for (WorldProperties properties : Sponge.getServer().getAllWorldProperties()) {
					WorldData worldData = WorldData.get(properties);

					if (worldData.getInventory().equalsIgnoreCase(inv)) {
						worldData.setInventory("DEFAULT").save();
					}
				}

				Inventories.delete(inv);

				src.sendMessage(Text.of(TextColors.DARK_GREEN, "Deleted inventory ", inv));

				confirm.remove(src);
			}
			return CommandResult.success();
		}

		if (!Inventories.exists(name)) {
			throw new CommandException(Text.of(TextColors.RED, name, " does not exist"), false);
		}

		if (name.equalsIgnoreCase("DEFAULT")) {
			throw new CommandException(Text.of(TextColors.RED, name, " inventory cannot be deleted"), false);
		}

		src.sendMessage(Text.builder().color(TextColors.RED).append(Text.of(TextColors.RED, "[WARNING] ", TextColors.YELLOW, "This will delete players inventories and cannot be undone. Confirm? ")).onClick(TextActions.runCommand("/pji:inventory delete yes")).append(Text.of(TextColors.DARK_PURPLE, TextStyles.UNDERLINE, "/inventory delete yes")).build());

		confirm.put(src, name);

		return CommandResult.success();
	}
}
