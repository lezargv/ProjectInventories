package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.data.PlayerInventoryData;

public class PlayerDB {

	public static HashMap<UUID, PlayerData> all() {
		HashMap<UUID, PlayerData> map = new HashMap<>();

		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.PLAYERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String uuid = result.getString("UUID");
				PlayerData playerData = PlayerData.deserialize(result.getBytes("Data"));

				map.put(UUID.fromString(uuid), playerData);
			}

			connection.close();
			statement.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	public static boolean exists(UUID uuid) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.PLAYERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("UUID").equalsIgnoreCase(uuid.toString())) {
					connection.close();
					statement.close();
					result.close();
					
					return true;
				}
			}

			connection.close();
			statement.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public static PlayerData get(UUID uuid) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.PLAYERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("UUID").equalsIgnoreCase(uuid.toString())) {
					PlayerData playerData = PlayerData.deserialize(result.getBytes("Data"));
					
					connection.close();
					statement.close();
					result.close();
					
					return playerData;
				}
			}

			connection.close();
			statement.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PlayerData playerData = new PlayerData("DEFAULT", new ArrayList<>());
		
		save(uuid, playerData);

		return playerData;
	}

	public static void save(UUID uuid, PlayerData playerData) {
		if (exists(uuid)) {
			update(uuid, playerData);
		} else {
			create(uuid, playerData);
		}
	}

	public static void remove(UUID uuid) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + sqlManager.getPrefix("PJI.PLAYERS") + " WHERE UUID = ?");

			statement.setString(1, uuid.toString());
			statement.executeUpdate();

			connection.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void create(UUID uuid, PlayerData playerData) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT INTO " + sqlManager.getPrefix("PJI.PLAYERS") + " (UUID, Data) VALUES (?, ?)");

			statement.setString(1, uuid.toString());
			statement.setBytes(2, PlayerData.serialize(playerData));

			statement.executeUpdate();

			connection.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void update(UUID uuid, PlayerData playerData) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + sqlManager.getPrefix("PJI.PLAYERS") + " SET Data = ? WHERE UUID = ?");

			statement.setString(2, uuid.toString());
			statement.setBytes(1, PlayerData.serialize(playerData));

			statement.executeUpdate();

			connection.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static class Data {

		public static Optional<PlayerInventoryData> get(Player player, String inventory) {
			try {
				SQLManager sqlManager = SQLManager.get(Main.getPlugin());
				Connection connection = sqlManager.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.INV." + inventory));

				ResultSet result = statement.executeQuery();

				while (result.next()) {
					if (result.getString("UUID").equals(player.getUniqueId().toString())) {
						PlayerInventoryData playerInventoryData = PlayerInventoryData.deserialize(result.getBytes("Data"));

						connection.close();
						statement.close();
						result.close();
						
						return Optional.of(playerInventoryData);
					}
				}

				connection.close();
				statement.close();
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return Optional.empty();
		}

		public static boolean exists(Player player, String name) {
			try {
				SQLManager sqlManager = SQLManager.get(Main.getPlugin());
				Connection connection = sqlManager.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.INV." + name));

				ResultSet result = statement.executeQuery();

				while (result.next()) {
					if (result.getString("UUID").equalsIgnoreCase(player.getUniqueId().toString())) {
						connection.close();
						statement.close();
						result.close();
						
						return true;
					}
				}

				connection.close();
				statement.close();
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return false;
		}

		public static void create(UUID uuid, PlayerInventoryData playerInventoryData) {
			try {
				SQLManager sqlManager = SQLManager.get(Main.getPlugin());
				Connection connection = sqlManager.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("INSERT into " + sqlManager.getPrefix("PJI.INV." + playerInventoryData.getName()) + " (UUID, Data) VALUES (?, ?)");

				statement.setString(1, uuid.toString());
				statement.setBytes(2, PlayerInventoryData.serialize(playerInventoryData));

				statement.executeUpdate();

				connection.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public static void update(UUID uuid, PlayerInventoryData playerInventoryData) {
			try {
				SQLManager sqlManager = SQLManager.get(Main.getPlugin());
				Connection connection = sqlManager.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("UPDATE " + sqlManager.getPrefix("PJI.INV." + playerInventoryData.getName()) + " SET Data = ? WHERE UUID = ?");

				statement.setString(2, uuid.toString());
				statement.setBytes(1, PlayerInventoryData.serialize(playerInventoryData));

				statement.executeUpdate();
				
				connection.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
