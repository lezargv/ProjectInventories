package com.gmail.trentech.pji.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Optional;

import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.type.GridInventory;

import com.gmail.trentech.pji.Main;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class InventoryTranslator {

	public static LinkedHashMap<Integer, String> serializeHotbar(Player player){
		Hotbar hotBar = player.getInventory().query(Hotbar.class);
		LinkedHashMap<Integer, String> hash = new LinkedHashMap<>();
		
		int i = 1;
		for(Inventory slotInv : hotBar.slots()){
			Optional<ItemStack> peek = slotInv.peek();
			
			if(peek.isPresent()){
				ItemStack itemStack = peek.get();
				String stackString = serializeItemStack(itemStack);				
				hash.put(i, stackString);
			}else{
				hash.remove(i);
			}
			i++;
		}
		
		return hash;
	}
	
	public static LinkedHashMap<Integer, String> serializeGrid(Player player){
		GridInventory gridInventory = player.getInventory().query(GridInventory.class);
		LinkedHashMap<Integer, String> hash = new LinkedHashMap<>();
		
		int i = 1;
		for(Inventory slotInv : gridInventory.slots()){
			Optional<ItemStack> peek = slotInv.peek();
			
			if(peek.isPresent()){
				ItemStack itemStack = peek.get();
				String stackString = serializeItemStack(itemStack);				
				hash.put(i, stackString);
			}else{
				hash.remove(i);
			}
			i++;
		}
		
		return hash;
	}
	
	public static LinkedHashMap<Integer, String> serializeArmor(Player player){
		LinkedHashMap<Integer, String> hash = new LinkedHashMap<>();
		
		if(player.getHelmet().isPresent()){
			ItemStack itemStack = player.getHelmet().get();
			String stackString = serializeItemStack(itemStack);			
			hash.put(1, stackString);
		}
		
		if(player.getChestplate().isPresent()){
			ItemStack itemStack = player.getChestplate().get();
			String stackString = serializeItemStack(itemStack);		
			hash.put(2, stackString);
		}
		
		if(player.getLeggings().isPresent()){
			ItemStack itemStack = player.getLeggings().get();
			String stackString = serializeItemStack(itemStack);			
			hash.put(3, stackString);
		}
		
		if(player.getBoots().isPresent()){
			ItemStack itemStack = player.getBoots().get();
			String stackString = serializeItemStack(itemStack);			
			hash.put(4, stackString);
		}
		
		return hash;
	}
	
	public static String serializeItemStack(ItemStack itemStack){
		ConfigurationNode node = ConfigurateTranslator.instance().translateData(itemStack.toContainer());
		
		StringWriter stringWriter = new StringWriter();
		try {
		    HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
		} catch (IOException e) {
		    e.printStackTrace();
		}

		return stringWriter.toString();
	}
	
	public static ItemStack deserializeItemStack(String item) {
		ConfigurationNode node = null;
		try {
			node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    ConfigurateTranslator translator = ConfigurateTranslator.instance();
	    DataManager manager = Main.getGame().getDataManager();

		DataView dataView = translator.translateFrom(node);

	    Optional<ItemStack> deserializedOptional = manager.deserialize(ItemStack.class, dataView);

	    if(deserializedOptional.isPresent()) {
	        return deserializedOptional.get();
	    }
	    
	    return null;
	}
}
