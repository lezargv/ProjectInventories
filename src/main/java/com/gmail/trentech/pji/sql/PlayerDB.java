package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

import com.gmail.trentech.pji.service.InventoryData;
import com.gmail.trentech.pji.utils.DataSerializer;

public class PlayerDB extends SQLUtils {

	public static HashMap<UUID, String> all() {
		HashMap<UUID, String> map = new HashMap<>();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.PLAYERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String uuid = result.getString("UUID");
				String inventory = result.getString("Inventory");

				map.put(UUID.fromString(uuid), inventory);
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	public static String get(Player player) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.PLAYERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("UUID").equals(player.getUniqueId().toString())) {
					String inventory = result.getString("Inventory");
					
					connection.close();

					return inventory;
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		save(player, "DEFAULT");

		return "DEFAULT";
	}

	public static void save(Player player, String inventory) {
		if (all().containsKey(player.getUniqueId())) {
			update(player, inventory);
		} else {
			create(player, inventory);
		}
	}

	public static void remove(Player player) {
		UUID uuid = player.getUniqueId();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE from " + getPrefix("PJI.PLAYERS") + " WHERE UUID = ?");

			statement.setString(1, uuid.toString());
			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void create(Player player, String inventory) {
		UUID uuid = player.getUniqueId();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PJI.PLAYERS") + " (UUID, Inventory) VALUES (?, ?)");

			statement.setString(1, uuid.toString());
			statement.setString(2, inventory);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void update(Player player, String inventory) {
		UUID uuid = player.getUniqueId();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PJI.PLAYERS") + " SET Inventory = ? WHERE UUID = ?");

			statement.setString(2, uuid.toString());
			statement.setString(1, inventory);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static class Data {

		public static Optional<InventoryData> get(Player player, String inventory) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INV." + inventory));

				ResultSet result = statement.executeQuery();

				while (result.next()) {
					if (result.getString("UUID").equals(player.getUniqueId().toString())) {
						InventoryData inventoryData = DataSerializer.deserializeInventoryData(result.getString("Inventory"));

						connection.close();

						return Optional.of(inventoryData);
					}
				}

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return Optional.empty();
		}

		public static boolean exists(Player player, String name) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INV." + name));

				ResultSet result = statement.executeQuery();

				while (result.next()) {
					if (result.getString("UUID").equalsIgnoreCase(player.getUniqueId().toString())) {
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

		public static void create(Player player, InventoryData inventoryData) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PJI.INV." + inventoryData.getName()) + " (UUID, Inventory) VALUES (?, ?)");

				statement.setString(1, player.getUniqueId().toString());
				statement.setString(2, DataSerializer.serializeInventoryData(inventoryData));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public static void update(Player player, InventoryData inventoryData) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PJI.INV." + inventoryData.getName()) + " SET Inventory = ? WHERE UUID = ?");

				statement.setString(2, player.getUniqueId().toString());
				statement.setString(1, DataSerializer.serializeInventoryData(inventoryData));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
