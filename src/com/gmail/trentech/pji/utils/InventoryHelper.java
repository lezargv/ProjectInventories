package com.gmail.trentech.pji.utils;

import java.io.IOException;
import java.util.Optional;

import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.type.GridInventory;

import com.gmail.trentech.pji.Main;

import ninja.leaping.configurate.ConfigurationNode;

public class InventoryHelper {

	public static void save(Player player, String invName){
		ConfigManager configManager = new ConfigManager(invName, player.getUniqueId().toString() + ".conf");
		ConfigurationNode config = configManager.getConfig();
		
		GridInventory gridInventory = player.getInventory().query(GridInventory.class);

		Hotbar hotBar = player.getInventory().query(Hotbar.class);
		
		int i = 1;
		for(Inventory slotInv : hotBar.slots()){
			Optional<ItemStack> peek = slotInv.peek();
			if(peek.isPresent()){
				ItemStack itemStack = peek.get();
				
				ConfigurationNode node = ConfigurateTranslator.instance().translateData(itemStack.toContainer());
				
				//String stackString = node.getString();
				// SAVE TO DB
				config.getNode("hotbar", Integer.toString(i)).setValue(node);
			}else{
				// REMOVE FROM DB
				config.getNode("hotbar").removeChild(Integer.toString(i));
			}
			i++;
		}

		i = 1;
		for(Inventory slotInv : gridInventory.slots()){
			Optional<ItemStack> peek = slotInv.peek();
			if(peek.isPresent()){
				ItemStack itemStack = peek.get();
				
				ConfigurationNode node = ConfigurateTranslator.instance().translateData(itemStack.toContainer());
				config.getNode("inventory", Integer.toString(i)).setValue(node);
			}
			i++;
		}
		
		if(player.getHelmet().isPresent()){
			ItemStack itemStack = player.getHelmet().get();

			ConfigurationNode node = ConfigurateTranslator.instance().translateData(itemStack.toContainer());
			config.getNode("armor", "1").setValue(node);
		}
		
		if(player.getChestplate().isPresent()){
			ItemStack itemStack = player.getChestplate().get();

			ConfigurationNode node = ConfigurateTranslator.instance().translateData(itemStack.toContainer());
			config.getNode("armor", "2").setValue(node);
		}
		
		if(player.getLeggings().isPresent()){
			ItemStack itemStack = player.getLeggings().get();

			ConfigurationNode node = ConfigurateTranslator.instance().translateData(itemStack.toContainer());
			config.getNode("armor", "3").setValue(node);
		}
		
		if(player.getBoots().isPresent()){
			ItemStack itemStack = player.getBoots().get();

			ConfigurationNode node = ConfigurateTranslator.instance().translateData(itemStack.toContainer());
			config.getNode("armor", "4").setValue(node);
		}

		double health = player.health().get();
		config.getNode("health").setValue(health);	
		
		int foodLevel = player.foodLevel().get();
		config.getNode("food").setValue(foodLevel);

		double saturation = player.saturation().get();
		config.getNode("saturation").setValue(saturation);
		
		configManager.save();
	}
	
	public static void set(Player player, String invName){
		ConfigManager configManager = new ConfigManager(invName, player.getUniqueId().toString() + ".conf");
		ConfigurationNode config = configManager.getConfig();
		
		GridInventory gridInventory = player.getInventory().query(GridInventory.class);

		Hotbar hotBar = player.getInventory().query(Hotbar.class);
		
		int i = 1;
		for(Inventory slotInv : hotBar.slots()){
			String slot = Integer.toString(i);
			
			i++;
			
			slotInv.clear();
			// GET SLOT FROM DB, CONVERT TO NODE
			//ConfigurationNode node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(GET SLOT STACK STRING))).build().load();
			ConfigurationNode node = config.getNode("hotbar", slot);
			
			if(node.isVirtual()){
				continue;
			}

			ItemStack itemStack = null;
			try {
				itemStack = deserializeItemStack(node, slot);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			slotInv.offer(itemStack);
		}
		
		i = 1;
		for(Inventory slotInv : gridInventory.slots()){
			String slot = Integer.toString(i);
			
			i++;
			
			slotInv.clear();
			
			ConfigurationNode node = config.getNode("inventory", slot);

			if(node.isVirtual()){
				continue;
			}
			
			ItemStack itemStack = null;
			try {
				itemStack = deserializeItemStack(node, slot);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			slotInv.offer(itemStack);
		}

		player.setHelmet(null);
		player.setChestplate(null);
		player.setLeggings(null);
		player.setBoots(null);
		
		if(!config.getNode("armor", "1").isVirtual()){
			ItemStack itemStack = null;
			try {
				itemStack = deserializeItemStack(config.getNode("armor", "1"), "1");
			} catch (IOException e) {
				e.printStackTrace();
			}

			player.setHelmet(itemStack);
		}
		
		if(!config.getNode("armor", "2").isVirtual()){
			ItemStack itemStack = null;
			try {
				itemStack = deserializeItemStack(config.getNode("armor", "2"), "2");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			player.setChestplate(itemStack);
		}
		
		if(!config.getNode("armor", "3").isVirtual()){
			ItemStack itemStack = null;
			try {
				itemStack = deserializeItemStack(config.getNode("armor", "3"), "3");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			player.setLeggings(itemStack);
		}
		
		if(!config.getNode("armor", "4").isVirtual()){
			ItemStack itemStack = null;
			try {
				itemStack = deserializeItemStack(config.getNode("armor", "4"), "4");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			player.setBoots(itemStack);
		}
		
		if(!config.getNode("health").isVirtual()){
			player.health().set(config.getNode("health").getDouble());
		}
		
		if(!config.getNode("food").isVirtual()){
			player.foodLevel().set(config.getNode("food").getInt());
		}
		
		if(!config.getNode("saturation").isVirtual()){
			player.saturation().set(config.getNode("saturation").getDouble());
		}
	}
	
	public static ItemStack deserializeItemStack(ConfigurationNode node, String path) throws IOException {
	    ConfigurateTranslator translator = ConfigurateTranslator.instance();
	    DataManager manager = Main.getGame().getDataManager();

		DataView dataView = translator.translateFrom(node);
		dataView = dataView.getView(DataQuery.of(path)).get();
		
	    Optional<ItemStack> deserializedOptional = manager.deserialize(ItemStack.class, dataView);

	    if(deserializedOptional.isPresent()) {
	        return deserializedOptional.get();
	    } else {
	    	throw new IOException("Invalid node");
	    }
	}
}
