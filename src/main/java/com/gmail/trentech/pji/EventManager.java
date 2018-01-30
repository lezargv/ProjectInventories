package com.gmail.trentech.pji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pji.data.EnderChestData;
import com.gmail.trentech.pji.data.InventoryData;
import com.gmail.trentech.pji.data.PlayerChestData;
import com.gmail.trentech.pji.settings.ChestSettings;
import com.gmail.trentech.pji.settings.InventorySettings;
import com.gmail.trentech.pji.settings.PlayerSettings;
import com.gmail.trentech.pji.settings.WorldSettings;

import ninja.leaping.configurate.ConfigurationNode;

public class EventManager {

	private static List<UUID> list = new ArrayList<>();

	@Listener(order = Order.PRE)
	public void onInteractBlockEvent(InteractBlockEvent.Secondary event, @Root Player player) {
		ConfigurationNode config = ConfigManager.get(Main.getPlugin()).getConfig();

		if (!config.getNode("options", "per-world-enderchests").getBoolean()) {
			return;
		}
		
		BlockType blockType = event.getTargetBlock().getState().getType();

		if(!blockType.equals(BlockTypes.ENDER_CHEST)) {
			return;
		}		
		
		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);

		ChestSettings chestSettings = inventoryService.getChestSettings();	
		PlayerChestData playerChestData = chestSettings.get(player);
		Map<UUID, EnderChestData> chests = playerChestData.getChests();
		
		UUID world = player.getWorld().getUniqueId();
		
		EnderChestData enderChestData;
		
		if(chests.containsKey(world)) {
			enderChestData = playerChestData.getChests().get(world);
		} else {
			enderChestData = new EnderChestData(player.getUniqueId(), world, new HashMap<>());
			
			playerChestData.addChest(world, enderChestData);
			
			chestSettings.save(player, playerChestData);
		}

		Inventory inventory = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 5))
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("Ender Chest")))
				.listener(ClickInventoryEvent.class, (e) -> {
					int i = 0;
					for (Inventory slot : e.getTargetInventory().slots()) {
						Optional<ItemStack> optionalItem = slot.peek();

						if (optionalItem.isPresent()) {
							enderChestData.addItem(i, optionalItem.get());
						} else {
							enderChestData.removeItem(i);
						}
						i++;
					}
					
					playerChestData.addChest(world, enderChestData);
					
					chestSettings.save(player, playerChestData);
				}).listener(InteractInventoryEvent.Close.class, (e) -> {
					int i = 0;
					for (Inventory slot : e.getTargetInventory().slots()) {
						Optional<ItemStack> optionalItem = slot.peek();

						if (optionalItem.isPresent()) {
							enderChestData.addItem(i, optionalItem.get());
						} else {
							enderChestData.removeItem(i);
						}
						i++;
					}
					
					playerChestData.addChest(world, enderChestData);
					
					chestSettings.save(player, playerChestData);
				})
				.build(Main.getPlugin());

		Map<Integer, ItemStack> grid = enderChestData.getInventory();

		int i = 0;
		for (Inventory slot : inventory.slots()) {
			if (grid.containsKey(i)) {
				slot.set(grid.get(i));
			}
			i++;
		}
		event.setCancelled(true);
		player.openInventory(inventory);
	}
	
	@Listener(order = Order.POST)
	public void ClientConnectionEventJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
		list.add(player.getUniqueId());
		
		long delay = 0;
		
		ConfigurationNode config = ConfigManager.get(Main.getPlugin()).getConfig();

		if(ConfigManager.get(com.gmail.trentech.pjc.Main.getPlugin()).getConfig().getNode("settings", "sql", "enable").getBoolean()) {
			delay = config.getNode("settings", "sql", "login-delay").getLong();
		}

		Task.builder().async().delayTicks(delay).execute(t -> {
			InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
			
			InventorySettings inventorySettings = inventoryService.getInventorySettings();
			PlayerSettings playerSettings = inventoryService.getPlayerSettings();

			InventoryData inventoryData = inventorySettings.get(playerSettings.getPlayerData(player).getInventoryName()).get();

			Optional<GameMode> optionalGamemode = inventoryData.getGamemode();

			if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
				player.offer(Keys.GAME_MODE, optionalGamemode.get());
			}

			playerSettings.set(player, inventoryData, true);
			
			list.remove(player.getUniqueId());
		}).submit(Main.getPlugin());
	}
	
	@Listener(order = Order.FIRST)
	public void onDamageEntityEvent(DamageEntityEvent event, @Getter("getTargetEntity") Player player) {
		if(!list.contains(player.getUniqueId())) {
			return;
		}
		
		event.setCancelled(true);
	}

	@Listener(order = Order.FIRST)
	public void onAffectSlotEvent(AffectSlotEvent event, @Root Player player) {
		if(!list.contains(player.getUniqueId())) {
			return;
		}
		
		player.sendMessage(Text.of(TextColors.GOLD, "Please wait while inventory is updated!"));
		
		event.setCancelled(true);
	}
	
	@Listener(order = Order.FIRST)
	public void onDropItemEvent(DropItemEvent event, @Root Player player) {
		if(!list.contains(player.getUniqueId())) {
			return;
		}
		
		player.sendMessage(Text.of(TextColors.GOLD, "Please wait while inventory is updated!"));
		
		event.setCancelled(true);
	}
	
	@Listener(order = Order.FIRST)
	public void onInteractInventoryEvent(InteractInventoryEvent event, @Root Player player) {
		if(!list.contains(player.getUniqueId())) {
			return;
		}
		
		player.sendMessage(Text.of(TextColors.GOLD, "Please wait while inventory is updated!"));
		
		event.setCancelled(true);
	}
	
	@Listener(order = Order.FIRST)
	public void onInteractItemEvent(InteractItemEvent event, @Root Player player) {
		if(!list.contains(player.getUniqueId())) {
			return;
		}
		
		player.sendMessage(Text.of(TextColors.GOLD, "Please wait while inventory is updated!"));
		
		event.setCancelled(true);
	}
	
	@Listener(order = Order.FIRST)
	public void onUseItemStackEvent(UseItemStackEvent event, @Root Player player) {
		if(!list.contains(player.getUniqueId())) {
			return;
		}
		
		player.sendMessage(Text.of(TextColors.GOLD, "Please wait while inventory is updated!"));
		
		event.setRemainingDuration(event.getOriginalRemainingDuration());
	}
	
	@Listener(order = Order.PRE)
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		PlayerSettings playerSettings = inventoryService.getPlayerSettings();
		
		playerSettings.save(player, playerSettings.copy(player));
		
		Task.builder().async().delayTicks(40).execute(t -> {
			player.getInventory().clear();
		}).submit(Main.getPlugin());
	}

	@Listener
	public void onSaveWorldEvent(SaveWorldEvent event) {
		Predicate<Entity> filter = new Predicate<Entity>() {

			@Override
			public boolean test(Entity entity) {
				return entity instanceof Player;
			}
		};

		for (Entity entity : event.getTargetWorld().getEntities(filter)) {
			Player player = (Player) entity;

			InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
			
			PlayerSettings playerSettings = inventoryService.getPlayerSettings();
			
			playerSettings.save(player, playerSettings.copy(player));
		}
	}

	@Listener
	public void onRespawnPlayerEvent(RespawnPlayerEvent event, @Getter("getTargetEntity") Player player) {
		WorldProperties from = event.getFromTransform().getExtent().getProperties();
		WorldProperties to = event.getToTransform().getExtent().getProperties();

		if (from.equals(to)) {
			return;
		}

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		WorldSettings worldSettings = inventoryService.getWorldSettings();
		PlayerSettings playerSettings = inventoryService.getPlayerSettings();		
		InventorySettings inventorySettings = inventoryService.getInventorySettings();

		if (worldSettings.get(to).contains(playerSettings.getPlayerData(player).getInventoryName()) && !ConfigManager.get(Main.getPlugin()).getConfig().getNode("options", "default-on-world-change").getBoolean()) {
			return;
		}

		playerSettings.save(player, playerSettings.copy(player));
		
		InventoryData inventoryData = inventorySettings.get(worldSettings.get(to).getDefault()).get();

		Optional<GameMode> optionalGamemode = inventoryData.getGamemode();

		if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
			player.offer(Keys.GAME_MODE, optionalGamemode.get());
		}

		playerSettings.set(player, inventoryData, false);
	}

	@Listener(order = Order.POST)
	public void onMoveEntityEventTeleport(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player player) {
		WorldProperties from = event.getFromTransform().getExtent().getProperties();
		WorldProperties to = event.getToTransform().getExtent().getProperties();

		if (from.equals(to)) {
			return;
		}

		InventoryService inventoryService = Sponge.getServiceManager().provideUnchecked(InventoryService.class);
		
		WorldSettings worldSettings = inventoryService.getWorldSettings();
		PlayerSettings playerSettings = inventoryService.getPlayerSettings();
		InventorySettings inventorySettings = inventoryService.getInventorySettings();
		
		if (worldSettings.get(to).contains(playerSettings.getPlayerData(player).getInventoryName()) && !ConfigManager.get(Main.getPlugin()).getConfig().getNode("options", "default-on-world-change").getBoolean()) {
			return;
		}

		playerSettings.save(player, playerSettings.copy(player));

		InventoryData inventoryData = inventorySettings.get(worldSettings.get(to).getDefault()).get();

		Optional<GameMode> optionalGamemode = inventoryData.getGamemode();

		if (optionalGamemode.isPresent() && !player.hasPermission("pji.override.gamemode")) {
			player.offer(Keys.GAME_MODE, optionalGamemode.get());
		}

		playerSettings.set(player, inventoryData, false);
	}
}
