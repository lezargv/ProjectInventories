package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.pji.commands.elements.HelpElement;
import com.gmail.trentech.pji.commands.elements.InventoryElement;

public class CommandManager {

	public CommandSpec cmdTest = CommandSpec.builder()
		    .permission("pji.cmd.inventory.test")    
		    .arguments(new InventoryElement(Text.of("inv")))
		    .executor(new CMDTest())
		    .build();
	
	public CommandSpec cmdCreate = CommandSpec.builder()
		    .permission("pji.cmd.inventory.create")    
		    .arguments(GenericArguments.string(Text.of("inv")))
		    .executor(new CMDCreate())
		    .build();
	
	public CommandSpec cmdDelete = CommandSpec.builder()
		    .permission("pji.cmd.inventory.delete")
		    .arguments(new InventoryElement(Text.of("inv")))
		    .executor(new CMDDelete())
		    .build();

	public CommandSpec cmdSet = CommandSpec.builder()
		    .permission("pji.cmd.inventory.set")
		    .arguments(GenericArguments.world(Text.of("world")), GenericArguments.optional(new InventoryElement(Text.of("inv"))))
		    .executor(new CMDSet())
		    .build();

	public CommandSpec cmdList = CommandSpec.builder()
		    .permission("pji.cmd.inventory.list")
		    .executor(new CMDList())
		    .build();
	
	public CommandSpec cmdInfo = CommandSpec.builder()
		    .permission("pji.cmd.inventory.info")
		    .executor(new CMDInfo())
		    .build();
	
	public CommandSpec cmdHelp = CommandSpec.builder()
		    .permission("pji.cmd.inventory")    
		    .arguments(new HelpElement(Text.of("rawCommand")))
		    .executor(new CMDHelp())
		    .build();
	
	public CommandSpec cmdInventory = CommandSpec.builder()
			.permission("pji.cmd.inventory")
			//.child(cmdTest, "test", "t")
			.child(cmdCreate, "create", "c")
			.child(cmdDelete, "delete", "d")
			.child(cmdSet, "set", "s")
			.child(cmdList, "list", "l")
			.child(cmdInfo, "info", "i")
			.child(cmdHelp, "help", "h")
			.executor(new CMDInventory())
			.build();
}