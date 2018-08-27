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
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pji.InventoryService;
import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.PlayerInventoryData;
import com.gmail.trentech.pji.settings.PlayerSettings;
import com.gmail.trentech.pji.utils.ClickHandler;

public class CMDSee implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("inventory see").get();
		
		if (args.hasAny("help")) {			
			help.execute(src);
			return CommandResult.empty();
		}
		
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;

		if (!args.hasAny("player")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		Player target = args.<Player>getOne("player").get();

		if (!args.hasAny("inv")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		InventoryData inventoryData = args.<InventoryData>getOne("inv").get();

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);

		PlayerSettings playerSettings = inventoryService.getPlayerSettings();

		PlayerInventoryData playerInventoryData;

		if (playerSettings.getPlayerData(target).getInventoryName().equals(inventoryData.getName())) {
			playerInventoryData = playerSettings.copy(target);
			playerSettings.save(target, playerInventoryData);
		} else {
			Optional<PlayerInventoryData> optionalPlayerInventoryData = playerSettings.get(target, inventoryData.getName());

			if (optionalPlayerInventoryData.isPresent()) {
				playerInventoryData = optionalPlayerInventoryData.get();
			} else {
				playerInventoryData = playerSettings.empty(inventoryData.getName());
				playerSettings.save(target, playerInventoryData);
			}
		}

		Inventory inventory = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryDimension.of(9, 5))
				.property(InventoryTitle.of(Text.of(target.getName())))
				.listener(ClickInventoryEvent.class, new ClickHandler(target, playerInventoryData, playerSettings))
				.build(Main.getPlugin());

		Map<Integer, ItemStack> grid = playerInventoryData.getGrid();
		Map<Integer, ItemStack> hotbar = playerInventoryData.getHotbar();

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
					slot.set(playerInventoryData.getHelmet());
				} else if(i - 36 == 1) {
					slot.set(playerInventoryData.getChestPlate());
				} else if(i - 36 == 2) {
					slot.set(playerInventoryData.getLeggings());
				} else if(i - 36 == 3) {
					slot.set(playerInventoryData.getBoots());
				} else if(i - 36 == 4) {
					slot.set(playerInventoryData.getOffHand());
				} else {
					slot.set(ItemStack.of(ItemTypes.BARRIER, 1));
				}
			}

			i++;
		}

		player.openInventory(inventory);

		return CommandResult.success();
	}
}
