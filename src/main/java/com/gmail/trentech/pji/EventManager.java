package com.gmail.trentech.pji;

import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.settings.GamemodeSettings;
import com.gmail.trentech.pji.service.settings.PlayerSettings;
import com.gmail.trentech.pji.service.settings.WorldSettings;
import com.gmail.trentech.pji.utils.ConfigManager;

public class EventManager {

	@Listener(order = Order.POST)
	public void ClientConnectionEventJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
		Sponge.getScheduler().createTaskBuilder().async().delayTicks(35).execute(t -> {
			InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
			
			PlayerSettings playerSettings = inventoryService.getPlayerSettings();
			GamemodeSettings gamemodeSettings = inventoryService.getGamemodeSettings();

			String inventory = playerSettings.get(player);
			
			Optional<GameMode> optionalGamemode = gamemodeSettings.get(inventory);

			if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
				player.offer(Keys.GAME_MODE, optionalGamemode.get());
			}
			
			playerSettings.set(player, inventory, true);
		}).submit(Main.getPlugin());
	}

	@Listener(order = Order.PRE)
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		inventoryService.save(player, inventoryService.copy(player));
	}

	@Listener
	public void onSaveWorldEvent(SaveWorldEvent event) {
		Predicate<Entity> filter = new Predicate<Entity>() {

			@Override
			public boolean test(Entity entity) {
				return entity instanceof Player;
			}
		};

		for (Entity entity : event.getTargetWorld().getEntities(filter)) {
			Player player = (Player) entity;

			InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
			
			inventoryService.save(player, inventoryService.copy(player));
		}
	}

	@Listener
	public void onRespawnPlayerEvent(RespawnPlayerEvent event) {
		Player player = event.getTargetEntity();

		WorldProperties from = event.getFromTransform().getExtent().getProperties();
		WorldProperties to = event.getToTransform().getExtent().getProperties();

		if (from.equals(to)) {
			return;
		}

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		WorldSettings worldSettings = inventoryService.getWorldSettings();
		PlayerSettings playerSettings = inventoryService.getPlayerSettings();
		GamemodeSettings gamemodeSettings = inventoryService.getGamemodeSettings();
		
		if (worldSettings.contains(to, playerSettings.get(player)) && !ConfigManager.get().getConfig().getNode("options", "default_on_world_change").getBoolean()) {
			return;
		}

		inventoryService.save(player, inventoryService.copy(player));

		String inventory = worldSettings.getDefault(to);
		
		Optional<GameMode> optionalGamemode = gamemodeSettings.get(inventory);

		if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
			player.offer(Keys.GAME_MODE, optionalGamemode.get());
		}

		playerSettings.set(player, inventory, false);
	}

	@Listener(order = Order.POST)
	public void onMoveEntityEventTeleport(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player player) {
		WorldProperties from = event.getFromTransform().getExtent().getProperties();
		WorldProperties to = event.getToTransform().getExtent().getProperties();

		if (from.equals(to)) {
			return;
		}

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		WorldSettings worldSettings = inventoryService.getWorldSettings();
		PlayerSettings playerSettings = inventoryService.getPlayerSettings();
		GamemodeSettings gamemodeSettings = inventoryService.getGamemodeSettings();
		
		if (worldSettings.contains(to, playerSettings.get(player)) && !ConfigManager.get().getConfig().getNode("options", "default_on_world_change").getBoolean()) {
			return;
		}

		inventoryService.save(player, inventoryService.copy(player));

		String inventory = worldSettings.getDefault(to);
		
		Optional<GameMode> optionalGamemode = gamemodeSettings.get(inventory);

		if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
			player.offer(Keys.GAME_MODE, optionalGamemode.get());
		}
		
		playerSettings.set(player, inventory, false);
	}
}
