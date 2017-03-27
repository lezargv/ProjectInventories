package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.InventoryData;

public class InventoryDB {

	public static HashMap<String, InventoryData> all() {
		HashMap<String, InventoryData> map = new HashMap<>();
		
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.INVENTORIES"));

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
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.INVENTORIES"));

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
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + sqlManager.getPrefix("PJI.INVENTORIES") + " WHERE Name = ?");

			statement.setString(1, inventory);

			statement.executeUpdate();

			statement = connection.prepareStatement("DROP TABLE " + sqlManager.getPrefix("PJI.INV." + inventory));

			statement.executeUpdate();
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void create(InventoryData inventoryData) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + sqlManager.getPrefix("PJI.INVENTORIES") + " (Name, Data) VALUES (?, ?)");

			statement.setString(2, InventoryData.serialize(inventoryData));
			statement.setString(1, inventoryData.getName());

			statement.executeUpdate();

			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + sqlManager.getPrefix("PJI.INV." + inventoryData.getName()) + " (UUID TEXT, Data TEXT)");

			statement.executeUpdate();
				
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void update(InventoryData inventoryData) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + sqlManager.getPrefix("PJI.INVENTORIES") + " SET Data = ? WHERE Name = ?");

			statement.setString(1, InventoryData.serialize(inventoryData));
			statement.setString(2, inventoryData.getName());

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
