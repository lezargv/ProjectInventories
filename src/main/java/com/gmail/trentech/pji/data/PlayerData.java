package com.gmail.trentech.pji.data;

import static com.gmail.trentech.pji.data.DataQueries.EQUIPMENT;
import static com.gmail.trentech.pji.data.DataQueries.EXPERIENCE;
import static com.gmail.trentech.pji.data.DataQueries.EXP_LEVEL;
import static com.gmail.trentech.pji.data.DataQueries.FOOD;
import static com.gmail.trentech.pji.data.DataQueries.GRID;
import static com.gmail.trentech.pji.data.DataQueries.HEALTH;
import static com.gmail.trentech.pji.data.DataQueries.HOTBAR;
import static com.gmail.trentech.pji.data.DataQueries.NAME;
import static com.gmail.trentech.pji.data.DataQueries.OFF_HAND;
import static com.gmail.trentech.pji.data.DataQueries.SATURATION;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.sql.SQLUtils;
import com.gmail.trentech.pji.utils.ConfigManager;
import com.gmail.trentech.pji.utils.DataSerializer;

import ninja.leaping.configurate.ConfigurationNode;

public class PlayerData extends SQLUtils implements DataSerializable {

	private String name;
	private Player player;
	private Map<Integer, ItemStack> hotbar = new HashMap<>();
	private Optional<ItemStack> offHand = Optional.empty();
	private Map<Integer, ItemStack> equipment = new HashMap<>();
	private Map<Integer, ItemStack> grid = new HashMap<>();
	private double health = 20.0;
	private int food = 20;
	private double saturation = 20.0;
	private int expLevel = 0;
	private int experience = 0;

	protected PlayerData(String name, Optional<ItemStack> offHand, Map<Integer, ItemStack> hotbar, Map<Integer, ItemStack> equipment, Map<Integer, ItemStack> grid, double health, int food, double saturation, int expLevel, int experience) {
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

	public PlayerData(Player player, String name) {
		this.setPlayer(player);
		this.name = Sponge.getServiceManager().provideUnchecked(InventoryService.class).getPlayerSettings().getInventory(player);
	}
	
	public PlayerData(Player player) {
		this.setPlayer(player);
		this.name = Sponge.getServiceManager().provideUnchecked(InventoryService.class).getPlayerSettings().getInventory(player);
		
		PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

		int i = 0;
		for (Inventory item : inv.getHotbar().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				this.addHotbar(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (Inventory item : inv.getMain().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				this.addGrid(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (Inventory item : inv.getEquipment().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				this.addEquipment(i, peek.get());
			}
			i++;
		}

		this.setOffHand(inv.getOffhand().peek());
		this.setHealth(player.get(Keys.HEALTH).get());
		this.setFood(player.get(Keys.FOOD_LEVEL).get());
		this.setSaturation(player.get(Keys.SATURATION).get());
		this.setExpLevel(player.get(Keys.EXPERIENCE_LEVEL).get());
		this.setExperience(player.get(Keys.TOTAL_EXPERIENCE).get());
	}

	public String getName() {
		return name.toUpperCase();
	}

	public Player getPlayer() {
		return player;
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

	public void setPlayer(Player player) {
		this.player = player;
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
	
	public void set() {
		getPlayer().getInventory().clear();
		
		PlayerInventory inv = getPlayer().getInventory().query(PlayerInventory.class);

		Map<Integer, ItemStack> hotbar = this.getHotbar();

		if (!hotbar.isEmpty()) {
			int i = 0;
			for (Inventory slot : inv.getHotbar().slots()) {
				if (hotbar.containsKey(i)) {
					slot.set(hotbar.get(i));
				}
				i++;
			}
		}

		Map<Integer, ItemStack> grid = this.getGrid();

		if (!grid.isEmpty()) {
			int i = 0;
			for (Inventory slot : inv.getMain().slots()) {
				if (grid.containsKey(i)) {
					slot.set(grid.get(i));
				}
				i++;
			}
		}

		Map<Integer, ItemStack> equipment = this.getEquipment();

		if (!equipment.isEmpty()) {
			int i = 0;
			for (Inventory slot : inv.getEquipment().slots()) {
				if (equipment.containsKey(i)) {
					slot.set(equipment.get(i));
				}
				i++;
			}
		}
		
		Optional<ItemStack> offHand = this.getOffHand();
		
		if(offHand.isPresent()) {
			inv.getOffhand().set(offHand.get());
		}

		ConfigurationNode config = ConfigManager.get().getConfig();

		if (config.getNode("options", "health").getBoolean()) {
			getPlayer().offer(Keys.HEALTH, this.getHealth());
		}

		if (config.getNode("options", "hunger").getBoolean()) {
			getPlayer().offer(Keys.FOOD_LEVEL, this.getFood());
			getPlayer().offer(Keys.SATURATION, this.getSaturation());
		}

		if (config.getNode("options", "experience").getBoolean()) {
			getPlayer().offer(Keys.EXPERIENCE_LEVEL, this.getExpLevel());
			getPlayer().offer(Keys.TOTAL_EXPERIENCE, this.getExperience());
		}
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

		DataContainer container = new MemoryDataContainer()
				.set(NAME, getName())
				.set(HOTBAR, hotbar)
				.set(GRID, grid)
				.set(EQUIPMENT, equipment)
				.set(HEALTH, health)
				.set(FOOD, food)
				.set(SATURATION, saturation)
				.set(EXP_LEVEL, expLevel)
				.set(EXPERIENCE, experience);
		
		if(this.offHand.isPresent()) {
			container.set(OFF_HAND, DataSerializer.serializeItemStack(this.offHand.get()));
		}
		
		return container;
	}

	public static class Builder extends AbstractDataBuilder<PlayerData> {

		public Builder() {
			super(PlayerData.class, 1);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Optional<PlayerData> buildContent(DataView container) throws InvalidDataException {
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
				
				if(container.contains(OFF_HAND)) {
					offHand = Optional.of(DataSerializer.deserializeItemStack(container.getString(OFF_HAND).get()));
				}
				
				PlayerData playerData = new PlayerData(name, offHand, hotbar, equipment, grid, health, food, saturation, expLevel, experience);
				
				return Optional.of(playerData);
			}
			return Optional.empty();
		}
	}
}
