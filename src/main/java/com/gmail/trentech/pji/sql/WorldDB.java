package com.gmail.trentech.pji.sql;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.storage.WorldProperties;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WorldDB extends SQLUtils {

	public static HashMap<UUID, HashMap<String, Boolean>> all() {
		HashMap<UUID, HashMap<String, Boolean>> map = new HashMap<>();
		
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM Worlds");

			ResultSet result = statement.executeQuery();

			Gson gson = new Gson();
			Type type = new TypeToken<HashMap<String, Boolean>>() {
			}.getType();

			while (result.next()) {
				UUID uuid = UUID.fromString(result.getString("UUID"));
				HashMap<String, Boolean> inventories = gson.fromJson(result.getString("Inventories"), type);

				if(inventories.isEmpty()) {
					inventories.put("DEFAULT", true);
					add(Sponge.getServer().getWorldProperties(uuid).get(), "DEFAULT", true);
				}
				
				map.put(uuid, inventories);
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return map;
	}

	public static HashMap<String, Boolean> get(WorldProperties properties) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM Worlds");

			ResultSet result = statement.executeQuery();

			Gson gson = new Gson();
			Type type = new TypeToken<HashMap<String, Boolean>>() {
			}.getType();

			while (result.next()) {
				if(result.getString("UUID").equals(properties.getUniqueId().toString())) {
					connection.close();
					
					return gson.fromJson(result.getString("Inventories"), type);
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		HashMap<String, Boolean> list = new HashMap<>();
		list.put("DEFAULT", true);
		
		save(properties, list);
		
		return list;
	}

	public static void add(WorldProperties world, String inventory, boolean isDefault) {
		HashMap<String, Boolean> inventories = WorldDB.get(world);

		if(isDefault) {
			for(Entry<String, Boolean> entry : Maps.newHashMap(inventories).entrySet()) {
				inventories.put(entry.getKey(), false);
			}
		}
		
		inventories.put(inventory, isDefault);

		save(world, inventories);
	}

	public static void remove(WorldProperties world, String inventory) {
		HashMap<String, Boolean> inventories = WorldDB.get(world);

		inventories.remove(inventory);

		for(Entry<String, Boolean> entry : Maps.newHashMap(inventories).entrySet()) {
			if(entry.getValue()) {
				save(world, inventories);
				return;
			}
		}
		
		inventories.put(inventories.entrySet().iterator().next().getKey(), true);
		
		save(world, inventories);
	}

	public static void save(WorldProperties world, HashMap<String, Boolean> inventories) {
		if(all().containsKey(world.getUniqueId())) {
			update(world, inventories);
		} else {
			create(world, inventories);
		}
	}

	public static void remove(WorldProperties world) {
		UUID uuid = world.getUniqueId();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE from Worlds WHERE UUID = ?");

			statement.setString(1, uuid.toString());
			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void create(WorldProperties world, HashMap<String, Boolean> inventories) {
		UUID uuid = world.getUniqueId();

		Gson gson = new Gson();
		Type type = new TypeToken<HashMap<String, Boolean>>() {
		}.getType();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into Worlds (UUID, Inventories) VALUES (?, ?)");

			statement.setString(1, uuid.toString());
			statement.setString(2, gson.toJson(inventories, type));

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void update(WorldProperties world, HashMap<String, Boolean> inventories) {
		UUID uuid = world.getUniqueId();

		Gson gson = new Gson();
		Type type = new TypeToken<HashMap<String, Boolean>>() {
		}.getType();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE Worlds SET Inventories = ? WHERE UUID = ?");

			statement.setString(2, uuid.toString());
			statement.setString(1, gson.toJson(inventories, type));

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
