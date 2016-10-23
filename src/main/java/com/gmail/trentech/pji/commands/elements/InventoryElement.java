package com.gmail.trentech.pji.commands.elements;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.settings.Inventories;

public class InventoryElement extends CommandElement {

    public InventoryElement(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    	final String next = args.next();

		if (Inventories.exists(next)) {
			return next;
		}

		if (next.equalsIgnoreCase("DEFAULT")) {
			return next;
		}

		throw args.createError(Text.of(TextColors.RED, "Inventory not found"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    	List<String> list = new ArrayList<>();

    	Optional<String> next = args.nextIfPresent();
    	
    	if(next.isPresent()) {
            for(String inv : Inventories.all()) {
            	if(inv.startsWith(next.get())) {
            		list.add(inv);
            	}
            }
            if("DEFAULT".startsWith(next.get())) {
            	list.add("DEFAULT");
            }
    	} else {
    		for(String inv : Inventories.all()) {
            	list.add(inv);
            }
    		list.add("DEFAULT");
    	}

        return list;
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of(getKey());
    }
}
