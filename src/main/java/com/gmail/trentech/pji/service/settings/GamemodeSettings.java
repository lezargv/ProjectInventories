package com.gmail.trentech.pji.service.settings;

import java.util.HashMap;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.sql.GamemodeDB;

public class GamemodeSettings {

	InventoryService inventoryService;

	public GamemodeSettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public Optional<GameMode> get(String inventory) {
		return GamemodeDB.get(inventory);
	}

	public void set(String inventory, GameMode gamemode) {
		GamemodeDB.save(inventory, gamemode.getId());
	}

	public void remove(String inventory) {
		GamemodeDB.remove(inventory);
	}

	public boolean exists(String inventory) {
		return GamemodeDB.exists(inventory);
	}

	public HashMap<String, String> all() {
		return GamemodeDB.all();
	}
}
