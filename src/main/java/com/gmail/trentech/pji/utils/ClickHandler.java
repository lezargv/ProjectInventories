package com.gmail.trentech.pji.utils;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.settings.PlayerSettings;

import ninja.leaping.configurate.ConfigurationNode;

public class ClickHandler implements Consumer<ClickInventoryEvent> {

	private Player target;
	private PlayerData playerData;
	private PlayerSettings playerSettings;
	
	public ClickHandler(Player target, PlayerData playerData, PlayerSettings playerSettings) {
		this.target = target;
		this.playerData = playerData;
		this.playerSettings = playerSettings;
	}
	
	@Override
	public void accept(ClickInventoryEvent event) {
		for(SlotTransaction transaction : event.getTransactions()) {
			if(transaction.getOriginal().getType().equals(ItemTypes.BARRIER)) {
				event.setCancelled(true);
				return;
			}
		}
		int i = 0;
		for (Inventory slot : event.getTargetInventory().slots()) {
			if (i < 27) {
				Optional<ItemStack> optionalItem = slot.peek();

				if (optionalItem.isPresent()) {
					playerData.addGrid(i, optionalItem.get());
				} else {
					playerData.removeGrid(i);
				}
			} else if (i < 36) {
				Optional<ItemStack> optionalItem = slot.peek();

				if (optionalItem.isPresent()) {
					playerData.addHotbar(i - 27, optionalItem.get());
				} else {
					playerData.removeHotbar(i - 27);
				}
			} else {
				Optional<ItemStack> optionalItem = slot.peek();

				if (optionalItem.isPresent()) {
					playerData.addEquipment(i - 36, optionalItem.get());
				} else {
					playerData.removeEquipment(i - 36);
				}
			}

			i++;
		}
		playerSettings.save(target, playerData);

		if (playerSettings.getInventoryName(target).equals(playerData.getName())) {
			target.getInventory().clear();

			PlayerInventory inv = target.getInventory().query(PlayerInventory.class);

			Map<Integer, ItemStack> hotbar = playerData.getHotbar();

			if (!hotbar.isEmpty()) {
				i = 0;
				for (Inventory slot : inv.getHotbar().slots()) {
					if (hotbar.containsKey(i)) {
						slot.set(hotbar.get(i));
					}
					i++;
				}
			}

			Map<Integer, ItemStack> grid = playerData.getGrid();

			if (!grid.isEmpty()) {
				i = 0;
				for (Inventory slot : inv.getMain().slots()) {
					if (grid.containsKey(i)) {
						slot.set(grid.get(i));
					}
					i++;
				}
			}

			Optional<ItemStack> helmet = playerData.getHelmet();

			if (helmet.isPresent()) {
				target.setHelmet(helmet.get());
			}

			Optional<ItemStack> chestPlate = playerData.getChestPlate();

			if (chestPlate.isPresent()) {
				target.setChestplate(chestPlate.get());
			}
			
			Optional<ItemStack> leggings = playerData.getLeggings();

			if (leggings.isPresent()) {
				target.setLeggings(leggings.get());
			}
			
			Optional<ItemStack> boots = playerData.getBoots();

			if (boots.isPresent()) {
				target.setBoots(boots.get());
			}

			Optional<ItemStack> offHand = playerData.getOffHand();

			if (offHand.isPresent()) {
				target.setItemInHand(HandTypes.OFF_HAND, offHand.get());
			}

			ConfigurationNode config = ConfigManager.get().getConfig();

			if (config.getNode("options", "health").getBoolean()) {
				target.offer(Keys.HEALTH, playerData.getHealth());
			}

			if (config.getNode("options", "hunger").getBoolean()) {
				target.offer(Keys.FOOD_LEVEL, playerData.getFood());
				target.offer(Keys.SATURATION, playerData.getSaturation());
			}

			if (config.getNode("options", "experience").getBoolean()) {
				target.offer(Keys.EXPERIENCE_LEVEL, playerData.getExpLevel());
				target.offer(Keys.TOTAL_EXPERIENCE, playerData.getExperience());
			}	
		}
	}

}
