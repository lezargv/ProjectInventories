package com.gmail.trentech.pji.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

public class SQLSettings extends SQLUtils{


	
	public static Optional<String> getWorld(World world){
		Optional<String> optionalInv = Optional.empty();

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Settings");
		    
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				if (result.getString("World").equalsIgnoreCase(world.getName())) {
					optionalInv = Optional.of(result.getString("Inventory"));
					break;
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return optionalInv;
	}
	
	public static void saveWorld(World world){
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into " + prefix("Settings") + " (World, Inventory) VALUES (?, ?)");	
			
		    statement.setString(1, world.getName());
		    statement.setString(2, "default");

			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateWorld(World world, String name){
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE " + prefix("Settings") + " SET Inventory = ? WHERE World = ?");
			statement.setString(1, name);
			statement.setString(2, world.getName());
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void savePlayer(Player player){
		try {
		    Connection connection = getDataSource().getConnection();

		    PreparedStatement statement = connection.prepareStatement("INSERT into " + prefix("Players") + " (Player) VALUES (?)");	
			
		    statement.setString(1, player.getUniqueId().toString());

			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean getPlayer(Player player){
		boolean exist = false;

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix("Players"));
		    
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				if (result.getString("Player").equalsIgnoreCase(player.getUniqueId().toString())) {
					exist = true;
					break;
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return exist;
	}
	
	public static void saveInventory(String name){
		try {
		    Connection connection = getDataSource().getConnection();

		    PreparedStatement statement = connection.prepareStatement("INSERT into " + prefix("Inventories") + " (Name) VALUES (?)");	
			
		    statement.setString(1, name);

			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean getInventory(String name){
		boolean exist = false;

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix("Inventories") + "");
		    
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				if (result.getString("Name").equalsIgnoreCase(name)) {
					exist = true;
					break;
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return exist;
	}
	
	public static List<String> getInventoryList(){
		List<String> list = new ArrayList<>();

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix("Inventories"));
		    
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				list.add(result.getString("Name"));
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return list;
	}
	
	public static void deleteInventory(String name) {
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("DELETE from " + prefix("Inventories") + " WHERE Name = ?");
			statement.setString(1, name);
			statement.executeUpdate();
			connection.close();
		}catch (SQLException e) {
			e.printStackTrace();
		} 
	}
}
