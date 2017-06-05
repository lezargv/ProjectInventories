package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

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
		DataContainer container = new MemoryDataContainer().set(PLAYER_UUID, getPlayerUuid().toString());

		if(!this.chests.isEmpty()) {
			Map<String, String> chests = new HashMap<>();

			for (Entry<UUID, EnderChestData> entry : this.chests.entrySet()) {
				chests.put(entry.getKey().toString(), EnderChestData.serialize(entry.getValue()));
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

				HashMap<UUID, EnderChestData> chests = new HashMap<>();

				if (container.contains(CHESTS)) {
					for (Entry<String, String> entry : ((Map<String, String>) container.getMap(CHESTS).get()).entrySet()) {
						chests.put(UUID.fromString(entry.getKey()), EnderChestData.deserialize(entry.getValue()));
					}
				}

				return Optional.of(new PlayerChestData(playerUuid, chests));
			}
			
			return Optional.empty();
		}
	}

	public static String serialize(PlayerChestData playerChestData) {
		try {
			StringWriter sink = new StringWriter();
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
			ConfigurationNode node = loader.createEmptyNode();
			node.setValue(TypeToken.of(PlayerChestData.class), playerChestData);
			loader.save(node);
			return sink.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static PlayerChestData deserialize(String item) {
		try {
			StringReader source = new StringReader(item);
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
			ConfigurationNode node = loader.load();
			return node.getValue(TypeToken.of(PlayerChestData.class));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
