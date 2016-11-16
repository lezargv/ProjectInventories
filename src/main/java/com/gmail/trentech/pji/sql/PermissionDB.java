package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

public class PermissionDB extends SQLUtils {

	public static HashMap<String, String> all() {
		HashMap<String, String> map = new HashMap<>();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.PERMISSIONS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				map.put(result.getString("Inventory"), result.getString("Permission").toLowerCase());
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	public static Optional<String> get(String inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.PERMISSIONS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("Inventory").equalsIgnoreCase(inventory)) {
					String permission = result.getString("Permission");

					connection.close();

					return Optional.of(permission.toLowerCase());
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

	public static boolean exists(String inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.PERMISSIONS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("Inventory").equalsIgnoreCase(inventory)) {
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

	public static void save(String inventory, String permission) {
		if (all().containsKey(inventory)) {
			update(inventory, permission);
		} else {
			create(inventory, permission);
		}
	}

	public static void remove(String inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + getPrefix("PJI.PERMISSIONS") + " WHERE Inventory = ?");

			statement.setString(1, inventory);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void create(String inventory, String permission) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PJI.PERMISSIONS") + " (Inventory, Permission) VALUES (?, ?)");

			statement.setString(2, permission);
			statement.setString(1, inventory);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void update(String inventory, String permission) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PJI.PERMISSIONS") + " SET Permission = ? WHERE Inventory = ?");

			statement.setString(1, permission);
			statement.setString(2, inventory);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
