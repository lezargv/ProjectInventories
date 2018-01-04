package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

//import com.gmail.trentech.pjc.core.ItemSerializer;
import com.gmail.trentech.pji.Main;

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
		PlayerInventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(PlayerInventory.class));

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
		DataContainer container = DataContainer.createNew();

		if (this.offHand.isPresent()) {
			try {
				container.set(OFF_HAND, DataFormats.JSON.write(this.offHand.get().toContainer()));
			} catch (IOException e) {
				Main.instance().getLog().error("Could not serialize " + this.offHand.get().getType().getId());
				e.printStackTrace();
			}
		}

		if (this.helmet.isPresent()) {
			try {
				container.set(HELMET, DataFormats.JSON.write(this.helmet.get().toContainer()));
			} catch (IOException e) {
				Main.instance().getLog().error("Could not serialize " + this.helmet.get().getType().getId());
				e.printStackTrace();
			}
		}

		if (this.chestPlate.isPresent()) {
			try {
				container.set(CHEST_PLATE, DataFormats.JSON.write(this.chestPlate.get().toContainer()));
			} catch (IOException e) {
				Main.instance().getLog().error("Could not serialize " + this.chestPlate.get().getType().getId());
				e.printStackTrace();
			}
		}

		if (this.leggings.isPresent()) {
			try {
				container.set(LEGGINGS, DataFormats.JSON.write(this.leggings.get().toContainer()));
			} catch (IOException e) {
				Main.instance().getLog().error("Could not serialize " + this.leggings.get().getType().getId());
				e.printStackTrace();
			}
		}

		if (this.boots.isPresent()) {
			try {
				container.set(BOOTS, DataFormats.JSON.write(this.boots.get().toContainer()));
			} catch (IOException e) {
				Main.instance().getLog().error("Could not serialize " + this.boots.get().getType().getId());
				e.printStackTrace();
			}
		}
		
		if(!this.hotbar.isEmpty()) {
			Map<String, String> hotbar = new HashMap<>();

			for (Entry<Integer, ItemStack> entry : this.hotbar.entrySet()) {
				try {
					hotbar.put(entry.getKey().toString(), DataFormats.JSON.write(entry.getValue().toContainer()));
				} catch (IOException e) {
					Main.instance().getLog().error("Could not serialize " + entry.getValue().getType().getId());
					e.printStackTrace();
					continue;
				}
			}
			
			container.set(HOTBAR, hotbar);
		}

		if(!this.grid.isEmpty()) {
			Map<String, String> grid = new HashMap<>();

			for (Entry<Integer, ItemStack> entry : this.grid.entrySet()) {
				try {
					grid.put(entry.getKey().toString(), DataFormats.JSON.write(entry.getValue().toContainer()));
				} catch (IOException e) {
					Main.instance().getLog().error("Could not serialize " + entry.getValue().getType().getId());
					e.printStackTrace();
					continue;
				}
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
				try {
					offHand = Sponge.getDataManager().deserialize(ItemStack.class, DataFormats.JSON.read(container.getString(OFF_HAND).get()));
				} catch (IOException e) {
					offHand = Optional.of(itemStack);
					Main.instance().getLog().error("Could not deserialize item in off hand");
				}			
			}
			
			Optional<ItemStack> helmet = Optional.empty();

			if (container.contains(HELMET)) {
				try {
					helmet = Sponge.getDataManager().deserialize(ItemStack.class, DataFormats.JSON.read(container.getString(HELMET).get()));
				} catch (IOException e) {
					helmet = Optional.of(itemStack);
					Main.instance().getLog().error("Could not deserialize item in helmet slot");
				}	
			}
			
			Optional<ItemStack> chestPlate = Optional.empty();

			if (container.contains(CHEST_PLATE)) {
				try {
					chestPlate = Sponge.getDataManager().deserialize(ItemStack.class, DataFormats.JSON.read(container.getString(CHEST_PLATE).get()));
				} catch (IOException e) {
					chestPlate = Optional.of(itemStack);
					Main.instance().getLog().error("Could not deserialize item in chest plate slot");
				}		
			}
			
			Optional<ItemStack> leggings = Optional.empty();

			if (container.contains(LEGGINGS)) {
				try {
					leggings = Sponge.getDataManager().deserialize(ItemStack.class, DataFormats.JSON.read(container.getString(LEGGINGS).get()));
				} catch (IOException e) {
					leggings = Optional.of(itemStack);
					Main.instance().getLog().error("Could not deserialize item in leggings slot");
				}
			}
			
			Optional<ItemStack> boots = Optional.empty();

			if (container.contains(BOOTS)) {
				try {
					boots = Sponge.getDataManager().deserialize(ItemStack.class, DataFormats.JSON.read(container.getString(BOOTS).get()));
				} catch (IOException e) {
					boots = Optional.of(itemStack);
					Main.instance().getLog().error("Could not deserialize item in boots slot");
				}	
			}

			Map<Integer, ItemStack> hotbar = new HashMap<>();

			if (container.contains(HOTBAR)) {
				for (Entry<String, String> entry : ((Map<String, String>) container.getMap(HOTBAR).get()).entrySet()) {
					try {
						Optional<ItemStack> optionalItemStack = Sponge.getDataManager().deserialize(ItemStack.class, DataFormats.JSON.read(entry.getValue()));
						
						if(optionalItemStack.isPresent()) {
							hotbar.put(Integer.parseInt(entry.getKey()), optionalItemStack.get());
						}
					} catch (IOException e) {
						hotbar.put(Integer.parseInt(entry.getKey()), itemStack);
						Main.instance().getLog().error("Could not deserialize item in hotbar slot " + Integer.parseInt(entry.getKey()));
					}
				}
			}

			Map<Integer, ItemStack> grid = new HashMap<>();

			if (container.contains(GRID)) {
				for (Entry<String, String> entry : ((Map<String, String>) container.getMap(GRID).get()).entrySet()) {
					try {
						Optional<ItemStack> optionalItemStack = Sponge.getDataManager().deserialize(ItemStack.class, DataFormats.JSON.read(entry.getValue()));
						
						if(optionalItemStack.isPresent()) {
							grid.put(Integer.parseInt(entry.getKey()), optionalItemStack.get());
						}
					} catch (IOException e) {
						grid.put(Integer.parseInt(entry.getKey()), itemStack);
						Main.instance().getLog().error("Could not deserialize item in grid slot " + Integer.parseInt(entry.getKey()));
					}
				}
			}

			return Optional.of(new KitData(offHand, helmet, chestPlate, leggings, boots, hotbar, grid));
		}
	}

//	public static String serialize(KitData kitData) {
//		try {
//			return DataFormats.JSON.write(kitData.toContainer());
//		} catch (IOException e1) {
//			e1.printStackTrace();
//			return null;
//		}
//	}
//
//	public static KitData deserialize(String item) {
//		try {
//			return Sponge.getDataManager().deserialize(KitData.class, DataFormats.JSON.read(item)).get();
//		} catch (InvalidDataException | IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
}
