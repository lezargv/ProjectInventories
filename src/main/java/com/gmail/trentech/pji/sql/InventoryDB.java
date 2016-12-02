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
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.scheduler.Task;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.InventoryData;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class InventoryDB extends InitDB {

	public static HashMap<String, InventoryData> all() {
		HashMap<String, InventoryData> map = new HashMap<>();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INVENTORIES"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				map.put(result.getString("Name"), deserialize(result.getString("Data")));
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	public static Optional<InventoryData> get(String inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INVENTORIES"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("Name").equalsIgnoreCase(inventory)) {
					String data = result.getString("Data");

					connection.close();

					return Optional.of(deserialize(data));
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

	public static void save(InventoryData inventoryData) {
		if (get(inventoryData.getName()).isPresent()) {
			update(inventoryData);
		} else {
			create(inventoryData);
		}
	}

	public static void remove(String inventory) {
		Task.builder().async().execute(c -> {
			
		}).submit(Main.getPlugin());
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + getPrefix("PJI.INVENTORIES") + " WHERE Name = ?");

			statement.setString(1, inventory);

			statement.executeUpdate();

			statement = connection.prepareStatement("DROP TABLE " + getPrefix("PJI.INV." + inventory));

			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void create(InventoryData inventoryData) {
		Task.builder().async().execute(c -> {
			
		}).submit(Main.getPlugin());
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PJI.INVENTORIES") + " (Name, Data) VALUES (?, ?)");

			statement.setString(2, serialize(inventoryData));
			statement.setString(1, inventoryData.getName());

			statement.executeUpdate();

			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + getPrefix("PJI.INV." + inventoryData.getName()) + " (UUID TEXT, Data TEXT)");

			statement.executeUpdate();
				
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void update(InventoryData inventoryData) {
		Task.builder().async().execute(c -> {
			
		}).submit(Main.getPlugin());
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PJI.INVENTORIES") + " SET Data = ? WHERE Name = ?");

			statement.setString(1, serialize(inventoryData));
			statement.setString(2, inventoryData.getName());

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static String serialize(InventoryData inventoryData) {
		ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(inventoryData.toContainer());
		StringWriter stringWriter = new StringWriter();
		try {
			HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}

	private static InventoryData deserialize(String item) {
		ConfigurationNode node = null;
		try {
			node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);

		return Sponge.getDataManager().deserialize(InventoryData.class, dataView).get();
	}
}
