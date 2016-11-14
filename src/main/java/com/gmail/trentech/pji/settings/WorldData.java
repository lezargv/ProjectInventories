package com.gmail.trentech.pji.settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.utils.SQLUtils;

public class WorldData extends SQLUtils {

	private String inventory = "DEFAULT";
	private String worldUuid;
	private static ConcurrentHashMap<String, WorldData> list = new ConcurrentHashMap<String, WorldData>();
	
	private WorldData(String worldUuid, String inventory) {
		this.worldUuid = worldUuid;
		this.inventory = inventory.toUpperCase();
	}
	
	private WorldData(String worldUuid) {
		this.worldUuid = worldUuid;
	}
	
	public String getInventory() {
		return inventory;
	}
	
	public WorldData setInventory(String inventory) {
		this.inventory = inventory;
		return this;
	}
	
	public static void init() {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix("Settings"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				list.put(result.getString("World"), new WorldData(result.getString("World"), result.getString("Data")));
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static WorldData get(WorldProperties properties) {
		String worldUuid = properties.getUniqueId().toString();
		
		for(Entry<String, WorldData> entry : list.entrySet()) {
			if(entry.getKey().equals(worldUuid)) {
				return entry.getValue();
			}
		}

		return new WorldData(worldUuid);
	}

	public void save() {
		try {
			Connection connection = getDataSource().getConnection();
			PreparedStatement statement;
			
			if(exist(worldUuid)) {
				statement = connection.prepareStatement("UPDATE " + prefix("Settings") + " SET Data = ? WHERE World = ?");

				statement.setString(1, inventory);
				statement.setString(2, worldUuid);
			} else {
				statement = connection.prepareStatement("INSERT into " + prefix("Settings") + " (World, Data) VALUES (?, ?)");
				
				statement.setString(1, worldUuid);
				statement.setString(2, inventory);
			}
			statement.executeUpdate();
			
			connection.close();
			
			list.put(worldUuid, this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static boolean exist(String worldName) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix("Settings"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if(result.getString("World").equals(worldName)) {
					connection.close();
					return true;
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
