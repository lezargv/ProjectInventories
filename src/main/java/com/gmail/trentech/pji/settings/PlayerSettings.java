package com.gmail.trentech.pji.settings;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.KitData;
import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.data.PlayerInventoryData;
import com.gmail.trentech.pji.sql.PlayerDB;

import ninja.leaping.configurate.ConfigurationNode;

public class PlayerSettings {

	InventoryService inventoryService;

	public PlayerSettings(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public PlayerData getPlayerData(Player player) {
		return PlayerDB.get(player.getUniqueId());
	}
	
	public Optional<PlayerInventoryData> get(Player player, String inventory) {
		return PlayerDB.Data.get(player, inventory);
	}
	
	public PlayerInventoryData empty(String name) {
		return new PlayerInventoryData(name);
	}
	
	public PlayerInventoryData copy(Player player) {
		PlayerInventoryData playerInventoryData = new PlayerInventoryData(getPlayerData(player).getInventoryName());

		PlayerInventory inv = (PlayerInventory) player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(PlayerInventory.class));

		int i = 0;
		for (Inventory item : inv.getHotbar().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				playerInventoryData.addHotbar(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (Inventory item : inv.getMainGrid().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				playerInventoryData.addGrid(i, peek.get());
			}
			i++;
		}

		playerInventoryData.setOffHand(player.getItemInHand(HandTypes.OFF_HAND).orElse(ItemStack.empty()));
		playerInventoryData.setHelmet(player.getHelmet().orElse(ItemStack.empty()));
		playerInventoryData.setChestPlate(player.getChestplate().orElse(ItemStack.empty()));
		playerInventoryData.setLeggings(player.getLeggings().orElse(ItemStack.empty()));
		playerInventoryData.setBoots(player.getBoots().orElse(ItemStack.empty()));	
		playerInventoryData.setHealth(player.get(Keys.HEALTH).get());
		playerInventoryData.setFood(player.get(Keys.FOOD_LEVEL).get());
		playerInventoryData.setSaturation(player.get(Keys.SATURATION).get());
		playerInventoryData.setExpLevel(player.get(Keys.EXPERIENCE_LEVEL).get());
		playerInventoryData.setExperience(player.get(Keys.TOTAL_EXPERIENCE).get());
		playerInventoryData.setPotionEffects(player.get(PotionEffectData.class));
		
		return playerInventoryData;
	}

	public void save(Player player, PlayerInventoryData playerInventoryData) {
		if (PlayerDB.Data.exists(player, playerInventoryData.getName())) {
			PlayerDB.Data.update(player.getUniqueId(), playerInventoryData);
		} else {
			PlayerDB.Data.create(player.getUniqueId(), playerInventoryData);
		}
	}

	public void set(Player player, InventoryData inventoryData, boolean login) {
		PlayerData playerData = getPlayerData(player);

		if(!playerData.contains(inventoryData.getName())) {
			Optional<KitData> optionalKitData = inventoryData.getKitData();
			
			if(optionalKitData.isPresent()) {
				playerData.setInventoryName(inventoryData.getName());
				PlayerDB.save(player.getUniqueId(), playerData);
				set(player, playerData, optionalKitData.get());
				return;
			}
		}
		
		playerData.setInventoryName(inventoryData.getName());
		PlayerDB.save(player.getUniqueId(), playerData);
		set(player, playerData, login);
	}
	
	private void set(Player player, PlayerData playerData, KitData kitData) {
		PlayerInventoryData playerInventoryData = new PlayerInventoryData(playerData.getInventoryName(), kitData);

		save(player, playerInventoryData);

		set(player, playerInventoryData);
	}
	
	private void set(Player player, PlayerData playerData, boolean login) {
		Optional<PlayerInventoryData> optionalPlayerInventoryData = get(player, playerData.getInventoryName());

		if (optionalPlayerInventoryData.isPresent()) {
			PlayerInventoryData playerInventoryData = optionalPlayerInventoryData.get();
			
			set(player, playerInventoryData);
		} else {
			if (!login) {
				player.getInventory().clear();
			}

			save(player, copy(player));
		}
	}
	
	private void set(Player player, PlayerInventoryData playerInventoryData) {
		player.getInventory().clear();

		PlayerInventory inv = (PlayerInventory) player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(PlayerInventory.class));

		Map<Integer, ItemStack> hotbar = playerInventoryData.getHotbar();

		if (!hotbar.isEmpty()) {
			int i = 0;
			for (Inventory slot : inv.getHotbar().slots()) {
				if (hotbar.containsKey(i)) {
					slot.set(hotbar.get(i));
				}
				i++;
			}
		}

		Map<Integer, ItemStack> grid = playerInventoryData.getGrid();

		if (!grid.isEmpty()) {
			int i = 0;
			for (Inventory slot : inv.getMainGrid().slots()) {
				if (grid.containsKey(i)) {
					slot.set(grid.get(i));
				}
				i++;
			}
		}

		player.setHelmet(playerInventoryData.getHelmet());
		player.setChestplate(playerInventoryData.getChestPlate());
		player.setLeggings(playerInventoryData.getLeggings());
		player.setBoots(playerInventoryData.getBoots());
		player.setItemInHand(HandTypes.OFF_HAND, playerInventoryData.getOffHand());

		ConfigurationNode config = ConfigManager.get(Main.getPlugin()).getConfig();

		if (config.getNode("options", "health").getBoolean()) {
			player.offer(Keys.HEALTH, playerInventoryData.getHealth());
		}

		if (config.getNode("options", "hunger").getBoolean()) {
			player.offer(Keys.FOOD_LEVEL, playerInventoryData.getFood());
			player.offer(Keys.SATURATION, playerInventoryData.getSaturation());
		}

		if (config.getNode("options", "experience").getBoolean()) {
			player.offer(Keys.EXPERIENCE_LEVEL, playerInventoryData.getExpLevel());
			player.offer(Keys.TOTAL_EXPERIENCE, playerInventoryData.getExperience());
		}
		
		if (config.getNode("options", "potion-effects").getBoolean()) {
			Optional<PotionEffectData> potionEffects = playerInventoryData.getPotionEffects();
			
			if(potionEffects.isPresent()) {
				player.offer(potionEffects.get());
			} else {
				player.remove(PotionEffectData.class);
			}
		}	
	}
}
