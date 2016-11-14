package com.gmail.trentech.pji;

import java.util.Optional;
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
import com.gmail.trentech.pji.settings.WorldData;

public class EventManager {

	@Listener(order = Order.POST)
	public void ClientConnectionEventJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
		Sponge.getScheduler().createTaskBuilder().async().delayTicks(35).execute(t -> {
			WorldProperties properties = player.getWorld().getProperties();

			Optional<PlayerData> optionalPlayerData = PlayerData.get(player, WorldData.get(properties).getInventory());
			
			if(optionalPlayerData.isPresent()) {
				optionalPlayerData.get().set();
			}
		}).submit(Main.getPlugin());
	}

	@Listener(order = Order.PRE)
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
		WorldProperties properties = player.getWorld().getProperties();

		new PlayerData(player, WorldData.get(properties).getInventory()).save();
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

			WorldProperties properties = player.getWorld().getProperties();

			new PlayerData(player, WorldData.get(properties).getInventory()).save();
		}
	}

	@Listener
	public void onRespawnPlayerEvent(RespawnPlayerEvent event) {
		Player player = event.getTargetEntity();

		WorldProperties from = event.getFromTransform().getExtent().getProperties();
		WorldProperties to = event.getToTransform().getExtent().getProperties();
		
		String fromName = WorldData.get(from).getInventory();

		new PlayerData(player, fromName).save();
		
		if (from.equals(to)) {
			return;
		}

		String toName = WorldData.get(to).getInventory();
		
		if (toName.equals(fromName)) {	
			return;
		}

		Optional<PlayerData> optionalPlayerData = PlayerData.get(player, toName);
		
		if(optionalPlayerData.isPresent()) {
			optionalPlayerData.get().set();
		} else {
			player.getInventory().clear();
			new PlayerData(player, toName).save();
		}	
	}

	@Listener(order = Order.POST)
	public void onMoveEntityEventTeleport(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player player) {
		WorldProperties from = event.getFromTransform().getExtent().getProperties();
		WorldProperties to = event.getToTransform().getExtent().getProperties();

		if (from.equals(to)) {
			return;
		}

		String fromName =  WorldData.get(from).getInventory();
		String toName =  WorldData.get(to).getInventory();

		if (fromName.equalsIgnoreCase(toName)) {
			return;
		}

		new PlayerData(player, fromName).save();
		
		Optional<PlayerData> optionalPlayerData = PlayerData.get(player, toName);
		
		if(optionalPlayerData.isPresent()) {
			optionalPlayerData.get().set();
		} else {
			player.getInventory().clear();
			new PlayerData(player, toName).save();
		}
	}
}
