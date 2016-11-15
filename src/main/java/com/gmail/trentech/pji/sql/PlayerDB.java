package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.entity.living.player.Player;

import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.utils.DataSerializer;

public class PlayerDB extends SQLUtils {

	private static ConcurrentHashMap<UUID, String> cache = new ConcurrentHashMap<>();

	public static void init() {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM Players");

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String uuid = result.getString("UUID");
				String inventory = result.getString("Inventory");

				cache.put(UUID.fromString(uuid), inventory);
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ConcurrentHashMap<UUID, String> all() {
		return cache;
	}

	public static String get(Player player) {
		UUID uuid = player.getUniqueId();

		if (cache.containsKey(uuid)) {
			return cache.get(uuid);
		}

		save(player, "DEFAULT");
		
		return "DEFAULT";
	}

	public static void save(Player player, String inventory) {
		if (cache.containsKey(player.getUniqueId())) {
			update(player, inventory);
		} else {
			create(player, inventory);
		}
	}

	public static void remove(Player player) {
		UUID uuid = player.getUniqueId();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE from Players WHERE UUID = ?");

			statement.setString(1, uuid.toString());
			statement.executeUpdate();

			connection.close();

			cache.remove(uuid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void create(Player player, String inventory) {
		UUID uuid = player.getUniqueId();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into Players (UUID, Inventory) VALUES (?, ?)");

			statement.setString(1, uuid.toString());
			statement.setString(2, inventory);

			statement.executeUpdate();

			connection.close();

			cache.put(uuid, inventory);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void update(Player player, String inventory) {
		UUID uuid = player.getUniqueId();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE Players SET Inventory = ? WHERE UUID = ?");

			statement.setString(2, uuid.toString());
			statement.setString(1, inventory);

			statement.executeUpdate();

			connection.close();

			cache.put(uuid, inventory);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static class Data {
		
		public static Optional<PlayerData> get(Player player, String inventory) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM `PJI_" + inventory.toUpperCase() + "`");

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

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM `PJI_" + playerData.getName().toUpperCase() + "`");

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

				PreparedStatement statement = connection.prepareStatement("INSERT into `PJI_" + playerData.getName().toUpperCase() + "` (UUID, Inventory) VALUES (?, ?)");

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

				PreparedStatement statement = connection.prepareStatement("UPDATE `PJI_" + playerData.getName().toUpperCase() + "` SET Inventory = ? WHERE UUID = ?");

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
