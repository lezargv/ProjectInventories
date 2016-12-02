package com.gmail.trentech.pji.settings;

import java.util.HashMap;
import java.util.UUID;

import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.WorldData;
import com.gmail.trentech.pji.sql.WorldDB;

public class WorldSettings {

	InventoryService inventoryService;

	public WorldSettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public WorldData get(WorldProperties world) {
		return WorldDB.get(world.getUniqueId());
	}
	
	public HashMap<UUID, WorldData> all() {
		return WorldDB.all();
	}
	
	public void save(WorldData worldData) {
		WorldDB.update(worldData);
	}
	
	public void remove(WorldData worldData) {
		WorldDB.remove(worldData);
	}
}
