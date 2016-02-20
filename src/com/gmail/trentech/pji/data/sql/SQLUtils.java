package com.gmail.trentech.pji.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pji.Main;

public abstract class SQLUtils {

    protected static SqlService sql;

    protected static DataSource getDataSource() throws SQLException {
	    if (sql == null) {
	        sql = Main.getGame().getServiceManager().provide(SqlService.class).get();
	    }
	    
        return sql.getDataSource("jdbc:h2:./config/projectinventories/data");
	}

	public static void createInventory(String invName) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + invName + " (Player TEXT, Hotbar TEXT, Inventory TEXT, Armor TEXT, Health DOUBLE, Food INTEGER, Saturation DOUBLE, ExpLevel INTEGER, Experience INTEGER)");
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public static void deleteInventory(String invName) {
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("DROP TABLE " + invName);
		    
			statement.executeUpdate();
			
			connection.close();
		}catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public static void createSettings() {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Settings (World TEXT, Inventory TEXT)");
			
			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	public static void saveWorld(World world){
		try {
		    Connection connection = getDataSource().getConnection();
		    
		    PreparedStatement statement = connection.prepareStatement("INSERT into Settings (World, Inventory) VALUES (?, ?)");	
			
		    statement.setString(1, world.getName());
		    statement.setString(2, "default");

			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}