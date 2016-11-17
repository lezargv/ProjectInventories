package com.gmail.trentech.pji.service.settings;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

import com.gmail.trentech.pji.service.InventoryData;
import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.sql.PlayerDB;
import com.gmail.trentech.pji.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class PlayerSettings {

	InventoryService inventoryService;

	public PlayerSettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public String get(Player player) {
		return PlayerDB.get(player);
	}

	public void set(Player player, String inventory, boolean login) {
		PlayerDB.save(player, inventory);

		Optional<InventoryData> optionalInventoryData = inventoryService.get(player, inventory);

		if (optionalInventoryData.isPresent()) {
			InventoryData inventoryData = optionalInventoryData.get();
			
			player.getInventory().clear();

			PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

			Map<Integer, ItemStack> hotbar = inventoryData.getHotbar();

			if (!hotbar.isEmpty()) {
				int i = 0;
				for (Inventory slot : inv.getHotbar().slots()) {
					if (hotbar.containsKey(i)) {
						slot.set(hotbar.get(i));
					}
					i++;
				}
			}

			Map<Integer, ItemStack> grid = inventoryData.getGrid();

			if (!grid.isEmpty()) {
				int i = 0;
				for (Inventory slot : inv.getMain().slots()) {
					if (grid.containsKey(i)) {
						slot.set(grid.get(i));
					}
					i++;
				}
			}

			Map<Integer, ItemStack> equipment = inventoryData.getEquipment();

			if (!equipment.isEmpty()) {
				int i = 0;
				for (Inventory slot : inv.getEquipment().slots()) {
					if (equipment.containsKey(i)) {
						slot.set(equipment.get(i));
					}
					i++;
				}
			}

			Optional<ItemStack> offHand = inventoryData.getOffHand();

			if (offHand.isPresent()) {
				player.setItemInHand(HandTypes.OFF_HAND, offHand.get());
			}

			ConfigurationNode config = ConfigManager.get().getConfig();

			if (config.getNode("options", "health").getBoolean()) {
				player.offer(Keys.HEALTH, inventoryData.getHealth());
			}

			if (config.getNode("options", "hunger").getBoolean()) {
				player.offer(Keys.FOOD_LEVEL, inventoryData.getFood());
				player.offer(Keys.SATURATION, inventoryData.getSaturation());
			}

			if (config.getNode("options", "experience").getBoolean()) {
				player.offer(Keys.EXPERIENCE_LEVEL, inventoryData.getExpLevel());
				player.offer(Keys.TOTAL_EXPERIENCE, inventoryData.getExperience());
			}	
		} else {
			if (!login) {
				player.getInventory().clear();
			}

			inventoryService.save(player, inventoryService.copy(player));
		}
	}
}
