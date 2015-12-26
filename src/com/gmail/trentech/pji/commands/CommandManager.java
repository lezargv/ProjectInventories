package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Texts;

public class CommandManager {

	public CommandSpec cmdCreate = CommandSpec.builder()
		    .description(Texts.of("Create new inventory table"))
		    .permission("MultiInv.cmd.inv.create")    
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("inv"))))
		    .executor(new CMDCreate())
		    .build();
	
	public CommandSpec cmdDelete = CommandSpec.builder()
		    .description(Texts.of("Delete inventory table"))
		    .permission("MultiInv.cmd.inv.delete")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("inv"))))
		    .executor(new CMDDelete())
		    .build();

	public CommandSpec cmdSet = CommandSpec.builder()
		    .description(Texts.of("Set inventory for world"))
		    .permission("MultiInv.cmd.inv.delete")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("world"))), GenericArguments.optional(GenericArguments.string(Texts.of("inv"))))
		    .executor(new CMDSet())
		    .build();
	
	public CommandSpec cmdHelp = CommandSpec.builder()
		    .description(Texts.of("I need help"))
		    .permission("MultiInv.cmd.inv.help")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("command"))))
		    .executor(new CMDHelp())
		    .build();
	

	
	public CommandSpec cmdInventory = CommandSpec.builder()
			.description(Texts.of("Base command"))
			.permission("MultiInv.cmd.inventory")
			.child(cmdCreate, "create", "cr")
			.child(cmdDelete, "delete", "del")
			.child(cmdSet, "properties", "prop")
			.child(cmdHelp, "help", "hp")
			.executor(new CMDInventory())
			.build();
}
