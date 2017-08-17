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

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjc.core.ItemSerializer;
//import com.gmail.trentech.pjc.core.ItemSerializer;
import com.gmail.trentech.pji.Main;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class KitData implements DataSerializable {

	private final static DataQuery OFF_HAND = of("offhand");
	private final static DataQuery HELMET = of("helmet");
	private final static DataQuery CHEST_PLATE = of("chestplate");
	private final static DataQuery LEGGINGS = of("leggings");
	private final static DataQuery BOOTS = of("boots");
	private final static DataQuery HOTBAR = of("hotbar");
	private final static DataQuery GRID = of("grid");

	private Optional<ItemStack> offHand = Optional.empty();
	private Optional<ItemStack> helmet = Optional.empty();
	private Optional<ItemStack> chestPlate = Optional.empty();
	private Optional<ItemStack> leggings = Optional.empty();
	private Optional<ItemStack> boots = Optional.empty();
	private Map<Integer, ItemStack> hotbar = new HashMap<>();
	private Map<Integer, ItemStack> grid = new HashMap<>();

	protected KitData(Optional<ItemStack> offHand, Optional<ItemStack> helmet, Optional<ItemStack> chestPlate, Optional<ItemStack> leggings, Optional<ItemStack> boots, Map<Integer, ItemStack> hotbar, Map<Integer, ItemStack> grid) {
		this.offHand = offHand;
		this.hotbar = hotbar;
		this.grid = grid;
		this.helmet = helmet;
		this.chestPlate = chestPlate;
		this.leggings = leggings;
		this.boots = boots;
	}

	public KitData(Player player) {
		PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

		int i = 0;
		for (Inventory item : inv.getHotbar().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				addHotbar(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (Inventory item : inv.getMain().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				addGrid(i, peek.get());
			}
			i++;
		}

		setOffHand(player.getItemInHand(HandTypes.OFF_HAND));
		setHelmet(player.getHelmet());
		setChestPlate(player.getChestplate());
		setLeggings(player.getLeggings());
		setBoots(player.getBoots());	
	}
	
	public Optional<ItemStack> getOffHand() {
		return offHand;
	}

	public Optional<ItemStack> getHelmet() {
		return helmet;
	}

	public Optional<ItemStack> getChestPlate() {
		return chestPlate;
	}

	public Optional<ItemStack> getLeggings() {
		return leggings;
	}

	public Optional<ItemStack> getBoots() {
		return boots;
	}
	
	public Map<Integer, ItemStack> getHotbar() {
		return hotbar;
	}

	public Map<Integer, ItemStack> getGrid() {
		return grid;
	}
	
	public void setOffHand(Optional<ItemStack> itemStack) {
		this.offHand = itemStack;
	}
	
	public void setHelmet(Optional<ItemStack> helmet) {
		this.helmet = helmet;
	}

	public void setChestPlate(Optional<ItemStack> chestPlate) {
		this.chestPlate = chestPlate;
	}

	public void setLeggings(Optional<ItemStack> leggings) {
		this.leggings = leggings;
	}

	public void setBoots(Optional<ItemStack> boots) {
		this.boots = boots;
	}

	public void addEquipment(int slot, ItemStack itemStack) {
		if (slot == 0) {
			this.helmet = Optional.of(itemStack);
		} else if(slot == 1) {
			this.chestPlate = Optional.of(itemStack);
		} else if(slot == 2) {
			this.leggings = Optional.of(itemStack);
		} else if(slot == 3) {
			this.boots = Optional.of(itemStack);
		}
	}
	
	public void removeEquipment(int slot) {
		if (slot == 0) {
			this.helmet = Optional.empty();
		} else if(slot == 1) {
			this.chestPlate = Optional.empty();
		} else if(slot == 2) {
			this.leggings = Optional.empty();
		} else if(slot == 3) {
			this.boots = Optional.empty();
		}
	}
	
	public void addHotbar(Integer slot, ItemStack itemStack) {
		this.hotbar.put(slot, itemStack);
	}

	public void removeHotbar(Integer slot) {
		this.hotbar.remove(slot);
	}

	public void addGrid(Integer slot, ItemStack itemStack) {
		this.grid.put(slot, itemStack);
	}

	public void removeGrid(Integer slot) {
		this.grid.remove(slot);
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = new MemoryDataContainer();

		if (this.offHand.isPresent()) {
			Optional<String> optionalItem = ItemSerializer.serialize(this.offHand.get());
			
			if(optionalItem.isPresent()) {
				container.set(OFF_HAND, optionalItem.get());
			} else {
				Main.instance().getLog().error("Could not serialize " + this.offHand.get().getItem().getId());
			}
		}

		if (this.helmet.isPresent()) {
			Optional<String> optionalItem = ItemSerializer.serialize(this.helmet.get());
			
			if(optionalItem.isPresent()) {
				container.set(HELMET, optionalItem.get());
			} else {
				Main.instance().getLog().error("Could not serialize " + this.helmet.get().getItem().getId());
			}	
		}

		if (this.chestPlate.isPresent()) {
			Optional<String> optionalItem = ItemSerializer.serialize(this.chestPlate.get());
			
			if(optionalItem.isPresent()) {
				container.set(CHEST_PLATE, optionalItem.get());
			} else {
				Main.instance().getLog().error("Could not serialize " + this.chestPlate.get().getItem().getId());
			}
		}

		if (this.leggings.isPresent()) {
			Optional<String> optionalItem = ItemSerializer.serialize(this.leggings.get());
			
			if(optionalItem.isPresent()) {
				container.set(LEGGINGS, optionalItem.get());
			} else {
				Main.instance().getLog().error("Could not serialize " + this.leggings.get().getItem().getId());
			}
		}

		if (this.boots.isPresent()) {
			Optional<String> optionalItem = ItemSerializer.serialize(this.boots.get());
			
			if(optionalItem.isPresent()) {
				container.set(BOOTS, optionalItem.get());
			} else {
				Main.instance().getLog().error("Could not serialize " + this.boots.get().getItem().getId());
			}
		}
		
		if(!this.hotbar.isEmpty()) {
			Map<String, String> hotbar = new HashMap<>();

			for (Entry<Integer, ItemStack> entry : this.hotbar.entrySet()) {
				Optional<String> optionalItem = ItemSerializer.serialize(entry.getValue());
				
				if(!optionalItem.isPresent()) {
					Main.instance().getLog().error("Could not serialize " + entry.getValue().getItem().getId());
					
					continue;
				}
				
				hotbar.put(entry.getKey().toString(), optionalItem.get());
			}
			
			container.set(HOTBAR, hotbar);
		}

		if(!this.grid.isEmpty()) {
			Map<String, String> grid = new HashMap<>();

			for (Entry<Integer, ItemStack> entry : this.grid.entrySet()) {
				Optional<String> optionalItem = ItemSerializer.serialize(entry.getValue());
				
				if(!optionalItem.isPresent()) {
					Main.instance().getLog().error("Could not serialize " + entry.getValue().getItem().getId());
					
					continue;
				}
				
				grid.put(entry.getKey().toString(), optionalItem.get());
			}
			
			container.set(GRID, grid);
		}

		return container;
	}

	public static class Builder extends AbstractDataBuilder<KitData> {

		public Builder() {
			super(KitData.class, 1);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Optional<KitData> buildContent(DataView container) throws InvalidDataException {
			ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.BARRIER).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Could not deserialize item")).build();
			
			Optional<ItemStack> offHand = Optional.empty();

			if (container.contains(OFF_HAND)) {
				Optional<ItemStack> optionalItemStack = ItemSerializer.deserialize(container.getString(OFF_HAND).get());
				
				if(optionalItemStack.isPresent()) {
					offHand = Optional.of(optionalItemStack.get());
				} else {
					offHand = Optional.of(itemStack);
					Main.instance().getLog().error("Could not deserialize item in off hand");
				}
			}
			
			Optional<ItemStack> helmet = Optional.empty();

			if (container.contains(HELMET)) {
				Optional<ItemStack> optionalItemStack = ItemSerializer.deserialize(container.getString(HELMET).get());
				
				if(optionalItemStack.isPresent()) {
					helmet = Optional.of(optionalItemStack.get());
				} else {
					helmet = Optional.of(itemStack);
					Main.instance().getLog().error("Could not deserialize item in helmet slot");
				}		
			}
			
			Optional<ItemStack> chestPlate = Optional.empty();

			if (container.contains(CHEST_PLATE)) {
				Optional<ItemStack> optionalItemStack = ItemSerializer.deserialize(container.getString(CHEST_PLATE).get());
				
				if(optionalItemStack.isPresent()) {
					chestPlate = Optional.of(optionalItemStack.get());
				} else {
					chestPlate = Optional.of(itemStack);
					Main.instance().getLog().error("Could not deserialize item in chest plate slot");
				}			
			}
			
			Optional<ItemStack> leggings = Optional.empty();

			if (container.contains(LEGGINGS)) {
				Optional<ItemStack> optionalItemStack = ItemSerializer.deserialize(container.getString(LEGGINGS).get());
				
				if(optionalItemStack.isPresent()) {
					leggings = Optional.of(optionalItemStack.get());
				} else {
					leggings = Optional.of(itemStack);
					Main.instance().getLog().error("Could not deserialize item in leggings slot");
				}
			}
			
			Optional<ItemStack> boots = Optional.empty();

			if (container.contains(BOOTS)) {
				Optional<ItemStack> optionalItemStack = ItemSerializer.deserialize(container.getString(BOOTS).get());
				
				if(optionalItemStack.isPresent()) {
					boots = Optional.of(optionalItemStack.get());
				} else {
					boots = Optional.of(itemStack);
					Main.instance().getLog().error("Could not deserialize item in boots slot");
				}		
			}

			Map<Integer, ItemStack> hotbar = new HashMap<>();

			if (container.contains(HOTBAR)) {
				for (Entry<String, String> entry : ((Map<String, String>) container.getMap(HOTBAR).get()).entrySet()) {
					Optional<ItemStack> optionalItemStack = ItemSerializer.deserialize(entry.getValue());
					
					if(optionalItemStack.isPresent()) {
						hotbar.put(Integer.parseInt(entry.getKey()), optionalItemStack.get());
					} else {
						hotbar.put(Integer.parseInt(entry.getKey()), itemStack);
						Main.instance().getLog().error("Could not deserialize item in hotbar slot " + Integer.parseInt(entry.getKey()));
					}						
				}
			}

			Map<Integer, ItemStack> grid = new HashMap<>();

			if (container.contains(GRID)) {
				for (Entry<String, String> entry : ((Map<String, String>) container.getMap(GRID).get()).entrySet()) {
					Optional<ItemStack> optionalItemStack = ItemSerializer.deserialize(entry.getValue());
					
					if(optionalItemStack.isPresent()) {
						grid.put(Integer.parseInt(entry.getKey()), optionalItemStack.get());
					} else {
						grid.put(Integer.parseInt(entry.getKey()), itemStack);
						Main.instance().getLog().error("Could not deserialize item in hotbar slot " + Integer.parseInt(entry.getKey()));
					}
				}
			}

			return Optional.of(new KitData(offHand, helmet, chestPlate, leggings, boots, hotbar, grid));
		}
	}

	public static String serialize(KitData kitData) {
		try {
			StringWriter sink = new StringWriter();
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
			ConfigurationNode node = loader.createEmptyNode();
			node.setValue(TypeToken.of(KitData.class), kitData);
			loader.save(node);
			return sink.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static KitData deserialize(String item) {
		try {
			StringReader source = new StringReader(item);
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
			ConfigurationNode node = loader.load();
			return node.getValue(TypeToken.of(KitData.class));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
