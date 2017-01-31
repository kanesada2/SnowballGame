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
		double coefficient = 2.0D;
		if(battedVec.length() < 0.05){
			battedVec.setX(-velocity.getX());
			battedVec.setX(-velocity.getY());
			battedVec.setZ(-velocity.getZ());
			power = force * 50;
		}else{
			power = force / Math.pow(battedVec.length(), 1.3);
		}
		if(force * 2 > velocity.length()){
			velocity.setX(-velocity.getX());
			velocity.setX(-velocity.getY());
			velocity.setZ(-velocity.getZ());
		}else{
			velocity.multiply(0.1);
		}
			switch(ball.getMetadata("ballType").get(0).asString()){
			case "higest":
				coefficient = 2.8D;
				break;
			case "higer":
				coefficient = 2.4D;
				break;
			case "normal":
				coefficient = 2.0D;
				break;
			case "lower":
				coefficient = 1.6D;
				break;
			case "lowest":
				coefficient = 1.2D;
				break;
			default:
				coefficient = 2.0D;
				break;
			}
		battedVec.multiply(power * coefficient);
		velocity = velocity.add(battedVec);
		ball.setGravity(true);
		ball.setVelocity(velocity);
		impactLoc.getWorld().playSound(impactLoc, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE , force, 1);
	}
}
