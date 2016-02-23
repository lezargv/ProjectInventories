package com.gmail.trentech.pji.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.item.inventory.ItemStack;

import com.gmail.trentech.pji.Main;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class ItemSerializer {

	public static String serializeItemStack(ItemStack itemStack){
		ConfigurationNode node = ConfigurateTranslator.instance().translateData(itemStack.toContainer());
		
		StringWriter stringWriter = new StringWriter();
		try {
		    HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
		} catch (IOException e) {
		    e.printStackTrace();
		}

		return stringWriter.toString();
	}
	
	public static ItemStack deserializeItemStack(String item) {
		ConfigurationNode node = null;
		try {
			node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    ConfigurateTranslator translator = ConfigurateTranslator.instance();
	    DataManager manager = Main.getGame().getDataManager();

		DataView dataView = translator.translateFrom(node);

	    Optional<ItemStack> deserializedOptional = manager.deserialize(ItemStack.class, dataView);

	    if(deserializedOptional.isPresent()) {
	        return deserializedOptional.get();
	    }
	    
	    return null;
	}
}
