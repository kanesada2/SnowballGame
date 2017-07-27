package com.github.kanesada2.SnowballGame;

import org.bukkit.Particle;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BallMovingTask extends BukkitRunnable {
    private Vector moveVector;
    private Projectile ball;
    private Particle particle = null;
    private double random = 0;
    private double x,y,z;

	public BallMovingTask(Projectile ball, Vector moveVector, Particle particle, double random) {
        this.ball = ball;
        this.particle = particle;
        this.random = random;
        if(random != 0){
        	this.x = Math.random() * 2 * Math.PI;
        	this.y = Math.random() * 2 * Math.PI;
        	this.z = Math.random() * 2 * Math.PI;
        }
        this.moveVector = moveVector;
    }
	public BallMovingTask(Projectile ball, Vector moveVector, double random) {
        this.ball = ball;
        this.random = random;
        if(random != 0){
        	this.x = Math.random() * 2 * Math.PI;
        	this.y = Math.random() * 2 * Math.PI;
        	this.z = Math.random() * 2 * Math.PI;
        }
        this.moveVector = moveVector;
	}
    @Override
    public void run() {
    	if(!ball.hasMetadata("moving")){
    		this.cancel();
    	}
    	Vector velocity = ball.getVelocity();
    	if(random != 0){
        	this.x += Math.random() * 0.3;
        	this.y += Math.random() * 0.3;
        	this.z += Math.random() * 0.3;
        	Vector toAdd = new Vector(Math.sin(x), Math.sin(y), Math.sin(z));
        	toAdd.multiply(random);
        	velocity.add(toAdd);
    	}
        velocity.add(moveVector);
    	ball.setVelocity(velocity);
    	if(particle != null){
    		ball.getWorld().spawnParticle(particle, ball.getLocation(), 5, 0.5, 0.5, 0.5);
    	}
    }

}
