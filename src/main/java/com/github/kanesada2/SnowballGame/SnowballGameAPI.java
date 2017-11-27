package com.github.kanesada2.SnowballGame;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class SnowballGameAPI {
	private static SnowballGame plugin = SnowballGame.getPlugin(SnowballGame.class);

	public static Projectile launch(ProjectileSource shooter, ItemStack from, boolean isPitching, String ballType, String ballName, Vector velocity, Vector spinVector, double acceleration, double random, Particle tracker, Location rPoint,Vector vModifier){
		Projectile launched = (Projectile)rPoint.getWorld().spawnEntity(rPoint, EntityType.SNOWBALL);
		launched.setGravity(true);
		launched.setGlowing(true);
		launched.setVelocity(velocity.add(vModifier));
		launched.setShooter(shooter);
		launched.setMetadata("ballType", new FixedMetadataValue(plugin, ballType));
		if(ballName != null){
			launched.setMetadata("moving", new FixedMetadataValue(plugin, ballName));
		}
		if(isPitching){
			launched.getWorld().playSound(rPoint, Sound.ENTITY_ARROW_SHOOT, 1, 0);
			if(plugin.getConfig().getBoolean("Particle.Throw_Ball.Enabled")){
				rPoint.getWorld().spawnParticle(Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.Throw_Ball")), rPoint, 3);
			}
			if(shooter instanceof Player){
				Player player = (Player)shooter;
				player.setMetadata("onMotion", new FixedMetadataValue(plugin, true));
				new PlayerCoolDownTask(plugin, player).runTaskLater(plugin, plugin.getConfig().getInt("Ball.Cool_Time", 30));
				if(player.getGameMode() != GameMode.CREATIVE){
					from = null;
				}
			}
			if(from != null){
					from.setAmount(from.getAmount() - 1);
			}
			Collection<Entity> entities = launched.getNearbyEntities(50, 10, 50);
			for (Entity entity : entities) {
				if(entity instanceof ArmorStand && entity.getCustomName() != null && entity.getCustomName().equalsIgnoreCase(plugin.getConfig().getString("Umpire.Umpire_Name"))){
					Location inBottom = entity.getLocation().add(new Vector(-0.5, plugin.getConfig().getDouble("Umpire.Bottom"), -0.5));
					Location outTop = entity.getLocation().add(new Vector(0.5, plugin.getConfig().getDouble("Umpire.Top") , 0.5));
					new BallJudgeTask(launched, inBottom, outTop, plugin).runTaskTimer(plugin, 0, 1);
				}
			}
		}
		if(!(spinVector.length() == 0 && acceleration == 0 && random == 0)){
			if(tracker != null){
				new BallMovingTask(launched, spinVector, acceleration , tracker, random).runTaskTimer(plugin, 0, 1);
			}else{
				new BallMovingTask(launched, spinVector, acceleration, random).runTaskTimer(plugin, 0, 1);
			}
		}
		return launched;
	}
	public static Projectile tryHit(Player player, Location center, Vector range, float force, double rate, Vector batMove, double coefficient){
		Entity meetCursor = center.getWorld().spawnEntity(center, EntityType.SNOWBALL);
		meetCursor.setGravity(false);
		meetCursor.remove();
		Collection <Entity> nearByEntities = center.getWorld().getNearbyEntities(center, range.getX(), range.getY(), range.getZ());
		String msg;
		int brRange;
		if(force < 0.4 && plugin.getConfig().getBoolean("Broadcast.Bunt.Enabled")){
			msg = plugin.getConfig().getString("Broadcast.Bunt.Message");
			brRange = plugin.getConfig().getInt("Broadcast.Bunt.Range");
			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", player.getName().toString());
			msg = Util.addColors(msg);
			Util.broadcastRange(plugin, player, msg, brRange);
		}else if(force >= 0.4 && plugin.getConfig().getBoolean("Broadcast.Swing.Enabled")){
			msg = plugin.getConfig().getString("Broadcast.Swing.Message");
			brRange = plugin.getConfig().getInt("Broadcast.Swing.Range");
			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", player.getName().toString());
			msg = Util.addColors(msg);
			Util.broadcastRange(plugin, player, msg, brRange);
		}
		for (Entity entity : nearByEntities) {
			if(entity.getType() == EntityType.SNOWBALL && entity.hasMetadata("ballType")){
				if(plugin.getConfig().getBoolean("Broadcast.Hit.Enabled")){
					msg = plugin.getConfig().getString("Broadcast.Hit.Message");
					brRange = plugin.getConfig().getInt("Broadcast.Hit.Range");
					msg = msg.replaceAll("\\Q[[PLAYER]]\\E", player.getName().toString());
					msg = Util.addColors(msg);
					Util.broadcastRange(plugin, player, msg, brRange);
				}
				if(plugin.getConfig().getBoolean("Particle.Hit_by_Bat.Enabled") && Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.Hit_by_Bat")) != null){
					player.getWorld().spawnParticle(Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.Hit_by_Bat")), center, 1);
				}
				Vector velocity = entity.getVelocity();
				Vector fromCenter = entity.getLocation().toVector().subtract(center.toVector());
				double power = force * Math.pow(rate, -fromCenter.length());
				switch(entity.getMetadata("ballType").get(0).asString()){
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
				entity.remove();
				center.getWorld().playSound(center, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE , force, 1);
				Vector spinVector = fromCenter.getCrossProduct(velocity).normalize().multiply(fromCenter.length());
				Particle tracker = null;
				if(plugin.getConfig().getBoolean("Particle.BattedBall_InFlight.Enabled")){
					tracker = Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.BattedBall_InFlight"));
				}
				Projectile hitball = SnowballGameAPI.launch(player, null, false, entity.getMetadata("ballType").get(0).asString(), "batted", velocity, spinVector.multiply(0.008 * force), 0, 0, tracker, entity.getLocation(), new Vector(0,0,0));
				return hitball;
			}
		}
		return null;
	}
	public static Projectile playWithCoach(Player player, ArmorStand coach, String ballType){
		Vector knockedVec = player.getLocation().toVector().subtract(coach.getLocation().toVector()).normalize();
		double distance = Math.sqrt(player.getLocation().distanceSquared(coach.getLocation()));
		double randomY = (Math.random() - Math.random()) * (distance / 30);
		knockedVec.multiply(Math.pow(2.2, -randomY));
		Vector randomizer = new Vector((Math.random() - Math.random()) / (distance / 8), randomY , (Math.random() - Math.random()) / (distance / 8));
		knockedVec.add(randomizer);
		if(knockedVec.angle(knockedVec.clone().setY(0)) > 0.5){
			knockedVec.multiply(0.7);
		}
		knockedVec.multiply(distance / 25);
		Particle tracker = null;
		if(plugin.getConfig().getBoolean("Particle.BattedBall_InFlight.Enabled")){
			tracker = Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.BattedBall_InFlight"));
		}
		player.sendMessage("Catch the ball!!!");
		return SnowballGameAPI.launch((ProjectileSource)coach, null, false, ballType, "batted", knockedVec, Vector.getRandom().normalize().multiply(0.0015 * knockedVec.length()), 0, 0, tracker, coach.getEyeLocation().add(knockedVec.clone().normalize().multiply(0.5)), new Vector(0,0,0));

	}
	public static Projectile bounce(Projectile ball, Block hitBlock, Vector repulsion, Vector spinVector){
		if(repulsion.getX() < 0 || repulsion.getY() < 0 || repulsion.getZ() < 0){
			Bukkit.getLogger().info("Repulsion rate must be positive.");
			return ball;
		}
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
				ballSpin = spinVector;
			}
			if(ballSpin.length() != 0){
				moveFromSpin = velocity.clone().multiply(-1).getCrossProduct(ballSpin).normalize().multiply(ballSpin.length() * 11);
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
			moveFromSpin = new Vector(moveFromSpin.getX() * repulsion.getX(), moveFromSpin.getY() * repulsion.getY(), moveFromSpin.getZ() * repulsion.getZ());
			double angle = velocity.angle(linear) / Math.toRadians(90);
			velocity.setX(x * Math.pow(repulsion.getX(), angle));
			velocity.setY(y * Math.pow(repulsion.getY(), angle));
			velocity.setZ(z * Math.pow(repulsion.getZ(), angle));
			velocity.multiply(Math.pow(1.3, -(velocity.length())));
			velocity.add(moveFromSpin);
			ballSpin.multiply(0.01).add(linear.getCrossProduct(normal).multiply(0.003));
		}
		ball.remove();
		bounced = (Projectile)SnowballGameAPI.launch(ball.getShooter(), null, false, ball.getMetadata("ballType").get(0).asString(), null, velocity, new Vector(0,0,0), 0, 0, null, hitLoc, new Vector(0,0,0));
		bounced.setMetadata("spin", new FixedMetadataValue(plugin, ballSpin));
		bounced.setMetadata("bouncedLoc", new FixedMetadataValue(plugin, hitLoc));
		bounced.setMetadata("samePlace", new FixedMetadataValue(plugin, samePlace));
		return bounced;
	}
	public static Vector getBatmoveFromName(Location eye, double roll, int rolld, String batName){
		 Location pushD = eye.clone();
		 pushD.setYaw(pushD.getYaw() + (float)(90 * rolld));
		 Vector direction = pushD.getDirection().setY(0).normalize();
		 SnowballGame plugin = SnowballGame.getPlugin(SnowballGame.class);
		 double upper = 0;
			if(plugin.getConfig().getStringList("Bat.Swing.Type").contains(batName)){
				upper = plugin.getConfig().getDouble("Bat.Swing." + batName + ".Fly", 0);
				if(Math.abs(upper) > 1){
					upper = 1 * Math.signum(upper);
				}
				if(upper > 0){
					upper = upper * 0.5;
				}
			}
		 double theta = Math.abs(roll * 2) + Math.PI * upper;
		 double x = Math.cos(roll) * direction.normalize().getX() * (theta - Math.sin(theta)) - Math.sin(roll) * direction.normalize().getZ() * (theta - Math.sin(theta));
		 double y = -(1 - Math.cos(theta));
		 double z = Math.sin(roll) * direction.normalize().getX() * (theta - Math.sin(theta)) + Math.cos(roll) * direction.normalize().getZ() * (theta - Math.sin(theta));
		 return new Vector (x,y,z);
	 }
	public static HashMap<String, Object> getBallValuesFromName(String ballName, Vector velocity, boolean isRightHanded, boolean isFromDispenser){
		Vector moveVector = new Vector(0,0,0);
		FileConfiguration config = plugin.getConfig();
		int moved;
		Vector spinVector = new Vector(0,0,0);
		Vector vModifier = new Vector(0,0,0);
		double acceleration = 0;
		double random = 0;
		Particle tracker = null;
		if(isRightHanded){
			moved = 1;
		}else{
			moved = -1;
		}
		if(config.getStringList("Ball.Move.Type").contains(ballName)){
			String section = "Ball.Move." + ballName;
			vModifier = velocity.clone().multiply(config.getDouble(section + ".Velocity") - 1);
			if(config.getDouble(section + ".Random") != 0){
				random = config.getDouble(section + ".Random");
			}
			acceleration = config.getDouble(section + ".Acceleration", 0);
			Vector linear = velocity.clone().setY(0).normalize();
			double angle = velocity.angle(linear) * Math.signum(velocity.getY());
			Vector vertical = new Vector(linear.getX() * -Math.sin(angle), Math.cos(angle), linear.getZ() * -Math.sin(angle)).normalize().multiply(config.getDouble(section + ".Vertical",0));
			Vector horizontal = linear.getCrossProduct(new Vector(0,1,0)).normalize().multiply(moved * config.getDouble(section + ".Horizontal",0));
			moveVector = vertical.clone().add(horizontal);
			spinVector = moveVector.getCrossProduct(velocity);
			if(spinVector.length() != 0){
				spinVector.normalize().multiply(moveVector.length());
			}
			if(isFromDispenser){
				vModifier.add(vertical.clone().add(horizontal.clone().multiply(0.65)).multiply(-(15 / velocity.length())));
			}
			if(plugin.getConfig().getBoolean("Particle.MovingBall.Enabled")){
				tracker = Util.getParticle(config.getConfigurationSection(section));
			}
		}
		HashMap<String,Object> values = new HashMap<String,Object>();
		values.put("vModifier", vModifier);
		values.put("spinVector", spinVector);
		values.put("acceleration",acceleration);
		values.put("random", random);
		values.put("tracker", tracker);
		return values;
	}
}
