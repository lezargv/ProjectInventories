package com.gmail.trentech.pji.settings;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.data.WorldData;
import com.gmail.trentech.pji.sql.InventoryDB;
import com.gmail.trentech.pji.sql.PlayerDB;

public class InventorySettings {

	InventoryService inventoryService;

	public InventorySettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}
	
	public HashMap<String, InventoryData> all() {
		return InventoryDB.all();
	}
	
	public Optional<InventoryData> get(String inventory) {
		return InventoryDB.get(inventory);
	}

	public void save(InventoryData inventoryData) {
		if (InventoryDB.get(inventoryData.getName()).isPresent()) {
			InventoryDB.update(inventoryData);
		} else {
			InventoryDB.create(inventoryData);
		}
	}

	public void delete(String inventory) {
		for (WorldProperties properties : Sponge.getServer().getAllWorldProperties()) {
			WorldSettings worldSettings = inventoryService.getWorldSettings();

			WorldData worldData = worldSettings.get(properties);
			
			if(worldData.contains(inventory)) {
				if(worldData.getDefault().equalsIgnoreCase(inventory)) {
					worldData.add("DEFAULT", true);
				}
				
				worldData.remove(inventory);
				
				if(worldData.getInventories().isEmpty()) {
					worldData.add("DEFAULT", true);
				}
			}		

			PlayerSettings playerSettings = inventoryService.getPlayerSettings();
			InventorySettings inventorySettings = inventoryService.getInventorySettings();
			
			for (Entry<UUID, PlayerData> entry : PlayerDB.all().entrySet()) {
				UUID uuid = entry.getKey();
				PlayerData playerData = entry.getValue();

				Sponge.getServer().getPlayer(uuid).ifPresent(player -> {
					if (playerData.getInventoryName().equals(inventory)) {
						playerSettings.set(player, inventorySettings.get(worldData.getDefault()).get(), false);

						player.sendMessage(Text.of(TextColors.RED, "[PJI] ", TextColors.YELLOW, "The inventory been permenently deleted by an admin. Changing to default inventory"));
					}
				});
				
				PlayerData playerData2 = PlayerDB.get(uuid);
				playerData2.remove(inventory);
				PlayerDB.save(uuid, playerData2);
			}
			
			worldSettings.save(worldData);
		}

		InventoryDB.remove(inventory);
	}

}
