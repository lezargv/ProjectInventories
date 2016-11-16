package com.gmail.trentech.pji.service.settings;

import java.util.List;
import java.util.function.Predicate;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.sql.InventoryDB;

public class InventorySettings {

	InventoryService inventoryService;

	public InventorySettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public void create(String inventory) {
		InventoryDB.create(inventory);
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

					if (playerSettings.get(player).equals(inventory)) {
						inventoryService.get(player, worldSettings.getDefault(properties)).get().set();

						player.sendMessage(Text.of(TextColors.RED, "[PJI] ", TextColors.YELLOW, "The inventory for this world has been removed by an admin. Changing to default inventory"));
					}
				}
			});
		}

		inventoryService.getPermissionSettings().remove(inventory);

		InventoryDB.remove(inventory);
	}

	public boolean exists(String inventory) {
		return InventoryDB.exists(inventory);
	}

	public List<String> all() {
		return InventoryDB.all();
	}
}
