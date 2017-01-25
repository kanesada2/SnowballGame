package com.github.kanesada2.SnowballGame;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class BallProcess {
	private BallProcess() {}
	public static Projectile bounce(Projectile ball,  BlockFace hitFace){
		Vector velocity = ball.getVelocity();
		Location hitLoc = ball.getLocation();
		Projectile bounced;
		Double x = velocity.getX();
		Double y = velocity.getY();
		Double z = velocity.getZ();
		if(hitFace == BlockFace.SOUTH || hitFace == BlockFace.NORTH){
			z = -z;
		}else if(hitFace == BlockFace.EAST || hitFace == BlockFace.WEST){
			x = -x;
		}else{
			y = -y;
		}
		velocity.setX(x * 0.6);
		velocity.setY(y * 0.6);
		velocity.setZ(z * 0.6);
		bounced = (Projectile)hitLoc.getWorld().spawnEntity(hitLoc, EntityType.SNOWBALL);
		bounced.setVelocity(velocity);
		bounced.setShooter(ball.getShooter());
		return bounced;
	}
	public static void hit(Projectile ball, Location impactLoc, float force){;
		Vector velocity = ball.getVelocity();
		Vector battedVec = ball.getLocation().toVector().subtract(impactLoc.toVector());
		double power;
		if(battedVec.length() < 0.5){
			power = 2 * force;
		}else{
			power = force / battedVec.length();
		}
		if(force * 1.2 > velocity.length()){
			velocity.setX(-velocity.getX());
			velocity.setX(-velocity.getY());
			velocity.setZ(-velocity.getZ());
		}else{
			velocity.multiply(0.1);
		}
		battedVec.multiply(power * 1.4);
		velocity = velocity.add(battedVec);
		ball.setVelocity(velocity);
		impactLoc.getWorld().playSound(impactLoc, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE , force, 1);
	}
}
