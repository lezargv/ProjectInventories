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
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.settings.InventorySettings;
import com.gmail.trentech.pji.settings.PlayerSettings;
import com.gmail.trentech.pji.settings.WorldSettings;

import ninja.leaping.configurate.ConfigurationNode;

public class EventManager {

	@Listener(order = Order.POST)
	public void ClientConnectionEventJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
		long delay = 0;
		
		ConfigurationNode config = ConfigManager.get(Main.getPlugin()).getConfig();
		
		if(config.getNode("settings", "sql", "enable").getBoolean()) {
			delay = config.getNode("settings", "sql", "login-delay").getLong();
		}
		
		Task.builder().async().delayTicks(delay).execute(t -> {
			InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
			
			InventorySettings inventorySettings = inventoryService.getInventorySettings();
			PlayerSettings playerSettings = inventoryService.getPlayerSettings();

			InventoryData inventoryData = inventorySettings.get(playerSettings.getPlayerData(player).getInventoryName()).get();

			Optional<GameMode> optionalGamemode = inventoryData.getGamemode();

			if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
				player.offer(Keys.GAME_MODE, optionalGamemode.get());
			}

			playerSettings.set(player, inventoryData, true);	
		}).submit(Main.getPlugin());
	}
	
	@Listener(order = Order.PRE)
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		PlayerSettings playerSettings = inventoryService.getPlayerSettings();
		
		playerSettings.save(player, playerSettings.copy(player));

		player.getInventory().clear();
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

		if (worldSettings.get(to).contains(playerSettings.getPlayerData(player).getInventoryName()) && !ConfigManager.get(Main.getPlugin()).getConfig().getNode("options", "default-on-world-change").getBoolean()) {
			return;
		}

		playerSettings.save(player, playerSettings.copy(player));
		
		InventoryData inventoryData = inventorySettings.get(worldSettings.get(to).getDefault()).get();

		Optional<GameMode> optionalGamemode = inventoryData.getGamemode();

		if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
			player.offer(Keys.GAME_MODE, optionalGamemode.get());
		}

		playerSettings.set(player, inventoryData, false);
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
		
		if (worldSettings.get(to).contains(playerSettings.getPlayerData(player).getInventoryName()) && !ConfigManager.get(Main.getPlugin()).getConfig().getNode("options", "default_on_world_change").getBoolean()) {
			return;
		}

		playerSettings.save(player, playerSettings.copy(player));

		InventoryData inventoryData = inventorySettings.get(worldSettings.get(to).getDefault()).get();

		Optional<GameMode> optionalGamemode = inventoryData.getGamemode();

		if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
			player.offer(Keys.GAME_MODE, optionalGamemode.get());
		}

		playerSettings.set(player, inventoryData, false);
	}
}
