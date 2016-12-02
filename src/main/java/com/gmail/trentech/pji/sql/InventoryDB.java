package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

import org.spongepowered.api.scheduler.Task;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.InventoryData;

public class InventoryDB extends InitDB {

	public static HashMap<String, InventoryData> all() {
		HashMap<String, InventoryData> map = new HashMap<>();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INVENTORIES"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				map.put(result.getString("Name"), InventoryData.deserialize(result.getString("Data")));
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

					return Optional.of(InventoryData.deserialize(data));
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

			statement.setString(2, InventoryData.serialize(inventoryData));
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

			statement.setString(1, InventoryData.serialize(inventoryData));
			statement.setString(2, inventoryData.getName());

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
