package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.pji.commands.elements.InventoryElement;
import com.gmail.trentech.pji.commands.world.CMDAdd;
import com.gmail.trentech.pji.commands.world.CMDRemove;
import com.gmail.trentech.pji.commands.world.CMDWorld;

public class CommandManager {

	public CommandSpec cmdSee = CommandSpec.builder()
		    .permission("pji.cmd.inventory.see")    
		    .arguments(GenericArguments.player(Text.of("player")), new InventoryElement(Text.of("inv")))
		    .executor(new CMDSee())
		    .build();
	
	public CommandSpec cmdGet = CommandSpec.builder()
		    .permission("pji.cmd.inventory.get")    
		    .arguments(GenericArguments.optional(new InventoryElement(Text.of("inv"))))
		    .executor(new CMDGet())
		    .build();
	
	public CommandSpec cmdCreate = CommandSpec.builder()
		    .permission("pji.cmd.inventory.create")    
		    .arguments(GenericArguments.string(Text.of("inv")))
		    .executor(new CMDCreate())
		    .build();
	
	public CommandSpec cmdYes = CommandSpec.builder()
		    .permission("pji.cmd.inventory.delete")
		    .executor(new CMDYes())
		    .build();
	
	public CommandSpec cmdDelete = CommandSpec.builder()
		    .permission("pji.cmd.inventory.delete")
		    .arguments(new InventoryElement(Text.of("inv")))
		    .child(cmdYes, "yes", "y")
		    .executor(new CMDDelete())
		    .build();

	public CommandSpec cmdList = CommandSpec.builder()
		    .permission("pji.cmd.inventory.list")
		    .executor(new CMDList())
		    .build();
	
	public CommandSpec cmdAdd = CommandSpec.builder()
		    .permission("pji.cmd.inventory.world.add")
		    .arguments(GenericArguments.world(Text.of("world")), new InventoryElement(Text.of("inv")), GenericArguments.optional(GenericArguments.bool(Text.of("true|false"))))
		    .executor(new CMDAdd())
		    .build();

	public CommandSpec cmdRemove = CommandSpec.builder()
		    .permission("pji.cmd.inventory.world.remove")
		    .arguments(GenericArguments.world(Text.of("world")), new InventoryElement(Text.of("inv")))
		    .executor(new CMDRemove())
		    .build();
	
	public CommandSpec cmdWorldList = CommandSpec.builder()
		    .permission("pji.cmd.inventory.world.list")
		    .arguments(GenericArguments.optional(GenericArguments.world(Text.of("world"))))
		    .executor(new com.gmail.trentech.pji.commands.world.CMDList())
		    .build();
	
	public CommandSpec cmdWorld = CommandSpec.builder()
			.permission("pji.cmd.inventory.world")
			.child(cmdRemove, "remove", "rm")
			.child(cmdAdd, "add", "a")
			.child(cmdWorldList, "list", "ls")
			.executor(new CMDWorld())
			.build();

	public CommandSpec cmdInventory = CommandSpec.builder()
			.permission("pji.cmd.inventory")
			.child(cmdSee, "see", "s")
			.child(cmdGet, "get", "g")
			.child(cmdCreate, "create", "c")
			.child(cmdDelete, "delete", "d")
			.child(cmdList, "list", "ls")
			.child(cmdWorld, "world", "w")
			.executor(new CMDInventory())
			.build();
}