package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;

import com.gmail.trentech.pji.data.inventory.Inventory;
import com.gmail.trentech.pji.data.inventory.extra.InventorySerializer;

public class SQLInventory extends SQLUtils {

	public static boolean createInventory(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix(name) + " (Player TEXT, Inventory TEXT)");

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void update(Player player, String name, Inventory inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + prefix(name) + " SET Inventory = ? WHERE Player = ?");

			statement.setString(1, InventorySerializer.serializeInventory(inventory));
			statement.setString(2, player.getUniqueId().toString());

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void create(Player player, String name, Inventory inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + prefix(name) + " (Player, Inventory) VALUES (?, ?)");

			statement.setString(1, player.getUniqueId().toString());
			statement.setString(2, InventorySerializer.serializeInventory(inventory));

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Optional<Inventory> get(Player player, String name) {
		Optional<Inventory> optionalInventory = Optional.empty();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix(name));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("Player").equalsIgnoreCase(player.getUniqueId().toString())) {
					optionalInventory = Optional.of(InventorySerializer.deserializeInventory(result.getString("Inventory")));

					break;
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return optionalInventory;
	}

	public static boolean exists(Player player, String name) {
		boolean b = false;

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix(name));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("Player").equalsIgnoreCase(player.getUniqueId().toString())) {
					b = true;

					break;
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return b;
	}
}
