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
import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.data.InventoryData;
import com.gmail.trentech.pji.service.data.InventoryPlayer;
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

		InventoryData inventoryData = args.<InventoryData>getOne("inv").get();

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);

		PlayerSettings playerSettings = inventoryService.getPlayerSettings();

		InventoryPlayer inventoryPlayer;

		if (playerSettings.getInventoryName(target).equals(inventoryData.getName())) {
			inventoryPlayer = playerSettings.copy(target);
			playerSettings.save(target, inventoryPlayer);
		} else {
			Optional<InventoryPlayer> optionalInventoryPlayer = playerSettings.get(target, inventoryData.getName());

			if (optionalInventoryPlayer.isPresent()) {
				inventoryPlayer = optionalInventoryPlayer.get();
			} else {
				inventoryPlayer = playerSettings.empty(inventoryData.getName());
				playerSettings.save(target, inventoryPlayer);
			}
		}

		Inventory inventory = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).property(InventoryDimension.PROPERTY_NAM, new InventoryDimension(9, 5)).property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(target.getName()))).listener(InteractInventoryEvent.Close.class, (event) -> {
			int i = 0;
			for (Inventory slot : event.getTargetInventory().slots()) {
				if (i < 27) {
					Optional<ItemStack> optionalItem = slot.peek();

					if (optionalItem.isPresent()) {
						inventoryPlayer.addGrid(i, optionalItem.get());
					} else {
						inventoryPlayer.removeGrid(i);
					}
				} else if (i < 36) {
					Optional<ItemStack> optionalItem = slot.peek();

					if (optionalItem.isPresent()) {
						inventoryPlayer.addHotbar(i - 27, optionalItem.get());
					} else {
						inventoryPlayer.removeHotbar(i - 27);
					}
				} else {
					Optional<ItemStack> optionalItem = slot.peek();

					if (optionalItem.isPresent()) {
						inventoryPlayer.addEquipment(i - 36, optionalItem.get());
					} else {
						inventoryPlayer.removeEquipment(i - 36);
					}
				}

				i++;
			}
			playerSettings.save(target, inventoryPlayer);

			if (playerSettings.getInventoryName(target).equals(inventoryData.getName())) {
				player.getInventory().clear();

				PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

				Map<Integer, ItemStack> hotbar = inventoryPlayer.getHotbar();

				if (!hotbar.isEmpty()) {
					i = 0;
					for (Inventory slot : inv.getHotbar().slots()) {
						if (hotbar.containsKey(i)) {
							slot.set(hotbar.get(i));
						}
						i++;
					}
				}

				Map<Integer, ItemStack> grid = inventoryPlayer.getGrid();

				if (!grid.isEmpty()) {
					i = 0;
					for (Inventory slot : inv.getMain().slots()) {
						if (grid.containsKey(i)) {
							slot.set(grid.get(i));
						}
						i++;
					}
				}

				Map<Integer, ItemStack> equipment = inventoryPlayer.getEquipment();

				if (!equipment.isEmpty()) {
					i = 0;
					for (Inventory slot : inv.getEquipment().slots()) {
						if (equipment.containsKey(i)) {
							slot.set(equipment.get(i));
						}
						i++;
					}
				}

				Optional<ItemStack> offHand = inventoryPlayer.getOffHand();

				if (offHand.isPresent()) {
					player.setItemInHand(HandTypes.OFF_HAND, offHand.get());
				}

				ConfigurationNode config = ConfigManager.get().getConfig();

				if (config.getNode("options", "health").getBoolean()) {
					player.offer(Keys.HEALTH, inventoryPlayer.getHealth());
				}

				if (config.getNode("options", "hunger").getBoolean()) {
					player.offer(Keys.FOOD_LEVEL, inventoryPlayer.getFood());
					player.offer(Keys.SATURATION, inventoryPlayer.getSaturation());
				}

				if (config.getNode("options", "experience").getBoolean()) {
					player.offer(Keys.EXPERIENCE_LEVEL, inventoryPlayer.getExpLevel());
					player.offer(Keys.TOTAL_EXPERIENCE, inventoryPlayer.getExperience());
				}	
			}
		}).build(Main.getPlugin());

		Map<Integer, ItemStack> grid = inventoryPlayer.getGrid();
		Map<Integer, ItemStack> hotbar = inventoryPlayer.getHotbar();
		Map<Integer, ItemStack> equipment = inventoryPlayer.getEquipment();

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
