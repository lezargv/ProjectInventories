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

import com.gmail.trentech.pji.Main;

public class PlayerChestData implements DataSerializable {

	private final static DataQuery PLAYER_UUID = of("player");
	private final static DataQuery CHESTS = of("chests");
	
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
			Map<String, String> chests = new HashMap<>();

			for (Entry<UUID, EnderChestData> entry : this.chests.entrySet()) {
				try {
					chests.put(entry.getKey().toString(), DataFormats.JSON.write(entry.getValue().toContainer()));
				} catch (IOException e) {
					Main.instance().getLog().error("Could not serialize EnderChestData");
					e.printStackTrace();
					continue;
				}
			}
			
			container.set(CHESTS, chests);
		}

		return container;
	}
	
	public static class Builder extends AbstractDataBuilder<PlayerChestData> {

		public Builder() {
			super(PlayerChestData.class, 1);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Optional<PlayerChestData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(PLAYER_UUID)) {
				UUID playerUuid = UUID.fromString(container.getString(PLAYER_UUID).get());

				Map<UUID, EnderChestData> chests = new HashMap<>();

				if (container.contains(CHESTS)) {
					for (Entry<String, String> entry : ((Map<String, String>) container.getMap(CHESTS).get()).entrySet()) {
						try {
							Optional<EnderChestData> optionalEnderChestData = Sponge.getDataManager().deserialize(EnderChestData.class, DataFormats.JSON.read(entry.getValue()));
							
							if(optionalEnderChestData.isPresent()) {
								chests.put(UUID.fromString(entry.getKey()), optionalEnderChestData.get());
							}
						} catch (IOException e) {
							Main.instance().getLog().error("Could not deserialize PlayerChestData");
						}
					}
				}

				return Optional.of(new PlayerChestData(playerUuid, chests));
			}
			
			return Optional.empty();
		}
	}

	public static String serialize(PlayerChestData playerChestData) {
		try {
			return DataFormats.JSON.write(playerChestData.toContainer());
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	public static PlayerChestData deserialize(String item) {
		try {
			return Sponge.getDataManager().deserialize(PlayerChestData.class, DataFormats.JSON.read(item)).get();
		} catch (InvalidDataException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
