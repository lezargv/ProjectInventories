package com.gmail.trentech.pji.service.settings;

import java.util.HashMap;
import java.util.Map.Entry;

import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.sql.WorldDB;

public class WorldSettings {

	InventoryService inventoryService;

	public WorldSettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public HashMap<String, Boolean> all(WorldProperties world) {
		return WorldDB.get(world);
	}
	
	public String getDefault(WorldProperties world) {
		for (Entry<String, Boolean> entry : all(world).entrySet()) {
			if (entry.getValue()) {
				return entry.getKey();
			}
		}

		return null;
	}

	public void add(WorldProperties world, String inventory, boolean isDefault) {
		WorldDB.add(world, inventory, isDefault);
	}

	public void remove(WorldProperties world, String inventory) {
		WorldDB.remove(world, inventory);
	}

	public boolean contains(WorldProperties world, String inventory) {
		return WorldDB.get(world).containsKey(inventory);
	}

}
