package com.gmail.trentech.pji.commands;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.PlayerData;

public class CMDSee implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) src;
		
		Player target = args.<Player> getOne("player").get();
		
		String name = args.<String> getOne("inv").get().toUpperCase();

		PlayerData playerData = new PlayerData(target, name).save();

		Inventory inventory = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryDimension.PROPERTY_NAM, new InventoryDimension(9, 5)).property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(target.getName()))).listener(InteractInventoryEvent.Close.class, (event) -> {		
					int i = 0;
					System.out.println("INTERACT");
					for (Inventory slot : event.getTargetInventory().slots()) {
						if(i < 27) {
							Optional<ItemStack> optionalItem = slot.peek();
							
							if(optionalItem.isPresent()) {
								playerData.addGrid(i, optionalItem.get());
							} else {
								playerData.removeGrid(i);
							}
						} else if(i < 36) {
							Optional<ItemStack> optionalItem = slot.peek();
							
							if(optionalItem.isPresent()) {
								playerData.addHotbar(i - 27, optionalItem.get());
							} else {
								playerData.removeHotbar(i - 27);
							}
						} else {
							Optional<ItemStack> optionalItem = slot.peek();
							
							if(optionalItem.isPresent()) {
								playerData.addEquipment(i - 36, optionalItem.get());
							} else {
								playerData.removeEquipment(i - 36);
							}
						}
						
						i++;
					}
					playerData.save();
					playerData.set();
				}).build(Main.getPlugin());

		Map<Integer, ItemStack> grid = playerData.getGrid();
		Map<Integer, ItemStack> hotbar = playerData.getHotbar();
		Map<Integer, ItemStack> equipment = playerData.getEquipment();

		int i = 0;
		
		for (Inventory slot : inventory.slots()) {
			if(i < 27) {
				if (grid.containsKey(i)) {
					slot.set(grid.get(i));
				}
			} else if(i < 36) {
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
