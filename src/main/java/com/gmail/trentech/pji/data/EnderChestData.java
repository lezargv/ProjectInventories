package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.inventory.ItemStack;

public class EnderChestData implements DataSerializable {

	private final static DataQuery PLAYER_UUID = of("player");
	private final static DataQuery WORLD_UUID = of("world");
	private final static DataQuery INVENTORY = of("inventory");
	private final static DataQuery SLOT_POSITION = of("slot_position");
	private final static DataQuery ITEM_STACK = of("item_stack");
	
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
		DataContainer container = DataContainer.createNew().set(PLAYER_UUID, getPlayerUuid().toString()).set(WORLD_UUID, getWorldUuid().toString());

		if(!this.inventory.isEmpty()) {
			List<DataView> gridData = new LinkedList<>();

			for (Entry<Integer, ItemStack> entry : inventory.entrySet()) {
				gridData.add(DataContainer.createNew().set(SLOT_POSITION, entry.getKey()).set(ITEM_STACK, entry.getValue().toContainer()));
			}

			container.set(INVENTORY, gridData);
		}

		return container;
	}

	public static class Builder extends AbstractDataBuilder<EnderChestData> {

		public Builder() {
			super(EnderChestData.class, 1);
		}

		@Override
		protected Optional<EnderChestData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(PLAYER_UUID, WORLD_UUID)) {
				UUID playerUuid = UUID.fromString(container.getString(PLAYER_UUID).get());
				UUID worldUuid = UUID.fromString(container.getString(WORLD_UUID).get());

				Map<Integer, ItemStack> inventory = new HashMap<>();

				if (container.contains(INVENTORY)) {
					for (DataView data : container.getViewList(INVENTORY).get()) {
						inventory.put(data.getInt(SLOT_POSITION).get(), ItemStack.builder().fromContainer(data.getView(ITEM_STACK).get()).build());
					}
				}

				return Optional.of(new EnderChestData(playerUuid, worldUuid, inventory));
			}
			
			return Optional.empty();
		}
	}
}
