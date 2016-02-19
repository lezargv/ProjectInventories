package com.gmail.trentech.pji.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.type.GridInventory;

import com.gmail.trentech.pji.utils.ConfigManager;
import com.gmail.trentech.pji.utils.SQLUtils;

import ninja.leaping.configurate.ConfigurationNode;

public class InventoryData extends SQLUtils {

	private final Player player;
	private final String invName;
	
	private LinkedHashMap<Integer, String> hotbar = new LinkedHashMap<>();
	private LinkedHashMap<Integer, String> inventory = new LinkedHashMap<>();
	private LinkedHashMap<Integer, String> armor = new LinkedHashMap<>();
	
	private double health = 20;
	private int food = 10;
	private double saturation = 20;
	
	private InventoryData(Player player, String invName, LinkedHashMap<Integer, String> hotbar, LinkedHashMap<Integer, String> inventory, LinkedHashMap<Integer, String> armor,
			double health, int food, double saturation){
		this.player = player;
		this.invName = invName;
		this.hotbar = hotbar;
		this.inventory = inventory;
		this.armor = armor;
		this.health = health;
		this.food = food;
		this.saturation = saturation;
	}
	
	public InventoryData(Player player, String invName){
		this.player = player;
		this.invName = invName;
		
		create();
	}
	
	public void save(){
		this.hotbar = InventoryTranslator.serializeHotbar(player);
		this.inventory = InventoryTranslator.serializeGrid(player);
		this.armor = InventoryTranslator.serializeArmor(player);
		this.health = player.health().get();
		this.food = player.foodLevel().get();
		this.saturation = player.saturation().get();

		updateHotbar();
		updateInventory();
		updateArmor();
		
		ConfigurationNode config = new ConfigManager().getConfig();
		
		if(config.getNode("health").getBoolean()){
			updateHealth();
		}
		
		if(config.getNode("hunger").getBoolean()){
			updateFood();
			updateSaturation();
		}
		
		if(config.getNode("experience").getBoolean()){
			updateFood();
			updateSaturation();
		}
	}
	
	public void set() throws IOException{
		GridInventory gridInventory = this.player.getInventory().query(GridInventory.class);

		Hotbar hotBar = this.player.getInventory().query(Hotbar.class);
		
		int i = 1;
		for(Inventory slotInv : hotBar.slots()){
			final int slot = i;
			i++;
			
			slotInv.clear();
			
			if(!this.hotbar.containsKey(slot)){
				continue;
			}

			ItemStack itemStack = InventoryTranslator.deserializeItemStack(this.hotbar.get(slot));

			slotInv.offer(itemStack);
		}
		
		i = 1;
		for(Inventory slotInv : gridInventory.slots()){
			final int slot = i;
			i++;
			
			slotInv.clear();
			
			if(!this.inventory.containsKey(slot)){
				continue;
			}

			ItemStack itemStack = InventoryTranslator.deserializeItemStack(this.inventory.get(slot));
			
			slotInv.offer(itemStack);
		}

		this.player.setHelmet(null);
		if(this.armor.containsKey(1)){
			ItemStack itemStack = InventoryTranslator.deserializeItemStack(this.armor.get(1));
			this.player.setHelmet(itemStack);
		}
		
		this.player.setChestplate(null);
		if(this.armor.containsKey(2)){
			ItemStack itemStack = InventoryTranslator.deserializeItemStack(this.armor.get(2));
			this.player.setChestplate(itemStack);
		}
		
		this.player.setLeggings(null);
		if(this.armor.containsKey(3)){
			ItemStack itemStack = InventoryTranslator.deserializeItemStack(this.armor.get(3));
			this.player.setLeggings(itemStack);
		}
		
		this.player.setBoots(null);
		if(this.armor.containsKey(4)){
			ItemStack itemStack = InventoryTranslator.deserializeItemStack(this.armor.get(4));
			this.player.setBoots(itemStack);
		}
		
		ConfigurationNode config = new ConfigManager().getConfig();
		
		if(config.getNode("health").getBoolean()){
			player.health().set(this.health);
		}

		if(config.getNode("health").getBoolean()){
			player.foodLevel().set(this.food);
			player.saturation().set(this.saturation);
		}
		
		if(config.getNode("experience").getBoolean()){

		}
	}
	
	private void updateHotbar() {
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + this.invName + " SET Hotbar = ? WHERE Player = ?");
		    
		    StringBuilder stringBuilder = new StringBuilder();
		    
		    if(!this.hotbar.isEmpty()){   
			    for (Entry<Integer, String> entry : this.hotbar.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + entry.getValue() + ";");
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
	
	private void updateInventory() {
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + this.invName + " SET Inventory = ? WHERE Player = ?");
		    
		    StringBuilder stringBuilder = new StringBuilder();
		    
		    if(!this.inventory.isEmpty()){   
			    for (Entry<Integer, String> entry : this.inventory.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + entry.getValue() + ";");
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
	
	private void updateArmor() {
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + this.invName + " SET Armor = ? WHERE Player = ?");
		    
		    StringBuilder stringBuilder = new StringBuilder();
		    
		    if(!this.armor.isEmpty()){   
			    for (Entry<Integer, String> entry : this.armor.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + entry.getValue() + ";");
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
	
	private void updateHealth() {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + this.invName + " SET Health = ? WHERE Player = ?");

		    statement.setDouble(1, this.health);
			statement.setString(2, player.getUniqueId().toString());
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void updateFood() {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + this.invName + " SET Food = ? WHERE Player = ?");

		    statement.setInt(1, this.food);
			statement.setString(2, player.getUniqueId().toString());
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void updateSaturation() {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + this.invName + " SET Saturation = ? WHERE Player = ?");

		    statement.setDouble(1, this.saturation);
			statement.setString(2, player.getUniqueId().toString());
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void create(){	
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into " + this.invName + " (Player, Hotbar, Inventory, Armor, Health, Food, Saturation) VALUES (?, ?, ?, ?, ?, ?, ?)");	
			
		    statement.setString(1, this.player.getUniqueId().toString());
		    
		    StringBuilder stringBuilder = new StringBuilder();
		    
		    if(!this.hotbar.isEmpty()){   
			    for (Entry<Integer, String> entry : this.hotbar.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + entry.getValue() + ";");
			    }
			    statement.setString(2, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
		    }else{
		    	statement.setString(2, null);
		    }

		    if(!this.inventory.isEmpty()){ 
			    stringBuilder = new StringBuilder();
			    for (Entry<Integer, String> entry : this.inventory.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + entry.getValue() + ";");
			    }
			    statement.setString(3, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
		    }else{
		    	statement.setString(3, null);
		    }

		    if(!this.armor.isEmpty()){
		    	stringBuilder = new StringBuilder();
			    for (Entry<Integer, String> entry : this.armor.entrySet()){
			    	stringBuilder.append(entry.getKey() + "^" + entry.getValue() + ";");
			    }
			    statement.setString(4, stringBuilder.toString().substring(0, stringBuilder.length() - 1));
		    }else{
		    	statement.setString(4, null);
		    }

		    statement.setDouble(5, this.health);
		    statement.setInt(6, this.food);
		    statement.setDouble(7, this.saturation);

			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static InventoryData get(Player player, String invName){
		Optional<InventoryData> optionalInventoryData = Optional.empty();
		
		String playerUuid = player.getUniqueId().toString();

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + invName);
		    
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				if (result.getString("Player").equalsIgnoreCase(playerUuid)) {
					LinkedHashMap<Integer, String> hotbar = new LinkedHashMap<>();
					LinkedHashMap<Integer, String> inventory = new LinkedHashMap<>();
					LinkedHashMap<Integer, String> armor = new LinkedHashMap<>();
					
					if(result.getString("Hotbar") != null){
						String[] hotbarArray = result.getString("Hotbar").split(";");
						for(String slot : hotbarArray){
							String[] split = slot.split("\\^");
							hotbar.put(Integer.parseInt(split[0]), split[1]);
						}
					}

					if(result.getString("Inventory") != null){
						String[] invArray = result.getString("Inventory").split(";");
						for(String slot : invArray){
							String[] split = slot.split("\\^");
							inventory.put(Integer.parseInt(split[0]), split[1]);
						}
					}

					if(result.getString("Armor") != null){
						String[] armorArray = result.getString("Armor").split(";");
						for(String slot : armorArray){
							String[] split = slot.split("\\^");
							armor.put(Integer.parseInt(split[0]), split[1]);
						}
					}

					double health = result.getDouble("Health");
					int food = result.getInt("Food");
					double saturation = result.getDouble("Saturation");
					
					optionalInventoryData = Optional.of(new InventoryData(player, invName, hotbar, inventory, armor, health, food, saturation));
					
					break;
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(!optionalInventoryData.isPresent()){
			return new InventoryData(player, invName);
		}else{
			return optionalInventoryData.get();
		}
	}
}
