package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
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

public class PlayerChestData implements DataSerializable {

	private final static DataQuery PLAYER_UUID = of("player");
	private final static DataQuery CHESTS = of("chests");
	private final static DataQuery CHEST_DATA = of("chest_data");
	private final static DataQuery UUID_ = of("uuid");
	
	private UUID player;
	private Map<UUID, EnderChestData> chests = new HashMap<>();
	
	public PlayerChestData(UUID player, Map<UUID, EnderChestData> hashMap) {
		this.player = player;
		this.chests = hashMap;
	}

	public UUID getPlayerUuid() {
		return player;
	}

	public Map<UUID, EnderChestData> getChests() {
		return chests;
	}

	public void addChest(UUID world, EnderChestData chest) {
		this.chests.put(world, chest);
	}

	public void removeChest(UUID world) {
		this.chests.remove(world);
	}
	
	public void setChests(Map<UUID, EnderChestData> chests) {
		this.chests = chests;
	}
	
	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = DataContainer.createNew().set(PLAYER_UUID, getPlayerUuid().toString());

		if(!this.chests.isEmpty()) {
			List<DataView> chestData = new LinkedList<>();

			for (Entry<UUID, EnderChestData> entry : chests.entrySet()) {
				chestData.add(DataContainer.createNew().set(UUID_, entry.getKey()).set(CHEST_DATA, entry.getValue().toContainer()));
			}

			container.set(CHESTS, chestData);
		}

		return container;
	}
	
	public static class Builder extends AbstractDataBuilder<PlayerChestData> {

		public Builder() {
			super(PlayerChestData.class, 1);
		}

		@Override
		protected Optional<PlayerChestData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(PLAYER_UUID)) {
				UUID playerUuid = UUID.fromString(container.getString(PLAYER_UUID).get());

				Map<UUID, EnderChestData> chests = new HashMap<>();

				if (container.contains(CHESTS)) {
					for (DataView data : container.getViewList(CHESTS).get()) {
						chests.put(UUID.fromString(data.getString(UUID_).get()), Sponge.getDataManager().deserialize(EnderChestData.class, data.getView(CHEST_DATA).get()).get());
					}
				}

				return Optional.of(new PlayerChestData(playerUuid, chests));
			}
			
			return Optional.empty();
		}
	}

	public static byte[] serialize(PlayerChestData playerChestData) {
		try {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			GZIPOutputStream gZipOutStream = new GZIPOutputStream(byteOutStream);
			DataFormats.NBT.writeTo(gZipOutStream, playerChestData.toContainer());
			gZipOutStream.close();
			return byteOutStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static PlayerChestData deserialize(byte[] bytes) {
		try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
			GZIPInputStream gZipInputSteam = new GZIPInputStream(byteInputStream);
			DataContainer container = DataFormats.NBT.readFrom(gZipInputSteam);
			return Sponge.getDataManager().deserialize(PlayerChestData.class, container).get();
		} catch (InvalidDataFormatException | IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
