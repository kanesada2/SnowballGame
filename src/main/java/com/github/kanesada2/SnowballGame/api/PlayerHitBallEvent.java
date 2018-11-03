package com.github.kanesada2.SnowballGame.api;


import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

public class PlayerHitBallEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Projectile beforeHit;
	private Vector spinVector;
	private Vector velocity;
	private double acceleration;
	private double random;
	private Particle tracker;

	public PlayerHitBallEvent(Player who, Projectile beforeHit, Vector spinVector, Vector velocity, double acceleration, double random, Particle tracker) {
		super(who);
		this.beforeHit = beforeHit;
		this.spinVector = spinVector;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.random = random;
		this.tracker = tracker;

	}
	public Vector getVelocity() {
		return velocity;
	}
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	public double getAcceleration() {
		return acceleration;
	}
	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}
	public double getRandom() {
		return random;
	}
	public void setRandom(double random) {
		this.random = random;
	}
	public Particle getTracker() {
		return tracker;
	}
	public void setTracker(Particle tracker) {
		this.tracker = tracker;
	}
	public void setBeforeHit(Projectile beforeHit) {
		this.beforeHit = beforeHit;
	}
	public void setSpinVector(Vector spinVector) {
		this.spinVector = spinVector;
	}
	public Projectile getBeforeHit(){
		return beforeHit;
	}
	public Vector getSpinVector(){
		return spinVector;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}