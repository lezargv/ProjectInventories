package com.gmail.trentech.pji.settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gmail.trentech.pji.utils.SQLUtils;

public class Inventories extends SQLUtils {
	
	public static void save(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + prefix("Inventories") + " (Name) VALUES (?)");

			statement.setString(1, name);

			statement.executeUpdate();

			connection.close();
			
			createTable(name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean exists(String name) {
		boolean exist = false;

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix("Inventories"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("Name").equalsIgnoreCase(name)) {
					exist = true;
					break;
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exist;
	}

	public static List<String> all() {
		List<String> list = new ArrayList<>();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix("Inventories"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				list.add(result.getString("Name"));
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void delete(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE from " + prefix("Inventories") + " WHERE Name = ?");

			statement.setString(1, name);

			statement.executeUpdate();

			connection.close();
			
			deleteTable(name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
