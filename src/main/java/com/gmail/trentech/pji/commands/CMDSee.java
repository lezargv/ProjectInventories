package com.gmail.trentech.pji.commands;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.service.InventoryData;
import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.settings.PlayerSettings;
import com.gmail.trentech.pji.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDSee implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;

		Player target = args.<Player>getOne("player").get();

		String name = args.<String>getOne("inv").get().toUpperCase();

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		PlayerSettings playerSettings = inventoryService.getPlayerSettings();

		InventoryData inventoryData;

		if (playerSettings.get(target).equals(name)) {
			inventoryData = inventoryService.copy(target);
			inventoryService.save(target, inventoryData);
		} else {
			Optional<InventoryData> optionalInventoryData = inventoryService.get(target, name);

			if (optionalInventoryData.isPresent()) {
				inventoryData = optionalInventoryData.get();
			} else {
				inventoryData = inventoryService.empty(name);
				inventoryService.save(target, inventoryData);
			}
		}

		Inventory inventory = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).property(InventoryDimension.PROPERTY_NAM, new InventoryDimension(9, 5)).property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(target.getName()))).listener(InteractInventoryEvent.Close.class, (event) -> {
			int i = 0;
			for (Inventory slot : event.getTargetInventory().slots()) {
				if (i < 27) {
					Optional<ItemStack> optionalItem = slot.peek();

					if (optionalItem.isPresent()) {
						inventoryData.addGrid(i, optionalItem.get());
					} else {
						inventoryData.removeGrid(i);
					}
				} else if (i < 36) {
					Optional<ItemStack> optionalItem = slot.peek();

					if (optionalItem.isPresent()) {
						inventoryData.addHotbar(i - 27, optionalItem.get());
					} else {
						inventoryData.removeHotbar(i - 27);
					}
				} else {
					Optional<ItemStack> optionalItem = slot.peek();

					if (optionalItem.isPresent()) {
						inventoryData.addEquipment(i - 36, optionalItem.get());
					} else {
						inventoryData.removeEquipment(i - 36);
					}
				}

				i++;
			}
			inventoryService.save(target, inventoryData);

			if (playerSettings.get(target).equals(name)) {
				player.getInventory().clear();

				PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

				Map<Integer, ItemStack> hotbar = inventoryData.getHotbar();

				if (!hotbar.isEmpty()) {
					i = 0;
					for (Inventory slot : inv.getHotbar().slots()) {
						if (hotbar.containsKey(i)) {
							slot.set(hotbar.get(i));
						}
						i++;
					}
				}

				Map<Integer, ItemStack> grid = inventoryData.getGrid();

				if (!grid.isEmpty()) {
					i = 0;
					for (Inventory slot : inv.getMain().slots()) {
						if (grid.containsKey(i)) {
							slot.set(grid.get(i));
						}
						i++;
					}
				}

				Map<Integer, ItemStack> equipment = inventoryData.getEquipment();

				if (!equipment.isEmpty()) {
					i = 0;
					for (Inventory slot : inv.getEquipment().slots()) {
						if (equipment.containsKey(i)) {
							slot.set(equipment.get(i));
						}
						i++;
					}
				}

				Optional<ItemStack> offHand = inventoryData.getOffHand();

				if (offHand.isPresent()) {
					player.setItemInHand(HandTypes.OFF_HAND, offHand.get());
				}

				ConfigurationNode config = ConfigManager.get().getConfig();

				if (config.getNode("options", "health").getBoolean()) {
					player.offer(Keys.HEALTH, inventoryData.getHealth());
				}

				if (config.getNode("options", "hunger").getBoolean()) {
					player.offer(Keys.FOOD_LEVEL, inventoryData.getFood());
					player.offer(Keys.SATURATION, inventoryData.getSaturation());
				}

				if (config.getNode("options", "experience").getBoolean()) {
					player.offer(Keys.EXPERIENCE_LEVEL, inventoryData.getExpLevel());
					player.offer(Keys.TOTAL_EXPERIENCE, inventoryData.getExperience());
				}	
			}
		}).build(Main.getPlugin());

		Map<Integer, ItemStack> grid = inventoryData.getGrid();
		Map<Integer, ItemStack> hotbar = inventoryData.getHotbar();
		Map<Integer, ItemStack> equipment = inventoryData.getEquipment();

		int i = 0;
		for (Inventory slot : inventory.slots()) {
			if (i < 27) {
				if (grid.containsKey(i)) {
					slot.set(grid.get(i));
				}
			} else if (i < 36) {
				if (hotbar.containsKey(i - 27)) {
					slot.set(hotbar.get(i - 27));
				}
			} else {
				if (equipment.containsKey(i - 36)) {
					slot.set(equipment.get(i - 36));
				}
			}

			i++;
		}

		player.openInventory(inventory, Cause.of(NamedCause.simulated(player)));

		return CommandResult.success();
	}
}
