package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.persistence.InvalidDataFormatException;
import org.spongepowered.api.item.inventory.ItemStack;

public class PlayerInventoryData implements DataSerializable {

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
	private final static DataQuery POTION_EFFECTS = of("potioneffects");
	private final static DataQuery SLOT_POSITION = of("slot_position");
	private final static DataQuery ITEM_STACK = of("item_stack");
	
	private String name;	
	private ItemStack offHand = ItemStack.empty();
	private ItemStack helmet = ItemStack.empty();
	private ItemStack chestPlate = ItemStack.empty();
	private ItemStack leggings = ItemStack.empty();
	private ItemStack boots = ItemStack.empty();
	private Map<Integer, ItemStack> hotbar = new HashMap<>();
	private Map<Integer, ItemStack> grid = new HashMap<>();
	private double health = 20.0;
	private int food = 20;
	private double saturation = 20.0;
	private int expLevel = 0;
	private int experience = 0;
	private Optional<PotionEffectData> potionEffects = Optional.empty();
	
	protected PlayerInventoryData(String name, ItemStack offHand, ItemStack helmet, ItemStack chestPlate, ItemStack leggings, ItemStack boots, Map<Integer, ItemStack> hotbar, Map<Integer, ItemStack> grid, double health, int food, double saturation, int expLevel, int experience, Optional<PotionEffectData> potionEffects) {
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
		this.potionEffects = potionEffects;
	}

	public PlayerInventoryData(String name) {
		this.name = name;
	}

	public PlayerInventoryData(String name, KitData kitData) {
		this.name = name;
		this.offHand = kitData.getOffHand();
		this.hotbar = kitData.getHotbar();
		this.grid = kitData.getGrid();
		this.helmet = kitData.getHelmet();
		this.chestPlate = kitData.getChestPlate();
		this.leggings = kitData.getLeggings();
		this.boots = kitData.getBoots();
	}
	
	public String getName() {
		return name.toUpperCase();
	}

	public ItemStack getOffHand() {
		return this.offHand;
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
	
	public Optional<PotionEffectData> getPotionEffects() {
		return potionEffects;
	}
	
	public void setOffHand(ItemStack itemStack) {
		this.offHand = itemStack;
	}
	
	public void setHelmet(ItemStack helmet) {
		this.helmet = helmet;
	}

	public void setChestPlate(ItemStack chestPlate) {
		this.chestPlate = chestPlate;
	}

	public void setLeggings(ItemStack leggings) {
		this.leggings = leggings;
	}

	public void setBoots(ItemStack boots) {
		this.boots = boots;
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

	public void setPotionEffects(Optional<PotionEffectData> potionEffects) {
		this.potionEffects = potionEffects;
	}
	
	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = DataContainer.createNew().set(NAME, this.name).set(OFF_HAND, this.offHand.toContainer()).set(HELMET, this.helmet.toContainer())
				.set(CHEST_PLATE, this.chestPlate.toContainer()).set(LEGGINGS, this.leggings.toContainer()).set(BOOTS, this.boots.toContainer());

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

		return container.set(HEALTH, health).set(FOOD, food).set(SATURATION, saturation).set(EXP_LEVEL, expLevel).set(EXPERIENCE, experience);
	}

	public static class Builder extends AbstractDataBuilder<PlayerInventoryData> {

		public Builder() {
			super(PlayerInventoryData.class, 1);
		}

		@Override
		protected Optional<PlayerInventoryData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(NAME, HEALTH, FOOD, SATURATION, EXP_LEVEL, EXPERIENCE)) {
				String name = container.getString(NAME).get();
				
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

				Optional<PotionEffectData> potionEffects = Optional.empty();
				
				if(container.contains(POTION_EFFECTS)) {
					potionEffects = Optional.of(container.getSerializable(POTION_EFFECTS, PotionEffectData.class).get());
				}
				
				double health = container.getDouble(HEALTH).get();
				int food = container.getInt(FOOD).get();
				double saturation = container.getDouble(SATURATION).get();
				int expLevel = container.getInt(EXP_LEVEL).get();
				int experience = container.getInt(EXPERIENCE).get();

				return Optional.of(new PlayerInventoryData(name, offHand, helmet, chestPlate, leggings, boots, hotbar, grid, health, food, saturation, expLevel, experience, potionEffects));
			}
			
			return Optional.empty();
		}
	}

	public static byte[] serialize(PlayerInventoryData playerInventoryData) {
		try {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			GZIPOutputStream gZipOutStream = new GZIPOutputStream(byteOutStream);
			DataFormats.NBT.writeTo(gZipOutStream, playerInventoryData.toContainer());
			gZipOutStream.close();
			return byteOutStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static PlayerInventoryData deserialize(byte[] bytes) {
		try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
			GZIPInputStream gZipInputSteam = new GZIPInputStream(byteInputStream);
			DataContainer container = DataFormats.NBT.readFrom(gZipInputSteam);
			return Sponge.getDataManager().deserialize(PlayerInventoryData.class, container).get();
		} catch (InvalidDataFormatException | IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
