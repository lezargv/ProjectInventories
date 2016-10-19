package com.gmail.trentech.pji.data.inventory.extra;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.inventory.Inventory;
import com.gmail.trentech.pji.sql.SQLInventory;
import com.gmail.trentech.pji.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class InventoryHelper {

	public static void setInventory(Player player, String name) {
		Sponge.getScheduler().createTaskBuilder().delayTicks(5).execute(c -> {
			player.getInventory().clear();
			
			Inventory inventory;
			Optional<Inventory> optionalInventory = SQLInventory.get(player, name);

			if (optionalInventory.isPresent()) {
				inventory = optionalInventory.get();
			} else {
				inventory = new Inventory();
			}

			PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

			Map<Integer, ItemStack> hotbar = inventory.getHotbar();

			if (!hotbar.isEmpty()) {
				int i = 0;
				for (org.spongepowered.api.item.inventory.Inventory slot : inv.getHotbar().slots()) {
					if (hotbar.containsKey(i)) {
						slot.set(hotbar.get(i));
					}
					i++;
				}
			}

			Map<Integer, ItemStack> grid = inventory.getGrid();

			if (!grid.isEmpty()) {
				int i = 0;
				for (org.spongepowered.api.item.inventory.Inventory slot : inv.getMain().slots()) {
					if (grid.containsKey(i)) {
						slot.set(grid.get(i));
					}
					i++;
				}
			}

			Map<Integer, ItemStack> equipment = inventory.getEquipment();

			if (!equipment.isEmpty()) {
				int i = 0;
				for (org.spongepowered.api.item.inventory.Inventory slot : inv.getEquipment().slots()) {
					if (equipment.containsKey(i)) {
						slot.set(equipment.get(i));
					}
					i++;
				}
			}
			
			Optional<ItemStack> offHand = inventory.getOffHand();
			
			if(offHand.isPresent()) {
				inv.getOffhand().set(offHand.get());
			}

			ConfigurationNode config = ConfigManager.get().getConfig();

			if (config.getNode("options", "health").getBoolean()) {
				player.offer(Keys.HEALTH, inventory.getHealth());
			}

			if (config.getNode("options", "hunger").getBoolean()) {
				player.offer(Keys.FOOD_LEVEL, inventory.getFood());
				player.offer(Keys.SATURATION, inventory.getSaturation());
			}

			if (config.getNode("options", "experience").getBoolean()) {
				player.offer(Keys.EXPERIENCE_LEVEL, inventory.getExpLevel());
				player.offer(Keys.TOTAL_EXPERIENCE, inventory.getExperience());
			}
		}).submit(Main.getPlugin());
	}

	public static void saveInventory(Player player, String name) {
		Inventory inventory = new Inventory();

		PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

		int i = 0;
		for (org.spongepowered.api.item.inventory.Inventory item : inv.getHotbar().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				//SlotIndex slotIndex = item.getProperty(SlotIndex.class, null).get();
				inventory.addHotbar(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (org.spongepowered.api.item.inventory.Inventory item : inv.getMain().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				//SlotPos slotPos = slot.getProperty(SlotPos.class, null).get();
				inventory.addGrid(i, peek.get());
			}
			i++;
		}

		i = 0;
		for (org.spongepowered.api.item.inventory.Inventory item : inv.getEquipment().slots()) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if (peek.isPresent()) {
				inventory.addEquipment(i, peek.get());
			}
			i++;
		}

		inventory.setOffHand(inv.getOffhand().peek());
		inventory.setHealth(player.get(Keys.HEALTH).get());
		inventory.setFood(player.get(Keys.FOOD_LEVEL).get());
		inventory.setSaturation(player.get(Keys.SATURATION).get());
		inventory.setExpLevel(player.get(Keys.EXPERIENCE_LEVEL).get());
		inventory.setExperience(player.get(Keys.TOTAL_EXPERIENCE).get());

		if (SQLInventory.exists(player, name)) {
			SQLInventory.update(player, name, inventory);
		} else {
			SQLInventory.create(player, name, inventory);
		}
	}
}
