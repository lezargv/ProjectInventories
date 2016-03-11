package com.gmail.trentech.pji.data;

import java.util.LinkedHashMap;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.type.GridInventory;

import com.gmail.trentech.pji.data.sql.SQLInventory;
import com.gmail.trentech.pji.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class InventoryPlayer {

	private Player player;
	
	private InventoryPlayer(Player player){
		this.player = player;
	}
	
	public static InventoryPlayer get(Player player){
		return new InventoryPlayer(player);
	}
	
	public void setInventory(String name){
		InventoryData inventoryData = SQLInventory.get(player, name);

		LinkedHashMap<Integer, ItemStack> hotbar = inventoryData.getHotbar();
		LinkedHashMap<Integer, ItemStack> grid = inventoryData.getGrid();
		LinkedHashMap<Integer, ItemStack> armor = inventoryData.getArmor();

		int i = 1;
		for(Inventory slotInv : this.player.getInventory().query(Hotbar.class).slots()){
			slotInv.clear();

			if(!hotbar.containsKey(i)){
				i++;
				continue;
			}

			slotInv.offer(hotbar.get(i));
			
			i++;
		}
		
		i = 1;
		for(Inventory slotInv : this.player.getInventory().query(GridInventory.class).slots()){
			slotInv.clear();

			if(!grid.containsKey(i)){
				i++;
				continue;
			}

			slotInv.offer(grid.get(i));
			
			i++;
		}

		if(armor.containsKey(1)){
			this.player.setHelmet(armor.get(1));
		}else{
			this.player.setHelmet(null);
		}
	
		if(armor.containsKey(2)){
			this.player.setChestplate(armor.get(2));
		}else{
			this.player.setChestplate(null);
		}
	
		if(armor.containsKey(3)){
			this.player.setLeggings(armor.get(3));
		}else{
			this.player.setLeggings(null);
		}

		if(armor.containsKey(4)){
			this.player.setBoots(armor.get(4));
		}else{
			this.player.setBoots(null);
		}
		
		ConfigurationNode config = new ConfigManager().getConfig();
		
		if(config.getNode("options", "health").getBoolean()){
			player.offer(Keys.HEALTH, inventoryData.getHealth());
		}

		if(config.getNode("options", "health").getBoolean()){
			player.offer(Keys.FOOD_LEVEL, inventoryData.getFood());
			player.offer(Keys.SATURATION, inventoryData.getSaturation());
		}
		
		if(config.getNode("options", "experience").getBoolean()){
			player.offer(Keys.EXPERIENCE_LEVEL, inventoryData.getExpLevel());
			player.offer(Keys.TOTAL_EXPERIENCE, inventoryData.getExperience());
		}
	}
	
	public void saveInventory(String name){
		InventoryData inventoryData = SQLInventory.get(player, name);
		
		LinkedHashMap<Integer, ItemStack> hotbar = new LinkedHashMap<>();
		LinkedHashMap<Integer, ItemStack> grid = new LinkedHashMap<>();
		LinkedHashMap<Integer, ItemStack> armor = new LinkedHashMap<>();
		
		int i = 1;
		for(Inventory slotInv : this.player.getInventory().query(Hotbar.class).slots()){
			Optional<ItemStack> peek = slotInv.peek();
			
			if(peek.isPresent()){
				hotbar.put(i, peek.get());
			}else{
				hotbar.remove(i);
			}
			i++;
		}
		inventoryData.setHotbar(hotbar);

		i = 1;
		for(Inventory slotInv : this.player.getInventory().query(GridInventory.class).slots()){
			Optional<ItemStack> peek = slotInv.peek();
			
			if(peek.isPresent()){		
				grid.put(i, peek.get());
			}else{
				grid.remove(i);
			}
			i++;
		}
		inventoryData.setGrid(grid);
		
		if(player.getHelmet().isPresent()){
			ItemStack itemStack = player.getHelmet().get();		
			armor.put(1, itemStack);
		}
		
		if(player.getChestplate().isPresent()){
			ItemStack itemStack = player.getChestplate().get();	
			armor.put(2, itemStack);
		}
		
		if(player.getLeggings().isPresent()){
			ItemStack itemStack = player.getLeggings().get();		
			armor.put(3, itemStack);
		}
		
		if(player.getBoots().isPresent()){
			ItemStack itemStack = player.getBoots().get();	
			armor.put(4, itemStack);
		}
		inventoryData.setArmor(armor);
		
		inventoryData.setHealth(player.get(Keys.HEALTH).get());
		inventoryData.setFood(player.get(Keys.FOOD_LEVEL).get());
		inventoryData.setSaturation(player.get(Keys.SATURATION).get());
		inventoryData.setExperience(player.get(Keys.TOTAL_EXPERIENCE).get());
		inventoryData.setExpLevel(player.get(Keys.EXPERIENCE_LEVEL).get());
	}

}
