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
import com.gmail.trentech.pji.service.data.InventoryData;
import com.gmail.trentech.pji.service.settings.InventorySettings;
import com.gmail.trentech.pji.service.settings.PlayerSettings;
import com.gmail.trentech.pji.service.settings.WorldSettings;
import com.gmail.trentech.pji.utils.ConfigManager;

public class EventManager {

	@Listener(order = Order.POST)
	public void ClientConnectionEventJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
		Sponge.getScheduler().createTaskBuilder().async().delayTicks(35).execute(t -> {
			InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
			
			InventorySettings inventorySettings = inventoryService.getInventorySettings();
			PlayerSettings playerSettings = inventoryService.getPlayerSettings();

			InventoryData inventory = inventorySettings.get(playerSettings.getInventoryName(player)).get();
			
			Optional<GameMode> optionalGamemode = inventory.getGamemode();

			if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
				player.offer(Keys.GAME_MODE, optionalGamemode.get());
			}
			
			playerSettings.set(player, inventory.getName(), true);
		}).submit(Main.getPlugin());
	}

	@Listener(order = Order.PRE)
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		PlayerSettings playerSettings = inventoryService.getPlayerSettings();
		
		playerSettings.save(player, playerSettings.copy(player));
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
			
			PlayerSettings playerSettings = inventoryService.getPlayerSettings();
			
			playerSettings.save(player, playerSettings.copy(player));
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
		InventorySettings inventorySettings = inventoryService.getInventorySettings();

		if (worldSettings.contains(to, playerSettings.getInventoryName(player)) && !ConfigManager.get().getConfig().getNode("options", "default_on_world_change").getBoolean()) {
			return;
		}

		playerSettings.save(player, playerSettings.copy(player));
		
		InventoryData inventory = inventorySettings.get(worldSettings.getDefault(to)).get();

		Optional<GameMode> optionalGamemode = inventory.getGamemode();

		if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
			player.offer(Keys.GAME_MODE, optionalGamemode.get());
		}

		playerSettings.set(player, inventory.getName(), false);
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
		InventorySettings inventorySettings = inventoryService.getInventorySettings();
		
		if (worldSettings.contains(to, playerSettings.getInventoryName(player)) && !ConfigManager.get().getConfig().getNode("options", "default_on_world_change").getBoolean()) {
			return;
		}

		playerSettings.save(player, playerSettings.copy(player));

		InventoryData inventory = inventorySettings.get(worldSettings.getDefault(to)).get();
		
		Optional<GameMode> optionalGamemode = inventory.getGamemode();

		if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
			player.offer(Keys.GAME_MODE, optionalGamemode.get());
		}
		
		playerSettings.set(player, inventory.getName(), false);
	}
}
