package com.gmail.trentech.pji.commands;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.PlayerData;
import com.gmail.trentech.pji.settings.PlayerSettings;
import com.gmail.trentech.pji.utils.ClickHandler;

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
				.listener(ClickInventoryEvent.class, new ClickHandler(target, playerData, playerSettings))
				.build(Main.getPlugin());

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
				} else {
					slot.set(ItemStack.of(ItemTypes.BARRIER, 1));
				}
			}

			i++;
		}

		player.openInventory(inventory, Cause.of(NamedCause.simulated(player)));

		return CommandResult.success();
	}
}
