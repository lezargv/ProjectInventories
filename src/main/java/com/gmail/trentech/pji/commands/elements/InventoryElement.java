package com.gmail.trentech.pji.commands.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.data.InventoryData;
import com.gmail.trentech.pji.service.settings.InventorySettings;

public class InventoryElement extends CommandElement {

	public InventoryElement(Text key) {
		super(key);
	}

	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		final String next = args.next().toUpperCase();

		InventorySettings inventorySettings = Sponge.getServiceManager().provideUnchecked(InventoryService.class).getInventorySettings();

		Optional<InventoryData> optionalData = inventorySettings.get(next);
		
		if (optionalData.isPresent()) {
			return optionalData.get();
		}

		throw args.createError(Text.of(TextColors.RED, "Inventory not found"));
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		List<String> list = new ArrayList<>();

		Optional<String> next = args.nextIfPresent();

		InventorySettings inventorySettings = Sponge.getServiceManager().provideUnchecked(InventoryService.class).getInventorySettings();

		if (next.isPresent()) {
			for (Entry<String, InventoryData> entry : inventorySettings.all().entrySet()) {
				String inv = entry.getKey();
				
				if (inv.startsWith(next.get().toUpperCase())) {
					list.add(inv);
				}
			}
		} else {
			for (Entry<String, InventoryData> entry : inventorySettings.all().entrySet()) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	@Override
	public Text getUsage(CommandSource src) {
		return Text.of(getKey());
	}
}
