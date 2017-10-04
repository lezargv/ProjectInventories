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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

//import com.gmail.trentech.pjc.core.ItemSerializer;
import com.gmail.trentech.pji.Main;

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
		DataContainer container = DataContainer.createNew().set(PLAYER_UUID, getPlayerUuid().toString()).set(WORLD_UUID, getWorldUuid().toString());

		if(!this.inventory.isEmpty()) {
			Map<String, String> inventory = new HashMap<>();

			for (Entry<Integer, ItemStack> entry : this.inventory.entrySet()) {
				try {
					inventory.put(entry.getKey().toString(), DataFormats.JSON.write(entry.getValue().toContainer()));
				} catch (IOException e) {
					Main.instance().getLog().error("Could not serialize " + entry.getValue().getType().getId());
					e.printStackTrace();
					continue;
				}
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
				ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.BARRIER).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Could not deserialize item")).build();
				
				UUID playerUuid = UUID.fromString(container.getString(PLAYER_UUID).get());
				UUID worldUuid = UUID.fromString(container.getString(WORLD_UUID).get());

				Map<Integer, ItemStack> inventory = new HashMap<>();

				if (container.contains(INVENTORY)) {
					for (Entry<String, String> entry : ((Map<String, String>) container.getMap(INVENTORY).get()).entrySet()) {
						try {
							Optional<ItemStack> optionalItemStack = Sponge.getDataManager().deserialize(ItemStack.class, DataFormats.JSON.read(entry.getValue()));
							
							if(optionalItemStack.isPresent()) {
								inventory.put(Integer.parseInt(entry.getKey()), optionalItemStack.get());
							}
						} catch (IOException e) {
							inventory.put(Integer.parseInt(entry.getKey()), itemStack);
							Main.instance().getLog().error("Could not deserialize item in inventory slot " + Integer.parseInt(entry.getKey()));
						}
					}
				}

				return Optional.of(new EnderChestData(playerUuid, worldUuid, inventory));
			}
			
			return Optional.empty();
		}
	}
}
