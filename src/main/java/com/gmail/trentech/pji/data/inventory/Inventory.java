package com.gmail.trentech.pji.data.inventory;

import static com.gmail.trentech.pji.data.DataQueries.EQUIPMENT;
import static com.gmail.trentech.pji.data.DataQueries.EXPERIENCE;
import static com.gmail.trentech.pji.data.DataQueries.EXP_LEVEL;
import static com.gmail.trentech.pji.data.DataQueries.FOOD;
import static com.gmail.trentech.pji.data.DataQueries.GRID;
import static com.gmail.trentech.pji.data.DataQueries.HEALTH;
import static com.gmail.trentech.pji.data.DataQueries.HOTBAR;
import static com.gmail.trentech.pji.data.DataQueries.OFF_HAND;
import static com.gmail.trentech.pji.data.DataQueries.SATURATION;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.item.inventory.ItemStack;

import com.gmail.trentech.pji.data.inventory.extra.InventorySerializer;

public class Inventory implements DataSerializable {

	private Map<Integer, ItemStack> hotbar = new HashMap<>();
	private Optional<ItemStack> offHand = Optional.empty();
	private Map<Integer, ItemStack> equipment = new HashMap<>();
	private Map<Integer, ItemStack> grid = new HashMap<>();
	private double health = 20.0;
	private int food = 20;
	private double saturation = 20.0;
	private int expLevel = 0;
	private int experience = 0;

	protected Inventory(Optional<ItemStack> offHand, Map<Integer, ItemStack> hotbar, Map<Integer, ItemStack> equipment, Map<Integer, ItemStack> grid, double health, int food, double saturation, int expLevel, int experience) {
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

	public Inventory() {

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

	public void addGrid(Integer slot, ItemStack itemStack) {
		this.grid.put(slot, itemStack);
	}

	public void addEquipment(Integer slot, ItemStack itemStack) {
		this.equipment.put(slot, itemStack);
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
		return 0;
	}

	@Override
	public DataContainer toContainer() {
		Map<String, String> hotbar = new HashMap<>();

		for (Entry<Integer, ItemStack> entry : this.hotbar.entrySet()) {
			hotbar.put(entry.getKey().toString(), InventorySerializer.serializeItemStack(entry.getValue()));
		}

		Map<String, String> grid = new HashMap<>();

		for (Entry<Integer, ItemStack> entry : this.grid.entrySet()) {
			grid.put(entry.getKey().toString(), InventorySerializer.serializeItemStack(entry.getValue()));
		}

		Map<String, String> equipment = new HashMap<>();

		for (Entry<Integer, ItemStack> entry : this.equipment.entrySet()) {
			equipment.put(entry.getKey().toString(), InventorySerializer.serializeItemStack(entry.getValue()));
		}

		DataContainer container = new MemoryDataContainer()
				.set(HOTBAR, hotbar)
				.set(GRID, grid)
				.set(EQUIPMENT, equipment)
				.set(HEALTH, health)
				.set(FOOD, food)
				.set(SATURATION, saturation)
				.set(EXP_LEVEL, expLevel)
				.set(EXPERIENCE, experience);
		
		if(this.offHand.isPresent()) {
			container.set(OFF_HAND, InventorySerializer.serializeItemStack(this.offHand.get()));
		}
		
		return container;
	}
}
