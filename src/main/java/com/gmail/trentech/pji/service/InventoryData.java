package com.gmail.trentech.pji.service;

import static org.spongepowered.api.data.DataQuery.of;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.inventory.ItemStack;

import com.gmail.trentech.pji.utils.DataSerializer;

public class InventoryData implements DataSerializable {

	private final static DataQuery NAME = of("name");
	private final static DataQuery HOTBAR = of("hotbar");
	private final static DataQuery OFF_HAND = of("offhand");
	private final static DataQuery EQUIPMENT = of("equipment");
	private final static DataQuery GRID = of("grid");
	private final static DataQuery HEALTH = of("health");
	private final static DataQuery FOOD = of("food");
	private final static DataQuery SATURATION = of("saturation");
	private final static DataQuery EXP_LEVEL = of("xplevel");
	private final static DataQuery EXPERIENCE = of("experience");
	
	private String name;
	private Map<Integer, ItemStack> hotbar = new HashMap<>();
	private Optional<ItemStack> offHand = Optional.empty();
	private Map<Integer, ItemStack> equipment = new HashMap<>();
	private Map<Integer, ItemStack> grid = new HashMap<>();
	private double health = 20.0;
	private int food = 20;
	private double saturation = 20.0;
	private int expLevel = 0;
	private int experience = 0;

	protected InventoryData(String name, Optional<ItemStack> offHand, Map<Integer, ItemStack> hotbar, Map<Integer, ItemStack> equipment, Map<Integer, ItemStack> grid, double health, int food, double saturation, int expLevel, int experience) {
		this.name = name;
		this.offHand = offHand;
		this.hotbar = hotbar;
		this.grid = grid;
		this.equipment = equipment;
		this.health = health;
		this.food = food;
		this.saturation = saturation;
		this.expLevel = expLevel;
		this.experience = experience;
	}

	protected InventoryData(String name) {
		this.name = name;
	}

	public String getName() {
		return name.toUpperCase();
	}

	public Optional<ItemStack> getOffHand() {
		return this.offHand;
	}

	public Map<Integer, ItemStack> getHotbar() {
		return this.hotbar;
	}

	public Map<Integer, ItemStack> getGrid() {
		return this.grid;
	}

	public Map<Integer, ItemStack> getEquipment() {
		return this.equipment;
	}

	public double getHealth() {
		return this.health;
	}

	public int getFood() {
		return this.food;
	}

	public double getSaturation() {
		return this.saturation;
	}

	public int getExpLevel() {
		return this.expLevel;
	}

	public int getExperience() {
		return this.experience;
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

	public void addEquipment(Integer slot, ItemStack itemStack) {
		this.equipment.put(slot, itemStack);
	}

	public void removeEquipment(Integer slot) {
		this.equipment.remove(slot);
	}

	public void setOffHand(Optional<ItemStack> itemStack) {
		this.offHand = itemStack;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public void setSaturation(double saturation) {
		this.saturation = saturation;
	}

	public void setExpLevel(int expLevel) {
		this.expLevel = expLevel;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		Map<String, String> hotbar = new HashMap<>();

		for (Entry<Integer, ItemStack> entry : this.hotbar.entrySet()) {
			hotbar.put(entry.getKey().toString(), DataSerializer.serializeItemStack(entry.getValue()));
		}

		Map<String, String> grid = new HashMap<>();

		for (Entry<Integer, ItemStack> entry : this.grid.entrySet()) {
			grid.put(entry.getKey().toString(), DataSerializer.serializeItemStack(entry.getValue()));
		}

		Map<String, String> equipment = new HashMap<>();

		for (Entry<Integer, ItemStack> entry : this.equipment.entrySet()) {
			equipment.put(entry.getKey().toString(), DataSerializer.serializeItemStack(entry.getValue()));
		}

		DataContainer container = new MemoryDataContainer().set(NAME, getName()).set(HOTBAR, hotbar).set(GRID, grid).set(EQUIPMENT, equipment).set(HEALTH, health).set(FOOD, food).set(SATURATION, saturation).set(EXP_LEVEL, expLevel).set(EXPERIENCE, experience);

		if (this.offHand.isPresent()) {
			container.set(OFF_HAND, DataSerializer.serializeItemStack(this.offHand.get()));
		}

		return container;
	}

	public static class Builder extends AbstractDataBuilder<InventoryData> {

		public Builder() {
			super(InventoryData.class, 1);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Optional<InventoryData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(NAME, HOTBAR, GRID, EQUIPMENT, HEALTH, FOOD, SATURATION, EXP_LEVEL, EXPERIENCE)) {
				String name = container.getString(NAME).get();

				Map<Integer, ItemStack> hotbar = new HashMap<>();

				for (Entry<String, String> entry : ((Map<String, String>) container.getMap(HOTBAR).get()).entrySet()) {
					hotbar.put(Integer.parseInt(entry.getKey()), DataSerializer.deserializeItemStack(entry.getValue()));
				}

				Map<Integer, ItemStack> grid = new HashMap<>();

				for (Entry<String, String> entry : ((Map<String, String>) container.getMap(GRID).get()).entrySet()) {
					grid.put(Integer.parseInt(entry.getKey()), DataSerializer.deserializeItemStack(entry.getValue()));
				}

				Map<Integer, ItemStack> equipment = new HashMap<>();

				for (Entry<String, String> entry : ((Map<String, String>) container.getMap(EQUIPMENT).get()).entrySet()) {
					equipment.put(Integer.parseInt(entry.getKey()), DataSerializer.deserializeItemStack(entry.getValue()));
				}

				double health = container.getDouble(HEALTH).get();
				int food = container.getInt(FOOD).get();
				double saturation = container.getDouble(SATURATION).get();
				int expLevel = container.getInt(EXP_LEVEL).get();
				int experience = container.getInt(EXPERIENCE).get();
				Optional<ItemStack> offHand = Optional.empty();

				if (container.contains(OFF_HAND)) {
					offHand = Optional.of(DataSerializer.deserializeItemStack(container.getString(OFF_HAND).get()));
				}

				InventoryData inventoryData = new InventoryData(name, offHand, hotbar, equipment, grid, health, food, saturation, expLevel, experience);

				return Optional.of(inventoryData);
			}
			return Optional.empty();
		}
	}
}
