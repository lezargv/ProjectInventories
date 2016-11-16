package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryDB extends SQLUtils {

	public static void create(String inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + getPrefix("PJI.INV." + inventory) + " (UUID TEXT, Inventory TEXT)");

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void remove(String inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DROP TABLE " + getPrefix("PJI.INV." + inventory));

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean exists(String inventory) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PJI.INV." + inventory));

			statement.executeQuery();

			connection.close();

			return true;
		} catch (SQLException e) {

		}

		return false;
	}

	public static List<String> all() {
		List<String> list = new ArrayList<>();

		try {
			Connection connection = getDataSource().getConnection();

			DatabaseMetaData metaData = connection.getMetaData();

			ResultSet result = metaData.getTables(null, null, "%", null);
			while (result.next()) {
				String name = stripPrefix(result.getString(3));

				if (name.startsWith("PJI.INV.")) {
					list.add(name.replace("PJI.INV.", ""));
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
}
