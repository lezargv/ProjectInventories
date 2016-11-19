package com.gmail.trentech.pji;

import com.gmail.trentech.pji.settings.InventorySettings;
import com.gmail.trentech.pji.settings.PlayerSettings;
import com.gmail.trentech.pji.settings.WorldSettings;

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
