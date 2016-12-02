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

import com.gmail.trentech.pji.data.PlayerInventoryData;
import com.gmail.trentech.pji.settings.PlayerSettings;

import ninja.leaping.configurate.ConfigurationNode;

public class ClickHandler implements Consumer<ClickInventoryEvent> {

	private Player target;
	private PlayerInventoryData playerInventoryData;
	private PlayerSettings playerSettings;
	
	public ClickHandler(Player target, PlayerInventoryData playerInventoryData, PlayerSettings playerSettings) {
		this.target = target;
		this.playerInventoryData = playerInventoryData;
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
					playerInventoryData.addGrid(i, optionalItem.get());
				} else {
					playerInventoryData.removeGrid(i);
				}
			} else if (i < 36) {
				Optional<ItemStack> optionalItem = slot.peek();

				if (optionalItem.isPresent()) {
					playerInventoryData.addHotbar(i - 27, optionalItem.get());
				} else {
					playerInventoryData.removeHotbar(i - 27);
				}
			} else if (i < 40) {
				Optional<ItemStack> optionalItem = slot.peek();

				if (optionalItem.isPresent()) {
					playerInventoryData.addEquipment(i - 36, optionalItem.get());
				} else {
					playerInventoryData.removeEquipment(i - 36);
				}
			} else if(i == 40) {
				Optional<ItemStack> optionalItem = slot.peek();

				playerInventoryData.setOffHand(optionalItem);
			}

			i++;
		}
		playerSettings.save(target, playerInventoryData);

		if (playerSettings.getPlayerData(target).getInventoryName().equals(playerInventoryData.getName())) {
			target.getInventory().clear();

			PlayerInventory inv = target.getInventory().query(PlayerInventory.class);

			Map<Integer, ItemStack> hotbar = playerInventoryData.getHotbar();

			if (!hotbar.isEmpty()) {
				i = 0;
				for (Inventory slot : inv.getHotbar().slots()) {
					if (hotbar.containsKey(i)) {
						slot.set(hotbar.get(i));
					}
					i++;
				}
			}

			Map<Integer, ItemStack> grid = playerInventoryData.getGrid();

			if (!grid.isEmpty()) {
				i = 0;
				for (Inventory slot : inv.getMain().slots()) {
					if (grid.containsKey(i)) {
						slot.set(grid.get(i));
					}
					i++;
				}
			}

			Optional<ItemStack> helmet = playerInventoryData.getHelmet();

			if (helmet.isPresent()) {
				target.setHelmet(helmet.get());
			}

			Optional<ItemStack> chestPlate = playerInventoryData.getChestPlate();

			if (chestPlate.isPresent()) {
				target.setChestplate(chestPlate.get());
			}
			
			Optional<ItemStack> leggings = playerInventoryData.getLeggings();

			if (leggings.isPresent()) {
				target.setLeggings(leggings.get());
			}
			
			Optional<ItemStack> boots = playerInventoryData.getBoots();

			if (boots.isPresent()) {
				target.setBoots(boots.get());
			}

			Optional<ItemStack> offHand = playerInventoryData.getOffHand();

			if (offHand.isPresent()) {
				target.setItemInHand(HandTypes.OFF_HAND, offHand.get());
			}

			ConfigurationNode config = ConfigManager.get().getConfig();

			if (config.getNode("options", "health").getBoolean()) {
				target.offer(Keys.HEALTH, playerInventoryData.getHealth());
			}

			if (config.getNode("options", "hunger").getBoolean()) {
				target.offer(Keys.FOOD_LEVEL, playerInventoryData.getFood());
				target.offer(Keys.SATURATION, playerInventoryData.getSaturation());
			}

			if (config.getNode("options", "experience").getBoolean()) {
				target.offer(Keys.EXPERIENCE_LEVEL, playerInventoryData.getExpLevel());
				target.offer(Keys.TOTAL_EXPERIENCE, playerInventoryData.getExperience());
			}	
		}
	}

}
