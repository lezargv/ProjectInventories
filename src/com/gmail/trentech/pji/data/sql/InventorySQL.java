package com.gmail.trentech.pji.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.InventoryTranslator;

public class InventorySQL extends SQLUtils {

	public static void updateHotbar(Player player, String name, LinkedHashMap<Integer, ItemStack> hotbar) {
		System.out.println("update: " + name);
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + name + " SET Hotbar = ? WHERE Player = ?");

		    if(!hotbar.isEmpty()){  
		    	StringBuilder stringBuilder = new StringBuilder();
		    	
			    for (Entry<Integer, ItemStack> entry : hotbar.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + InventoryTranslator.serializeItemStack(entry.getValue()) + ";");
			    }
			    statement.setString(1, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
		    }else{
		    	statement.setString(1, null);
		    }

			statement.setString(2, player.getUniqueId().toString());
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateGrid(Player player, String name, LinkedHashMap<Integer, ItemStack> grid) {
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + name + " SET Inventory = ? WHERE Player = ?");

		    if(!grid.isEmpty()){
		    	StringBuilder stringBuilder = new StringBuilder();
		    	
			    for (Entry<Integer, ItemStack> entry : grid.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + InventoryTranslator.serializeItemStack(entry.getValue()) + ";");
			    }
			    statement.setString(1, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
		    }else{
		    	statement.setString(1, null);
		    }

			statement.setString(2, player.getUniqueId().toString());
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateArmor(Player player, String name, LinkedHashMap<Integer, ItemStack> armor) {
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + name + " SET Armor = ? WHERE Player = ?");

		    if(!armor.isEmpty()){   
		    	StringBuilder stringBuilder = new StringBuilder();
		    	
			    for (Entry<Integer, ItemStack> entry : armor.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + InventoryTranslator.serializeItemStack(entry.getValue()) + ";");
			    }
			    statement.setString(1, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
		    }else{
		    	statement.setString(1, null);
		    }

			statement.setString(2, player.getUniqueId().toString());
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateHealth(Player player, String name, double health) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + name + " SET Health = ? WHERE Player = ?");

		    statement.setDouble(1, health);
			statement.setString(2, player.getUniqueId().toString());
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateFoodLevel(Player player, String name, int foodLevel) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + name + " SET Food = ? WHERE Player = ?");

		    statement.setInt(1, foodLevel);
			statement.setString(2, player.getUniqueId().toString());
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateSaturation(Player player, String name, double saturation) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + name + " SET Saturation = ? WHERE Player = ?");

		    statement.setDouble(1, saturation);
			statement.setString(2, player.getUniqueId().toString());
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateExperienceLevel(Player player, String name, int expLevel) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + name + " SET ExpLevel = ? WHERE Player = ?");

		    statement.setInt(1, expLevel);
			statement.setString(2, player.getUniqueId().toString());
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateExperience(Player player, String name, int experience) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + name + " SET Experience = ? WHERE Player = ?");

		    statement.setInt(1, experience);
			statement.setString(2, player.getUniqueId().toString());
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void create(InventoryData inventoryData){	
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into " + inventoryData.getName() + " (Player, Hotbar, Inventory, Armor, Health, Food, Saturation, ExpLevel, Experience) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");	
			
		    statement.setString(1, inventoryData.getPlayer().getUniqueId().toString());
		    
		    LinkedHashMap<Integer, ItemStack> hotbar = inventoryData.getHotbar();
		    LinkedHashMap<Integer, ItemStack> grid = inventoryData.getGrid();
		    LinkedHashMap<Integer, ItemStack> armor = inventoryData.getArmor();
		    
		    StringBuilder stringBuilder = new StringBuilder();
		    
		    if(!hotbar.isEmpty()){   
			    for (Entry<Integer, ItemStack> entry : hotbar.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + InventoryTranslator.serializeItemStack(entry.getValue()) + ";");
			    }
			    statement.setString(2, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
		    }else{
		    	statement.setString(2, null);
		    }

		    if(!grid.isEmpty()){ 
			    stringBuilder = new StringBuilder();
			    for (Entry<Integer, ItemStack> entry : grid.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + InventoryTranslator.serializeItemStack(entry.getValue()) + ";");
			    }
			    statement.setString(3, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
		    }else{
		    	statement.setString(3, null);
		    }

		    if(!armor.isEmpty()){
		    	stringBuilder = new StringBuilder();
			    for (Entry<Integer, ItemStack> entry : armor.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + InventoryTranslator.serializeItemStack(entry.getValue()) + ";");
			    }
			    statement.setString(4, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
		    }else{
		    	statement.setString(4, null);
		    }

		    statement.setDouble(5, inventoryData.getHealth());
		    statement.setInt(6, inventoryData.getFood());
		    statement.setDouble(7, inventoryData.getSaturation());
		    statement.setInt(8, inventoryData.getExpLevel());
		    statement.setInt(9, inventoryData.getExperience());
		    
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static InventoryData get(Player player, String name){
		Optional<InventoryData> optionalInventoryData = Optional.empty();
		
		String playerUuid = player.getUniqueId().toString();

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + name);
		    
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				if (result.getString("Player").equalsIgnoreCase(playerUuid)) {
					LinkedHashMap<Integer, ItemStack> hotbar = new LinkedHashMap<>();
					LinkedHashMap<Integer, ItemStack> grid = new LinkedHashMap<>();
					LinkedHashMap<Integer, ItemStack> armor = new LinkedHashMap<>();
					
					if(result.getString("Hotbar") != null){
						String[] hotbarArray = result.getString("Hotbar").split(";");
						for(String slot : hotbarArray){
							String[] split = slot.split("\\^");
							hotbar.put(Integer.parseInt(split[0]), InventoryTranslator.deserializeItemStack(split[1]));
						}
					}

					if(result.getString("Inventory") != null){
						String[] gridArray = result.getString("Inventory").split(";");
						for(String slot : gridArray){
							String[] split = slot.split("\\^");
							grid.put(Integer.parseInt(split[0]), InventoryTranslator.deserializeItemStack(split[1]));
						}
					}

					if(result.getString("Armor") != null){
						String[] armorArray = result.getString("Armor").split(";");
						for(String slot : armorArray){
							String[] split = slot.split("\\^");
							armor.put(Integer.parseInt(split[0]), InventoryTranslator.deserializeItemStack(split[1]));
						}
					}

					double health = result.getDouble("Health");
					int food = result.getInt("Food");
					double saturation = result.getDouble("Saturation");
					int expLevel = result.getInt("ExpLevel");
					int experience = result.getInt("Experience");
					
					optionalInventoryData = Optional.of(new InventoryData(player, name, hotbar, grid, armor, health, food, saturation, expLevel, experience));
					
					break;
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(!optionalInventoryData.isPresent()){
			System.out.println("NOT PRESENT");
			InventoryData inventoryData = new InventoryData(player, name);
			InventorySQL.create(inventoryData);
			return inventoryData;
		}else{
			return optionalInventoryData.get();
		}
	}
}
