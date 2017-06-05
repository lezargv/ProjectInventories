package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import com.google.common.reflect.TypeToken;

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
			container.set(KIT, KitData.serialize(this.kitData.get()));
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
					kitData = Optional.of(KitData.deserialize(container.getString(KIT).get()));
				}
				
				return Optional.of(new InventoryData(name, permission, gamemode, kitData));
			}
			
			return Optional.empty();
		}
	}

	public static String serialize(InventoryData inventoryData) {
		try {
			StringWriter sink = new StringWriter();
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
			ConfigurationNode node = loader.createEmptyNode();
			node.setValue(TypeToken.of(InventoryData.class), inventoryData);
			loader.save(node);
			return sink.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static InventoryData deserialize(String item) {
		try {
			StringReader source = new StringReader(item);
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
			ConfigurationNode node = loader.load();
			return node.getValue(TypeToken.of(InventoryData.class));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
