package com.gmail.trentech.pji.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.pjc.help.Argument;
import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjc.help.Usage;
import com.gmail.trentech.pji.Main;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Common {

	public static void init() {
		initConfig();
		initHelp();
		initData();
	}
	
	public static void initData() {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + sqlManager.getPrefix("PJI.WORLDS") + " (UUID TEXT, Data LONGTEXT)");

			statement.executeUpdate();

			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + sqlManager.getPrefix("PJI.PLAYERS") + " (UUID TEXT, Data LONGTEXT)");

			statement.executeUpdate();

			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + sqlManager.getPrefix("PJI.CHESTS") + " (UUID TEXT, Data LONGTEXT)");

			statement.executeUpdate();
			
			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + sqlManager.getPrefix("PJI.INVENTORIES") + " (Name TEXT, Data LONGTEXT)");

			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void initHelp() {
		Usage usageCreate = new Usage(Argument.of("<inv>", "Specifies the name of the targeted inventory"))
				.addArgument(Argument.of("[-p <permission>]", "Adds a permission node to inventory"))
				.addArgument(Argument.of("[-g <gamemode>]", "Sets a gamemode type when inventory is active."));
		
		Help invCreate = new Help("inventory create", "create", "Create a new inventory or edit existing")
				.setPermission("pji.cmd.inventory.create")
				.setUsage(usageCreate)
				.addExample("/inventory create tools -p my.permission.node -g ");
	
		Usage usageInv = new Usage(Argument.of("<inv>", "Specifies the name of the targeted inventory"));
		
		Help invDelete = new Help("inventory delete", "delete", "Delete an existing inventory. WARNING: This cannot be undone.")
				.setPermission("pji.cmd.inventory.delete")
				.setUsage(usageInv)
				.addExample("/inventory delete creative");
		
		Usage usagePlayer = new Usage(Argument.of("[player]", "Specifies the player you want to clear inventory of"));
		
		Help invClear = new Help("inventory clear", "clear", "Deletes all items in players inv. WARNING: This cannot be undone.")
				.setPermission("pji.cmd.inventory.clear")
				.setUsage(usagePlayer)
				.addExample("/inventory clear")
				.addExample("/inventory clear Notch");
		
		Help invKit = new Help("inventory kit", "kit", "Sets a pre-defined inventory the first time player equips.")
				.setPermission("pji.cmd.inventory.kit")
				.setUsage(usageInv)
				.addExample("/inventory kit tools");
		
		Help invList = new Help("inventory list", "list", "List all inventories. Hover over to see permission and gamemode assignments if any.")
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
		
		Usage usageAdd = new Usage(Argument.of("<world>", "Specifies the targeted world"))
				.addArgument(Argument.of("<inv>", "Specifies the name of the targeted inventory"))
				.addArgument(Argument.of("[true|false]", "Set whether or not this inventory is the default for specified world"));
		
		Help invAdd = new Help("inventory add", "add", "Adds an inventory to the specified world or alters already assigned")
				.setPermission("pji.cmd.inventory.add")
				.setUsage(usageAdd)
				.addExample("/inventory add DIM-1 nether");

		Usage usageRemove = new Usage(Argument.of("<world>", "Specifies the targeted world"))
				.addArgument(Argument.of("<inv>", "Specifies the name of the targeted inventory"));
		
		Help invRemove = new Help("inventory remove", "remove", "Removes an inventory from the specified world")
				.setPermission("pji.cmd.inventory.remove")
				.setUsage(usageRemove)
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
				.addChild(invKit)
				.addChild(invDelete)
				.addChild(invCreate)
				.addChild(invClear)
				.addChild(invGet)
				.addChild(invSee)
				.addChild(invRemove)
				.addChild(invAdd)
				.addChild(invInfo);
		
		Help.register(inv);
	}
	
	public static void initConfig() {
		ConfigManager configManager = ConfigManager.init(Main.getPlugin());
		CommentedConfigurationNode config = configManager.getConfig();

		if (config.getNode("options", "health").isVirtual()) {
			config.getNode("options", "health").setValue(true).setComment("Enable inventory specific health");
		}
		if (config.getNode("options", "hunger").isVirtual()) {
			config.getNode("options", "hunger").setValue(true).setComment("Enable inventory specific hunger");
		}
		if (config.getNode("options", "experience").isVirtual()) {
			config.getNode("options", "experience").setValue(true).setComment("Enable inventory specific experience");
		}
		if (config.getNode("options", "potion-effects").isVirtual()) {
			config.getNode("options", "potion-effects").setValue(true).setComment("Enable inventory specific potion effects");
		}			
		if (config.getNode("options", "default-on-world-change").isVirtual()) {
			config.getNode("options", "default-on-world-change").setValue(false).setComment("Always set inventory to world default when entering");
		}
		if (config.getNode("options", "per-world-enderchests").isVirtual()) {
			config.getNode("options", "per-world-enderchests").setValue(false).setComment("Seperate ender chest inventory for each world");
		}
		if (config.getNode("settings", "sql").isVirtual()) {
			config.getNode("settings", "sql", "login-delay").setValue(35).setComment("Sets delay in ticks the server will wait to set player inventory on login. Change this is de-sync issues occur with Bungee servers");
		}
		
		configManager.save();
	}
}
