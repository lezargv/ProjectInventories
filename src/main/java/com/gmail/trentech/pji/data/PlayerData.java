package com.gmail.trentech.pji.data;

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

import com.gmail.trentech.pji.utils.ItemSerializer;

public class PlayerData implements DataSerializable {

	private final static DataQuery NAME = of("name");
	private final static DataQuery OFF_HAND = of("offhand");
	private final static DataQuery HELMET = of("helmet");
	private final static DataQuery CHEST_PLATE = of("chestplate");
	private final static DataQuery LEGGINGS = of("leggings");
	private final static DataQuery BOOTS = of("boots");
	private final static DataQuery HOTBAR = of("hotbar");
	private final static DataQuery GRID = of("grid");
	private final static DataQuery HEALTH = of("health");
	private final static DataQuery FOOD = of("food");
	private final static DataQuery SATURATION = of("saturation");
	private final static DataQuery EXP_LEVEL = of("xplevel");
	private final static DataQuery EXPERIENCE = of("experience");
	
	private String name;	
	private Optional<ItemStack> offHand = Optional.empty();
	private Optional<ItemStack> helmet = Optional.empty();
	private Optional<ItemStack> chestPlate = Optional.empty();
	private Optional<ItemStack> leggings = Optional.empty();
	private Optional<ItemStack> boots = Optional.empty();
	private Map<Integer, ItemStack> hotbar = new HashMap<>();
	private Map<Integer, ItemStack> grid = new HashMap<>();
	private double health = 20.0;
	private int food = 20;
	private double saturation = 20.0;
	private int expLevel = 0;
	private int experience = 0;

	protected PlayerData(String name, Optional<ItemStack> offHand, Optional<ItemStack> helmet, Optional<ItemStack> chestPlate, Optional<ItemStack> leggings, Optional<ItemStack> boots, Map<Integer, ItemStack> hotbar, Map<Integer, ItemStack> grid, double health, int food, double saturation, int expLevel, int experience) {
		this.name = name;
		this.offHand = offHand;
		this.hotbar = hotbar;
		this.grid = grid;
		this.helmet = helmet;
		this.chestPlate = chestPlate;
		this.leggings = leggings;
		this.boots = boots;
		this.health = health;
		this.food = food;
		this.saturation = saturation;
		this.expLevel = expLevel;
		this.experience = experience;
	}

	public PlayerData(String name) {
		this.name = name;
	}

	public String getName() {
		return name.toUpperCase();
	}

	public Optional<ItemStack> getOffHand() {
		return this.offHand;
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
		return this.hotbar;
	}

	public Map<Integer, ItemStack> getGrid() {
		return this.grid;
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
		DataContainer container = new MemoryDataContainer().set(NAME, getName());

		if (this.offHand.isPresent()) {
			container.set(OFF_HAND, ItemSerializer.serialize(this.offHand.get()));
		}
		
		if (this.helmet.isPresent()) {
			container.set(HELMET, ItemSerializer.serialize(this.helmet.get()));
		}
		
		if (this.chestPlate.isPresent()) {
			container.set(CHEST_PLATE, ItemSerializer.serialize(this.chestPlate.get()));
		}
		
		if (this.leggings.isPresent()) {
			container.set(LEGGINGS, ItemSerializer.serialize(this.leggings.get()));
		}
		
		if (this.boots.isPresent()) {
			container.set(BOOTS, ItemSerializer.serialize(this.boots.get()));
		}
		
		Map<String, String> hotbar = new HashMap<>();

		for (Entry<Integer, ItemStack> entry : this.hotbar.entrySet()) {
			hotbar.put(entry.getKey().toString(), ItemSerializer.serialize(entry.getValue()));
		}

		Map<String, String> grid = new HashMap<>();

		for (Entry<Integer, ItemStack> entry : this.grid.entrySet()) {
			grid.put(entry.getKey().toString(), ItemSerializer.serialize(entry.getValue()));
		}

		container.set(HOTBAR, hotbar).set(GRID, grid).set(HEALTH, health).set(FOOD, food).set(SATURATION, saturation).set(EXP_LEVEL, expLevel).set(EXPERIENCE, experience);

		return container;
	}

	public static class Builder extends AbstractDataBuilder<PlayerData> {

		public Builder() {
			super(PlayerData.class, 1);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Optional<PlayerData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(NAME, HOTBAR, GRID, HEALTH, FOOD, SATURATION, EXP_LEVEL, EXPERIENCE)) {
				String name = container.getString(NAME).get();

				Optional<ItemStack> offHand = Optional.empty();

				if (container.contains(OFF_HAND)) {
					offHand = Optional.of(ItemSerializer.deserialize(container.getString(OFF_HAND).get()));
				}
				
				Optional<ItemStack> helmet = Optional.empty();

				if (container.contains(HELMET)) {
					helmet = Optional.of(ItemSerializer.deserialize(container.getString(HELMET).get()));
				}
				
				Optional<ItemStack> chestPlate = Optional.empty();

				if (container.contains(CHEST_PLATE)) {
					chestPlate = Optional.of(ItemSerializer.deserialize(container.getString(CHEST_PLATE).get()));
				}
				
				Optional<ItemStack> leggings = Optional.empty();

				if (container.contains(LEGGINGS)) {
					leggings = Optional.of(ItemSerializer.deserialize(container.getString(LEGGINGS).get()));
				}
				
				Optional<ItemStack> boots = Optional.empty();

				if (container.contains(BOOTS)) {
					boots = Optional.of(ItemSerializer.deserialize(container.getString(BOOTS).get()));
				}

				Map<Integer, ItemStack> hotbar = new HashMap<>();

				for (Entry<String, String> entry : ((Map<String, String>) container.getMap(HOTBAR).get()).entrySet()) {
					hotbar.put(Integer.parseInt(entry.getKey()), ItemSerializer.deserialize(entry.getValue()));
				}

				Map<Integer, ItemStack> grid = new HashMap<>();

				for (Entry<String, String> entry : ((Map<String, String>) container.getMap(GRID).get()).entrySet()) {
					grid.put(Integer.parseInt(entry.getKey()), ItemSerializer.deserialize(entry.getValue()));
				}

				double health = container.getDouble(HEALTH).get();
				int food = container.getInt(FOOD).get();
				double saturation = container.getDouble(SATURATION).get();
				int expLevel = container.getInt(EXP_LEVEL).get();
				int experience = container.getInt(EXPERIENCE).get();

				PlayerData playerData = new PlayerData(name, offHand, helmet, chestPlate, leggings, boots, hotbar, grid, health, food, saturation, expLevel, experience);

				return Optional.of(playerData);
			}
			return Optional.empty();
		}
	}


}
