package com.github.kanesada2.SnowballGame;

import java.util.Collection;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.kanesada2.SnowballGame.api.UmpireCallEvent;

public class AprilFoolListener implements Listener {
	private SnowballGame plugin;

	public AprilFoolListener(SnowballGame plugin) {
        this.plugin = plugin;
    }

	@EventHandler(priority = EventPriority.LOW)
	public void onKnockerDamaged(EntityDamageEvent event){
		if(!(event.getEntity() instanceof ArmorStand)){
			return;
		}
		if(Util.isHeroineName(event.getEntity().getCustomName())){
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
	public void onKnockerDamaged(UmpireCallEvent e){
		if(!(e.getBall().getShooter() instanceof Player)) return;

		Player player = (Player)e.getBall().getShooter();
		Collection<Entity> entities = player.getNearbyEntities(50, 10, 50);
		for(Entity entity : entities){
			if(!(entity instanceof ArmorStand)) continue;
			if(!Util.isHeroineName(entity.getCustomName())) continue;
			String name = entity.getCustomName();
			String ballMove = "";
			if(e.getBall().hasMetadata("moving")){
				ballMove = e.getBall().getMetadata("moving").get(0).asString();
			}
			switch(name){
			case "Masumi":
				if(ballMove.equals("Fast") && e.getBall().getLocation().getY() - e.getEntity().getLocation().getY() < 0.9){
					new CoachAction(plugin, (ArmorStand)entity).glad();
					return;
				}
				break;
			case "Yui":
				if(e.getBall().getVelocity().length() > 1.8){
					new CoachAction(plugin, (ArmorStand)entity).glad();
					return;
				}
				break;
			case "Nagisa":
				if(ballMove.equals("Slider")){
					new CoachAction(plugin, (ArmorStand)entity).glad();
					return;
				}
				break;
			case "Chihiro":
				if(!ballMove.equals("Fast") && plugin.getConfig().getStringList("Ball.Move.Type").contains((ballMove))){
					new CoachAction(plugin, (ArmorStand)entity).glad();
					return;
				}
				break;
			}
		}
	}

}
