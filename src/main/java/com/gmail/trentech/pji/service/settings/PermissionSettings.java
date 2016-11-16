package com.gmail.trentech.pji.service.settings;

import java.util.HashMap;
import java.util.Optional;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.sql.PermissionDB;

public class PermissionSettings {

	InventoryService inventoryService;

	public PermissionSettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public Optional<String> get(String inventory) {
		return PermissionDB.get(inventory);
	}

	public void set(String inventory, String permission) {
		PermissionDB.save(inventory, permission);
	}

	public void remove(String inventory) {
		PermissionDB.remove(inventory);
	}

	public boolean exists(String inventory) {
		return PermissionDB.exists(inventory);
	}

	public HashMap<String, String> all() {
		return PermissionDB.all();
	}
}
