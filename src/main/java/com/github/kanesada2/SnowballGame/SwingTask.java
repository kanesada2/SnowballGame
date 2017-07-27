package com.github.kanesada2.SnowballGame;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SwingTask extends BukkitRunnable {
	Location start,impact;
	double theta = 0;
	double roll = 0;
	Vector direction;
	Vector swing = new Vector(0,0,0);
	double x,y,z,upper;
	int rolld;
	public SwingTask(Location eye, Location impact, int rolld, double upper){
		this.start = eye.add(eye.getDirection().normalize().multiply(-0.3));
		this.impact = impact;
		this.rolld = rolld;
		eye.setYaw(eye.getYaw() + (float)(90 * rolld));
		this.direction = eye.getDirection().setY(0).normalize();
		this.upper = upper;
	}
	@Override
	public void run() {
		x = Math.cos(roll) * direction.normalize().getX() * (theta - Math.sin(theta)) - Math.sin(roll) * direction.normalize().getZ() * (theta - Math.sin(theta));
		y = -(1 - Math.cos(theta));
		z = Math.sin(roll) * direction.normalize().getX() * (theta - Math.sin(theta)) + Math.cos(roll) * direction.normalize().getZ() * (theta - Math.sin(theta));
		start.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, start.getX() + x / 2, start.getY() + y / 2, start.getZ() + z / 2, 1);
		if(Math.abs(Math.round(roll * 100)) == 157){
			start.getWorld().spawnParticle(Particle.BARRIER, start.getX() + x / 2, start.getY() + y / 2, start.getZ() + z / 2, 1);
		}
		roll = roll + (Math.PI / 20) * -rolld;
		theta = Math.abs(roll * 2) + Math.PI * upper;
		if(theta >= Math.PI * 2){
			this.cancel();
		}
	}

}
