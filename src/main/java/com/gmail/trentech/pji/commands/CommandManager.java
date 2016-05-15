package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

	public CommandSpec cmdTest = CommandSpec.builder()
		    .permission("pji.cmd.inventory.test")    
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("inv"))))
		    .executor(new CMDTest())
		    .build();
	
	public CommandSpec cmdCreate = CommandSpec.builder()
		    .permission("pji.cmd.inventory.create")    
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("inv"))))
		    .executor(new CMDCreate())
		    .build();
	
	public CommandSpec cmdDelete = CommandSpec.builder()
		    .permission("pji.cmd.inventory.delete")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("inv"))))
		    .executor(new CMDDelete())
		    .build();

	public CommandSpec cmdSet = CommandSpec.builder()
		    .permission("pji.cmd.inventory.set")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("world"))), GenericArguments.optional(GenericArguments.string(Text.of("inv"))))
		    .executor(new CMDSet())
		    .build();

	public CommandSpec cmdList = CommandSpec.builder()
		    .permission("pji.cmd.inventory.list")
		    .executor(new CMDList())
		    .build();
	
	public CommandSpec cmdInventory = CommandSpec.builder()
			.permission("pji.cmd.inventory")
			//.child(cmdTest, "test", "t")
			.child(cmdCreate, "create", "c")
			.child(cmdDelete, "delete", "d")
			.child(cmdSet, "set", "s")
			.child(cmdList, "list", "l")
			.executor(new CMDInventory())
			.build();
}
