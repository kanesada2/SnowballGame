package com.github.kanesada2.SnowballGame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class BallProcess {
	private BallProcess() {}
	public static Projectile bounce(Projectile ball, Block hitBlock){
		Vector velocity = ball.getVelocity();
		Location hitLoc = ball.getLocation();
		Projectile bounced;
		Double x = velocity.getX();
		Double y = velocity.getY();
		Double z = velocity.getZ();
		BlockFace hitFace = hitBlock.getFace(hitLoc.getBlock());
		if(!Util.doesRepel(hitBlock)){
			hitFace = BlockFace.UP;
		}
		if(hitFace == null || hitFace.toString().contains("_")){
			 BlockIterator blockIterator = new BlockIterator(hitLoc.getWorld(), hitLoc.toVector(), velocity, 0.0D, 3);
			 Block previousBlock = hitLoc.getBlock();
			 Block nextBlock = blockIterator.next();
			while (blockIterator.hasNext() && (nextBlock.getType() == Material.AIR ||nextBlock.isLiquid() || nextBlock.equals(hitLoc.getBlock()))) {
					previousBlock = nextBlock;
					nextBlock = blockIterator.next();
			 }
			 hitFace = nextBlock.getFace(previousBlock);
		 }
		if(hitFace == BlockFace.SOUTH || hitFace == BlockFace.NORTH){
			z = -z;
		}else if(hitFace == BlockFace.EAST || hitFace == BlockFace.WEST){
			x = -x;
		}else{
				y = -y;
		}
		velocity.setX(x * 0.7);
		velocity.setY(y * 0.4);
		velocity.setZ(z * 0.7);
		bounced = (Projectile)hitLoc.getWorld().spawnEntity(hitLoc, EntityType.SNOWBALL);
		bounced.setVelocity(velocity);
		bounced.setGlowing(true);
		bounced.setShooter(ball.getShooter());
		return bounced;
	}
	public static void hit(Projectile ball, Location impactLoc, float force){;
		Vector velocity = ball.getVelocity();
		Vector battedVec = ball.getLocation().toVector().subtract(impactLoc.toVector());
		double power;
		if(battedVec.length() < 0.1){
			power = 10 * force;
		}else{
			power = force / battedVec.length();
		}
		if(force * 1.6 > velocity.length()){

			velocity.setX(-velocity.getX());
			velocity.setX(-velocity.getY());
			velocity.setZ(-velocity.getZ());
		}else{
			velocity.multiply(0.1);
		}
		battedVec.multiply(power * 2.5);
		velocity = velocity.add(battedVec);
		ball.setGravity(true);
		ball.setVelocity(velocity);
		impactLoc.getWorld().playSound(impactLoc, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE , force, 1);
	}
}
