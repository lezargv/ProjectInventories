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
import org.spongepowered.api.item.inventory.ItemStack;

import com.gmail.trentech.pjc.core.ItemSerializer;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class EnderChestData implements DataSerializable {

	private final static DataQuery PLAYER_UUID = of("player");
	private final static DataQuery WORLD_UUID = of("world");
	private final static DataQuery INVENTORY = of("inventory");
	
	private UUID player;	
	private UUID world;	
	private Map<Integer, ItemStack> inventory = new HashMap<>();

	public EnderChestData(UUID player, UUID world, Map<Integer, ItemStack> inventory) {
		this.player = player;
		this.world = world;
		this.inventory = inventory;
	}

	public UUID getPlayerUuid() {
		return player;
	}
	
	public UUID getWorldUuid() {
		return world;
	}

	public Map<Integer, ItemStack> getInventory() {
		return this.inventory;
	}
	
	public void addItem(Integer slot, ItemStack itemStack) {
		this.inventory.put(slot, itemStack);
	}

	public void removeItem(Integer slot) {
		this.inventory.remove(slot);
	}

	public void setInventory(Map<Integer, ItemStack> inventory) {
		this.inventory = inventory;
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = new MemoryDataContainer().set(PLAYER_UUID, getPlayerUuid().toString()).set(WORLD_UUID, getWorldUuid().toString());

		if(!this.inventory.isEmpty()) {
			Map<String, String> inventory = new HashMap<>();

			for (Entry<Integer, ItemStack> entry : this.inventory.entrySet()) {
				inventory.put(entry.getKey().toString(), ItemSerializer.serialize(entry.getValue()));
			}
			
			container.set(INVENTORY, inventory);
		}

		return container;
	}

	public static class Builder extends AbstractDataBuilder<EnderChestData> {

		public Builder() {
			super(EnderChestData.class, 1);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Optional<EnderChestData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(PLAYER_UUID, WORLD_UUID)) {
				UUID playerUuid = UUID.fromString(container.getString(PLAYER_UUID).get());
				UUID worldUuid = UUID.fromString(container.getString(WORLD_UUID).get());

				Map<Integer, ItemStack> inventory = new HashMap<>();

				if (container.contains(INVENTORY)) {
					for (Entry<String, String> entry : ((Map<String, String>) container.getMap(INVENTORY).get()).entrySet()) {
						inventory.put(Integer.parseInt(entry.getKey()), ItemSerializer.deserialize(entry.getValue()));
					}
				}

				return Optional.of(new EnderChestData(playerUuid, worldUuid, inventory));
			}
			
			return Optional.empty();
		}
	}

	public static String serialize(EnderChestData enderChestData) {
		try {
			StringWriter sink = new StringWriter();
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
			ConfigurationNode node = loader.createEmptyNode();
			node.setValue(TypeToken.of(EnderChestData.class), enderChestData);
			loader.save(node);
			return sink.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static EnderChestData deserialize(String item) {
		try {
			StringReader source = new StringReader(item);
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
			ConfigurationNode node = loader.load();
			return node.getValue(TypeToken.of(EnderChestData.class));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
