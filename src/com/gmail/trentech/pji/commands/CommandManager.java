package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

	public CommandSpec cmdCreate = CommandSpec.builder()
		    .description(Text.of("Create new inventory table"))
		    .permission("MultiInv.cmd.inv.create")    
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("inv"))))
		    .executor(new CMDCreate())
		    .build();
	
	public CommandSpec cmdDelete = CommandSpec.builder()
		    .description(Text.of("Delete inventory table"))
		    .permission("MultiInv.cmd.inv.delete")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("inv"))))
		    .executor(new CMDDelete())
		    .build();

	public CommandSpec cmdSet = CommandSpec.builder()
		    .description(Text.of("Set inventory for world"))
		    .permission("MultiInv.cmd.inv.delete")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("world"))), GenericArguments.optional(GenericArguments.string(Text.of("inv"))))
		    .executor(new CMDSet())
		    .build();
	
	public CommandSpec cmdHelp = CommandSpec.builder()
		    .description(Text.of("I need help"))
		    .permission("MultiInv.cmd.inv.help")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("command"))))
		    .executor(new CMDHelp())
		    .build();

	public CommandSpec cmdInventory = CommandSpec.builder()
			.description(Text.of("Base command"))
			.permission("MultiInv.cmd.inventory")
			.child(cmdCreate, "create", "c")
			.child(cmdDelete, "delete", "d")
			.child(cmdSet, "properties", "p")
			.child(cmdHelp, "help")
			.executor(new CMDInventory())
			.build();
}
