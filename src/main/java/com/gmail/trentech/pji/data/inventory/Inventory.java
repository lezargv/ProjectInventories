package com.gmail.trentech.pji.data.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.item.inventory.ItemStack;

import com.gmail.trentech.pji.data.DataQueries;
import com.gmail.trentech.pji.data.inventory.extra.InventorySerializer;

public class Inventory implements DataSerializable {

	private Map<String, String> hotbar = new HashMap<>();
	private Map<String, String> grid = new HashMap<>();
	private Map<String, String> armor = new HashMap<>();
	
	private double health = 20;
	private int food = 20;
	private double saturation = 20;
	private int expLevel = 0;
	private int experience = 0;
	
	public Inventory(Map<String, String> hotbar, Map<String, String> grid, Map<String, String> armor, double health, int food, double saturation, int expLevel, int experience) {
		this.hotbar = hotbar;
		this.grid = grid;
		this.armor = armor;
		this.health = health;
		this.food = food;
		this.saturation = saturation;
		this.expLevel = expLevel;
		this.experience = experience;
	}

	public Inventory() {

	}

	public Map<Integer, ItemStack> getHotbar() {
		Map<Integer, ItemStack> map = new HashMap<>();
		
		for(Entry<String, String> entry : hotbar.entrySet()) {
			map.put(Integer.parseInt(entry.getKey()), InventorySerializer.deserializeItemStack(entry.getValue()));
		}
		
		return map;
	}
	
	public Map<Integer, ItemStack> getGrid() {
		Map<Integer, ItemStack> map = new HashMap<>();
		
		for(Entry<String, String> entry : grid.entrySet()) {
			map.put(Integer.parseInt(entry.getKey()), InventorySerializer.deserializeItemStack(entry.getValue()));
		}
		
		return map;
	}
	
	public Map<Integer, ItemStack> getArmor() {
		Map<Integer, ItemStack> map = new HashMap<>();
		
		for(Entry<String, String> entry : armor.entrySet()) {
			map.put(Integer.parseInt(entry.getKey()), InventorySerializer.deserializeItemStack(entry.getValue()));
		}
		
		return map;
	}

	public double getHealth() {
		return health;
	}

	public int getFood() {
		return food;
	}

	public double getSaturation() {
		return saturation;
	}

	public int getExpLevel() {
		return expLevel;
	}

	public int getExperience() {
		return experience;
	}
	
	public void addHotbar(Integer slot, ItemStack itemStack) {
		hotbar.put(slot.toString(), InventorySerializer.serializeItemStack(itemStack));
	}

	public void addGrid(Integer slot, ItemStack itemStack) {
		grid.put(slot.toString(), InventorySerializer.serializeItemStack(itemStack));
	}
	
	public void addArmor(Integer slot, ItemStack itemStack) {
		armor.put(slot.toString(), InventorySerializer.serializeItemStack(itemStack));
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
		return new MemoryDataContainer().set(DataQueries.HOTBAR, hotbar).set(DataQueries.INVENTORY, grid).set(DataQueries.ARMOR, armor)
				.set(DataQueries.HEALTH, health).set(DataQueries.FOOD, food).set(DataQueries.SATURATION, saturation)
				.set(DataQueries.EXP_LEVEL, expLevel).set(DataQueries.EXPERIENCE, experience);
	}


}
