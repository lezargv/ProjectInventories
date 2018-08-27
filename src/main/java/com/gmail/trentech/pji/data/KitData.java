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
	
	private ItemStack offHand = ItemStack.empty();
	private ItemStack helmet = ItemStack.empty();
	private ItemStack chestPlate = ItemStack.empty();
	private ItemStack leggings = ItemStack.empty();
	private ItemStack boots = ItemStack.empty();
	private Map<Integer, ItemStack> hotbar = new HashMap<>();
	private Map<Integer, ItemStack> grid = new HashMap<>();

	protected KitData(ItemStack offHand, ItemStack helmet, ItemStack chestPlate, ItemStack leggings, ItemStack boots, Map<Integer, ItemStack> hotbar, Map<Integer, ItemStack> grid) {
		this.offHand = offHand;
		this.hotbar = hotbar;
		this.grid = grid;
		this.helmet = helmet;
		this.chestPlate = chestPlate;
		this.leggings = leggings;
		this.boots = boots;
	}

	public KitData(Player player) {
		PlayerInventory inv = (PlayerInventory) player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(PlayerInventory.class));

		int i = 0;
		for (Inventory item : inv.getHotbar().slots()) {
			Slot slot = (Slot) item;

			addHotbar(i, slot.peek());
			i++;
		}

		i = 0;
		for (Inventory item : inv.getStorage().slots()) {
			Slot slot = (Slot) item;

			addGrid(i, slot.peek());
			i++;
		}

		setOffHand(player.getItemInHand(HandTypes.OFF_HAND));
		setHelmet(player.getHelmet());
		setChestPlate(player.getChestplate());
		setLeggings(player.getLeggings());
		setBoots(player.getBoots());	
	}
	
	public ItemStack getOffHand() {
		return offHand;
	}

	public ItemStack getHelmet() {
		return helmet;
	}

	public ItemStack getChestPlate() {
		return chestPlate;
	}

	public ItemStack getLeggings() {
		return leggings;
	}

	public ItemStack getBoots() {
		return boots;
	}
	
	public Map<Integer, ItemStack> getHotbar() {
		return hotbar;
	}

	public Map<Integer, ItemStack> getGrid() {
		return grid;
	}
	
	public void setOffHand(ItemStack itemStack) {
		this.offHand = itemStack;
	}
	
	public void setHelmet(ItemStack itemStack) {
		this.helmet = itemStack;
	}

	public void setChestPlate(ItemStack itemStack) {
		this.chestPlate = itemStack;
	}

	public void setLeggings(ItemStack itemStack) {
		this.leggings = itemStack;
	}

	public void setBoots(ItemStack itemStack) {
		this.boots = itemStack;
	}

	public void addEquipment(int slot, ItemStack itemStack) {
		if (slot == 0) {
			this.helmet = itemStack;
		} else if(slot == 1) {
			this.chestPlate = itemStack;
		} else if(slot == 2) {
			this.leggings = itemStack;
		} else if(slot == 3) {
			this.boots = itemStack;
		}
	}
	
	public void removeEquipment(int slot) {
		if (slot == 0) {
			this.helmet = ItemStack.empty();
		} else if(slot == 1) {
			this.chestPlate = ItemStack.empty();
		} else if(slot == 2) {
			this.leggings = ItemStack.empty();
		} else if(slot == 3) {
			this.boots = ItemStack.empty();
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
		DataContainer container = DataContainer.createNew().set(OFF_HAND, this.offHand.toContainer()).set(HELMET, this.helmet.toContainer()).set(CHEST_PLATE, this.chestPlate.toContainer())
				.set(LEGGINGS, this.leggings.toContainer()).set(BOOTS, this.boots.toContainer());

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
			ItemStack offHand = ItemStack.builder().fromContainer(container.getView(OFF_HAND).get()).build();
			ItemStack helmet = ItemStack.builder().fromContainer(container.getView(HELMET).get()).build();
			ItemStack chestPlate = ItemStack.builder().fromContainer(container.getView(CHEST_PLATE).get()).build();
			ItemStack leggings = ItemStack.builder().fromContainer(container.getView(LEGGINGS).get()).build();
			ItemStack boots = ItemStack.builder().fromContainer(container.getView(BOOTS).get()).build();

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
