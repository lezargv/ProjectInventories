package com.gmail.trentech.pji.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.spongepowered.api.world.World;

import com.gmail.trentech.pji.utils.SQLUtils;

public class WorldData extends SQLUtils {

	String worldName;
	String inventory;
	
	private WorldData(World world, String inventory){
		this.worldName = world.getName();
		this.inventory = inventory;
	}
	
	public String getInventory(){
		return this.inventory;
	}
	
	public void setInventory(String inventory) {
		this.inventory = inventory;
		save();
	}
	
	private void save(){
		try {
		    Connection connection = getDataSource().getConnection();
		    PreparedStatement statement = connection.prepareStatement("UPDATE Settings SET Inventory = ? WHERE World = ?");
			statement.setString(1, this.inventory);
			statement.setString(2, this.worldName);
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Optional<WorldData> get(World world){
		Optional<WorldData> optionalInv = Optional.empty();

		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("SELECT * FROM Settings");
		    
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				if (result.getString("World").equalsIgnoreCase(world.getName())) {
					optionalInv = Optional.of(new WorldData(world, result.getString("Inventory")));
					break;
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return optionalInv;
	}
}
