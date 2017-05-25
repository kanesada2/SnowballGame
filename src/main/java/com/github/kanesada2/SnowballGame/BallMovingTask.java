package com.github.kanesada2.SnowballGame;

import org.bukkit.Particle;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BallMovingTask extends BukkitRunnable {
    private Vector moveVector;
    private Projectile ball;
    private Particle particle = null;

	public BallMovingTask(Projectile ball, Vector moveVector, Particle particle) {
        this.ball = ball;
        this.moveVector = moveVector;
        this.particle = particle;
    }
	public BallMovingTask(Projectile ball, Vector moveVector) {
        this.ball = ball;
        this.moveVector = moveVector;
    }
    @Override
    public void run() {
    	if(!ball.hasMetadata("moving")){
    		this.cancel();
    	}
    	Vector velocity = ball.getVelocity();
    	velocity.add(moveVector);
    	ball.setVelocity(velocity);
    	if(particle != null){
    		ball.getWorld().spawnParticle(particle, ball.getLocation(), 5, 0.5, 0.5, 0.5);
    	}
    }

}
