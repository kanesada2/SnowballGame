package com.github.kanesada2.SnowballGame;

import java.util.Collection;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.util.Vector;

public class SnowballGameListener implements Listener {

	private SnowballGame plugin;

	public SnowballGameListener(SnowballGame plugin) {
        this.plugin = plugin;
    }
	@EventHandler(priority = EventPriority.LOW)
	public void onDispense(BlockDispenseEvent event){
		if(!(plugin.getConfig().getBoolean("Ball.Enabled_Ball") && event.getBlock().getType() == Material.DISPENSER && Util.isBall(event.getItem()))){
			return;
		}
		Dispenser from = (Dispenser)event.getBlock().getState();
		from.setMetadata("ballType", new FixedMetadataValue(plugin, Util.getBallType(event.getItem().getItemMeta().getLore())));
		if(event.getItem().getItemMeta().hasDisplayName()){
			from.setMetadata("moving", new FixedMetadataValue(plugin, event.getItem().getItemMeta().getDisplayName()));
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		if(!(projectile instanceof Snowball)){
			return;
		}
		if(projectile.getShooter() instanceof Player){
			Player player = (Player)projectile.getShooter();
			ItemStack hand = new ItemStack(Material.AIR);
			boolean isR = true;
			if(Util.isBall(player.getInventory().getItemInMainHand())){
				hand = player.getInventory().getItemInMainHand();
				if(player.getMainHand() == MainHand.LEFT){
					isR = false;
				}
			}else if(Util.isBall(player.getInventory().getItemInOffHand()) && player.getInventory().getItemInMainHand().getType() != Material.SNOW_BALL){
				hand = player.getInventory().getItemInOffHand();
				if(player.getMainHand() == MainHand.RIGHT){
					isR = false;
				}
			}
			if(Util.isBall(hand)){
				projectile.setMetadata("ballType", new FixedMetadataValue(plugin, Util.getBallType(hand.getItemMeta().getLore())));
				projectile.setGlowing(true);
				projectile.setVelocity(projectile.getVelocity().add(player.getVelocity()));
				if(hand.getItemMeta().hasDisplayName()){
					projectile.setMetadata("moving", new FixedMetadataValue(plugin, hand.getItemMeta().getDisplayName()));
					new BallProcess(plugin).move(projectile, player.getLocation(), isR);
				}
			}
		}else if(projectile.getShooter() instanceof BlockProjectileSource){
			BlockProjectileSource source = (BlockProjectileSource)projectile.getShooter();
			Block from = source.getBlock();
			if(from.hasMetadata("ballType")){
				projectile.setMetadata("ballType", new FixedMetadataValue(plugin, from.getMetadata("ballType").get(0).asString()));
				projectile.setGlowing(true);
				from.removeMetadata("ballType", plugin);
				if(from.hasMetadata("moving")){
					projectile.setMetadata("moving", new FixedMetadataValue(plugin, from.getMetadata("moving").get(0).asString()));
					DirectionalContainer fromdata = (DirectionalContainer)from.getState().getData();
					Location facingLoc = from.getLocation();
					switch(fromdata.getFacing()){
					case EAST:
						facingLoc.setYaw(270);
						break;
					case WEST:
						facingLoc.setYaw(90);
						break;
					case NORTH:
						facingLoc.setYaw(180);
						break;
					default:
						facingLoc.setYaw(0);
						break;
					}
					Vector validVec = from.getRelative(fromdata.getFacing()).getLocation().subtract(facingLoc).toVector();
					Vector velocity = projectile.getVelocity();
					double speed = velocity.length();
					projectile.setVelocity(velocity.getMidpoint(validVec).normalize().multiply(speed));
					new BallProcess(plugin).move(projectile, facingLoc, true);
					from.removeMetadata("moving", plugin);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onProjectileHit(ProjectileHitEvent event) {
		 Projectile projectile = event.getEntity();
		 if (!(projectile instanceof Snowball)) {
	            return;
	        }
		 if((projectile.hasMetadata("ballType"))){
			 ItemStack ball = Util.getBall(projectile.getMetadata("ballType").get(0).asString());
			 String moving = "";
			 if(projectile.hasMetadata("moving")){
				 moving = projectile.getMetadata("moving").get(0).asString();
				 projectile.removeMetadata("moving", plugin);
			 }
			 if(event.getHitEntity() instanceof Player){
				Player player = (Player)event.getHitEntity();
				PlayerInventory inventory = player.getInventory();
				ItemStack offHand = inventory.getItemInOffHand();
				if(plugin.getConfig().getBoolean("Glove.Enabled_Glove") && Util.isGlove(offHand)){
					if(plugin.getConfig().getBoolean("Broadcast.Catch.Enabled") && moving.equalsIgnoreCase("batted")){
						Util.broadcastRange(plugin, player, Util.addColors(plugin.getConfig().getString("Broadcast.Catch.Message").replaceAll("\\Q[[PLAYER]]\\E", player.getName().toString())), plugin.getConfig().getInt("Broadcast.Catch.Range"));
					}
					if(inventory.containsAtLeast(ball,1) || inventory.firstEmpty() != -1){
						inventory.addItem(ball);
						return;
					}
				} else {
					if(plugin.getConfig().getBoolean("Knockback_For_Players") && player.getGameMode() != GameMode.CREATIVE)
					Util.causeKnockBack(player,projectile);
				}
			 }
			 if(event.getHitBlock() != null && projectile.getVelocity().length() > 0.15){
				new BallProcess(plugin).bounce(projectile,event.getHitBlock());
				return;
			 }
			 if(event.getHitEntity() instanceof Snowman && event.getHitEntity().getCustomName().equalsIgnoreCase(plugin.getConfig().getString("Umpire.Umpire_Name"))){
				 Snowman pl = (Snowman)event.getHitEntity();
				 Location location = projectile.getLocation();
				 double top = pl.getLocation().getBlockY() + plugin.getConfig().getDouble("Umpire.Top");
				 double bottom = pl.getLocation().getBlockY() + plugin.getConfig().getDouble("Umpire.Bottom");
				 if(top >= projectile.getLocation().getY() && projectile.getLocation().getY() >= bottom && moving != ""){
					 String msg = plugin.getConfig().getString("Broadcast.Strike.Message");
					 msg = Util.addColors(msg);
					 msg = msg.replaceAll("\\Q[[SPEED]]\\E", String.valueOf(Math.round(projectile.getVelocity().length() * 720) / 10) + "km/h");
					 msg = msg.replaceAll("\\Q[[TYPE]]\\E", moving);
					 if(plugin.getConfig().getInt("Broadcast.Strike.Range") > 0){
						 int range = (int)Math.pow((double)plugin.getConfig().getInt("Broadcast.Strike.Range"),2);
				         List<Player> players = projectile.getWorld().getPlayers();
				         	for (Player player : players) {
				         		if (location.distanceSquared(player.getLocation()) <= range) {
				         			player.sendMessage(msg);
				                }
				            }
					}else{
						plugin.getServer().broadcastMessage(msg);
					}
					}
				 new UmpireResetTask(pl).runTaskLater(plugin, 20);
				 pl.setCollidable(false);
				 Projectile throughed = (Projectile)location.getWorld().spawnEntity(location, EntityType.SNOWBALL);
				 throughed.setVelocity(projectile.getVelocity());
				 throughed.setGlowing(true);
				 throughed.setShooter(projectile.getShooter());
				 throughed.setMetadata("ballType", new FixedMetadataValue(plugin, projectile.getMetadata("ballType").get(0).asString()));

				 return;
			 }
			 projectile.getWorld().dropItem(projectile.getLocation(), ball);
			} else {
			Entity hitEntity = event.getHitEntity();
			if(hitEntity instanceof Player){
				Player player = (Player)hitEntity;
				if(player.getGameMode() != GameMode.CREATIVE){
					Util.causeKnockBack(player, projectile);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onSwing(EntityShootBowEvent event){
		if(!(plugin.getConfig().getBoolean("Bat.Enabled_Bat") && event.getEntity() instanceof Player && Util.isBat(event.getBow()))){
			return;
		}
		Entity batArrow = event.getProjectile();
		float force = event.getForce();
		double size = 2.2 - force;
		Location impactLoc = batArrow.getLocation().add(batArrow.getVelocity().multiply(1/force));
		Entity meetCursor = impactLoc.getWorld().spawnEntity(impactLoc, EntityType.SNOWBALL);
		Player player = (Player)event.getEntity();
		meetCursor.setGravity(false);
		meetCursor.remove();
		Collection <Entity> nearByEntities = impactLoc.getWorld().getNearbyEntities(impactLoc, size, size, size);
		String msg;
		int range;
		event.setCancelled(true);
		if(force < 0.4 && plugin.getConfig().getBoolean("Broadcast.Bunt.Enabled")){
			msg = plugin.getConfig().getString("Broadcast.Bunt.Message");
			range = plugin.getConfig().getInt("Broadcast.Bunt.Range");
			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", player.getName().toString());
			msg = Util.addColors(msg);
			Util.broadcastRange(plugin, player, msg, range);
		}else if(force >= 0.4 && plugin.getConfig().getBoolean("Broadcast.Swing.Enabled")){
			msg = plugin.getConfig().getString("Broadcast.Swing.Message");
			range = plugin.getConfig().getInt("Broadcast.Swing.Range");
			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", player.getName().toString());
			msg = Util.addColors(msg);
			Util.broadcastRange(plugin, player, msg, range);
		}
		for (Entity entity : nearByEntities) {
			if(entity.getType() == EntityType.SNOWBALL && entity.hasMetadata("ballType")){
				new BallProcess(plugin).hit((Projectile)entity,impactLoc , force);
				if(plugin.getConfig().getBoolean("Broadcast.Hit.Enabled")){
					msg = plugin.getConfig().getString("Broadcast.Hit.Message");
					range = plugin.getConfig().getInt("Broadcast.Hit.Range");
					msg = msg.replaceAll("\\Q[[PLAYER]]\\E", player.getName().toString());
					msg = Util.addColors(msg);
					Util.broadcastRange(plugin, player, msg, range);
				}
				break;
			}
		}
		if(force > 0.7){
			Location playerLoc = player.getLocation();
			playerLoc.setY(impactLoc.getY());
			player.getWorld().playSound(playerLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, force, 1);
			player.spawnParticle(Particle.SWEEP_ATTACK, playerLoc, 1);
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onTeeInteracted(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.BREWING_STAND){
			ItemStack hand;
			if(Util.isBall(event.getPlayer().getInventory().getItemInMainHand())){
				hand = event.getPlayer().getInventory().getItemInMainHand();
			}else if(Util.isBall(event.getPlayer().getInventory().getItemInOffHand())){
				hand = event.getPlayer().getInventory().getItemInOffHand();
			}else{
				return;
			}
			event.setCancelled(true);
			Block tee = event.getClickedBlock();
			Location ballLoc = tee.getLocation();
			ballLoc.setX(ballLoc.getX() + 0.5);
			ballLoc.setY(ballLoc.getY() + 1.1);
			ballLoc.setZ(ballLoc.getZ() + 0.5);
			Collection <Entity> nearByEntities = ballLoc.getWorld().getNearbyEntities(ballLoc, 0.1, 0.1, 0.1);
			for (Entity entity : nearByEntities) {
				if(entity.getType() == EntityType.SNOWBALL && entity.hasMetadata("ballType")){
					return;
				}
			}
			Projectile placedBall = (Projectile)tee.getWorld().spawnEntity(ballLoc, EntityType.SNOWBALL);
			placedBall.setMetadata("ballType", new FixedMetadataValue(plugin, Util.getBallType(hand.getItemMeta().getLore())));
			placedBall.setShooter(event.getPlayer());
			placedBall.setGravity(false);
			placedBall.setGlowing(true);
			if(event.getPlayer().getGameMode() == GameMode.CREATIVE){
				return;
			}
			hand.setAmount(hand.getAmount() - 1);
		}
		if(event.hasItem() && Util.isUmpire(event.getItem())){
			if(!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)){
				return;
			}
			event.getPlayer().setMetadata("isUmpireSetter", new FixedMetadataValue(plugin,true));
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onTagged(EntityDamageByEntityEvent event){
		if(!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)){
			return;
		}
		Player runner = (Player)event.getEntity();
		Player fielder = (Player)event.getDamager();
		if(!Util.isBall(fielder.getInventory().getItemInMainHand())){
			return;
		}
		event.setCancelled(true);
		if(plugin.getConfig().getBoolean("Broadcast.Tag.Enabled")){
			String msg = plugin.getConfig().getString("Broadcast.Tag.Message");
			int range = plugin.getConfig().getInt("Broadcast.Tag.Range");
			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", fielder.getName().toString());
			msg = msg.replaceAll("\\Q[[RUNNER]]\\E", runner.getName().toString());
			msg = Util.addColors(msg);
			Util.broadcastRange(plugin, fielder, msg, range);
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onUmpireSpawn(CreatureSpawnEvent event){
		if(!(event.getEntity() instanceof ArmorStand && plugin.getConfig().getBoolean("Umpire.Enabled_Umpire"))){
			return;
		}
		Collection <Entity> nearByEntities = event.getEntity().getNearbyEntities(10, 10, 10);
		for (Entity entity : nearByEntities) {
			if(entity instanceof Player && entity.hasMetadata("isUmpireSetter")){
				event.setCancelled(true);
				Snowman pl = (Snowman)event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.SNOWMAN);
				pl.setCustomName(plugin.getConfig().getString("Umpire.Umpire_Name"));
				pl.setCustomNameVisible(true);
				pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
				pl.setAI(false);
				entity.removeMetadata("isUmpireSetter", plugin);
				return;
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onUmpireDamaged(EntityDamageEvent event){
		if(event.getEntity() instanceof Snowman && event.getEntity().getCustomName().equalsIgnoreCase(plugin.getConfig().getString("Umpire.Umpire_Name"))){
			event.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onUmpireDamagedByPlayer(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Snowman && event.getEntity().getCustomName().equalsIgnoreCase(plugin.getConfig().getString("Umpire.Umpire_Name")) && event.getDamager() instanceof Player){
			Snowman pl = (Snowman)event.getEntity();
			pl.remove();
			pl.getWorld().dropItem(pl.getLocation(), Util.getUmpire());
		}
	}
}
