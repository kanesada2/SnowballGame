package com.github.kanesada2.SnowballGame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.kanesada2.SnowballGame.api.UmpireCallEvent;

public class BallJudgeTask extends BukkitRunnable {
	private ArmorStand umpire;
	private Location inBottom;
	private Location outTop;
	private Projectile ball;
	private int count;
	private SnowballGame plugin;
	public BallJudgeTask(Projectile ball, ArmorStand umpire ,Location inBottom, Location outTop, SnowballGame plugin) {
        this.umpire = umpire;
		this.inBottom = inBottom;
        this.outTop = outTop;
        this.ball = ball;
        this.count = 0;
        this.plugin = plugin;
    }
    @Override
    public void run() {
    	count++;
    	if(ball.isDead() || !ball.hasMetadata("moving")){
    		this.cancel();
    		return;
    	}
    	if(count > 100){
    		this.cancel();
    	}
    	for(double i=0; i<1; i=i+0.1){
	    	if(ball.hasMetadata("moving") && ball.getLocation().clone().add(ball.getVelocity().multiply(i)).toVector().isInAABB(inBottom.toVector(), outTop.toVector())){
	    		String msg = plugin.getConfig().getString("Broadcast.Strike.Message").replaceAll("\\Q[[SPEED]]\\E", String.format("%.1f", ball.getVelocity().length() * 72) + "km/h");
	    		msg = msg.replaceAll("\\Q[[TYPE]]\\E", ball.getMetadata("moving").get(0).asString());
	    		if(ball.getShooter() instanceof Player){
	    			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", ((Player)ball.getShooter()).getDisplayName());
	    		}else if(ball.getShooter() instanceof BlockProjectileSource){
	    			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", "Dispenser");
	    		}
	    		msg = Util.addColors(msg);
	    		UmpireCallEvent event = new UmpireCallEvent(umpire, ball, msg);
	    		Bukkit.getPluginManager().callEvent(event);
	    		if(event.isCancelled()){
	    			this.cancel();
		    		break;
	    		}
	    		Util.broadcastRange(event.getBall(), event.getMsg(), plugin.getConfig().getInt("Broadcast.Strike.Range"));
	    		this.cancel();
	    		break;
	    	}
    	}
    }

}
