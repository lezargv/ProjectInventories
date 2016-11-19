package com.gmail.trentech.pji.service.settings;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.data.InventoryPlayer;
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
	
	public Optional<InventoryPlayer> get(Player player, String inventory) {
		return PlayerDB.Data.get(player, inventory);
	}
	
	public InventoryPlayer empty(String name) {
		return new InventoryPlayer(name);
	}
	
	public InventoryPlayer copy(Player player) {
		InventoryPlayer inventoryPlayer = new InventoryPlayer(getInventoryName(player));

		PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

		int i = 0;
		for (Inventory item : inv.getHotbar().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				inventoryPlayer.addHotbar(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (Inventory item : inv.getMain().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				inventoryPlayer.addGrid(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (Inventory item : inv.getEquipment().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				inventoryPlayer.addEquipment(i, peek.get());
			}
			i++;
		}

		inventoryPlayer.setOffHand(player.getItemInHand(HandTypes.OFF_HAND));
		inventoryPlayer.setHealth(player.get(Keys.HEALTH).get());
		inventoryPlayer.setFood(player.get(Keys.FOOD_LEVEL).get());
		inventoryPlayer.setSaturation(player.get(Keys.SATURATION).get());
		inventoryPlayer.setExpLevel(player.get(Keys.EXPERIENCE_LEVEL).get());
		inventoryPlayer.setExperience(player.get(Keys.TOTAL_EXPERIENCE).get());
		
		return inventoryPlayer;
	}

	public void save(Player player, InventoryPlayer inventoryPlayer) {
		if (PlayerDB.Data.exists(player, inventoryPlayer.getName())) {
			PlayerDB.Data.update(player, inventoryPlayer);
		} else {
			PlayerDB.Data.create(player, inventoryPlayer);
		}
	}

	public void set(Player player, String inventory, boolean login) {
		PlayerDB.save(player, inventory);

		Optional<InventoryPlayer> optionalInventoryPlayer = get(player, inventory);

		if (optionalInventoryPlayer.isPresent()) {
			InventoryPlayer inventoryPlayer = optionalInventoryPlayer.get();
			
			player.getInventory().clear();

			PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

			Map<Integer, ItemStack> hotbar = inventoryPlayer.getHotbar();

			if (!hotbar.isEmpty()) {
				int i = 0;
				for (Inventory slot : inv.getHotbar().slots()) {
					if (hotbar.containsKey(i)) {
						slot.set(hotbar.get(i));
					}
					i++;
				}
			}

			Map<Integer, ItemStack> grid = inventoryPlayer.getGrid();

			if (!grid.isEmpty()) {
				int i = 0;
				for (Inventory slot : inv.getMain().slots()) {
					if (grid.containsKey(i)) {
						slot.set(grid.get(i));
					}
					i++;
				}
			}

			Map<Integer, ItemStack> equipment = inventoryPlayer.getEquipment();

			if (!equipment.isEmpty()) {
				int i = 0;
				for (Inventory slot : inv.getEquipment().slots()) {
					if (equipment.containsKey(i)) {
						slot.set(equipment.get(i));
					}
					i++;
				}
			}

			Optional<ItemStack> offHand = inventoryPlayer.getOffHand();

			if (offHand.isPresent()) {
				player.setItemInHand(HandTypes.OFF_HAND, offHand.get());
			}

			ConfigurationNode config = ConfigManager.get().getConfig();

			if (config.getNode("options", "health").getBoolean()) {
				player.offer(Keys.HEALTH, inventoryPlayer.getHealth());
			}

			if (config.getNode("options", "hunger").getBoolean()) {
				player.offer(Keys.FOOD_LEVEL, inventoryPlayer.getFood());
				player.offer(Keys.SATURATION, inventoryPlayer.getSaturation());
			}

			if (config.getNode("options", "experience").getBoolean()) {
				player.offer(Keys.EXPERIENCE_LEVEL, inventoryPlayer.getExpLevel());
				player.offer(Keys.TOTAL_EXPERIENCE, inventoryPlayer.getExperience());
			}	
		} else {
			if (!login) {
				player.getInventory().clear();
			}

			save(player, copy(player));
		}
	}
}
