package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class WorldData implements DataSerializable {

	private final static DataQuery NAME = of("name");
	private final static DataQuery INVENTORIES = of("inventories");
	
	private UUID uuid;
	private Map<String, Boolean> inventories = new HashMap<>();

	public WorldData(UUID uuid, Map<String, Boolean> inventories) {
		this.uuid = uuid;
		this.inventories = inventories;
	}

	public UUID getUniqueId() {
		return uuid;
	}
	
	public String getDefault() {
		for (Entry<String, Boolean> entry : inventories.entrySet()) {
			if (entry.getValue()) {
				return entry.getKey();
			}
		}

		return null;
	}

	public void add(String inventory, boolean isDefault) {
		if(isDefault) {
			for (Entry<String, Boolean> entry : inventories.entrySet()) {
				inventories.put(entry.getKey(), false);
			}
		}
		
		inventories.put(inventory, isDefault);

	}

	public void remove(String inventory) {
		if(contains(inventory)) {
			inventories.remove(inventory);
		}
	}

	public boolean contains(String inventory) {
		return inventories.containsKey(inventory);
	}
	
	public Map<String, Boolean> getInventories() {
		return inventories;
	}
	
	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = DataContainer.createNew().set(NAME, getUniqueId().toString());

		return container.set(INVENTORIES, this.inventories);
	}

	public static class Builder extends AbstractDataBuilder<WorldData> {

		public Builder() {
			super(WorldData.class, 1);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Optional<WorldData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(NAME)) {
				UUID uuid = UUID.fromString(container.getString(NAME).get());
				
				Map<String, Boolean> inventories = new HashMap<>();
				
				
				if(container.contains(INVENTORIES)) {
					inventories = (Map<String, Boolean>) container.getMap(INVENTORIES).get();
				}

				WorldData worldData = new WorldData(uuid, inventories);

				return Optional.of(worldData);
			}
			return Optional.empty();
		}
	}

	public static String serialize(WorldData worldData) {
		try {
			return DataFormats.JSON.write(worldData.toContainer());
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	public static WorldData deserialize(String item) {
		try {
			return Sponge.getDataManager().deserialize(WorldData.class, DataFormats.JSON.read(item)).get();
		} catch (InvalidDataException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
