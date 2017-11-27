package com.github.kanesada2.SnowballGame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;
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
		Vector ballSpin = new Vector(0,0,0);
		int samePlace = 0;
		if(ball.hasMetadata("bouncedLoc") && hitLoc.distance((Location)ball.getMetadata("bouncedLoc").get(0).value()) == 0){
			if(ball.hasMetadata("samePlace")){
				samePlace = ball.getMetadata("samePlace").get(0).asInt();
				ball.removeMetadata("samePlace", plugin);
			}
			samePlace++;
		}
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
		if(!Util.doesRepel(hitBlock) || samePlace > 5){
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
			Vector linear = new Vector(0,0,0);
			Vector moveFromSpin = new Vector(0,0,0);
			if(ball.hasMetadata("spin")){
				ballSpin = (Vector)ball.getMetadata("spin").get(0).value();
			}
			if(ballSpin.length() != 0){
				moveFromSpin = velocity.clone().multiply(-1).getCrossProduct(ballSpin).normalize().multiply(ballSpin.length() * 9);
			}
			if(hitFace == BlockFace.SOUTH || hitFace == BlockFace.NORTH){
				z = -z;
				linear = velocity.clone().setZ(0);
			}else if(hitFace == BlockFace.EAST || hitFace == BlockFace.WEST){
				x = -x;
				linear = velocity.clone().setX(0);
			}else{
				y = -y;
				linear = velocity.clone().setY(0);
			}
			Vector normal = linear.clone().subtract(velocity).normalize();
			moveFromSpin.setY(moveFromSpin.getY() * 0.5);
			double angle = velocity.angle(linear) / Math.toRadians(90);
			velocity.setX(x * Math.pow(0.85, angle));
			velocity.setY(y * Math.pow(0.55, angle));
			velocity.setZ(z * Math.pow(0.85, angle));
			velocity.multiply(Math.pow(1.3, -(velocity.length())));
			velocity.add(moveFromSpin);
			ballSpin.multiply(0.01).add(linear.getCrossProduct(normal).multiply(0.003));
		}
		bounced = (Projectile)hitLoc.getWorld().spawnEntity(hitLoc, EntityType.SNOWBALL);
		bounced.setVelocity(velocity);
		bounced.setGlowing(true);
		bounced.setShooter(ball.getShooter());
		bounced.setMetadata("ballType", new FixedMetadataValue(plugin, ball.getMetadata("ballType").get(0).asString()));
		bounced.setMetadata("spin", new FixedMetadataValue(plugin, ballSpin));
		bounced.setMetadata("bouncedLoc", new FixedMetadataValue(plugin, hitLoc));
		bounced.setMetadata("samePlace", new FixedMetadataValue(plugin, samePlace));
	}
	public void hit(Projectile ball, Location eye, Location impactLoc, float force, int rolld, String batType){;
		if(ball.hasMetadata("moving")){
			ball.removeMetadata("moving", plugin);
		}
		Vector velocity = ball.getVelocity();
		Vector fromCenter = ball.getLocation().toVector().subtract(impactLoc.toVector());
		double power = force * Math.pow(1.3, -fromCenter.length());
		double coefficient = 1.0D;
		Vector batMove = Util.getBatmove(eye, (Math.PI / 2 + 0.01) * -rolld, rolld, batType).subtract(Util.getBatmove(eye, Math.PI / 2 * -rolld, rolld, batType)).normalize();
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
		velocity.multiply(-0.3);
		velocity.add(batMove.add(fromCenter.clone().normalize().multiply(2))).multiply(power * coefficient);
		ball.remove();
		Projectile hitball = (Projectile)ball.getWorld().spawnEntity(ball.getLocation(), EntityType.SNOWBALL);
		hitball.setMetadata("moving",new FixedMetadataValue(plugin, "batted"));
		hitball.setGravity(true);
		hitball.setGlowing(true);
		hitball.setVelocity(velocity);
		hitball.setMetadata("ballType", new FixedMetadataValue(plugin, ball.getMetadata("ballType").get(0).asString()));
		impactLoc.getWorld().playSound(impactLoc, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE , force, 1);
		Vector spinVector = fromCenter.getCrossProduct(velocity).normalize().multiply(fromCenter.length());
		if(plugin.getConfig().getBoolean("Particle.BattedBall_InFlight.Enabled")){
			new BallMovingTask(hitball, spinVector.multiply(0.008 * force), 0, Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.BattedBall_InFlight")), 0).runTaskTimer(plugin, 0, 1);
		}else{
			new BallMovingTask(hitball, spinVector.multiply(0.008 * force), 0, 0).runTaskTimer(plugin, 0, 1);
		}
	}
	public void knock(Player player, ArmorStand knocker){
		Vector knockedVec = player.getLocation().toVector().subtract(knocker.getLocation().toVector()).normalize();
		double distance = Math.sqrt(player.getLocation().distanceSquared(knocker.getLocation()));
		double randomY = (Math.random() - Math.random()) * (distance / 30);
		knockedVec.multiply(Math.pow(2.2, -randomY));
		Vector randomizer = new Vector((Math.random() - Math.random()) / (distance / 8), randomY , (Math.random() - Math.random()) / (distance / 8));
		knockedVec.add(randomizer);
		if(knockedVec.angle(knockedVec.clone().setY(0)) > 0.5){
			knockedVec.multiply(0.7);
		}
		knockedVec.multiply(distance / 25);
		Projectile batted = ((ProjectileSource)knocker).launchProjectile(Snowball.class, knockedVec);
		if(plugin.getConfig().getBoolean("Particle.BattedBall_InFlight.Enabled")){
			new BallMovingTask(batted, Vector.getRandom().normalize().multiply(0.0015 * knockedVec.length()), 0, Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.BattedBall_InFlight")), 0).runTaskTimer(plugin, 0, 1);
		}else{
			new BallMovingTask(batted, Vector.getRandom().normalize().multiply(0.0015 * knockedVec.length()), 0, 0).runTaskTimer(plugin, 0, 1);
		}
		player.sendMessage("Catch the ball!!!");

	}
	public void move(Projectile ball, Location directionLoc, boolean isR){
		String moveType = ball.getMetadata("moving").get(0).asString();
		Vector velocity = ball.getVelocity();
		Vector moveVector = new Vector(0,0,0);
		FileConfiguration config = plugin.getConfig();
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
			double acceleration = config.getDouble(section + ".Acceleration", 0);
			Vector linear = directionLoc.getDirection().setY(0).normalize();
			double angle = directionLoc.getDirection().angle(linear) * Math.signum(directionLoc.getDirection().getY());
			Vector vertical = new Vector(linear.getX() * -Math.sin(angle), Math.cos(angle), linear.getZ() * -Math.sin(angle)).normalize().multiply(config.getDouble(section + ".Vertical",0));
			Vector horizontal = linear.getCrossProduct(new Vector(0,1,0)).normalize().multiply(moved * config.getDouble(section + ".Horizontal",0));
			moveVector = vertical.clone().add(horizontal);
			Vector spinVector = moveVector.getCrossProduct(velocity);
			if(spinVector.length() != 0){
				spinVector.normalize().multiply(moveVector.length());
			}
			if(ball.getShooter() instanceof BlockProjectileSource){
				velocity.add(vertical.clone().add(horizontal.clone().multiply(0.65)).multiply(-(15 / velocity.length())));
			}
			ball.setVelocity(velocity);
			if(plugin.getConfig().getBoolean("Particle.MovingBall.Enabled") && Util.getParticle(plugin.getConfig().getConfigurationSection(section)) != null){
				new BallMovingTask(ball, spinVector, acceleration , Util.getParticle(plugin.getConfig().getConfigurationSection(section)), random).runTaskTimer(plugin, 0, 1);
			}else{
				new BallMovingTask(ball, spinVector, acceleration, random).runTaskTimer(plugin, 0, 1);
			}

		}
	}
}
