package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

public class GamemodeDB extends SQLUtils {

	public static HashMap<String, String> all() {
		HashMap<String, String> map = new HashMap<>();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.GAMEMODE"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				map.put(result.getString("Inventory"), result.getString("Gamemode").toLowerCase());
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	public static Optional<GameMode> get(String inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.GAMEMODE"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("Inventory").equalsIgnoreCase(inventory)) {
					String gamemode = result.getString("Gamemode");

					connection.close();

					return Optional.of(Sponge.getRegistry().getType(GameMode.class, gamemode).get());
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

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.GAMEMODE"));

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

	public static void save(String inventory, String gamemode) {
		if (all().containsKey(inventory)) {
			update(inventory, gamemode);
		} else {
			create(inventory, gamemode);
		}
	}

	public static void remove(String inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + getPrefix("PJI.GAMEMODE") + " WHERE Inventory = ?");

			statement.setString(1, inventory);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void create(String inventory, String gamemode) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PJI.GAMEMODE") + " (Inventory, Gamemode) VALUES (?, ?)");

			statement.setString(2, gamemode);
			statement.setString(1, inventory);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void update(String inventory, String gamemode) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PJI.GAMEMODE") + " SET Gamemode = ? WHERE Inventory = ?");

			statement.setString(1, gamemode);
			statement.setString(2, inventory);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
