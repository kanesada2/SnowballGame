package com.github.kanesada2.SnowballGame.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerSwingBatEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private ItemStack bat;
	private Location center;
	private Vector hitRange;
	private float force;
	private double rate;
	private Vector batMove;
	double coefficient;

	public PlayerSwingBatEvent(Player who, ItemStack bat, Location center, Vector hitRange, float force, double rate, Vector batMove, double coefficient) {
		super(who);
		this.bat = bat;
		this.center = center;
		this.hitRange = hitRange;
		this.force = force;
		this.rate = rate;
		this.batMove = batMove;
		this.coefficient = coefficient;

	}
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public ItemStack getBat(){
		return bat;
	}

	public Location getCenter(){
		return center;
	}

	public void setCenter(Location center){
		this.center = center;
	}

	public Vector getHitRange(){
		return hitRange;
	}

	public void setHitRange(Vector hitRange){
		this.hitRange = hitRange;
	}
	public float getForce(){
		return force;
	}

	public void setForce(float force){
		this.force = force;
	}

	public double getRate(){
		return rate;
	}

	public void setRate(double rate){
		this.rate = rate;
	}

	public Vector getBatMove(){
		return batMove;
	}

	public void setBatMove(Vector batMove){
		this.batMove = batMove;
	}

	public double getCoefficient(){
		return coefficient;
	}

	public void setCoefficient(double coefficient){
		this.coefficient = coefficient;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
