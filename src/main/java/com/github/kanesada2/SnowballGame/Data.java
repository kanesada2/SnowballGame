package com.github.kanesada2.SnowballGame;

import java.util.HashSet;

import org.bukkit.Material;

public class Data {
	private static HashSet<Material> regardUp;
	private static HashSet<Material> noRepel;
	public Data() {
		regardUp = new HashSet<Material>();
		noRepel = new HashSet<Material>();
		regardUp.add(Material.ACTIVATOR_RAIL);
		regardUp.add(Material.BLACK_CARPET);
		regardUp.add(Material.BLUE_CARPET);
		regardUp.add(Material.BROWN_CARPET);
		regardUp.add(Material.CYAN_CARPET);
		regardUp.add(Material.GRAY_CARPET);
		regardUp.add(Material.GREEN_CARPET);
		regardUp.add(Material.LIGHT_BLUE_CARPET);
		regardUp.add(Material.LIGHT_GRAY_CARPET);
		regardUp.add(Material.LIME_CARPET);
		regardUp.add(Material.MAGENTA_CARPET);
		regardUp.add(Material.ORANGE_CARPET);
		regardUp.add(Material.PINK_CARPET);
		regardUp.add(Material.PURPLE_CARPET);
		regardUp.add(Material.YELLOW_CARPET);
		regardUp.add(Material.WHITE_CARPET);
		regardUp.add(Material.DAYLIGHT_DETECTOR);
		regardUp.add(Material.DETECTOR_RAIL);
		regardUp.add(Material.REPEATER);
		regardUp.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
		regardUp.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
		regardUp.add(Material.STONE_PRESSURE_PLATE);
		regardUp.add(Material.ACACIA_PRESSURE_PLATE);
		regardUp.add(Material.BIRCH_PRESSURE_PLATE);
		regardUp.add(Material.DARK_OAK_PRESSURE_PLATE);
		regardUp.add(Material.JUNGLE_PRESSURE_PLATE);
		regardUp.add(Material.OAK_PRESSURE_PLATE);
		regardUp.add(Material.SPRUCE_PRESSURE_PLATE);
		regardUp.add(Material.POWERED_RAIL);
		regardUp.add(Material.RAIL);
		regardUp.add(Material.COMPARATOR);
		regardUp.add(Material.REDSTONE_WIRE);
		regardUp.add(Material.SNOW);


		noRepel.add(Material.AIR);
		noRepel.add(Material.DEAD_BUSH);
		noRepel.add(Material.END_ROD);
		noRepel.add(Material.TALL_GRASS);
		noRepel.add(Material.GRASS);
		noRepel.add(Material.REDSTONE_TORCH);
		noRepel.add(Material.POPPY);
		noRepel.add(Material.BLUE_ORCHID);
		noRepel.add(Material.ALLIUM);
		noRepel.add(Material.AZURE_BLUET);
		noRepel.add(Material.RED_TULIP);
		noRepel.add(Material.ORANGE_TULIP);
		noRepel.add(Material.WHITE_TULIP);
		noRepel.add(Material.OXEYE_DAISY);
		noRepel.add(Material.ACACIA_SAPLING);
		noRepel.add(Material.DARK_OAK_SAPLING);
		noRepel.add(Material.JUNGLE_SAPLING);
		noRepel.add(Material.OAK_SAPLING);
		noRepel.add(Material.SPRUCE_SAPLING);
		noRepel.add(Material.TORCH);
		noRepel.add(Material.TRIPWIRE);
		noRepel.add(Material.COBWEB);
	}
	public static HashSet <Material> regardUpList(){
		return regardUp;
	}
	public static HashSet <Material> noRepelList(){
		return noRepel;
	}
}
