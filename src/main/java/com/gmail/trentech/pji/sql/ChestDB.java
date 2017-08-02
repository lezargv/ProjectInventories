package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.EnderChestData;
import com.gmail.trentech.pji.data.PlayerChestData;

public class ChestDB {

	public static HashMap<UUID, PlayerChestData> all() {
		HashMap<UUID, PlayerChestData> map = new HashMap<>();

		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.CHESTS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String uuid = result.getString("UUID");
				PlayerChestData playerChestData = PlayerChestData.deserialize(result.getString("Data"));

				map.put(UUID.fromString(uuid), playerChestData);
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

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.CHESTS"));

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
	
	public static PlayerChestData get(UUID uuid) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.CHESTS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("UUID").equalsIgnoreCase(uuid.toString())) {
					PlayerChestData playerChestData = PlayerChestData.deserialize(result.getString("Data"));
					
					connection.close();
					statement.close();
					result.close();
					
					return playerChestData;
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PlayerChestData playerChestData = new PlayerChestData(uuid, new HashMap<UUID, EnderChestData>());
		
		save(uuid, playerChestData);

		return playerChestData;
	}

	public static void save(UUID uuid, PlayerChestData playerChestData) {
		if (exists(uuid)) {
			update(uuid, playerChestData);
		} else {
			create(uuid, playerChestData);
		}
	}

	public static void remove(UUID uuid) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + sqlManager.getPrefix("PJI.CHESTS") + " WHERE UUID = ?");

			statement.setString(1, uuid.toString());
			statement.executeUpdate();

			connection.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void create(UUID uuid, PlayerChestData playerChestData) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT INTO " + sqlManager.getPrefix("PJI.CHESTS") + " (UUID, Data) VALUES (?, ?)");

			statement.setString(1, uuid.toString());
			statement.setString(2, PlayerChestData.serialize(playerChestData));

			statement.executeUpdate();

			connection.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void update(UUID uuid, PlayerChestData playerChestData) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + sqlManager.getPrefix("PJI.CHESTS") + " SET Data = ? WHERE UUID = ?");

			statement.setString(2, uuid.toString());
			statement.setString(1, PlayerChestData.serialize(playerChestData));

			statement.executeUpdate();

			connection.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
