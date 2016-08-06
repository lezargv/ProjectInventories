package com.gmail.trentech.pji;

import java.util.function.Predicate;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.data.inventory.extra.InventoryHelper;
import com.gmail.trentech.pji.sql.SQLSettings;

public class EventManager {

	@Listener
	public void ClientConnectionEventJoin(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();

		WorldProperties properties = player.getWorld().getProperties();

		String name = SQLSettings.getWorld(properties).get();

		if (!SQLSettings.getPlayer(player)) {
			SQLSettings.savePlayer(player);
			return;
		}
		InventoryHelper.setInventory(player, name);
	}

	@Listener
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event) {
		Player player = event.getTargetEntity();

		WorldProperties properties = player.getWorld().getProperties();

		String name = SQLSettings.getWorld(properties).get();

		InventoryHelper.saveInventory(player, name);
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

			String name = SQLSettings.getWorld(properties).get();

			InventoryHelper.saveInventory(player, name);
		}
	}

	@Listener
	public void onRespawnPlayerEvent(RespawnPlayerEvent event) {
		Player player = event.getTargetEntity();

		WorldProperties properties = player.getWorld().getProperties();

		String name = SQLSettings.getWorld(properties).get();

		InventoryHelper.saveInventory(player, name);
	}

	@Listener(order = Order.POST)
	public void onMoveEntityEventTeleport(MoveEntityEvent.Teleport event) {
		Entity entity = event.getTargetEntity();

		if (!(entity instanceof Player)) {
			return;
		}
		Player player = (Player) entity;

		WorldProperties from = event.getFromTransform().getExtent().getProperties();
		WorldProperties to = event.getToTransform().getExtent().getProperties();

		if (from.equals(to)) {
			return;
		}

		String fromName = SQLSettings.getWorld(from).get();
		String toName = SQLSettings.getWorld(to).get();

		if (fromName.equalsIgnoreCase(toName)) {
			return;
		}

		InventoryHelper.saveInventory(player, fromName);
		InventoryHelper.setInventory(player, toName);
	}

	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event) {
		WorldProperties properties = event.getTargetWorld().getProperties();

		if (!SQLSettings.getWorld(properties).isPresent()) {
			SQLSettings.saveWorld(properties);
		}
	}
}
