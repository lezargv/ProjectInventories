package com.gmail.trentech.pji.data;

import java.util.LinkedHashMap;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import com.gmail.trentech.pji.data.sql.SQLInventory;
import com.gmail.trentech.pji.data.sql.SQLUtils;

public class InventoryData extends SQLUtils {

	private final Player player;
	private final String name;
	
	private LinkedHashMap<Integer, ItemStack> hotbar = new  LinkedHashMap<>();
	private  LinkedHashMap<Integer, ItemStack> grid = new  LinkedHashMap<>();
	private  LinkedHashMap<Integer, ItemStack> armor = new  LinkedHashMap<>();
	
	private double health;
	private int food;
	private double saturation;
	private int expLevel;
	private int experience;
	
	public InventoryData(Player player, String name, LinkedHashMap<Integer, ItemStack> hotbar, LinkedHashMap<Integer, ItemStack> grid, LinkedHashMap<Integer, ItemStack> armor,
			double health, int food, double saturation, int expLevel, int experience){
		this.player = player;
		this.name = name;
		this.hotbar = hotbar;
		this.grid = grid;
		this.armor = armor;
		this.health = health;
		this.food = food;
		this.saturation = saturation;
		this.expLevel = expLevel;
		this.experience = experience;
	}
	
	public InventoryData(Player player, String name){
		this.player = player;
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public LinkedHashMap<Integer, ItemStack> getHotbar(){
		return hotbar;
	}
	
	public LinkedHashMap<Integer, ItemStack> getGrid(){
		return grid;
	}
	
	public LinkedHashMap<Integer, ItemStack> getArmor(){
		return armor;
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
	
	public void setHotbar(LinkedHashMap<Integer, ItemStack> hotbar) {
		this.hotbar = hotbar;
		SQLInventory.updateHotbar(this.player, this.name, this.hotbar);
	}

	public void setGrid(LinkedHashMap<Integer, ItemStack> grid) {
		this.grid = grid;
		SQLInventory.updateGrid(this.player, this.name, this.grid);
	}

	public void setArmor(LinkedHashMap<Integer, ItemStack> armor) {
		this.armor = armor;
		SQLInventory.updateArmor(this.player, this.name, this.armor);
	}

	public void setHealth(double health) {
		this.health = health;
		SQLInventory.updateHealth(this.player, this.name, this.health);
	}

	public void setFood(int food) {
		this.food = food;
		SQLInventory.updateFoodLevel(this.player, this.name, this.food);
	}

	public void setSaturation(double saturation) {
		this.saturation = saturation;
		SQLInventory.updateSaturation(this.player, this.name, this.saturation);
	}

	public void setExpLevel(int expLevel) {
		this.expLevel = expLevel;
		SQLInventory.updateExperienceLevel(this.player, this.name, this.expLevel);
	}

	public void setExperience(int experience) {
		this.experience = experience;
		SQLInventory.updateExperience(this.player, this.name, this.experience);
	}


}
