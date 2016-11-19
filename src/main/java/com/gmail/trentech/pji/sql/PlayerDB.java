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
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.living.player.Player;

import com.gmail.trentech.pji.service.data.InventoryPlayer;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

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

		public static Optional<InventoryPlayer> get(Player player, String inventory) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INV." + inventory));

				ResultSet result = statement.executeQuery();

				while (result.next()) {
					if (result.getString("UUID").equals(player.getUniqueId().toString())) {
						InventoryPlayer inventoryPlayer = deserialize(result.getString("Inventory"));

						connection.close();

						return Optional.of(inventoryPlayer);
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

		public static void create(Player player, InventoryPlayer inventoryPlayer) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PJI.INV." + inventoryPlayer.getName()) + " (UUID, Inventory) VALUES (?, ?)");

				statement.setString(1, player.getUniqueId().toString());
				statement.setString(2, serialize(inventoryPlayer));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public static void update(Player player, InventoryPlayer inventoryPlayer) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PJI.INV." + inventoryPlayer.getName()) + " SET Inventory = ? WHERE UUID = ?");

				statement.setString(2, player.getUniqueId().toString());
				statement.setString(1, serialize(inventoryPlayer));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		private static String serialize(InventoryPlayer inventoryData) {
			ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(inventoryData.toContainer());
			StringWriter stringWriter = new StringWriter();
			try {
				HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return stringWriter.toString();
		}

		private static InventoryPlayer deserialize(String item) {
			ConfigurationNode node = null;
			try {
				node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
			} catch (IOException e) {
				e.printStackTrace();
			}

			DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);

			return Sponge.getDataManager().deserialize(InventoryPlayer.class, dataView).get();
		}
	}
}
