package com.github.kanesada2.SnowballGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.util.Vector;

import com.github.kanesada2.SnowballGame.api.SnowballGameAPI;

public class CoachAction {
	private ArmorStand coach;
	private SnowballGame plugin;
	private ItemStack defaultEquips[] = {
			new ItemStack(Material.LEATHER_BOOTS),
			new ItemStack(Material.LEATHER_LEGGINGS),
			new ItemStack(Material.LEATHER_CHESTPLATE),
			new ItemStack(Material.SKULL_ITEM, 1, (short)4),
	};

	public CoachAction(SnowballGame plugin, ArmorStand coach){
		this.plugin = plugin;
		this.coach = coach;
	}

	public void init(){
		coach.setCustomName(plugin.getConfig().getString("Coach.Coach_Name"));
		coach.setCustomNameVisible(true);
		coach.setArms(true);
		coach.setGlowing(true);
		coach.getEquipment().setArmorContents(defaultEquips);
		coach.setItemInHand(Util.getBat());

	}

	public List<ItemStack> getDefaultEquips(){
		List<ItemStack> deflist = new ArrayList<ItemStack>(Arrays.asList(defaultEquips));
		deflist.add(Util.getBat());
		return deflist;
	}

	public void drop(){
		List<ItemStack> drops = new ArrayList<ItemStack>(Arrays.asList(coach.getEquipment().getArmorContents()));
		drops.add(coach.getEquipment().getItemInMainHand());
		drops.add(coach.getEquipment().getItemInOffHand());
		drops.removeAll(getDefaultEquips());
		drops.add(Util.getCoach());
		for(ItemStack drop : drops){
			if(drop.getType() == Material.AIR){
				continue;
			}
			coach.getWorld().dropItem(coach.getLocation(), drop);
		}
	}

	public String getType(){
		ItemStack hand = coach.getItemInHand();
		if(Util.isBat(hand)){
			return "Batting";
		}
		if(Util.isGlove(hand)){
			return "Catching";
		}
		return "Throwing";
	}

	public boolean ballHitAction(Projectile ball){
		if(!(ball.getShooter() instanceof BlockProjectileSource) && ((LivingEntity)ball.getShooter()).isDead()){
			return false;
		}
		switch(getType()){
		case "Batting":
			return hitBall(ball);
		case "Catching":
			return catchBall(ball);
		case "Throwing":
			return throwBall(ball);
		default:
			return false;
		}
	}

	public boolean hitBall(Projectile ball){
		if(ball.getShooter() instanceof BlockProjectileSource){
			 Location loc = coach.getLocation();
			 List<Player> players = loc.getWorld().getPlayers();
			 int range = plugin.getConfig().getInt("Coach.Coach_Range",120);
			 range *= range;
			 List<Player> fielders = new ArrayList<Player>();
			 for (Player player : players) {
	         	if (loc.distanceSquared(player.getLocation()) <= range && Util.isGlove(player.getInventory().getItemInOffHand())) {
	         		fielders.add(player);
	            }
	         }
			 if(fielders.size() > 0){
				 Player fielder = fielders.get((int) Math.floor(Math.random() * fielders.size()));
				 SnowballGameAPI.playWithCoach(fielder, coach, ball.getMetadata("ballType").get(0).asString());
			 }else{
				 return false;
			 }
		 }else if(ball.getShooter() instanceof Player){
			 SnowballGameAPI.playWithCoach((Player)ball.getShooter(), coach, ball.getMetadata("ballType").get(0).asString());
		 }
		return true;
	}

	public boolean catchBall(Projectile ball){
		Util.broadcastRange(coach, Util.addColors(plugin.getConfig().getString("Broadcast.Catch.Message").replaceAll("\\Q[[PLAYER]]\\E", this.coach.getCustomName())), plugin.getConfig().getInt("Broadcast.Catch.Range", 0));
		ItemStack itemBall = Util.getBall(ball.getMetadata("ballType").get(0).asString());
		Location loc = coach.getLocation();
		if(ball.getShooter() instanceof LivingEntity){
			loc = ((LivingEntity)ball.getShooter()).getEyeLocation();
		}else if(ball.getShooter() instanceof BlockProjectileSource){
			BlockProjectileSource source = (BlockProjectileSource)ball.getShooter();
			loc = source.getBlock().getLocation().add(0.5, 2.2, 0.5);
		}
		ball.getWorld().dropItem(loc, itemBall);
		return true;
	}

	public boolean throwBall(Projectile ball){
		if(ball.getShooter() instanceof ArmorStand){
			ArmorStand thrower = (ArmorStand)ball.getShooter();
			if(Util.isEntityCoach(thrower) && Util.isBall(thrower.getItemInHand())){
				return false;
			}
		}
		Vector velocity = coach.getEyeLocation().getDirection().normalize().multiply(1.5);
		SnowballGameAPI.launch(coach, null, false, ball.getMetadata("ballType").get(0).asString(), "ball", velocity, new Vector(0,0,0), 0, 0, null, coach.getEyeLocation(), new Vector(0,0,0));
		return true;
	}

}
