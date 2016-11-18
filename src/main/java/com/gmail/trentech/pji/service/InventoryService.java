package com.gmail.trentech.pji.service;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

import com.gmail.trentech.pji.service.settings.GamemodeSettings;
import com.gmail.trentech.pji.service.settings.InventorySettings;
import com.gmail.trentech.pji.service.settings.PermissionSettings;
import com.gmail.trentech.pji.service.settings.PlayerSettings;
import com.gmail.trentech.pji.service.settings.WorldSettings;
import com.gmail.trentech.pji.sql.PlayerDB;

public class InventoryService {

	public PlayerSettings getPlayerSettings() {
		return new PlayerSettings(this);
	}

	public WorldSettings getWorldSettings() {
		return new WorldSettings(this);
	}

	public InventorySettings getInventorySettings() {
		return new InventorySettings(this);
	}

	public PermissionSettings getPermissionSettings() {
		return new PermissionSettings(this);
	}

	public GamemodeSettings getGamemodeSettings() {
		return new GamemodeSettings(this);
	}
	
	public InventoryData empty(String name) {
		return new InventoryData(name);
	}
	
	public InventoryData copy(Player player) {
		InventoryData inventoryData = new InventoryData(Sponge.getServiceManager().provideUnchecked(InventoryService.class).getPlayerSettings().get(player));

		PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

		int i = 0;
		for (Inventory item : inv.getHotbar().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				inventoryData.addHotbar(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (Inventory item : inv.getMain().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				inventoryData.addGrid(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (Inventory item : inv.getEquipment().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				inventoryData.addEquipment(i, peek.get());
			}
			i++;
		}

		inventoryData.setOffHand(player.getItemInHand(HandTypes.OFF_HAND));
		inventoryData.setHealth(player.get(Keys.HEALTH).get());
		inventoryData.setFood(player.get(Keys.FOOD_LEVEL).get());
		inventoryData.setSaturation(player.get(Keys.SATURATION).get());
		inventoryData.setExpLevel(player.get(Keys.EXPERIENCE_LEVEL).get());
		inventoryData.setExperience(player.get(Keys.TOTAL_EXPERIENCE).get());
		
		return inventoryData;
	}
	
	public Optional<InventoryData> get(Player player, String inventory) {
		return PlayerDB.Data.get(player, inventory);
	}

	public void save(Player player, InventoryData inventoryData) {
		if (PlayerDB.Data.exists(player, inventoryData.getName())) {
			PlayerDB.Data.update(player, inventoryData);
		} else {
			PlayerDB.Data.create(player, inventoryData);
		}
	}
}
