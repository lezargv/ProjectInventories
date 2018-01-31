package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.persistence.InvalidDataFormatException;

public class PlayerData implements DataSerializable {

	private final static DataQuery INVENTORY = of("inventory");
	private final static DataQuery INVENTORIES = of("inventories");
	
	private String inventory;
	private List<String> inventories = new ArrayList<>();

	public PlayerData(String inventory, List<String> inventories) {
		this.inventory = inventory;
		this.inventories = inventories;
	}

	public String getInventoryName() {
		return inventory;
	}

	public void setInventoryName(String inventory) {
		this.inventory = inventory;
		add(inventory);
	}
	
	private void add(String inventory) {
		if(!contains(inventory)) {
			inventories.add(inventory);
		}
	}

	public void remove(String inventory) {
		if(contains(inventory)) {
			inventories.remove(inventory);
		}		
	}
	
	public boolean contains(String inventory) {
		return inventories.contains(inventory);
	}
	
	public List<String> getInventories() {
		return inventories;
	}
	
	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = DataContainer.createNew().set(INVENTORY, inventory);

		if(!this.inventories.isEmpty()) {
			container.set(INVENTORIES, this.inventories);
		}

		return container;
	}

	public static class Builder extends AbstractDataBuilder<PlayerData> {

		public Builder() {
			super(PlayerData.class, 1);
		}

		@Override
		protected Optional<PlayerData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(INVENTORY)) {
				String inventory = container.getString(INVENTORY).get();
				
				List<String> inventories = new ArrayList<>();

				if (container.contains(INVENTORIES)) {
					inventories = container.getStringList(INVENTORIES).get();
				}

				return Optional.of(new PlayerData(inventory, inventories));
			}
			
			return Optional.empty();
		}
	}
	
	public static byte[] serialize(PlayerData playerData) {
		try {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			GZIPOutputStream gZipOutStream = new GZIPOutputStream(byteOutStream);
			DataFormats.NBT.writeTo(gZipOutStream, playerData.toContainer());
			gZipOutStream.close();
			return byteOutStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static PlayerData deserialize(byte[] bytes) {
		try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
			GZIPInputStream gZipInputSteam = new GZIPInputStream(byteInputStream);
			DataContainer container = DataFormats.NBT.readFrom(gZipInputSteam);
			return Sponge.getDataManager().deserialize(PlayerData.class, container).get();
		} catch (InvalidDataFormatException | IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
