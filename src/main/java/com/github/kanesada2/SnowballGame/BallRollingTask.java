package com.github.kanesada2.SnowballGame;

import org.bukkit.Particle;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

public class BallRollingTask extends BukkitRunnable {
    private Projectile ball;
    private int time = 0;
	public BallRollingTask(Projectile ball) {
        this.ball = ball;
    }
    @Override
    public void run() {
    	if(ball.isDead() || time > 600){
    		this.cancel();
    	}
    	if(ball.getVelocity().length() < 0.05 || !Util.doesRepel(ball.getLocation().add(0, -0.15 ,0).getBlock())){
    		ball.setGravity(true);
    		this.cancel();
    	}else if(Util.doesRegardUp(ball.getLocation().add(0, -1 ,0).getBlock()) && !Util.doesRegardUp(ball.getLocation().add(0, -1.15 ,0).getBlock())){
    		ball.setGravity(true);
    		this.cancel();
    	}
    	time++;
    	ball.getWorld().spawnParticle(Particle.SNOWBALL, ball.getLocation(), 1);
    	ball.setVelocity(ball.getVelocity().multiply(0.98));
    }

}
