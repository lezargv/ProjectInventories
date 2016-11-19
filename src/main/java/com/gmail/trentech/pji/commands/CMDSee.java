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
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.settings.PlayerSettings;
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

		PlayerData playerData;

		if (playerSettings.getInventoryName(target).equals(inventoryData.getName())) {
			playerData = playerSettings.copy(target);
			playerSettings.save(target, playerData);
		} else {
			Optional<PlayerData> optionalPlayerData = playerSettings.get(target, inventoryData.getName());

			if (optionalPlayerData.isPresent()) {
				playerData = optionalPlayerData.get();
			} else {
				playerData = playerSettings.empty(inventoryData.getName());
				playerSettings.save(target, playerData);
			}
		}

		Inventory inventory = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryDimension.PROPERTY_NAM, new InventoryDimension(9, 5))
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(target.getName())))
				.listener(ClickInventoryEvent.class, (event) -> {
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

					if (playerSettings.getInventoryName(target).equals(inventoryData.getName())) {
						player.getInventory().clear();

						PlayerInventory inv = player.getInventory().query(PlayerInventory.class);

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
							player.setHelmet(helmet.get());
						}

						Optional<ItemStack> chestPlate = playerData.getChestPlate();

						if (chestPlate.isPresent()) {
							player.setChestplate(chestPlate.get());
						}
						
						Optional<ItemStack> leggings = playerData.getLeggings();

						if (leggings.isPresent()) {
							player.setLeggings(leggings.get());
						}
						
						Optional<ItemStack> boots = playerData.getBoots();

						if (boots.isPresent()) {
							player.setBoots(boots.get());
						}

						Optional<ItemStack> offHand = playerData.getOffHand();

						if (offHand.isPresent()) {
							player.setItemInHand(HandTypes.OFF_HAND, offHand.get());
						}

						ConfigurationNode config = ConfigManager.get().getConfig();

						if (config.getNode("options", "health").getBoolean()) {
							player.offer(Keys.HEALTH, playerData.getHealth());
						}

						if (config.getNode("options", "hunger").getBoolean()) {
							player.offer(Keys.FOOD_LEVEL, playerData.getFood());
							player.offer(Keys.SATURATION, playerData.getSaturation());
						}

						if (config.getNode("options", "experience").getBoolean()) {
							player.offer(Keys.EXPERIENCE_LEVEL, playerData.getExpLevel());
							player.offer(Keys.TOTAL_EXPERIENCE, playerData.getExperience());
						}	
					}
				}).listener(InteractInventoryEvent.Close.class, (event) -> {
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
		
					if (playerSettings.getInventoryName(target).equals(inventoryData.getName())) {
						player.getInventory().clear();
		
						PlayerInventory inv = player.getInventory().query(PlayerInventory.class);
		
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
							player.setHelmet(helmet.get());
						}

						Optional<ItemStack> chestPlate = playerData.getChestPlate();

						if (chestPlate.isPresent()) {
							player.setChestplate(chestPlate.get());
						}
						
						Optional<ItemStack> leggings = playerData.getLeggings();

						if (leggings.isPresent()) {
							player.setLeggings(leggings.get());
						}
						
						Optional<ItemStack> boots = playerData.getBoots();

						if (boots.isPresent()) {
							player.setBoots(boots.get());
						}
		
						Optional<ItemStack> offHand = playerData.getOffHand();
		
						if (offHand.isPresent()) {
							player.setItemInHand(HandTypes.OFF_HAND, offHand.get());
						}
		
						ConfigurationNode config = ConfigManager.get().getConfig();
		
						if (config.getNode("options", "health").getBoolean()) {
							player.offer(Keys.HEALTH, playerData.getHealth());
						}
		
						if (config.getNode("options", "hunger").getBoolean()) {
							player.offer(Keys.FOOD_LEVEL, playerData.getFood());
							player.offer(Keys.SATURATION, playerData.getSaturation());
						}
		
						if (config.getNode("options", "experience").getBoolean()) {
							player.offer(Keys.EXPERIENCE_LEVEL, playerData.getExpLevel());
							player.offer(Keys.TOTAL_EXPERIENCE, playerData.getExperience());
						}	
					}
				}).build(Main.getPlugin());

		Map<Integer, ItemStack> grid = playerData.getGrid();
		Map<Integer, ItemStack> hotbar = playerData.getHotbar();

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
				if (i - 36 == 0) {
					Optional<ItemStack> helmet = playerData.getHelmet();

					if (helmet.isPresent()) {
						slot.set(helmet.get());
					}
				} else if(i - 36 == 1) {
					Optional<ItemStack> chestPlate = playerData.getChestPlate();

					if (chestPlate.isPresent()) {
						slot.set(chestPlate.get());
					}
				} else if(i - 36 == 2) {
					Optional<ItemStack> leggings = playerData.getLeggings();

					if (leggings.isPresent()) {
						slot.set(leggings.get());
					}
				} else if(i - 36 == 3) {
					Optional<ItemStack> boots = playerData.getBoots();

					if (boots.isPresent()) {
						slot.set(boots.get());
					}
				}
			}

			i++;
		}

		player.openInventory(inventory, Cause.of(NamedCause.simulated(player)));

		return CommandResult.success();
	}
}
