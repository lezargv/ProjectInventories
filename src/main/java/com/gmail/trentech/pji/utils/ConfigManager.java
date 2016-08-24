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

	public ConfigManager() {
		String folder = "config" + File.separator + Resource.NAME.toLowerCase();
		if (!new File(folder).isDirectory()) {
			new File(folder).mkdirs();
		}
		file = new File(folder, "config.conf");

		create();
		load();
	}

	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	public void save() {
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}

	public ConfigManager init() {
		if (file.getName().equalsIgnoreCase("config.conf")) {
			if (config.getNode("options", "health").isVirtual()) {
				config.getNode("options", "health").setValue(true).setComment("Enable inventory specific health");
			}
			if (config.getNode("options", "hunger").isVirtual()) {
				config.getNode("options", "hunger").setValue(true).setComment("Enable inventory specific hunger");
			}
			if (config.getNode("options", "experience").isVirtual()) {
				config.getNode("options", "experience").setValue(true).setComment("Enable inventory specific experience");
			}
			if (config.getNode("settings", "sql").isVirtual()) {
				config.getNode("settings", "sql", "enable").setValue(false);
				config.getNode("settings", "sql", "prefix").setValue("NONE");
				config.getNode("settings", "sql", "url").setValue("localhost:3306/database");
				config.getNode("settings", "sql", "username").setValue("root");
				config.getNode("settings", "sql", "password").setValue("password");
			}
			save();
		}
		
		return this;
	}

	private void create() {
		if (!file.exists()) {
			try {
				Main.getLog().info("Creating new " + file.getName() + " file...");
				file.createNewFile();
			} catch (IOException e) {
				Main.getLog().error("Failed to create new config file");
				e.printStackTrace();
			}
		}
	}

	private void load() {
		loader = HoconConfigurationLoader.builder().setFile(file).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}
}
