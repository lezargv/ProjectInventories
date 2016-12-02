package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class InventoryData implements DataSerializable {

	private final static DataQuery NAME = of("name");
	private final static DataQuery PERMISSION = of("permission");
	private final static DataQuery GAMEMODE = of("gamemode");
	private final static DataQuery KIT = of("kit");
	
	private String name;
	private Optional<String> permission = Optional.empty();
	private Optional<GameMode> gamemode = Optional.empty();
	private Optional<KitData> kitData = Optional.empty();
	
	protected InventoryData(String name, Optional<String> permission, Optional<GameMode> gamemode, Optional<KitData> kitData) {
		this.name = name;
		this.permission = permission;
		this.gamemode = gamemode;
		this.kitData = kitData;
	}

	public InventoryData(String name) {
		this.name = name;
	}

	public String getName() {
		return name.toUpperCase();
	}

	public Optional<String> getPermission() {
		return this.permission;
	}

	public Optional<GameMode> getGamemode() {
		return this.gamemode;
	}

	public Optional<KitData> getKitData() {
		return this.kitData;
	}
	
	public void setGamemode(GameMode gamemode) {
		this.gamemode = Optional.of(gamemode);
	}
	
	public void setPermission(String permission) {
		this.permission = Optional.of(permission);
	}

	public void setKitData(KitData kitData) {
		this.kitData = Optional.of(kitData);
	}
	
	public void removePermission() {
		this.permission = Optional.empty();
	}
	
	public void removeGamemode() {
		this.gamemode = Optional.empty();
	}
	
	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = new MemoryDataContainer().set(NAME, getName());

		if (this.permission.isPresent()) {
			container.set(PERMISSION, this.permission.get());
		}
		
		if (this.gamemode.isPresent()) {
			container.set(GAMEMODE, this.gamemode.get().getId());
		}

		if (this.kitData.isPresent()) {
			container.set(KIT, serialize(this.kitData.get()));
		}
		
		return container;
	}

	public static class Builder extends AbstractDataBuilder<InventoryData> {

		public Builder() {
			super(InventoryData.class, 1);
		}

		@Override
		protected Optional<InventoryData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(NAME)) {
				String name = container.getString(NAME).get();
				Optional<String> permission = Optional.empty();
				Optional<GameMode> gamemode = Optional.empty();
				Optional<KitData> kitData = Optional.empty();
				
				if (container.contains(PERMISSION)) {
					permission = Optional.of(container.getString(PERMISSION).get());
				}

				if (container.contains(GAMEMODE)) {
					gamemode = Optional.of(Sponge.getRegistry().getType(GameMode.class, container.getString(GAMEMODE).get()).get());
				}

				if (container.contains(KIT)) {
					kitData = Optional.of(deserialize(container.getString(KIT).get()));
				}
				
				return Optional.of(new InventoryData(name, permission, gamemode, kitData));
			}
			
			return Optional.empty();
		}
	}
	
	private static String serialize(KitData kitData) {
		ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(kitData.toContainer());
		StringWriter stringWriter = new StringWriter();
		try {
			HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}

	private static KitData deserialize(String item) {
		ConfigurationNode node = null;
		try {
			node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);

		return Sponge.getDataManager().deserialize(KitData.class, dataView).get();
	}
}
