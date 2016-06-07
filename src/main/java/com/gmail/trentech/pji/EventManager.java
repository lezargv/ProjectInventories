package com.gmail.trentech.pji;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pji.data.inventory.extra.InventoryHelper;
import com.gmail.trentech.pji.sql.SQLSettings;

public class EventManager {

	@Listener
	public void ClientConnectionEventJoin(ClientConnectionEvent.Join event) {
	    Player player = event.getTargetEntity();
	    
	    World world = player.getWorld();

		String name = SQLSettings.getWorld(world).get();

		if(!SQLSettings.getPlayer(player)) {
			SQLSettings.savePlayer(player);
			return;
		}
		InventoryHelper.setInventory(player, name);
	}
	
	@Listener
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event) {
	    Player player = event.getTargetEntity();
	    
	    World world = player.getWorld();

		String name = SQLSettings.getWorld(world).get();

		InventoryHelper.saveInventory(player, name);
	}

	@Listener
	public void onSaveWorldEvent(SaveWorldEvent event) {
		for(Entity entity : event.getTargetWorld().getEntities()) {
			if(entity instanceof Player) {
				Player player = (Player) entity;
				
			    World world = player.getWorld();

				String name = SQLSettings.getWorld(world).get();

				InventoryHelper.saveInventory(player, name);
			}
		}
	}	
	
	@Listener(order = Order.POST)
	public void onMoveEntityEvent(MoveEntityEvent.Teleport event) {
		Entity entity = event.getTargetEntity();
		
		if(!(entity instanceof Player)) {
			return;
		}
		Player player = (Player) entity;

		World from = event.getFromTransform().getExtent();
		World to = event.getToTransform().getExtent();
		
		if(from.equals(to)) {
			return;
		}

		String fromName = SQLSettings.getWorld(from).get();
		String toName = SQLSettings.getWorld(to).get();

		if(fromName.equalsIgnoreCase(toName)) {
			return;
		}

		InventoryHelper.saveInventory(player, fromName);
		InventoryHelper.setInventory(player, toName);	
	}

	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event) {
		World world = event.getTargetWorld();
		
		if(!SQLSettings.getWorld(world).isPresent()) {
			SQLSettings.saveWorld(world);
		}
	}
}
