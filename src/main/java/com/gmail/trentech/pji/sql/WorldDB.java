package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.scheduler.Task;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.WorldData;

public class WorldDB extends InitDB {

	public static HashMap<UUID, WorldData> all() {
		HashMap<UUID, WorldData> map = new HashMap<>();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.WORLDS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				map.put(UUID.fromString(result.getString("UUID")), WorldData.deserialize(result.getString("Data")));
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}
	
	public static WorldData get(UUID uuid) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.WORLDS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if(uuid.equals(UUID.fromString(result.getString("UUID")))) {
					WorldData worldData = WorldData.deserialize(result.getString("Data"));

					if (worldData.getInventories().isEmpty()) {
						worldData.add("DEFAULT", true);
						
						update(worldData);
					}
					
					connection.close();
					
					return worldData;
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Map<String, Boolean> list = new HashMap<>();
		list.put("DEFAULT", true);

		WorldData worldData = new WorldData(uuid, list);
		
		create(worldData);
		
		return worldData;
	}

	public static void remove(WorldData worldData) {
		Task.builder().async().execute(c -> {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("DELETE from " + getPrefix("PJI.WORLDS") + " WHERE UUID = ?");

				statement.setString(1, worldData.getUniqueId().toString());
				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).submit(Main.getPlugin());
	}

	private static void create(WorldData worldData) {
		Task.builder().async().execute(c -> {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PJI.WORLDS") + " (UUID, Data) VALUES (?, ?)");

				statement.setString(1, worldData.getUniqueId().toString());
				statement.setString(2, WorldData.serialize(worldData));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).submit(Main.getPlugin());
	}

	public static void update(WorldData worldData) {
		Task.builder().async().execute(c -> {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PJI.WORLDS") + " SET Data = ? WHERE UUID = ?");

				statement.setString(2, worldData.getUniqueId().toString());
				statement.setString(1, WorldData.serialize(worldData));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).submit(Main.getPlugin());
	}
	

}
