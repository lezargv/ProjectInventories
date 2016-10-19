package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.data.inventory.extra.InventoryHelper;

public class SQLSettings extends SQLUtils {

	public static Optional<String> getWorld(WorldProperties properties) {
		Optional<String> optionalInv = Optional.empty();

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix("Settings"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("World").equalsIgnoreCase(properties.getWorldName())) {
					optionalInv = Optional.of(result.getString("Inventory"));

					break;
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return optionalInv;
	}

	public static void saveWorld(WorldProperties properties) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + prefix("Settings") + " (World, Inventory) VALUES (?, ?)");

			statement.setString(1, properties.getWorldName());
			statement.setString(2, "default");

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateWorld(WorldProperties properties, String oldInv, String newInv) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + prefix("Settings") + " SET Inventory = ? WHERE World = ?");

			statement.setString(1, newInv);
			statement.setString(2, properties.getWorldName());

			statement.executeUpdate();

			connection.close();

			Sponge.getServer().getWorld(properties.getWorldName()).ifPresent(world -> {
				Predicate<Entity> filter = new Predicate<Entity>() {

					@Override
					public boolean test(Entity entity) {
						return entity instanceof Player;
					}
				};
				
				for(Entity entity: world.getEntities(filter)) {
					Player player = (Player) entity;
					
					InventoryHelper.saveInventory(player, oldInv);
					InventoryHelper.setInventory(player, newInv);
					player.sendMessage(Text.of(TextColors.RED, "[PJP] ", TextColors.YELLOW, "The inventory for this world has been changed by an admin"));
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void savePlayer(Player player) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + prefix("Players") + " (Player) VALUES (?)");

			statement.setString(1, player.getUniqueId().toString());

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean getPlayer(Player player) {
		boolean exist = false;

		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix("Players"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("Player").equalsIgnoreCase(player.getUniqueId().toString())) {
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

	public static void saveInventory(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + prefix("Inventories") + " (Name) VALUES (?)");

			statement.setString(1, name);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean getInventory(String name) {
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

	public static List<String> getInventoryList() {
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

	public static void deleteInventory(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE from " + prefix("Inventories") + " WHERE Name = ?");

			statement.setString(1, name);

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
