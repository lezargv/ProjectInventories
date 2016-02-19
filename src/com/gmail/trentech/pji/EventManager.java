package com.gmail.trentech.pji;

import java.io.IOException;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.WorldData;
import com.gmail.trentech.pji.utils.SQLUtils;

public class EventManager {

	@Listener
	public void ClientConnectionEventJoin(ClientConnectionEvent.Join event) {
	    Player player = event.getTargetEntity();
	    World world = player.getWorld();

		String invName = WorldData.get(world).get().getInventory();

		InventoryData inventoryData = InventoryData.get(player, invName);
		
		try {
			inventoryData.set();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Listener
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event) {
	    Player player = event.getTargetEntity();
	    World world = player.getWorld();

		String invName = WorldData.get(world).get().getInventory();

		InventoryData inventoryData = InventoryData.get(player, invName);
		
		inventoryData.save();
	}
	
	@Listener
	public void onChangeInventoryEventPickup(ChangeInventoryEvent.Pickup event, @First Player player) {
	    World world = player.getWorld();

		String invName = WorldData.get(world).get().getInventory();

		InventoryData inventoryData = InventoryData.get(player, invName);
		
		inventoryData.save();
	}
	
	@Listener
	public void onChangeInventoryEventTransfer(ChangeInventoryEvent.Transfer event, @First Player player) {
	    World world = player.getWorld();

		String invName = WorldData.get(world).get().getInventory();

		InventoryData inventoryData = InventoryData.get(player, invName);
		
		inventoryData.save();
	}
	
	@Listener
	public void onSaveWorldEvent(SaveWorldEvent event){
		for(Entity entity : event.getTargetWorld().getEntities()){
			if(entity instanceof Player){
				Player player = (Player) entity;
			    World world = player.getWorld();

				String invName = WorldData.get(world).get().getInventory();

				InventoryData inventoryData = InventoryData.get(player, invName);
				
				inventoryData.save();
			}
		}
	}
	
	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event) {
		Player player = event.getTargetEntity();
		
		World worldSrc = event.getFromTransform().getExtent();
		World worldDest = event.getToTransform().getExtent();

		if(worldSrc.equals(worldDest)){
			return;
		}

		String invSrc = WorldData.get(worldSrc).get().getInventory();
		String invDest = WorldData.get(worldDest).get().getInventory();
		
		if(invSrc.equalsIgnoreCase(invDest)){
			return;
		}

		InventoryData inventoryDataSrc = InventoryData.get(player, invSrc);
		
		inventoryDataSrc.save();	

		InventoryData inventoryDataDest = InventoryData.get(player, invDest);
		
		try {
			inventoryDataDest.set();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event){
		World world = event.getTargetWorld();
		
		if(!WorldData.get(world).isPresent()){
			SQLUtils.saveWorld(world);
		}
	}
}
