package com.github.kanesada2.SnowballGame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public final class Util {
	 private Util() {}

	 public static void causeKnockBack(Player player, Projectile projectile){
		 double health = player.getHealth();
			if(health <= 2){
				player.setHealth(health + 1);
				player.damage(1);
			} else{
				player.damage(1);
				player.setHealth(health);
			}
			Vector knockbackVec = projectile.getVelocity().multiply(0.6);
			if(knockbackVec.getY() < 0.3){
				knockbackVec.setY(0.3);
			}
			player.setVelocity(knockbackVec);
	 }
	 public static boolean isMyItem(ItemStack item){
		 if(!item.hasItemMeta()){
			 return false;
		 }
		 ItemMeta itemMeta = item.getItemMeta();
		 return itemMeta.hasLore() && itemMeta.getLore().contains("SnowballGame Item");
	 }
	 public static boolean isBall(ItemStack item){
		 return isMyItem(item) && item.getType() == Material.SNOW_BALL;
	 }
	 public static boolean isBat(ItemStack item){
		 return isMyItem(item) && item.getType() == Material.BOW;
	 }
	 public static boolean isGlove(ItemStack item){
		 return isMyItem(item) && item.getType() == Material.LEATHER;
	 }
	 public static ItemStack getBall(){
		 ItemStack ball = new ItemStack(Material.SNOW_BALL);
		 ItemMeta ballMeta = ball.getItemMeta();
		 List<String> lore = new ArrayList<String>();
		 lore.add("SnowballGame Item");
		 lore.add("Ball");
		 ballMeta.setLore(lore);
		 String name = SnowballGame.getPlugin(SnowballGame.class).getConfig().getString("Ball.Ball_Name");
		 ballMeta.setDisplayName(name);
		 ball.setItemMeta(ballMeta);
		 return ball;
	 }
	 public static ItemStack getBat(){
		 ItemStack bat = new ItemStack(Material.BOW);
		 ItemMeta batMeta = bat.getItemMeta();
		 List<String> lore = new ArrayList<String>();
		 lore.add("SnowballGame Item");
		 lore.add("Bat");
		 batMeta.setLore(lore);
		 String name = SnowballGame.getPlugin(SnowballGame.class).getConfig().getString("Bat.Bat_Name");
		 batMeta.setDisplayName(name);
		 bat.setItemMeta(batMeta);
		 return bat;
	 }
	 public static ItemStack getGlove(){
		 ItemStack glove = new ItemStack(Material.LEATHER);
		 ItemMeta gloveMeta = glove.getItemMeta();
		 List<String> lore = new ArrayList<String>();
		 lore.add("SnowballGame Item");
		 lore.add("Glove");
		 gloveMeta.setLore(lore);
		 String name = SnowballGame.getPlugin(SnowballGame.class).getConfig().getString("Glove.Glove_Name");
		 gloveMeta.setDisplayName(name);
		 glove.setItemMeta(gloveMeta);
		 return glove;
	 }
	 public static ShapedRecipe getBallRecipe(){
		 ItemStack ball = getBall();
		 ShapedRecipe ballRecipe = new ShapedRecipe(ball);
		 ballRecipe.shape("LLL","LBL","LLL");
		 ballRecipe.setIngredient('L', Material.LEATHER);
		 ballRecipe.setIngredient('B', Material.SNOW_BALL);
		 return ballRecipe;
	 }
	 public static ShapedRecipe getBatRecipe(){
		 ItemStack bat = getBat();
		 ShapedRecipe batRecipe = new ShapedRecipe(bat);
		 batRecipe.shape("  S"," S ","S  ");
		 batRecipe.setIngredient('S', Material.STICK);
		 return batRecipe;
	 }
	 public static ShapedRecipe getGloveRecipe(){
		 ItemStack glove = getGlove();
		 ShapedRecipe gloveRecipe = new ShapedRecipe(glove);
		 gloveRecipe.shape("LLL","LLL"," L ");
		 gloveRecipe.setIngredient('L', Material.LEATHER);
		 return gloveRecipe;
	 }
}
