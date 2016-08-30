package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import com.gmail.trentech.pji.utils.ConfigManager;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public abstract class SQLUtils {

	protected static String prefix = ConfigManager.get().getConfig().getNode("settings", "sql", "prefix").getString();
	protected static boolean enableSQL = ConfigManager.get().getConfig().getNode("settings", "sql", "enable").getBoolean();
	protected static SqlService sql;

	protected static DataSource getDataSource() throws SQLException {
		if (sql == null) {
			sql = Sponge.getServiceManager().provide(SqlService.class).get();
		}
		DataSource dataSource = null;
		if (enableSQL) {
			CommentedConfigurationNode config = ConfigManager.get().getConfig();

			String url = config.getNode("settings", "sql", "url").getString();
			String username = config.getNode("settings", "sql", "username").getString();
			String password = config.getNode("settings", "sql", "password").getString();

			dataSource = sql.getDataSource("jdbc:mysql://" + url + "?user=" + username + "&password=" + password);
		} else {
			dataSource = sql.getDataSource("jdbc:h2:./config/projectinventories/data");
		}
		return dataSource;
	}

	protected static String prefix(String table) {
		if (!prefix.equalsIgnoreCase("NONE") && enableSQL) {
			return "`" + prefix + table + "`";
		}
		return "`" + table + "`";
	}

	public static void deleteTable(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DROP TABLE " + prefix(name));

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createSettings() {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix("Settings") + " (World TEXT, Inventory TEXT)");

			statement.executeUpdate();

			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix("Players") + " (Player TEXT)");

			statement.executeUpdate();

			statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix("Inventories") + " (Name TEXT)");

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}