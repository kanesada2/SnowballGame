package com.github.kanesada2.SnowballGame;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.util.Vector;

import com.github.kanesada2.SnowballGame.api.BallBounceEvent;
import com.github.kanesada2.SnowballGame.api.PlayerCatchBallEvent;
import com.github.kanesada2.SnowballGame.api.PlayerSwingBatEvent;
import com.github.kanesada2.SnowballGame.api.PlayerThrowBallEvent;
import com.github.kanesada2.SnowballGame.api.SnowballGameAPI;

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
				String gloveName = "";
				if(Util.isGlove(player.getInventory().getItemInOffHand())){
					ItemMeta glove = player.getInventory().getItemInOffHand().getItemMeta();
					if(glove.hasDisplayName()){
						gloveName = glove.getDisplayName();
					}
				}
				HashMap<String,Vector> modifiers = SnowballGameAPI.getModifiersFromGloveName(gloveName, player.getEyeLocation(), isR);
				Vector rpModifier = modifiers.get("rp");
				Vector vlModifier = modifiers.get("velocity");

				Projectile old = projectile;
				event.setCancelled(true);
				Location location = old.getLocation().add(rpModifier);
				String ballName = "";
				Vector spinVector = new Vector(0,0,0);
				Vector vModifier = new Vector(0,0,0);
				double acceleration = 0;
				double random = 0;
				Particle tracker = null;
				if(hand.getItemMeta().hasDisplayName()){
					ballName = hand.getItemMeta().getDisplayName();
					HashMap<String,Object> values = SnowballGameAPI.getBallValuesFromName(ballName, old.getVelocity(), isR, false);
					spinVector = (Vector)values.get("spinVector");
					vModifier = (Vector)values.get("vModifier");
					acceleration = (double)values.get("acceleration");
					random = (double)values.get("random");
					tracker = (Particle)values.get("tracker");
				}
				Vector velocity = old.getVelocity().add(vlModifier).add(player.getVelocity());
				PlayerThrowBallEvent throwEvent = new PlayerThrowBallEvent(player, hand, velocity, spinVector, acceleration, random, tracker, location, vModifier);
				Bukkit.getPluginManager().callEvent(throwEvent);
				if(throwEvent.isCancelled()){
					return;
				}
				SnowballGameAPI.launch(throwEvent.getPlayer(), throwEvent.getItemBall(), true, throwEvent.getBallType(), throwEvent.getBallName(), throwEvent.getVelocity(), throwEvent.getSpinVector(), throwEvent.getAcceleration(), throwEvent.getRandom(), throwEvent.getTracker(), throwEvent.getRPoint(), throwEvent.getVModifier());
				player.getWorld().playSound(location, Sound.ENTITY_SNOWBALL_THROW , 1, 0);
			}
		}else if(projectile.getShooter() instanceof BlockProjectileSource){
			BlockProjectileSource source = (BlockProjectileSource)projectile.getShooter();
			Block from = source.getBlock();
			if(from.hasMetadata("ballType")){
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
				double speed = projectile.getVelocity().length();
				Vector velocity = projectile.getVelocity().getMidpoint(validVec).normalize().multiply(speed * 1.4);
				String ballName = "";
				Vector spinVector = new Vector(0,0,0);
				Vector vModifier = new Vector(0,0,0);
				double acceleration = 0;
				double random = 0;
				Particle tracker = null;
				if(from.hasMetadata("moving")){
					ballName = from.getMetadata("moving").get(0).asString();
					HashMap<String,Object> values = SnowballGameAPI.getBallValuesFromName(ballName, velocity, true, true);
					spinVector = (Vector)values.get("spinVector");
					vModifier = (Vector)values.get("vModifier");
					acceleration = (double)values.get("acceleration");
					random = (double)values.get("random");
					tracker = (Particle)values.get("tracker");
					from.removeMetadata("moving", plugin);
				}
				projectile.remove();
				SnowballGameAPI.launch(source, null, true, from.getMetadata("ballType").get(0).asString(), ballName, velocity, spinVector, acceleration, random, tracker, projectile.getLocation(), vModifier);
				from.removeMetadata("ballType", plugin);
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
			 projectile.remove();
			 ItemStack ball = Util.getBall(projectile.getMetadata("ballType").get(0).asString());
			 boolean isDirect = false;
			 if(projectile.hasMetadata("moving") && projectile.getMetadata("moving").get(0).asString().equalsIgnoreCase("batted")){
				 isDirect = true;
			 }
			 if(event.getHitEntity() instanceof Player){
				Player player = (Player)event.getHitEntity();
				PlayerInventory inventory = player.getInventory();
				ItemStack offHand = inventory.getItemInOffHand();
				if(plugin.getConfig().getBoolean("Glove.Enabled_Glove") && Util.isGlove(offHand)){
					SnowballGameAPI.tryCatch(player, projectile.getLocation(), new Vector(1,1,1), 10);
					return;
				} else {
					if(plugin.getConfig().getBoolean("Knockback_For_Players") && player.getGameMode() != GameMode.CREATIVE){
						Util.causeKnockBack(player,projectile);
					}
					if(plugin.getConfig().getBoolean("Particle.Hit_to_Players.Enabled") && Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.Hit_to_Players")) != null){
						player.getWorld().spawnParticle(Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.Hit_to_Players")), player.getLocation(), 5, 0.5, 0.5, 0.5);
					}
				}
			 }
			 if(event.getHitBlock() != null){
				Vector spinVector = new Vector(0, 0, 0);
				if(projectile.hasMetadata("spin")){
						spinVector = (Vector)projectile.getMetadata("spin").get(0).value();
				}
				Projectile bounced = SnowballGameAPI.bounce(projectile, event.getHitBlock(), new Vector(0.7, 0.4, 0.7), spinVector, isDirect);
				if(projectile.getVelocity().length() < 0.15){
					bounced.remove();
				}else{
					return;
				}
			 }else if(Util.isEntityCoach(event.getHitEntity())){
				 if(new CoachAction(plugin, (ArmorStand)event.getHitEntity()).ballHitAction(projectile)){
					 return;
				 }
			 }
			 projectile.getWorld().dropItem(projectile.getLocation(), ball);
			} else {
			Entity hitEntity = event.getHitEntity();
			if(hitEntity instanceof Player){
				Player player = (Player)hitEntity;
				if(plugin.getConfig().getBoolean("Knockback_For_Players") && player.getGameMode() != GameMode.CREATIVE){
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
		event.setCancelled(true);
		Entity batArrow = event.getProjectile();
		float force = event.getForce();
		double size = 2.2 - force;
		Vector batRange = new Vector(size, size, size);
		Location impactLoc = batArrow.getLocation().add(batArrow.getVelocity().multiply(1/force));
		Player player = (Player)event.getEntity();
		String bowName = "";
		if(event.getBow().hasItemMeta() && event.getBow().getItemMeta().hasDisplayName()){
			bowName = event.getBow().getItemMeta().getDisplayName();
		}
		int rolld = -1;
		if(Util.isBat(player.getInventory().getItemInMainHand())){
			if(player.getMainHand() == MainHand.LEFT){
				rolld = 1;
			}
		}else if(Util.isBat(player.getInventory().getItemInOffHand()) && !Util.isBat(player.getInventory().getItemInMainHand())){
			if(player.getMainHand() == MainHand.RIGHT){
				rolld = 1;
			}
		}
		Vector batMove = SnowballGameAPI.getBatPositionFromName(player.getEyeLocation(), (Math.PI / 2 + 0.01) * rolld, rolld, bowName).subtract(SnowballGameAPI.getBatPositionFromName(player.getEyeLocation(), Math.PI / 2 * rolld, rolld, bowName)).toVector().normalize();
		PlayerSwingBatEvent swingEvent = new PlayerSwingBatEvent(player, event.getBow(), impactLoc, batRange, force, 1.3, batMove, 1);
		Bukkit.getPluginManager().callEvent(swingEvent);
		if(swingEvent.isCancelled()){
			return;
		}
		SnowballGameAPI.tryHit(swingEvent.getPlayer(), swingEvent.getCenter(), swingEvent.getHitRange(), swingEvent.getForce(), swingEvent.getRate(), swingEvent.getBatMove(), swingEvent.getCoefficient());
		if(force > 0.7){
			Location playerLoc = player.getLocation();
			playerLoc.setY(impactLoc.getY());
			player.getWorld().playSound(playerLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, force, 1);
			if(plugin.getConfig().getBoolean("Particle.Swing_Bat.Enabled") && Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.Swing_Bat")) != null){
				player.getWorld().spawnParticle(Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.Swing_Bat")), playerLoc, 1);
			}
			if(plugin.getConfig().getBoolean("Particle.Swing_Bat_Sequent.Enabled") && Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.Swing_Bat_Sequent")) != null){
				Location eye = player.getEyeLocation();
				for(double i=0; Math.abs(i)<Math.PI; i=i+0.15708 * rolld){
					Location swing = SnowballGameAPI.getBatPositionFromName(eye, i, rolld, bowName);
					swing.add(0, eye.getDirection().getY() + 1, 0);
					player.getWorld().spawnParticle(Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.Swing_Bat_Sequent")), swing, 1);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onInteracted(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.BREWING_STAND){
			ItemStack hand;
			if(Util.isBall(player.getInventory().getItemInMainHand())){
				hand = player.getInventory().getItemInMainHand();
			}else if(Util.isBall(player.getInventory().getItemInOffHand())){
				hand = player.getInventory().getItemInOffHand();
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
			placedBall.setShooter(player);
			placedBall.setGravity(false);
			placedBall.setGlowing(true);
			if(player.getGameMode() == GameMode.CREATIVE){
				return;
			}
			hand.setAmount(hand.getAmount() - 1);
		}else if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.hasItem() && Util.isCoach(event.getItem())){
				player.setMetadata("coachsetter", new FixedMetadataValue(plugin, true));
			}else{
				if(player.hasMetadata("catchTried")){
					return;
				}
				if(!(plugin.getConfig().getBoolean("Glove.Enabled_Glove") && Util.isGlove(player.getInventory().getItemInOffHand()) && player.getInventory().getItemInMainHand().getType() == Material.AIR)){
					return;
				}
				event.setCancelled(SnowballGameAPI.tryCatch(player, player.getEyeLocation(), new Vector(3, 4, 3), 8));
				player.setMetadata("catchTried", new FixedMetadataValue(plugin,true));
				plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable()
			      {
			        @Override
			        public void run()
			        {
			        	player.removeMetadata("catchTried", plugin);
			        }
			      }, (4L));
			}
		}else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
			if(!(event.getClickedBlock().getDrops().contains(new ItemStack(Material.STEP,1,(short)7)) || event.getClickedBlock().getType() == Material.QUARTZ_BLOCK)){
				return;
			}
			String msg;
			int range;
			Collection<Entity> entities;
			Location loc = player.getLocation();
			if(plugin.getConfig().getBoolean("Glove.Enabled_Glove") && Util.isGlove(player.getInventory().getItemInOffHand()) && player.getInventory().getItemInMainHand().getType() == Material.AIR){
				msg = plugin.getConfig().getString("Broadcast.Standing_Base.Message");
				range = plugin.getConfig().getInt("Broadcast.Standing_Base.Range", 0);
				entities = loc.getWorld().getNearbyEntities(loc, 2, 2, 2);
			}else if(event.hasItem() && Util.isBat(event.getItem())){
				msg = plugin.getConfig().getString("Broadcast.Reach_Base.Message");
				range = plugin.getConfig().getInt("Broadcast.Reach_Base.Range", 0);
				entities = loc.getWorld().getNearbyEntities(loc, 0.9, 1, 0.9);
			}else{
				return;
			}
			event.setCancelled(true);
			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", player.getName().toString());
			for (Entity entity : entities) {
				if(entity instanceof ArmorStand && Util.isUmpire(((ArmorStand)entity).getBoots()) && entity.getCustomName() != null){
					msg = msg.replaceAll("\\Q[[BASE]]\\E", entity.getCustomName());
					Util.broadcastRange(player, Util.addColors(msg), range);
				}else if(entity instanceof ArmorStand && Util.isBase(((ArmorStand)entity).getBoots()) && entity.getCustomName() != null){
					msg = msg.replaceAll("\\Q[[BASE]]\\E", entity.getCustomName());
					Util.broadcastRange(player, Util.addColors(msg), range);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onTagged(EntityDamageByEntityEvent event){
		if(!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)){
			return;
		}
		Player runner = (Player)event.getEntity();
		Player fielder = (Player)event.getDamager();
		if(Util.isBall(fielder.getInventory().getItemInMainHand())){
			event.setCancelled(true);
			if(plugin.getConfig().getBoolean("Broadcast.Tag.Enabled")){
				String msg = plugin.getConfig().getString("Broadcast.Tag.Message");
				int range = plugin.getConfig().getInt("Broadcast.Tag.Range", 0);
				msg = msg.replaceAll("\\Q[[PLAYER]]\\E", fielder.getName().toString());
				msg = msg.replaceAll("\\Q[[RUNNER]]\\E", runner.getName().toString());
				msg = Util.addColors(msg);
				Util.broadcastRange(fielder, msg, range);
			}
		}else if(Util.isBat(fielder.getInventory().getItemInMainHand())){
			event.setCancelled(true);
			fielder.sendMessage("OOPS! It is not allowed to slug with a bat more valuable thing than telephone in dugout.");
			String msg = plugin.getConfig().getString("Broadcast.Reach_Base.Message");
			int range = plugin.getConfig().getInt("Broadcast.Reach_Base.Range", 0);
			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", fielder.getName().toString());
			Location loc = fielder.getLocation();
			Collection<Entity>entities = loc.getWorld().getNearbyEntities(loc, 0.9, 1, 0.9);
			for (Entity entity : entities) {
				if(entity instanceof ArmorStand && Util.isUmpire(((ArmorStand)entity).getBoots()) && entity.getCustomName() != null){
					msg = msg.replaceAll("\\Q[[BASE]]\\E", entity.getCustomName());
					Util.broadcastRange(fielder, Util.addColors(msg), range);
				}else if(entity instanceof ArmorStand && Util.isBase(((ArmorStand)entity).getBoots()) && entity.getCustomName() != null){
					msg = msg.replaceAll("\\Q[[BASE]]\\E", entity.getCustomName());
					Util.broadcastRange(fielder, Util.addColors(msg), range);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onBasePlaced(BlockPlaceEvent event){
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if(!(Util.isBase(item) || Util.isUmpire(item))) return;
		Util.setUpBase(event.getBlock(), item, Util.isUmpire(item));
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onBaseBroken(BlockBreakEvent event){
		if(!(event.getBlock().getType() == Material.QUARTZ_BLOCK || event.getBlock().getDrops().contains(new ItemStack(Material.STEP,1,(short)7)))){
			return;
		}
		Block target = event.getBlock();
		Location loc = target.getLocation();
		Collection <Entity> entities = loc.getWorld().getNearbyEntities(loc, 2, 2, 2);
		boolean isDropped = false;
		for (Entity entity : entities) {
			if(!(Util.isMyMarker(entity))) continue;
			ItemStack label = ((ArmorStand)entity).getBoots();
			String locString = label.getItemMeta().getDisplayName();
			String[] coords = locString.split(",");
			if(coords.length != 3){
				entity.remove();
				continue;
			}
			Location labelLoc = new Location(loc.getWorld(), Float.parseFloat(coords[0]), Float.parseFloat(coords[1]), Float.parseFloat(coords[2]));
			if(! loc.equals(labelLoc)) continue;
			entity.remove();
			event.setCancelled(true);
			if(target.getType() != Material.AIR){
				target.setType(Material.AIR);
			}
			if(isDropped) continue;
			ItemStack drop = Util.getUmpire();
			if(Util.isBaseMarker(entity)){
				drop = Util.getBase();
			}
			loc.getWorld().dropItemNaturally(loc, drop);
			isDropped = true;
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onCoachSpawned(CreatureSpawnEvent event){
		if(!(event.getEntity() instanceof ArmorStand)){
			return;
		}
		Collection <Entity> entities = event.getEntity().getNearbyEntities(5, 5, 5);
		for(Entity entity: entities){
			if(entity.hasMetadata("coachsetter")){
				new CoachAction(plugin, (ArmorStand)event.getEntity()).init();
				entity.removeMetadata("coachsetter", plugin);
				break;
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onKnockerDamaged(EntityDamageEvent event){
		if(Util.isEntityCoach(event.getEntity())){
			if(event.getCause() == DamageCause.ENTITY_ATTACK){
				ArmorStand coach = (ArmorStand)event.getEntity();
				new CoachAction(plugin, coach).drop();
				coach.remove();
			}else{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onKnockerManipulated(PlayerArmorStandManipulateEvent event){
		if(!Util.isEntityCoach(event.getRightClicked())){
			return;
		}
		ArmorStand coach = (ArmorStand)event.getRightClicked();
		List<ItemStack> defs = new CoachAction(plugin, coach).getDefaultEquips();
		ItemStack newItem = event.getPlayerItem();
		if(!defs.contains(event.getArmorStandItem())){
			return;
		}
		event.setCancelled(true);
		switch(event.getSlot()){
			case HAND :
				coach.getEquipment().setItemInMainHand(newItem);
				break;
			case OFF_HAND :
				coach.getEquipment().setItemInOffHand(newItem);
				break;
			case HEAD :
				coach.setHelmet(newItem);
				break;
			case CHEST :
				coach.setChestplate(newItem);
				break;
			case FEET :
				coach.setBoots(newItem);
				break;
			case LEGS :
				coach.setLeggings(newItem);
				break;
		}
		Player player = event.getPlayer();
		if(player.getGameMode() != GameMode.CREATIVE && !defs.contains(newItem)){
			event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		}

	}
	@EventHandler(priority = EventPriority.LOW)
	public void onSlide(PlayerToggleSneakEvent event){
		Player player = event.getPlayer();
		if(event.isSneaking() && Util.isBall(player.getInventory().getItemInMainHand())){
			Location loc = player.getLocation();
			Collection <Entity> entities = loc.getWorld().getNearbyEntities(loc, 0.8, 0.5, 0.8);
			for (Entity entity : entities) {
				if(Util.isMyMarker(entity)){
					String msg = plugin.getConfig().getString("Broadcast.Touch_Base.Message");
					int range = plugin.getConfig().getInt("Broadcast.Touch_Base.Range", 0);
					msg = msg.replaceAll("\\Q[[PLAYER]]\\E", player.getName().toString());
					Util.broadcastRange(player, Util.addColors(msg).replaceAll("\\Q[[BASE]]\\E", entity.getCustomName()), range);
				}
			}
		}
		if(Util.isGlove(player.getInventory().getItemInOffHand()) || Util.isBat(player.getInventory().getItemInMainHand()) || Util.isBat(player.getInventory().getItemInOffHand())){
			if(event.getPlayer().hasMetadata("onSlide") || !event.getPlayer().isSprinting()){
				return;
			}
			player.setVelocity(player.getLocation().getDirection().normalize().multiply(1.2).setY(0));
			player.setMetadata("onSlide", new FixedMetadataValue(plugin,true));
			new PlayerCoolDownTask(plugin,player).runTaskLater(plugin, 50);
			Location newfacing = player.getLocation();
			newfacing.setPitch(60);
			player.teleport(newfacing);
			player.sendMessage(Util.addColors("[[DARK_AQUA]][[BOLD]]*** YOU ARE TRYING TO DIVE! ***"));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 128));
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onCatch(PlayerCatchBallEvent event){
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		Collection <Entity> entities = loc.getWorld().getNearbyEntities(loc, 0.8, 0.5, 0.8);
		for (Entity entity : entities) {
			if(Util.isMyMarker(entity)){
				String msg = plugin.getConfig().getString("Broadcast.Touch_Base.Message");
				int range = plugin.getConfig().getInt("Broadcast.Touch_Base.Range", 0);
				msg = msg.replaceAll("\\Q[[PLAYER]]\\E", player.getName().toString());
				Util.broadcastRange(player, Util.addColors(msg).replaceAll("\\Q[[BASE]]\\E", entity.getCustomName()), range);
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onToss(PlayerSwapHandItemsEvent event){
		if(!Util.isBall(event.getOffHandItem())){
			return;
		}
		event.setCancelled(true);
		Player player = event.getPlayer();
		Location eye = player.getEyeLocation();
		Vector velocity = eye.getDirection().normalize();
		Vector zero = new Vector(0,0,0);
		velocity.add(zero.clone().setY(0.5)).multiply(0.5);
		PlayerThrowBallEvent throwEvent = new PlayerThrowBallEvent(player, player.getInventory().getItemInMainHand(), velocity, zero, 0, 0, null, eye.add(zero.clone().setY(-1)), zero);
		Bukkit.getPluginManager().callEvent(throwEvent);
		if(throwEvent.isCancelled()){
			return;
		}
		SnowballGameAPI.launch(throwEvent.getPlayer(), throwEvent.getItemBall(), false, throwEvent.getBallType(), throwEvent.getBallName(), throwEvent.getVelocity(), throwEvent.getSpinVector(), throwEvent.getAcceleration(), throwEvent.getRandom(), throwEvent.getTracker(), throwEvent.getRPoint(), throwEvent.getVModifier());
		player.getWorld().playSound(eye.add(zero.clone().setY(-1)), Sound.ENTITY_SNOWBALL_THROW , 0.5f, 0);
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onThrow(PlayerThrowBallEvent event){
		if(event.isCancelled()){
			return;
		}
		Player player = event.getPlayer();
		if(player.hasMetadata("onMotion") || player.hasMetadata("onSlide")){
			player.sendMessage("You can't throw the ball so quickly.");
			event.setCancelled(true);
			return;
		}
		player.setMetadata("onMotion", new FixedMetadataValue(plugin, true));
		new PlayerCoolDownTask(plugin, player).runTaskLater(plugin, plugin.getConfig().getInt("Ball.Cool_Time", 30));
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBounce(BallBounceEvent event){
		if(plugin.getConfig().getBoolean("Particle.BattedBall_Ground.Enabled") && Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.BattedBall_Ground")) != null && event.isFirst()){
			AreaEffectCloud cloud = (AreaEffectCloud)event.getBeforeBounce().getWorld().spawnEntity(event.getBeforeBounce().getLocation(), EntityType.AREA_EFFECT_CLOUD);
			cloud.setParticle(Util.getParticle(plugin.getConfig().getConfigurationSection("Particle.BattedBall_Ground")));
			cloud.setDuration(plugin.getConfig().getInt("Particle.BattedBall_Ground.Time",200));
			cloud.setRadius(1.5f);
		}
	}

}

