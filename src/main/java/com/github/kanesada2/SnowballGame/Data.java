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
		regardUp.add(Material.CARPET);
		regardUp.add(Material.DAYLIGHT_DETECTOR);
		regardUp.add(Material.DAYLIGHT_DETECTOR_INVERTED);
		regardUp.add(Material.DETECTOR_RAIL);
		regardUp.add(Material.DIODE_BLOCK_OFF);
		regardUp.add(Material.DIODE_BLOCK_ON);
		regardUp.add(Material.GOLD_PLATE);
		regardUp.add(Material.IRON_PLATE);;
		regardUp.add(Material.POWERED_RAIL);
		regardUp.add(Material.RAILS);
		regardUp.add(Material.REDSTONE_COMPARATOR_OFF);
		regardUp.add(Material.REDSTONE_COMPARATOR_ON);
		regardUp.add(Material.REDSTONE_WIRE);
		regardUp.add(Material.SNOW);
		regardUp.add(Material.STONE_PLATE);
		regardUp.add(Material.WOOD_PLATE);

		noRepel.add(Material.AIR);
		noRepel.add(Material.DEAD_BUSH);
		noRepel.add(Material.END_ROD);
		noRepel.add(Material.LONG_GRASS);
		noRepel.add(Material.REDSTONE_TORCH_OFF);
		noRepel.add(Material.REDSTONE_TORCH_ON);
		noRepel.add(Material.RED_ROSE);
		noRepel.add(Material.SAPLING);
		noRepel.add(Material.TORCH);
		noRepel.add(Material.TRIPWIRE);
		noRepel.add(Material.WEB);
		noRepel.add(Material.YELLOW_FLOWER);
	}
	public static HashSet <Material> regardUpList(){
		return regardUp;
	}
	public static HashSet <Material> noRepelList(){
		return noRepel;
	}
}
