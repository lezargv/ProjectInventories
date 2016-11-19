package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

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

public class InventoryData implements DataSerializable {

	private final static DataQuery NAME = of("name");
	private final static DataQuery PERMISSION = of("permission");
	private final static DataQuery GAMEMODE = of("gamemode");
	
	private String name;
	private Optional<String> permission = Optional.empty();
	private Optional<GameMode> gamemode = Optional.empty();

	protected InventoryData(String name, Optional<String> permission, Optional<GameMode> gamemode) {
		this.name = name;
		this.permission = permission;
		this.gamemode = gamemode;
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

	public void setGamemode(GameMode gamemode) {
		this.gamemode = Optional.of(gamemode);
	}
	
	public void setPermission(String permission) {
		this.permission = Optional.of(permission);
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
				
				if (container.contains(PERMISSION)) {
					permission = Optional.of(container.getString(PERMISSION).get());
				}

				if (container.contains(GAMEMODE)) {
					gamemode = Optional.of(Sponge.getRegistry().getType(GameMode.class, container.getString(GAMEMODE).get()).get());
				}

				return Optional.of(new InventoryData(name, permission, gamemode));
			}
			
			return Optional.empty();
		}
	}
}
