package com.github.kanesada2.SnowballGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public final class Util {
	private static SnowballGame plugin = SnowballGame.getPlugin(SnowballGame.class);
	private Util() {
	 }

	 public static void causeKnockBack(Player player, Projectile projectile){
		 double health = player.getHealth();
			if(health <= 2){
				player.setHealth(health + 1);
				player.damage(1);
			} else{
				player.damage(1);
				player.setHealth(health);
			}
			Vector knockbackVec = projectile.getVelocity().multiply(0.5);
			if(knockbackVec.getY() < 0.3){
				knockbackVec.setY(0.3);
			}
			player.setVelocity(knockbackVec);
	 }
	 public static String addColors(String input) {
	        input = input.replaceAll("\\Q[[BLACK]]\\E", ChatColor.BLACK.toString());
	        input = input.replaceAll("\\Q[[DARK_BLUE]]\\E", ChatColor.DARK_BLUE.toString());
	        input = input.replaceAll("\\Q[[DARK_GREEN]]\\E", ChatColor.DARK_GREEN.toString());
	        input = input.replaceAll("\\Q[[DARK_AQUA]]\\E", ChatColor.DARK_AQUA.toString());
	        input = input.replaceAll("\\Q[[DARK_RED]]\\E", ChatColor.DARK_RED.toString());
	        input = input.replaceAll("\\Q[[DARK_PURPLE]]\\E", ChatColor.DARK_PURPLE.toString());
	        input = input.replaceAll("\\Q[[GOLD]]\\E", ChatColor.GOLD.toString());
	        input = input.replaceAll("\\Q[[GRAY]]\\E", ChatColor.GRAY.toString());
	        input = input.replaceAll("\\Q[[DARK_GRAY]]\\E", ChatColor.DARK_GRAY.toString());
	        input = input.replaceAll("\\Q[[BLUE]]\\E", ChatColor.BLUE.toString());
	        input = input.replaceAll("\\Q[[GREEN]]\\E", ChatColor.GREEN.toString());
	        input = input.replaceAll("\\Q[[AQUA]]\\E", ChatColor.AQUA.toString());
	        input = input.replaceAll("\\Q[[RED]]\\E", ChatColor.RED.toString());
	        input = input.replaceAll("\\Q[[LIGHT_PURPLE]]\\E", ChatColor.LIGHT_PURPLE.toString());
	        input = input.replaceAll("\\Q[[YELLOW]]\\E", ChatColor.YELLOW.toString());
	        input = input.replaceAll("\\Q[[WHITE]]\\E", ChatColor.WHITE.toString());
	        input = input.replaceAll("\\Q[[BOLD]]\\E", ChatColor.BOLD.toString());
	        input = input.replaceAll("\\Q[[UNDERLINE]]\\E", ChatColor.UNDERLINE.toString());
	        input = input.replaceAll("\\Q[[ITALIC]]\\E", ChatColor.ITALIC.toString());
	        input = input.replaceAll("\\Q[[STRIKE]]\\E", ChatColor.STRIKETHROUGH.toString());
	        input = input.replaceAll("\\Q[[MAGIC]]\\E", ChatColor.MAGIC.toString());
	        input = input.replaceAll("\\Q[[RESET]]\\E", ChatColor.RESET.toString());
	        return input;
	 }
	 public static void broadcastRange(Entity sender, String msg, int range){
		 range *= range;
	     Location location = sender.getLocation();
	     List<Player> players = sender.getWorld().getPlayers();
	     List<Player> recievers = new ArrayList<Player>();
	     for(Player player : players){
	    	 if(plugin.notifyDisabled.contains(player.getUniqueId())){
	       		continue;
	       	 }
	       	 recievers.add(player);
	     }
	     for(Player player : recievers){
	       	if(range > 0 && location.distanceSquared(player.getLocation()) > range){
	       		continue;
	       	}
	         player.sendMessage(msg);
	     }
	 }
	 public static boolean doesRegardUp(Block block){
		 HashSet <Material> excluded = Data.regardUpList();
		return excluded.contains(block.getType());
	 }
	 public static boolean doesRepel(Block block){
		 HashSet <Material> excluded = Data.noRepelList();
		 return !(block.isLiquid() || excluded.contains(block.getType()));
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
	 public static boolean isUmpire(ItemStack item){
		 return isMyItem(item) && item.getType() == Material.QUARTZ_BLOCK;
	 }
	 public static boolean isBase(ItemStack item){
		 return isMyItem(item) && item.getType() ==Material.STEP && item.getDurability() == 7;
	 }
	 public static boolean isCoach(ItemStack item){
		 return isMyItem(item) && item.getType() == Material.ARMOR_STAND;
	 }

	 public static boolean isEntityCoach(Entity entity){
		 return entity instanceof ArmorStand && entity.getCustomName() != null && entity.getCustomName().equalsIgnoreCase(plugin.getConfig().getString("Coach.Coach_Name", "Coach"));
	 }

	 public static boolean isUmpireMarker(Entity entity){
		return entity instanceof ArmorStand && isUmpire(((ArmorStand)entity).getBoots());
	 }

	 public static boolean isBaseMarker(Entity entity){
		return entity instanceof ArmorStand && isBase(((ArmorStand)entity).getBoots());
	 }

	 public static boolean isMyMarker(Entity entity){
		 return isUmpireMarker(entity) || isBaseMarker(entity);
	 }

	 public static void setUpBase(Block block, ItemStack item, boolean isUmpire){
		if(! item.hasItemMeta()) return;
		String enabledPath = "Base.Enabled_Base";
		String namePath = "Base.Base_Name";
		Location loc = block.getLocation().add(0.5, 0.5, 0.5);
		ItemStack label = getBase();
		if(isUmpire){
			enabledPath = "Umpire.Enabled_Umpire";
			namePath = "Umpire_Umpire_Name";
			loc.add(0, 0.5, 0);
			label = getUmpire();
		}
		if(!plugin.getConfig().getBoolean(enabledPath)) return;
		ArmorStand marker = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		String name = plugin.getConfig().getString(namePath);
		if(item.getItemMeta().hasDisplayName()){
			name = item.getItemMeta().getDisplayName();
		}
		marker.setCustomName(name);
		marker.setCustomNameVisible(true);
		marker.setVisible(false);
		marker.setCollidable(false);
		marker.setInvulnerable(true);
		marker.setMarker(true);
		marker.setGravity(false);

		String labelLoc = block.getLocation().toVector().toString();
		labelLoc = labelLoc.substring(0, labelLoc.length() -1);
		ItemMeta meta = label.getItemMeta();
		meta.setDisplayName(labelLoc);
		label.setItemMeta(meta);
		marker.setBoots(label);
	 }

	 public static ItemStack getBall(String type){
		 ItemStack ball = new ItemStack(Material.SNOW_BALL);
		 ItemMeta ballMeta = ball.getItemMeta();
		 String name = plugin.getConfig().getString("Ball.Ball_Name");
		 List<String> lore = new ArrayList<String>();
		 lore.add("SnowballGame Item");
		 lore.add("Ball");
		 switch(type){
			 case "highest":
				 lore.add("Highest-repulsion");
				 name = plugin.getConfig().getString("Ball.Repulsion.Highest");
				 break;
			 case "higher":
				 lore.add("Higher-repulsion");
				 name = plugin.getConfig().getString("Ball.Repulsion.Higher");
				 break;
			 case "lower":
				 lore.add("Lower-repulsion");
				 name = plugin.getConfig().getString("Ball.Repulsion.Lower");
				 break;
			 case "lowest":
				 lore.add("Lowest-repulsion");
				 name = plugin.getConfig().getString("Ball.Repulsion.Lowest");
				 break;
		 }
		 ballMeta.setLore(lore);
		 ballMeta.setDisplayName(name);
		 ball.setItemMeta(ballMeta);
		 return ball;
	 }
	 public static ItemStack getBat(){
		 ItemStack bat = new ItemStack(Material.BOW);
		 ItemMeta batMeta = bat.getItemMeta();
		 bat.setDurability((short)384);
		 batMeta.setUnbreakable(true);
		 batMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		 List<String> lore = new ArrayList<String>();
		 lore.add("SnowballGame Item");
		 lore.add("Bat");
		 batMeta.setLore(lore);
		 String name = plugin.getConfig().getString("Bat.Bat_Name");
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
		 String name = plugin.getConfig().getString("Glove.Glove_Name");
		 gloveMeta.setDisplayName(name);
		 glove.setItemMeta(gloveMeta);
		 return glove;
	 }
	 public static ItemStack getUmpire(){
		 ItemStack umpire = new ItemStack(Material.QUARTZ_BLOCK);
		 ItemMeta umpireMeta = umpire.getItemMeta();
		 List<String> lore = new ArrayList<String>();
		 lore.add("SnowballGame Item");
		 lore.add("Umpire");
		 umpireMeta.setLore(lore);
		 String name = plugin.getConfig().getString("Umpire.Umpire_Name");
		 umpireMeta.setDisplayName(name);
		 umpire.setItemMeta(umpireMeta);
		 return umpire;
	 }
	 public static ItemStack getBase(){
		 ItemStack base = new ItemStack(Material.STEP, 1, (short)7);
		 ItemMeta baseMeta = base.getItemMeta();
		 List<String> lore = new ArrayList<String>();
		 lore.add("SnowballGame Item");
		 lore.add("Base");
		 baseMeta.setLore(lore);
		 String name = plugin.getConfig().getString("Base.Base_Name");
		 baseMeta.setDisplayName(name);
		 base.setItemMeta(baseMeta);
		 return base;
	 }
	 public static ItemStack getCoach(){
		 ItemStack coach = new ItemStack(Material.ARMOR_STAND);
		 ItemMeta coachMeta = coach.getItemMeta();
		 List<String> lore = new ArrayList<String>();
		 lore.add("SnowballGame Item");
		 lore.add("Coach");
		 coachMeta.setLore(lore);
		 String name = plugin.getConfig().getString("Coach.Coach_Name");
		 coachMeta.setDisplayName(name);
		 coach.setItemMeta(coachMeta);
		 return coach;
	 }
	 public static ShapedRecipe getBallRecipe(String type){
		 ItemStack ball = getBall(type);
		 ball.setAmount(4);
		 NamespacedKey key = new NamespacedKey(plugin, plugin.getDescription().getName() + ball.getItemMeta().getDisplayName() + type);
		 ShapedRecipe ballRecipe = new ShapedRecipe(key, ball);
		 Material inclusion = Material.SNOW_BALL;
		 switch(type){
		 case "highest":
			 inclusion = Material.ENDER_PEARL;
			 break;
		 case "higher":
			 inclusion = Material.SLIME_BALL;
			 break;
		 case "lower":
			 inclusion = Material.EGG;
			 break;
		 case "lowest":
			 inclusion = Material.CLAY_BALL;
			 break;
		 }
		 ballRecipe.shape("LSL","SBS","LSL");
		 ballRecipe.setIngredient('L', Material.LEATHER);
		 ballRecipe.setIngredient('S', Material.STRING);
		 ballRecipe.setIngredient('B', inclusion);
		 return ballRecipe;
	 }
	 public static ShapedRecipe getBatRecipe(){
		 ItemStack bat = getBat();
		 NamespacedKey key = new NamespacedKey(plugin, plugin.getDescription().getName() + bat.getItemMeta().getDisplayName());
		 ShapedRecipe batRecipe = new ShapedRecipe(key, bat);
		 batRecipe.shape("  S"," S ","S  ");
		 batRecipe.setIngredient('S', Material.STICK);
		 return batRecipe;
	 }
	 public static ShapedRecipe getGloveRecipe(){
		 ItemStack glove = getGlove();
		 NamespacedKey key = new NamespacedKey(plugin, plugin.getDescription().getName() + glove.getItemMeta().getDisplayName());
		 ShapedRecipe gloveRecipe = new ShapedRecipe(key, glove);
		 gloveRecipe.shape("LLL","LLL"," L ");
		 gloveRecipe.setIngredient('L', Material.LEATHER);
		 return gloveRecipe;
	 }
	 public static ShapelessRecipe getUmpireRecipe(){
		 ItemStack umpire = getUmpire();
		 NamespacedKey key = new NamespacedKey(plugin, plugin.getDescription().getName() + umpire.getItemMeta().getDisplayName());
		 ShapelessRecipe umpireRecipe = new ShapelessRecipe(key, umpire);
		 umpireRecipe.addIngredient(1, Material.QUARTZ_BLOCK);
		 umpireRecipe.addIngredient(1, Material.OBSERVER);
		 return umpireRecipe;
	 }
	 public static ShapelessRecipe getBaseRecipe(){
		 ItemStack base = getBase();
		 NamespacedKey key = new NamespacedKey(plugin, plugin.getDescription().getName() + base.getItemMeta().getDisplayName());
		 ShapelessRecipe baseRecipe = new ShapelessRecipe(key, base);
		 baseRecipe.addIngredient(1, Material.STEP,7);
		 baseRecipe.addIngredient(1, Material.OBSERVER);
		 return baseRecipe;
	 }
	 public static ShapelessRecipe getCoachRecipe(){
		 ItemStack coach = getCoach();
		 NamespacedKey key = new NamespacedKey(plugin, plugin.getDescription().getName() + coach.getItemMeta().getDisplayName());
		 ShapelessRecipe coachRecipe = new ShapelessRecipe(key, coach);
		 coachRecipe.addIngredient(1, Material.ARMOR_STAND);
		 coachRecipe.addIngredient(1, Material.DISPENSER);
		 return coachRecipe;
	 }
	 public static String getBallType(List <String> lore){
		 String ballType = "";
		 if(lore.contains("Highest-repulsion")){
			 ballType = "highest";
		 }else if(lore.contains("Higher-repulsion")){
			 ballType = "higher";
		 }else if(lore.contains("Lower-repulsion")){
			 ballType = "lower";
		 }else if(lore.contains("Lowest-repulsion")){
			 ballType = "lowest";
		 }else{
			 ballType = "normal";
		 }
		 return ballType;
	 }
	 public static void deleteBalls(World world){
		 Collection <Snowball> balls = world.getEntitiesByClass(Snowball.class);
		 Bukkit.getLogger().info("[SnowballGame] Deleting Balls in " + world.getName() + "...");
		 balls.forEach(ball -> ball.remove());
	 }
	 public static Particle getParticle(ConfigurationSection config){
		 Particle particle = null;
		 if(config.contains("Particle")){
			 try{
				 particle =  Particle.valueOf(config.getString("Particle"));
			 }catch(IllegalArgumentException e){
				 Bukkit.broadcastMessage("The value of " + config.getCurrentPath() +".Particle : "+ config.getString("Particle") + " is invalid!!");
			 }
		 }
		 return particle;
	 }
}
