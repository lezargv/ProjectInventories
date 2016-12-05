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
import org.spongepowered.api.scheduler.Task;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.data.PlayerInventoryData;

public class PlayerDB extends InitDB {

	public static HashMap<UUID, PlayerData> all() {
		HashMap<UUID, PlayerData> map = new HashMap<>();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.PLAYERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String uuid = result.getString("UUID");
				PlayerData playerData = PlayerData.deserialize(result.getString("Data"));

				map.put(UUID.fromString(uuid), playerData);
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	public static boolean exists(UUID uuid) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.PLAYERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("UUID").equalsIgnoreCase(uuid.toString())) {
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
	
	public static PlayerData get(UUID uuid) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.PLAYERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("UUID").equalsIgnoreCase(uuid.toString())) {
					PlayerData playerData = PlayerData.deserialize(result.getString("Data"));
					
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
		if (exists(uuid)) {
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
				statement.setString(2, PlayerData.serialize(playerData));

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
				statement.setString(1, PlayerData.serialize(playerData));

				statement.executeUpdate();

				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).submit(Main.getPlugin());
	}

	public static class Data {

		public static Optional<PlayerInventoryData> get(Player player, String inventory) {
			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INV." + inventory));

				ResultSet result = statement.executeQuery();

				while (result.next()) {
					if (result.getString("UUID").equals(player.getUniqueId().toString())) {
						PlayerInventoryData playerInventoryData = PlayerInventoryData.deserialize(result.getString("Data"));

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
					statement.setString(2, PlayerInventoryData.serialize(playerInventoryData));

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
					statement.setString(1, PlayerInventoryData.serialize(playerInventoryData));

					statement.executeUpdate();

					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}).submit(Main.getPlugin());

		}
		

	}
}
