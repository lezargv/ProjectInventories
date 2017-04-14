package com.gmail.trentech.pji.settings;

import java.util.HashMap;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.PlayerChestData;
import com.gmail.trentech.pji.sql.ChestDB;

public class ChestSettings {
	
	InventoryService inventoryService;

	public ChestSettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}
	
	public HashMap<UUID, PlayerChestData> all() {
		return ChestDB.all();
	}
	
	public PlayerChestData get(Player player) {
		return ChestDB.get(player.getUniqueId());
	}

	public void save(Player player, PlayerChestData inventoryData) {
		ChestDB.save(player.getUniqueId(), inventoryData);
	}

	public void delete(Player player) {
		ChestDB.remove(player.getUniqueId());
	}
}
