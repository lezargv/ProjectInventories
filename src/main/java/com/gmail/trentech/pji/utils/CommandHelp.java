package com.gmail.trentech.pji.utils;

import org.spongepowered.api.Sponge;

import com.gmail.trentech.helpme.help.Argument;
import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.helpme.help.Usage;

public class CommandHelp {

	public static void init() {
		if (Sponge.getPluginManager().isLoaded("helpme")) {
			Usage usageCreate = new Usage(Argument.of("<inv>", "Specifies the name of the targeted inventory"))
					.addArgument(Argument.of("[permission]", "Adds a permission node to inventory"));
					
			Help invCreate = new Help("inventory create", "create", "Create a new inventory or edit existing")
					.setPermission("pji.cmd.inventory.create")
					.setUsage(usageCreate)
					.addExample("/inventory create nether");
		
			Usage usageInv = new Usage(Argument.of("<inv>", "Specifies the name of the targeted inventory"));
			
			Help invDelete = new Help("inventory delete", "delete", "Delete an existing inventory. WARNING: This cannot be undone.")
					.setPermission("pji.cmd.inventory.delete")
					.setUsage(usageInv)
					.addExample("/inventory delete nether");
			
			Help invList = new Help("inventory list", "list", "List all inventories")
					.setPermission("pji.cmd.inventory.list");
			
			Help invGet = new Help("inventory get", "get", "Changes players current inventory")
					.setPermission("pji.cmd.inventory.get")
					.setUsage(usageInv)
					.addExample("/inventory get nether");
			
			Usage usageSee = new Usage(Argument.of("<player>", "Specifies the name of the targeted player"))
					.addArgument(Argument.of("<inv>", "Specifies the name of the targeted inventory"));
			
			Help invSee = new Help("inventory see", "see", "View another players inventory")
					.setPermission("pji.cmd.inventory.see")
					.setUsage(usageSee)
					.addExample("/inventory get nether");
			
			Usage usageSet = new Usage(Argument.of("<world>", "Specifies the targeted world"))
					.addArgument(Argument.of("<inv>", "Specifies the name of the targeted inventory"));
				
			Help invAdd = new Help("inventory add", "add", "Adds an inventory to the specified world or altered already assigned")
					.setPermission("pji.cmd.inventory.add")
					.setUsage(usageSet)
					.addExample("/inventory add DIM-1 nether");

			Help invRemove = new Help("inventory remove", "remove", "Removes an inventory from the specified world")
					.setPermission("pji.cmd.inventory.remove")
					.setUsage(usageSet)
					.addExample("/inventory remove DIM-1 nether");
			
			Usage usageWorld = new Usage(Argument.of("[world]", "Specifies the targeted world"));
					
			Help invInfo = new Help("inventory info", "info", "List all worlds and there assigned inventories")
					.setPermission("pji.cmd.inventory.info")
					.setUsage(usageWorld)
					.addExample("/inventory info")
					.addExample("/inventory info world");

			Help inv = new Help("inventory", "inventory", "Base Project Inventories command")
					.setPermission("pji.cmd.inventory")
					.addChild(invList)
					.addChild(invDelete)
					.addChild(invCreate)
					.addChild(invGet)
					.addChild(invSee)
					.addChild(invRemove)
					.addChild(invAdd)
					.addChild(invInfo);
			
			Help.register(inv);
		}
	}
}
