package com.gmail.trentech.pji.service;

import com.gmail.trentech.pji.service.settings.InventorySettings;
import com.gmail.trentech.pji.service.settings.PlayerSettings;
import com.gmail.trentech.pji.service.settings.WorldSettings;

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

}
