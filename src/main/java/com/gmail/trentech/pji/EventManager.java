package com.gmail.trentech.pji;

import java.util.function.Predicate;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.settings.PlayerSettings;
import com.gmail.trentech.pji.service.settings.WorldSettings;
import com.gmail.trentech.pji.utils.ConfigManager;

public class EventManager {

	@Listener(order = Order.POST)
	public void ClientConnectionEventJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
		Sponge.getScheduler().createTaskBuilder().async().delayTicks(35).execute(t -> {
			PlayerSettings playerSettings = Sponge.getServiceManager().provideUnchecked(InventoryService.class).getPlayerSettings();
			
			playerSettings.set(player, playerSettings.get(player), true);
		}).submit(Main.getPlugin());
	}

	@Listener(order = Order.PRE)
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
		Sponge.getServiceManager().provideUnchecked(InventoryService.class).save(new PlayerData(player));
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

			Sponge.getServiceManager().provideUnchecked(InventoryService.class).save(new PlayerData(player));
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
		
		if(worldSettings.contains(to, playerSettings.get(player)) && !ConfigManager.get().getConfig().getNode("options", "default_on_world_change").getBoolean()) {
			return;
		}
		
		inventoryService.save(new PlayerData(player));

		playerSettings.set(player, worldSettings.getDefault(to), false);
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
		
		if(worldSettings.contains(to, playerSettings.get(player)) && !ConfigManager.get().getConfig().getNode("options", "default_on_world_change").getBoolean()) {
			return;
		}
		
		inventoryService.save(new PlayerData(player));
		
		playerSettings.set(player, worldSettings.getDefault(to), false);
	}
}
