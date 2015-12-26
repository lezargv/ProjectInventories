package com.gmail.trentech.pji;

import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pji.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class EventManager {

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
	    Player player = event.getTargetEntity();

		if(player.get(JoinData.class).isPresent()){
			return;
		}
	}
	
	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event) {
		if(!(event.getTargetEntity() instanceof Player)){
			return;
		}

		World worldSrc = event.getFromTransform().getExtent();
		World worldDest = event.getToTransform().getExtent();

		if(worldSrc != worldDest){
			ConfigurationNode config = new ConfigManager().getConfig();
			String invName = config.getNode("Worlds", worldDest).getString();
			// switch inventory
		}
	}

	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event){
		String worldName = event.getTargetWorld().getName();
		
		ConfigManager configManager = new ConfigManager();
		ConfigurationNode config = configManager.getConfig();

        if(config.getNode("Worlds", worldName).getString() == null) {
        	config.getNode("Worlds", worldName, "Inventory").setValue("default");
        	
        	configManager.save();
        }
	}
}
