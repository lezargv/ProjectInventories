package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

import com.gmail.trentech.pji.data.PlayerData;
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
				if(result.getString("UUID").equals(player.getUniqueId().toString())) {
					connection.close();
					
					return result.getString("Inventory");
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

			connection.close();;
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
		
		public static Optional<PlayerData> get(Player player, String inventory) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INV." + inventory));

				ResultSet result = statement.executeQuery();

				while (result.next()) {
					if (result.getString("UUID").equals(player.getUniqueId().toString())) {
						PlayerData playerData = DataSerializer.deserializePlayerData(result.getString("Inventory"));
						playerData.setPlayer(player);

						connection.close();

						return Optional.of(playerData);
					}
				}

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return Optional.empty();
		}
		
		public static boolean exists(PlayerData playerData) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INV." + playerData.getName()));

				ResultSet result = statement.executeQuery();

				while (result.next()) {
					if (result.getString("UUID").equalsIgnoreCase(playerData.getPlayer().getUniqueId().toString())) {
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

		public static void create(PlayerData playerData) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PJI.INV." + playerData.getName()) + " (UUID, Inventory) VALUES (?, ?)");

				statement.setString(1, playerData.getPlayer().getUniqueId().toString());
				statement.setString(2, DataSerializer.serializePlayerData(playerData));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public static void update(PlayerData playerData) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PJI.INV." + playerData.getName()) + " SET Inventory = ? WHERE UUID = ?");

				statement.setString(2, playerData.getPlayer().getUniqueId().toString());
				statement.setString(1, DataSerializer.serializePlayerData(playerData));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
