package com.gmail.trentech.pji.service;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;

import com.gmail.trentech.pji.data.PlayerData;
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
	
	public Optional<PlayerData> get(Player player, String inventory) {
		return PlayerDB.Data.get(player, inventory);
	}

	public void save(PlayerData playerData) {
		if (PlayerDB.Data.exists(playerData)) {
			PlayerDB.Data.update(playerData);
		} else {
			PlayerDB.Data.create(playerData);
		}
	}
}
