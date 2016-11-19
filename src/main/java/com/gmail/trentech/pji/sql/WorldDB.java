package com.gmail.trentech.pji.sql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.scheduler.Task;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.WorldData;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class WorldDB extends InitDB {

	public static WorldData get(UUID uuid) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.WORLDS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if(uuid.equals(UUID.fromString(result.getString("UUID")))) {
					WorldData worldData = deserialize(result.getString("Data"));

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
				statement.setString(2, serialize(worldData));

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
				statement.setString(1, serialize(worldData));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).submit(Main.getPlugin());
	}
	
	private static String serialize(WorldData worldData) {
		ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(worldData.toContainer());
		StringWriter stringWriter = new StringWriter();
		try {
			HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}

	private static WorldData deserialize(String item) {
		ConfigurationNode node = null;
		try {
			node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);

		return Sponge.getDataManager().deserialize(WorldData.class, dataView).get();
	}
}
