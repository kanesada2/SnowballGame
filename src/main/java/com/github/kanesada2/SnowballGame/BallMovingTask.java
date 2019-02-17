package com.github.kanesada2.SnowballGame;

import org.bukkit.Particle;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BallMovingTask extends BukkitRunnable {
    private Vector actualMove;
    private Vector spinVector = new Vector(0,0,0);
    private Projectile ball;
    private Particle particle = null;
    private double random = 0;
    private double acceleration = 0;
    private double x,y,z;
    private Vector initialMove;
	public BallMovingTask(Projectile ball, Vector spinVector, double acceleration, Particle particle, double random) {
        this.ball = ball;
        this.spinVector = spinVector;
        if(acceleration != 0){
        	this.acceleration = acceleration;
        }
        this.particle = particle;
        this.random = random;
        if(random != 0){
        	this.x = Math.random() * 2 * Math.PI;
        	this.y = Math.random() * 2 * Math.PI;
        	this.z = Math.random() * 2 * Math.PI;
        }
        ball.setMetadata("spin", new FixedMetadataValue(SnowballGame.getPlugin(SnowballGame.class), spinVector));
    }
	public BallMovingTask(Projectile ball, Vector spinVector, double acceleration, double random) {
        this.ball = ball;
        this.spinVector = spinVector;
        if(acceleration != 0){
        	this.acceleration = acceleration;
        }
        this.random = random;
        if(random != 0){
        	this.x = Math.random() * 2 * Math.PI;
        	this.y = Math.random() * 2 * Math.PI;
        	this.z = Math.random() * 2 * Math.PI;
        }
        ball.setMetadata("spin", new FixedMetadataValue(SnowballGame.getPlugin(SnowballGame.class), spinVector));
	}
    @Override
    public void run() {
    	if(ball.isDead() || !ball.hasMetadata("moving")){
    		this.cancel();
    	}
    	Vector velocity = ball.getVelocity();
    	if(spinVector.length() != 0 || acceleration != 0){
	    	actualMove = velocity.getCrossProduct(spinVector);
	    	this.spinVector.multiply(0.99);
	    	if(actualMove.length() != 0){
	    		actualMove.normalize().multiply(spinVector.length());
	    	}
	    	actualMove.add(velocity.clone().normalize().multiply(acceleration));
	    	velocity.add(actualMove);
    	}
    	if(random != 0){
        	this.x += Math.random() * 0.3;
        	this.y += Math.random() * 0.3;
        	this.z += Math.random() * 0.3;
        	Vector toAdd = new Vector(Math.sin(x), Math.sin(y), Math.sin(z));
        	toAdd.multiply(random);
    		velocity.add(toAdd);

    	}
    	ball.setVelocity(velocity);
    	if(particle != null){
    		ball.getWorld().spawnParticle(particle, ball.getLocation(), 5, 0.5, 0.5, 0.5);
    	}
    }

}
