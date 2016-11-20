package com.gmail.trentech.pji.settings;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.sql.PlayerDB;
import com.gmail.trentech.pji.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class PlayerSettings {

	InventoryService inventoryService;

	public PlayerSettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public String getInventoryName(Player player) {
		return PlayerDB.get(player);
	}
	
	public Optional<PlayerData> get(Player player, String inventory) {
		return PlayerDB.Data.get(player, inventory);
	}
	
	public PlayerData empty(String name) {
		return new PlayerData(name);
	}
	
	public PlayerData copy(Player player) {
		PlayerData playerData = new PlayerData(getInventoryName(player));

		PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

		int i = 0;
		for (Inventory item : inv.getHotbar().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				playerData.addHotbar(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (Inventory item : inv.getMain().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				playerData.addGrid(i, peek.get());
			}
			i++;
		}

		playerData.setOffHand(player.getItemInHand(HandTypes.OFF_HAND));
		playerData.setHelmet(player.getHelmet());
		playerData.setChestPlate(player.getChestplate());
		playerData.setLeggings(player.getLeggings());
		playerData.setBoots(player.getBoots());	
		playerData.setHealth(player.get(Keys.HEALTH).get());
		playerData.setFood(player.get(Keys.FOOD_LEVEL).get());
		playerData.setSaturation(player.get(Keys.SATURATION).get());
		playerData.setExpLevel(player.get(Keys.EXPERIENCE_LEVEL).get());
		playerData.setExperience(player.get(Keys.TOTAL_EXPERIENCE).get());
		
		return playerData;
	}

	public void save(Player player, PlayerData playerData) {
		if (PlayerDB.Data.exists(player, playerData.getName())) {
			PlayerDB.Data.update(player, playerData);
		} else {
			PlayerDB.Data.create(player, playerData);
		}
	}

	public void set(Player player, InventoryData inventoryData, boolean login) {
		PlayerDB.save(player, inventoryData.getName());

		Optional<PlayerData> optionalPlayerData = get(player, inventoryData.getName());

		if (optionalPlayerData.isPresent()) {
			PlayerData playerData = optionalPlayerData.get();
			
			player.getInventory().clear();

			PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

			Map<Integer, ItemStack> hotbar = playerData.getHotbar();

			if (!hotbar.isEmpty()) {
				int i = 0;
				for (Inventory slot : inv.getHotbar().slots()) {
					if (hotbar.containsKey(i)) {
						slot.set(hotbar.get(i));
					}
					i++;
				}
			}

			Map<Integer, ItemStack> grid = playerData.getGrid();

			if (!grid.isEmpty()) {
				int i = 0;
				for (Inventory slot : inv.getMain().slots()) {
					if (grid.containsKey(i)) {
						slot.set(grid.get(i));
					}
					i++;
				}
			}

			Optional<ItemStack> helmet = playerData.getHelmet();

			if (helmet.isPresent()) {
				player.setHelmet(helmet.get());
			}

			Optional<ItemStack> chestPlate = playerData.getChestPlate();

			if (chestPlate.isPresent()) {
				player.setChestplate(chestPlate.get());
			}
			
			Optional<ItemStack> leggings = playerData.getLeggings();

			if (leggings.isPresent()) {
				player.setLeggings(leggings.get());
			}
			
			Optional<ItemStack> boots = playerData.getBoots();

			if (boots.isPresent()) {
				player.setBoots(boots.get());
			}
			
			Optional<ItemStack> offHand = playerData.getOffHand();

			if (offHand.isPresent()) {
				player.setItemInHand(HandTypes.OFF_HAND, offHand.get());
			}

			ConfigurationNode config = ConfigManager.get().getConfig();

			if (config.getNode("options", "health").getBoolean()) {
				player.offer(Keys.HEALTH, playerData.getHealth());
			}

			if (config.getNode("options", "hunger").getBoolean()) {
				player.offer(Keys.FOOD_LEVEL, playerData.getFood());
				player.offer(Keys.SATURATION, playerData.getSaturation());
			}

			if (config.getNode("options", "experience").getBoolean()) {
				player.offer(Keys.EXPERIENCE_LEVEL, playerData.getExpLevel());
				player.offer(Keys.TOTAL_EXPERIENCE, playerData.getExperience());
			}	
		} else {
			if (!login) {
				player.getInventory().clear();
			}

			save(player, copy(player));
		}
	}
}
