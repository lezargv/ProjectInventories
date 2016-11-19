package com.gmail.trentech.pji.service.settings;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.data.InventoryData;
import com.gmail.trentech.pji.sql.InventoryDB;

public class InventorySettings {

	InventoryService inventoryService;

	public InventorySettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
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

			worldSettings.remove(properties, inventory);

			Sponge.getServer().getWorld(properties.getWorldName()).ifPresent(world -> {
				Predicate<Entity> filter = new Predicate<Entity>() {

					@Override
					public boolean test(Entity entity) {
						return entity instanceof Player;
					}
				};

				PlayerSettings playerSettings = inventoryService.getPlayerSettings();

				for (Entity entity : world.getEntities(filter)) {
					Player player = (Player) entity;

					if (playerSettings.getInventoryName(player).equals(inventory)) {
						playerSettings.set(player, worldSettings.getDefault(properties), false);

						player.sendMessage(Text.of(TextColors.RED, "[PJI] ", TextColors.YELLOW, "The inventory for this world has been removed by an admin. Changing to default inventory"));
					}
				}
			});
		}

		InventoryDB.remove(inventory);
	}

}
