package com.gmail.trentech.pji.init;

import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.pji.commands.CMDAdd;
import com.gmail.trentech.pji.commands.CMDClear;
import com.gmail.trentech.pji.commands.CMDClearYes;
import com.gmail.trentech.pji.commands.CMDCreate;
import com.gmail.trentech.pji.commands.CMDDelete;
import com.gmail.trentech.pji.commands.CMDDeleteYes;
import com.gmail.trentech.pji.commands.CMDGet;
import com.gmail.trentech.pji.commands.CMDInfo;
import com.gmail.trentech.pji.commands.CMDInventory;
import com.gmail.trentech.pji.commands.CMDKit;
import com.gmail.trentech.pji.commands.CMDList;
import com.gmail.trentech.pji.commands.CMDRemove;
import com.gmail.trentech.pji.commands.CMDSee;
import com.gmail.trentech.pji.commands.elements.InventoryElement;

public class Commands {

	private CommandElement element = GenericArguments.flags().flag("help").setAcceptsArbitraryLongFlags(true).buildWith(GenericArguments.none());
	
	private CommandSpec cmdSee = CommandSpec.builder()
		    .permission("pji.cmd.inventory.see")    
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.player(Text.of("player"))), 
		    		GenericArguments.optional(new InventoryElement(Text.of("inv"))))
		    .executor(new CMDSee())
		    .build();
	
	private CommandSpec cmdGet = CommandSpec.builder()
		    .permission("pji.cmd.inventory.get")    
		    .arguments(element,
		    		GenericArguments.optional(new InventoryElement(Text.of("inv"))))
		    .executor(new CMDGet())
		    .build();
	
	private CommandSpec cmdKit = CommandSpec.builder()
		    .permission("pji.cmd.inventory.kit")    
		    .arguments(element,
		    		GenericArguments.optional(new InventoryElement(Text.of("inv"))))
		    .executor(new CMDKit())
		    .build();
	
	private CommandSpec cmdCreate = CommandSpec.builder()
		    .permission("pji.cmd.inventory.create")    
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.string(Text.of("inv"))), 
		    		GenericArguments.flags().valueFlag(GenericArguments.string(Text.of("permission")), "p")
		    		.valueFlag(GenericArguments.catalogedElement(Text.of("gamemode"), GameMode.class), "g").buildWith(GenericArguments.none()))
		    .executor(new CMDCreate())
		    .build();
	
	private CommandSpec cmdClearYes = CommandSpec.builder()
		    .permission("pji.cmd.inventory.clear")
		    .executor(new CMDClearYes())
		    .build();
	
	private CommandSpec cmdClear = CommandSpec.builder()
		    .permission("pji.cmd.inventory.clear")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.player(Text.of("player"))))
		    .child(cmdClearYes, "yes", "y")
		    .executor(new CMDClear())
		    .build();
	
	private CommandSpec cmdDeleteYes = CommandSpec.builder()
		    .permission("pji.cmd.inventory.delete")
		    .executor(new CMDDeleteYes())
		    .build();
	
	private CommandSpec cmdDelete = CommandSpec.builder()
		    .permission("pji.cmd.inventory.delete")
		    .arguments(element,
		    		GenericArguments.optional(new InventoryElement(Text.of("inv"))))
		    .child(cmdDeleteYes, "yes", "y")
		    .executor(new CMDDelete())
		    .build();

	private CommandSpec cmdList = CommandSpec.builder()
		    .permission("pji.cmd.inventory.list")
		    .arguments(element)
		    .executor(new CMDList())
		    .build();
	
	private CommandSpec cmdAdd = CommandSpec.builder()
		    .permission("pji.cmd.inventory.add")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(new InventoryElement(Text.of("inv"))), 
		    		GenericArguments.optional(GenericArguments.bool(Text.of("true|false"))))
		    .executor(new CMDAdd())
		    .build();

	private CommandSpec cmdRemove = CommandSpec.builder()
		    .permission("pji.cmd.inventory.remove")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(new InventoryElement(Text.of("inv"))))
		    .executor(new CMDRemove())
		    .build();
	
	private CommandSpec cmdInfo = CommandSpec.builder()
		    .permission("pji.cmd.inventory.info")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))))
		    .executor(new CMDInfo())
		    .build();

	public CommandSpec cmdInventory = CommandSpec.builder()
			.permission("pji.cmd.inventory")
			.child(cmdSee, "see", "s")
			.child(cmdGet, "get", "g")
			.child(cmdKit, "kit", "k")
			.child(cmdCreate, "create", "c")
			.child(cmdClear, "clear", "cl")
			.child(cmdDelete, "delete", "d")
			.child(cmdList, "list", "ls")
			.child(cmdRemove, "remove", "rm")
			.child(cmdAdd, "add", "a")
			.child(cmdInfo, "info", "i")
			.executor(new CMDInventory())
			.build();
}