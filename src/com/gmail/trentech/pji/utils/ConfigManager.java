package com.gmail.trentech.pji.utils;

import java.io.File;
import java.io.IOException;

import com.gmail.trentech.pji.Main;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private File file;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	
	public ConfigManager(String folder, String configName) {
		folder = "config" + File.separator + "projectinventories" + File.separator + "inventories" + File.separator + folder;
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder, configName);
		
		create();
		load();
		init();
	}
	
	public ConfigManager(String configName) {
		String folder = "config" + File.separator + "projectinventories";
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder, configName);
		
		create();
		load();
		init();
	}
	
	public ConfigManager() {
		String folder = "config" + File.separator + "projectinventories";
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder, "config.conf");
		
		create();
		load();
		init();
	}
	
	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	public void save(){
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}
	
	private void init() {
		if(file.getName().equalsIgnoreCase("config.conf")){
			if(config.getNode("options", "health").isVirtual()) {
				config.getNode("options", "health").setValue(false).setComment("Enable inventory specific health");
			}
			if(config.getNode("options", "hunger").isVirtual()) {
				config.getNode("options", "hunger").setValue(false).setComment("Enable inventory specific hunger");
			}
			if(config.getNode("options", "experience").isVirtual()) {
				config.getNode("options", "experience").setValue(false).setComment("Enable inventory specific experience");
			}
			if(config.getNode("settings", "commands").isVirtual()){
				config.getNode("settings", "commands").setComment("Allow to set custom command aliases");
			}
			if(config.getNode("settings", "commands", "inventory").isVirtual()) {
				config.getNode("settings", "commands", "inventory").setValue("inv");
			}
			save();
		}
	}

	private void create(){
		if(!file.exists()) {
			try {
				Main.getLog().info("Creating new " + file.getName() + " file...");
				file.createNewFile();		
			} catch (IOException e) {				
				Main.getLog().error("Failed to create new config file");
				e.printStackTrace();
			}
		}
	}
	
	private void load(){
		loader = HoconConfigurationLoader.builder().setFile(file).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}
}
