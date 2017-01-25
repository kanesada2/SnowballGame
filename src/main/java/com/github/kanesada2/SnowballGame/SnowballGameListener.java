package com.github.kanesada2.SnowballGame;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BlockIterator;
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
		event.setCancelled(true);
		event.getItem().setAmount(event.getItem().getAmount() - 1);
		Dispenser from = (Dispenser)event.getBlock().getState();
		Projectile ball = from.getBlockProjectileSource().launchProjectile(Snowball.class);
		ball.setMetadata("ballType", new FixedMetadataValue(plugin, "katoRyozo"));
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		if(!(projectile.getShooter() instanceof Player)){
			return;
		}
		if(!(projectile instanceof Snowball)){
			return;
		}
		Player player = (Player)projectile.getShooter();
		ItemStack mainHand =  player.getInventory().getItemInMainHand();
		if(Util.isBall(mainHand)){
			projectile.setMetadata("ballType", new FixedMetadataValue(plugin, "katoRyozo"));
		}
		if(projectile.hasMetadata("ballType")){
			Vector velocity = projectile.getVelocity();
			velocity.setX(velocity.getX() * 0.8);
			velocity.setY(velocity.getY() * 0.8);
			velocity.setZ(velocity.getZ() * 0.8);
			projectile.setVelocity(velocity);
			Bukkit.getLogger().info(projectile.getVelocity().toString());
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onProjectileHit(ProjectileHitEvent event) {
		 Projectile projectile = event.getEntity();
		 if (!(projectile instanceof Snowball)) {
	            return;
	        }
		 if((projectile.hasMetadata("ballType"))){
			 ItemStack ball = Util.getBall();
			 if(event.getHitEntity() instanceof Player){
				Player player = (Player)event.getHitEntity();
				PlayerInventory inventory = player.getInventory();
				ItemStack offHand = inventory.getItemInOffHand();
				if(plugin.getConfig().getBoolean("Glove.Enabled_Glove") && Util.isGlove(offHand)){
					if(inventory.containsAtLeast(ball,1) || inventory.firstEmpty() != -1){
						inventory.addItem(ball);
						return;
					}
				} else {
					if(plugin.getConfig().getBoolean("Knockback_For_Players"))
					Util.causeKnockBack(player);
				}
			 }
			 if(event.getHitBlock() != null){
				 Vector velocity = projectile.getVelocity();
				 Location hitLoc = projectile.getLocation();
				 BlockFace Face = event.getHitBlock().getFace(hitLoc.getBlock());
				 if(Face == null || Face.toString().contains("_")){
					 BlockIterator blockIterator = new BlockIterator(hitLoc.getWorld(), hitLoc.toVector(), velocity, 0.0D, 3);
					 Block previousBlock = hitLoc.getBlock();
					 Block nextBlock = blockIterator.next();
					 while (blockIterator.hasNext() && (nextBlock.getType() == Material.AIR || nextBlock.isLiquid() || nextBlock.equals(hitLoc.getBlock()))) {
							previousBlock = nextBlock;
							nextBlock = blockIterator.next();
					 }
					 Face = nextBlock.getFace(previousBlock);

				 }

				 if(projectile.getVelocity().length() > 0.2){
					Projectile bounced = BallProcess.bounce(projectile,Face);
					bounced.setMetadata("ballType", new FixedMetadataValue(plugin, "katoRyozo"));
					return;
				 }
			 }
			 projectile.getWorld().dropItem(projectile.getLocation(), ball);
			} else {
			Entity hitEntity = event.getHitEntity();
			if(hitEntity instanceof Player){
				Util.causeKnockBack((Player)hitEntity);
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onShootBow(EntityShootBowEvent event){
		if(!(plugin.getConfig().getBoolean("Bat.Enabled_Bat") && event.getEntity() instanceof Player && Util.isBat(event.getBow()))){
			return;
		}
		Entity batArrow = event.getProjectile();
		float force = event.getForce();
		Location impactLoc = batArrow.getLocation().add(batArrow.getVelocity().multiply(1/force));
		Entity meetCursor = impactLoc.getWorld().spawnEntity(impactLoc, EntityType.SNOWBALL);
		meetCursor.setGravity(false);
		meetCursor.remove();
		Collection <Entity> nearByEntities = impactLoc.getWorld().getNearbyEntities(impactLoc, 1, 1, 1);
		for (Entity entity : nearByEntities) {
			if(entity.getType() == EntityType.SNOWBALL && entity.hasMetadata("ballType")){
				BallProcess.hit((Projectile)entity,impactLoc , force);
				break;
			}
		}

		event.setCancelled(true);
	}

}
