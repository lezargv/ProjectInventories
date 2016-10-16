package com.gmail.trentech.pji.commands.elements;
import java.util.Collections;
import java.util.List;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.sql.SQLSettings;

public class InventoryElement extends CommandElement {
	
    CommandArgs errorargs;

    protected InventoryElement(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    	final String next = args.next();

		if (SQLSettings.getInventory(next)) {
			return next;
		}

		if (next.equalsIgnoreCase("default")) {
			return next;
		}

		throw args.createError(Text.of(TextColors.RED, "Inventory not found"));
    }


    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Collections.emptyList();
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of(getKey());
    }
}
