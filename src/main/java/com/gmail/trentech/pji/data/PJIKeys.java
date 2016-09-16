package com.gmail.trentech.pji.data;

import java.util.Map;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStack;

import com.google.common.reflect.TypeToken;

public class PJIKeys {

	private static final TypeToken<Map<Integer, ItemStack>> MAP_INV = new TypeToken<Map<Integer, ItemStack>>() {
		private static final long serialVersionUID = 6711995583309149567L;
    };
	private static final TypeToken<MapValue<Integer, ItemStack>> VALUE_INV = new TypeToken<MapValue<Integer, ItemStack>>() {
		private static final long serialVersionUID = -8659236772851620684L;
    }; 
	private static final TypeToken<Value<Integer>> VALUE_INT = new TypeToken<Value<Integer>>() {
		private static final long serialVersionUID = 8397045307209728355L;
    };    
	private static final TypeToken<Value<Double>> VALUE_DOUBLE = new TypeToken<Value<Double>>() {
		private static final long serialVersionUID = 6686120503668717369L;
    };   
    
	public static final Key<MapValue<Integer, ItemStack>> GRID = KeyFactory.makeMapKey(MAP_INV, VALUE_INV, DataQuery.of("grid"), "pji:grid", "grid");
	public static final Key<MapValue<Integer, ItemStack>> HOTBAR = KeyFactory.makeMapKey(MAP_INV, VALUE_INV, DataQuery.of("hotbar"), "pji:hotbar", "hotbar");
	public static final Key<MapValue<Integer, ItemStack>> ARMOR = KeyFactory.makeMapKey(MAP_INV, VALUE_INV, DataQuery.of("armor"), "pji:armor", "armor");
	public static final Key<Value<Integer>> FOOD = KeyFactory.makeSingleKey(TypeToken.of(Integer.class), VALUE_INT, DataQuery.of("food"), "pji:food", "food");
	public static final Key<Value<Integer>> EXPERIENCE = KeyFactory.makeSingleKey(TypeToken.of(Integer.class), VALUE_INT, DataQuery.of("experience"), "pji:experience", "experience");
	public static final Key<Value<Integer>> EXPERIENCE_LEVEL = KeyFactory.makeSingleKey(TypeToken.of(Integer.class), VALUE_INT, DataQuery.of("expereince_level"), "pji:expereince_level", "expereince_level");
	public static final Key<Value<Double>> SATURATION = KeyFactory.makeSingleKey(TypeToken.of(Double.class), VALUE_DOUBLE, DataQuery.of("saturation"), "pji:saturation", "saturation");
	public static final Key<Value<Double>> HEALTH = KeyFactory.makeSingleKey(TypeToken.of(Double.class), VALUE_DOUBLE, DataQuery.of("healh"), "pji:healh", "healh");
	
}
