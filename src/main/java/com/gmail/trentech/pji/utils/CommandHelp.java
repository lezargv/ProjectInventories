package com.gmail.trentech.pji.utils;

import org.spongepowered.api.Sponge;

import com.gmail.trentech.helpme.help.Argument;
import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.helpme.help.Usage;

public class CommandHelp {

	public static void init() {
		if (Sponge.getPluginManager().isLoaded("helpme")) {
			Usage usageInv = new Usage(Argument.of("<inv>", "Specifies the name of the targgeted inventory"));
			
			Help invCreate = new Help("inventory create", "create", "Create a new inventory")
					.setPermission("pji.cmd.inventory.create")
					.setUsage(usageInv)
					.addExample("/inventory create nether");
			
			Help invDelete = new Help("inventory delete", "delete", "Delete an existing inventory. WARNING: This cannot be undone.")
					.setPermission("pji.cmd.inventory.delete")
					.setUsage(usageInv)
					.addExample("/inventory delete nether");
			
			Help invInfo = new Help("inventory info", "info", "Lists the worlds and their assigned inventories")
					.setPermission("pji.cmd.inventory.info");
			
			Help invList = new Help("inventory list", "list", "List all inventories")
					.setPermission("pji.cmd.inventory.list");
			
			Usage usageSet = new Usage(Argument.of("<world>", "Specifies the targetted world"))
					.addArgument(Argument.of("<inv>", "Specifies the name of the targgeted inventory"));
				
			Help invSet = new Help("inventory set", "set", "Set an inventory for the specified world")
					.setPermission("pji.cmd.inventory.set")
					.setUsage(usageSet)
					.addExample("/inventory set DIM-1 nether");	
			
			Help inv = new Help("inventory", "inventory", "Base Project Inventories command")
					.setPermission("pji.cmd.inventory")
					.addChild(invSet)
					.addChild(invList)
					.addChild(invInfo)
					.addChild(invDelete)
					.addChild(invCreate);
			
			Help.register(inv);
		}
	}
}
