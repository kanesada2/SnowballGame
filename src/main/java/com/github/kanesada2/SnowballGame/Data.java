package com.github.kanesada2.SnowballGame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class Data {
	private Data() {}
	public static List <Material> noRepelList(){
		List <Material> excluded = new ArrayList <Material>();
		excluded.add(Material.ACTIVATOR_RAIL);
		excluded.add(Material.AIR);
		excluded.add(Material.BREWING_STAND);
		excluded.add(Material.CARPET);
		excluded.add(Material.DAYLIGHT_DETECTOR);
		excluded.add(Material.DAYLIGHT_DETECTOR_INVERTED);
		excluded.add(Material.DEAD_BUSH);
		excluded.add(Material.DETECTOR_RAIL);
		excluded.add(Material.DIODE_BLOCK_OFF);
		excluded.add(Material.DIODE_BLOCK_ON);
		excluded.add(Material.END_ROD);
		excluded.add(Material.GOLD_PLATE);
		excluded.add(Material.IRON_PLATE);
		excluded.add(Material.LONG_GRASS);
		excluded.add(Material.POWERED_RAIL);
		excluded.add(Material.RAILS);
		excluded.add(Material.REDSTONE_COMPARATOR_OFF);
		excluded.add(Material.REDSTONE_COMPARATOR_ON);
		excluded.add(Material.REDSTONE_TORCH_OFF);
		excluded.add(Material.REDSTONE_TORCH_ON);
		excluded.add(Material.REDSTONE_WIRE);
		excluded.add(Material.RED_ROSE);
		excluded.add(Material.SAPLING);
		excluded.add(Material.SNOW);
		excluded.add(Material.STONE_PLATE);
		excluded.add(Material.TORCH);
		excluded.add(Material.VINE);
		excluded.add(Material.WEB);
		excluded.add(Material.WOOD_PLATE);
		excluded.add(Material.YELLOW_FLOWER);
		return excluded;
	}
}
