package com.gmail.trentech.pji.service.settings;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;

import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.sql.PlayerDB;

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

		Optional<PlayerData> optionalPlayerData = inventoryService.get(player, inventory);
		
		if(optionalPlayerData.isPresent()) {
			optionalPlayerData.get().set();
		} else {
			if(!login) {
				player.getInventory().clear();
			}
			
			inventoryService.save(new PlayerData(player));
		}
	}
}
