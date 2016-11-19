package com.gmail.trentech.pji.settings;

import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.WorldData;
import com.gmail.trentech.pji.sql.WorldDB;

public class WorldSettings {

	public WorldSettings(InventoryService inventoryService) {
		
	}

	public WorldData get(WorldProperties world) {
		return WorldDB.get(world.getUniqueId());
	}
	
	public void save(WorldData worldData) {
		WorldDB.update(worldData);
	}
	
	public void remove(WorldData worldData) {
		WorldDB.remove(worldData);
	}
}
