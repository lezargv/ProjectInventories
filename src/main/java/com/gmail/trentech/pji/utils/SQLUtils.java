package com.gmail.trentech.pji.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

public abstract class SQLUtils {

	protected static String prefix = ConfigManager.get().getConfig().getNode("settings", "sql", "prefix").getString();
	protected static boolean enableSQL = ConfigManager.get().getConfig().getNode("settings", "sql", "enable").getBoolean();
	protected static String url = ConfigManager.get().getConfig().getNode("settings", "sql", "url").getString();
	protected static String username = ConfigManager.get().getConfig().getNode("settings", "sql", "username").getString();
	protected static String password = ConfigManager.get().getConfig().getNode("settings", "sql", "password").getString();
	protected static SqlService sql;

	protected static DataSource getDataSource() throws SQLException {
		if (sql == null) {
			sql = Sponge.getServiceManager().provide(SqlService.class).get();
		}

		if (enableSQL) {
			return sql.getDataSource("jdbc:mysql://" + url + "?user=" + username + "&password=" + password);
		} else {
			return sql.getDataSource("jdbc:h2:./config/pji/data");
		}
	}

	protected static String prefix(String table) {
		if (!prefix.equalsIgnoreCase("NONE") && enableSQL) {
			return "`" + prefix + table + "`";
		}
		return "`" + table + "`";
	}

	public static boolean createTable(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix(name.toUpperCase()) + " (Player TEXT, Data TEXT)");

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean deleteTable(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DROP TABLE " + prefix(name.toUpperCase()));

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void createSettings() {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix("Settings") + " (World TEXT, Data TEXT)");

			statement.executeUpdate();

			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix("Inventories") + " (Name TEXT)");

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}