package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

public class KitData implements DataSerializable {

	private final static DataQuery OFF_HAND = of("offhand");
	private final static DataQuery HELMET = of("helmet");
	private final static DataQuery CHEST_PLATE = of("chestplate");
	private final static DataQuery LEGGINGS = of("leggings");
	private final static DataQuery BOOTS = of("boots");
	private final static DataQuery HOTBAR = of("hotbar");
	private final static DataQuery GRID = of("grid");
	private final static DataQuery SLOT_POSITION = of("slot_position");
	private final static DataQuery ITEM_STACK = of("item_stack");
	
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
			container.set(OFF_HAND, this.offHand.get().toContainer());
		}

		if (this.helmet.isPresent()) {
			container.set(HELMET, this.helmet.get().toContainer());
		}

		if (this.chestPlate.isPresent()) {
			container.set(CHEST_PLATE, this.chestPlate.get().toContainer());
		}

		if (this.leggings.isPresent()) {
			container.set(LEGGINGS, this.leggings.get().toContainer());
		}

		if (this.boots.isPresent()) {
			container.set(BOOTS, this.boots.get().toContainer());
		}
		
		if(!this.hotbar.isEmpty()) {
			List<DataView> hotbarData = new LinkedList<>();

			for (Entry<Integer, ItemStack> entry : hotbar.entrySet()) {
				hotbarData.add(DataContainer.createNew().set(SLOT_POSITION, entry.getKey()).set(ITEM_STACK, entry.getValue().toContainer()));
			}
			
			container.set(HOTBAR, hotbarData);
		}

		if(!this.grid.isEmpty()) {
			List<DataView> gridData = new LinkedList<>();

			for (Entry<Integer, ItemStack> entry : grid.entrySet()) {
				gridData.add(DataContainer.createNew().set(SLOT_POSITION, entry.getKey()).set(ITEM_STACK, entry.getValue().toContainer()));
			}

			container.set(GRID, gridData);
		}
		
		return container;
	}

	public static class Builder extends AbstractDataBuilder<KitData> {

		public Builder() {
			super(KitData.class, 1);
		}

		@Override
		protected Optional<KitData> buildContent(DataView container) throws InvalidDataException {
			Optional<ItemStack> offHand = Optional.empty();

			if (container.contains(OFF_HAND)) {
				offHand = Optional.of(ItemStack.builder().fromContainer(container.getView(OFF_HAND).get()).build());
			}
			
			Optional<ItemStack> helmet = Optional.empty();

			if (container.contains(HELMET)) {
				helmet = Optional.of(ItemStack.builder().fromContainer(container.getView(HELMET).get()).build());
			}
			
			Optional<ItemStack> chestPlate = Optional.empty();

			if (container.contains(CHEST_PLATE)) {
				chestPlate = Optional.of(ItemStack.builder().fromContainer(container.getView(CHEST_PLATE).get()).build());
			}
			
			Optional<ItemStack> leggings = Optional.empty();

			if (container.contains(LEGGINGS)) {
				leggings = Optional.of(ItemStack.builder().fromContainer(container.getView(LEGGINGS).get()).build());
			}
			
			Optional<ItemStack> boots = Optional.empty();

			if (container.contains(BOOTS)) {
				boots = Optional.of(ItemStack.builder().fromContainer(container.getView(BOOTS).get()).build());
			}

			Map<Integer, ItemStack> hotbar = new HashMap<>();

			if (container.contains(HOTBAR)) {
				for (DataView data : container.getViewList(HOTBAR).get()) {
					hotbar.put(data.getInt(SLOT_POSITION).get(), ItemStack.builder().fromContainer(data.getView(ITEM_STACK).get()).build());
				}
			}

			Map<Integer, ItemStack> grid = new HashMap<>();

			if (container.contains(GRID)) {
				for (DataView data : container.getViewList(GRID).get()) {
					grid.put(data.getInt(SLOT_POSITION).get(), ItemStack.builder().fromContainer(data.getView(ITEM_STACK).get()).build());
				}
			}

			return Optional.of(new KitData(offHand, helmet, chestPlate, leggings, boots, hotbar, grid));
		}
	}
}
