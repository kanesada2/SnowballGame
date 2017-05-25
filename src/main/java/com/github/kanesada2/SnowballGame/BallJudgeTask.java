package com.github.kanesada2.SnowballGame;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

public class BallJudgeTask extends BukkitRunnable {
	private Location inBottom;
	private Location outTop;
	private Projectile ball;
	private int count;
	private SnowballGame plugin;
	public BallJudgeTask(Projectile ball, Location inBottom, Location outTop, SnowballGame plugin) {
        this.inBottom = inBottom;
        this.outTop = outTop;
        this.ball = ball;
        this.count = 0;
        this.plugin = plugin;
    }
    @Override
    public void run() {
    	count++;
    	if(!ball.hasMetadata("moving")){
    		this.cancel();
    	}
    	if(count > 100){
    		this.cancel();
    	}
    	for(double i=0; i<1; i=i+0.1){
	    	if(ball.hasMetadata("moving") && ball.getLocation().add(ball.getVelocity().multiply(i)).toVector().isInAABB(inBottom.toVector(), outTop.toVector())){
	    		String msg = plugin.getConfig().getString("Broadcast.Strike.Message").replaceAll("\\Q[[SPEED]]\\E", String.format("%.1f", ball.getVelocity().length() * 72) + "km/h");
	    		msg = msg.replaceAll("\\Q[[TYPE]]\\E", ball.getMetadata("moving").get(0).asString());
	    		if(ball.getShooter() instanceof Player){
	    			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", ((Player)ball.getShooter()).getDisplayName());
	    		}else if(ball.getShooter() instanceof BlockProjectileSource){
	    			msg = msg.replaceAll("\\Q[[PLAYER]]\\E", "Dispenser");
	    		}
	    		msg = Util.addColors(msg);
	    		Util.broadcastRange(plugin, ball, msg, plugin.getConfig().getInt("Broadcast.Strike.Range"));
	    		this.cancel();
	    		break;
	    	}
    	}
    }

}
