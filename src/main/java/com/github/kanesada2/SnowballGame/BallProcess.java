package com.github.kanesada2.SnowballGame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class BallProcess {
	private SnowballGame plugin;
	public BallProcess(SnowballGame plugin) {
		this.plugin = plugin;
	}
	public void bounce(Projectile ball, Block hitBlock){
		Vector velocity = ball.getVelocity();
		Location hitLoc = ball.getLocation();
		Projectile bounced;
		if(hitBlock.getType() == Material.IRON_FENCE || hitBlock.getType() == Material.VINE){
			velocity.multiply(0.1);
		}
		Double x = velocity.getX();
		Double y = velocity.getY();
		Double z = velocity.getZ();
		BlockFace hitFace = hitBlock.getFace(hitLoc.getBlock());
		if(Util.doesRegardUp(hitBlock)){
			hitFace = BlockFace.UP;
		}else if(hitFace == null || hitFace.toString().contains("_")){
			 BlockIterator blockIterator = new BlockIterator(hitLoc.getWorld(), hitLoc.toVector(), velocity, 0.0D, 3);
			 Block previousBlock = hitLoc.getBlock();
			 Block nextBlock = blockIterator.next();
			while (blockIterator.hasNext() && (!Util.doesRepel(nextBlock) ||nextBlock.isLiquid() || nextBlock.equals(hitLoc.getBlock()))) {
					previousBlock = nextBlock;
					nextBlock = blockIterator.next();
			 }
			 hitFace = nextBlock.getFace(previousBlock);
		 }
		if(!Util.doesRepel(hitBlock)){
			if(hitBlock.getType() == Material.WEB){
				hitLoc = hitBlock.getLocation().add(0.5, 0, 0.5);
				velocity.zero();
			}else{
				hitLoc = hitLoc.add(velocity);
				while(!(hitLoc.getBlock().getType() == Material.AIR || hitLoc.getBlock().isLiquid())){
					hitLoc.setY(hitLoc.getY() + 0.1);
				}
			}
		}else{
			Vector vecToCompare;
			if(hitFace == BlockFace.SOUTH || hitFace == BlockFace.NORTH){
				z = -z;
				vecToCompare = velocity.clone().setZ(0);
			}else if(hitFace == BlockFace.EAST || hitFace == BlockFace.WEST){
				x = -x;
				vecToCompare = velocity.clone().setX(0);
			}else{
				y = -y;
				vecToCompare = velocity.clone().setY(0);
			}
			double angle = velocity.angle(vecToCompare) / Math.toRadians(90);
			velocity.setX(x * Math.pow(0.9, angle));
			velocity.setY(y * Math.pow(0.6, angle));
			velocity.setZ(z * Math.pow(0.9, angle));
			velocity.multiply(Math.pow(1.3, -(velocity.length())));
		}
		bounced = (Projectile)hitLoc.getWorld().spawnEntity(hitLoc, EntityType.SNOWBALL);
		bounced.setVelocity(velocity);
		bounced.setGlowing(true);
		bounced.setShooter(ball.getShooter());
		bounced.setMetadata("ballType", new FixedMetadataValue(plugin, ball.getMetadata("ballType").get(0).asString()));
	}
	public void hit(Projectile ball, Location eye, Location impactLoc, float force, int rolld){;
		if(ball.hasMetadata("moving")){
			ball.removeMetadata("moving", plugin);
		}
		Vector velocity = ball.getVelocity();
		Vector battedVec = ball.getLocation().toVector().subtract(impactLoc.toVector());
		double power;
		double coefficient = 1.5D;
		if(battedVec.length() < 0.06){
			battedVec.setX(-velocity.getX());
			battedVec.setY(-velocity.getY());
			battedVec.setZ(-velocity.getZ());
			power = force * 50;
		}else{
			power = force / Math.pow(battedVec.length(), 1.4);
		}
		if(force * 2 > velocity.length()){
			velocity.setX(-velocity.getX());
			velocity.setY(-velocity.getY());
			velocity.setZ(-velocity.getZ());
		}else{
			velocity.multiply(0.1);
		}
			switch(ball.getMetadata("ballType").get(0).asString()){
			case "highest":
				coefficient = coefficient * 1.4 ;
				break;
			case "higher":
				coefficient = coefficient * 1.2;
				break;
			case "lower":
				coefficient = coefficient * 0.8;
				break;
			case "lowest":
				coefficient = coefficient * 0.6;
				break;
			}
		battedVec.multiply(power * coefficient);
		velocity = velocity.add(battedVec);
		ball.remove();
		Projectile hitball = (Projectile)ball.getWorld().spawnEntity(ball.getLocation(), EntityType.SNOWBALL);
		hitball.setMetadata("moving",new FixedMetadataValue(plugin, "batted"));
		if(plugin.getConfig().getBoolean("Particle.BattedBall_InFlight.Enabled")){
			new BallMovingTask(hitball, battedVec.clone().normalize().multiply(0.005 * force), Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.BattedBall_InFlight")), 0).runTaskTimer(plugin, 0, 1);
		}else{
			new BallMovingTask(hitball, battedVec.clone().normalize().multiply(0.005 * force), 0).runTaskTimer(plugin, 0, 1);
		}
		hitball.setGravity(true);
		hitball.setGlowing(true);
		hitball.setVelocity(velocity);
		hitball.setMetadata("ballType", new FixedMetadataValue(plugin, ball.getMetadata("ballType").get(0).asString()));
		impactLoc.getWorld().playSound(impactLoc, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE , force, 1);
		/*
		 *  Temporary code for logging batted-ball.
		 */
		double angle = hitball.getVelocity().angle(hitball.getVelocity().clone().setY(0)) * 57.2958;
		if(hitball.getVelocity().getY() < 0){
			angle = -1 * angle;
		}
		hitball.setMetadata("ev", new FixedMetadataValue(plugin, String.format("%.1f", hitball.getVelocity().length() * 72 / 1.60934)));
		hitball.setMetadata("la", new FixedMetadataValue(plugin, String.format("%.1f", angle)));
		hitball.setMetadata("il", new FixedMetadataValue(plugin, impactLoc));
	}
	public void move(Projectile ball, Location directionLoc, boolean isR){
		String moveType = ball.getMetadata("moving").get(0).asString();
		Vector velocity = ball.getVelocity();
		Vector moveVector = new Vector(0,0,0);
		FileConfiguration config = SnowballGame.getPlugin(SnowballGame.class).getConfig();
		int moved;
		double random = 0;
		if(isR){
			moved = 1;
		}else{
			moved = -1;
		}
		if(config.getStringList("Ball.Move.Type").contains(moveType)){
			String section = "Ball.Move." + moveType;
			velocity.multiply(config.getDouble(section + ".Velocity"));
			if(config.getDouble(section + ".Random") != 0){
				random = config.getDouble(section + ".Random");
			}
			moveVector = directionLoc.getDirection().normalize().multiply(config.getDouble(section + ".Acceleration", 0));
			directionLoc.setYaw(directionLoc.getYaw() + 90);
			moveVector.add(directionLoc.getDirection().normalize().multiply(moved * config.getDouble(section + ".Horizontal")));
			moveVector.setY(config.getDouble(section + ".Vertical"));
			ball.setVelocity(velocity);
			if(plugin.getConfig().getBoolean("Particle.MovingBall.Enabled") && Util.getParticle(plugin.getConfig().getConfigurationSection(section)) != null){
				new BallMovingTask(ball, moveVector, Util.getParticle(plugin.getConfig().getConfigurationSection(section)), random).runTaskTimer(plugin, 0, 1);
			}else{
				new BallMovingTask(ball, moveVector, random).runTaskTimer(plugin, 0, 1);
			}

		}
	}
}
