package com.gmail.trentech.pji.commands.elements;
import java.util.Collections;
import java.util.List;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import com.flowpowered.math.vector.Vector2i;

public class InventoryElement extends CommandElement {
	
    CommandArgs errorargs;

    protected InventoryElement(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    	
        // <x> <y>
        errorargs=args;

        String xInput = args.next();
        int x = parseInt(xInput);

        String yInput = args.next();
        int y = parseInt(yInput);

        return new Vector2i(x, y);
    }

    private int parseInt(String input) throws ArgumentParseException {
        try {
            return Integer.parseInt(input);
        } catch(NumberFormatException e) {
            throw errorargs.createError(Text.of("'" + input + "' is not a valid number!"));
        }
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Collections.emptyList();
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of("<x> <y>");
    }
}
