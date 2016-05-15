package com.gmail.trentech.pji.data.inventory;

import static com.gmail.trentech.pji.data.DataQueries.ARMOR;
import static com.gmail.trentech.pji.data.DataQueries.EXPERIENCE;
import static com.gmail.trentech.pji.data.DataQueries.EXP_LEVEL;
import static com.gmail.trentech.pji.data.DataQueries.FOOD;
import static com.gmail.trentech.pji.data.DataQueries.HEALTH;
import static com.gmail.trentech.pji.data.DataQueries.HOTBAR;
import static com.gmail.trentech.pji.data.DataQueries.INVENTORY;
import static com.gmail.trentech.pji.data.DataQueries.SATURATION;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class InventoryBuilder extends AbstractDataBuilder<Inventory> {

    public InventoryBuilder() {
        super(Inventory.class, 1);
    }

    @SuppressWarnings("unchecked")
	@Override
    protected Optional<Inventory> buildContent(DataView container) throws InvalidDataException {
        if (container.contains(HOTBAR, INVENTORY, ARMOR, HEALTH, FOOD, SATURATION, EXP_LEVEL, EXPERIENCE)) {
        	Map<String, String> hotbar = ((Map<String, String>) container.getMap(HOTBAR).get());        	
        	Map<String, String> grid = ((Map<String, String>) container.getMap(INVENTORY).get());        	
        	Map<String, String> armor = ((Map<String, String>) container.getMap(ARMOR).get());
  
        	double health = container.getDouble(HEALTH).get();
        	int food = container.getInt(FOOD).get();
        	double saturation = container.getDouble(SATURATION).get();
        	int expLevel = container.getInt(EXP_LEVEL).get();
        	int experience = container.getInt(EXPERIENCE).get();
        	
        	Inventory inventory = new Inventory(hotbar, grid, armor, health, food, saturation, expLevel, experience);
        	return Optional.of(inventory);
        }
        return Optional.empty();
    }
}
