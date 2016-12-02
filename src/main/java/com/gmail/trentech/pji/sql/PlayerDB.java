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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.data.PlayerInventoryData;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class PlayerDB extends InitDB {

	public static HashMap<UUID, PlayerData> all() {
		Task.builder().async().execute(c -> {
			
		}).submit(Main.getPlugin());
		HashMap<UUID, PlayerData> map = new HashMap<>();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.PLAYERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String uuid = result.getString("UUID");
				PlayerData playerData = deserialize(result.getString("Data"));

				map.put(UUID.fromString(uuid), playerData);
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	public static PlayerData get(UUID uuid) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.PLAYERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("UUID").equals(uuid.toString())) {
					PlayerData playerData = deserialize(result.getString("Data"));
					
					connection.close();

					return playerData;
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PlayerData playerData = new PlayerData("DEFAULT", new ArrayList<>());
		
		save(uuid, playerData);

		return playerData;
	}

	public static void save(UUID uuid, PlayerData playerData) {
		if (all().containsKey(uuid)) {
			update(uuid, playerData);
		} else {
			create(uuid, playerData);
		}
	}

	public static void remove(UUID uuid) {
		Task.builder().async().execute(c -> {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("DELETE from " + getPrefix("PJI.PLAYERS") + " WHERE UUID = ?");

				statement.setString(1, uuid.toString());
				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).submit(Main.getPlugin());
	}

	private static void create(UUID uuid, PlayerData playerData) {
		Task.builder().async().execute(c -> {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PJI.PLAYERS") + " (UUID, Data) VALUES (?, ?)");

				statement.setString(1, uuid.toString());
				statement.setString(2, serialize(playerData));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).submit(Main.getPlugin());
	}

	private static void update(UUID uuid, PlayerData playerData) {
		Task.builder().async().execute(c -> {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PJI.PLAYERS") + " SET Data = ? WHERE UUID = ?");

				statement.setString(2, uuid.toString());
				statement.setString(1, serialize(playerData));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).submit(Main.getPlugin());
	}

	public static String serialize(PlayerData playerData) {
		ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(playerData.toContainer());
		StringWriter stringWriter = new StringWriter();
		try {
			HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}

	public static PlayerData deserialize(String item) {
		ConfigurationNode node = null;
		try {
			node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);

		return Sponge.getDataManager().deserialize(PlayerData.class, dataView).get();
	}
	
	public static class Data {

		public static Optional<PlayerInventoryData> get(Player player, String inventory) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INV." + inventory));

				ResultSet result = statement.executeQuery();

				while (result.next()) {
					if (result.getString("UUID").equals(player.getUniqueId().toString())) {
						PlayerInventoryData playerInventoryData = deserialize(result.getString("Data"));

						connection.close();

						return Optional.of(playerInventoryData);
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

		public static void create(Player player, PlayerInventoryData playerInventoryData) {
			Task.builder().async().execute(c -> {
				try {
					Connection connection = getDataSource().getConnection();

					PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PJI.INV." + playerInventoryData.getName()) + " (UUID, Data) VALUES (?, ?)");

					statement.setString(1, player.getUniqueId().toString());
					statement.setString(2, serialize(playerInventoryData));

					statement.executeUpdate();

					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}).submit(Main.getPlugin());

		}

		public static void update(Player player, PlayerInventoryData playerInventoryData) {
			Task.builder().async().execute(c -> {
				try {
					Connection connection = getDataSource().getConnection();

					PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PJI.INV." + playerInventoryData.getName()) + " SET Data = ? WHERE UUID = ?");

					statement.setString(2, player.getUniqueId().toString());
					statement.setString(1, serialize(playerInventoryData));

					statement.executeUpdate();

					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}).submit(Main.getPlugin());

		}
		
		public static String serialize(PlayerInventoryData inventoryData) {
			ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(inventoryData.toContainer());
			StringWriter stringWriter = new StringWriter();
			try {
				HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return stringWriter.toString();
		}

		public static PlayerInventoryData deserialize(String item) {
			ConfigurationNode node = null;
			try {
				node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
			} catch (IOException e) {
				e.printStackTrace();
			}

			DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);

			return Sponge.getDataManager().deserialize(PlayerInventoryData.class, dataView).get();
		}
	}
}
