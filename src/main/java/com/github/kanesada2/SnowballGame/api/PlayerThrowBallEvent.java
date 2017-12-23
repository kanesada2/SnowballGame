package com.github.kanesada2.SnowballGame.api;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.kanesada2.SnowballGame.Util;

public class PlayerThrowBallEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private ItemStack hand;
	private String ballType;
	private String ballName = "";
	private Vector velocity;
	private Vector spinVector;
	private double acceleration;
	private double random;
	private Particle tracker;
	private Location rPoint;
	private Vector vModifier;
	public PlayerThrowBallEvent(Player who, ItemStack itemBall, Vector velocity, Vector spinVector, double acceleration, double random, Particle tracker, Location rPoint,Vector vModifier) {
		super(who);
		this.hand = itemBall;
		this.ballType = Util.getBallType(hand.getItemMeta().getLore());
		if(hand.getItemMeta().hasDisplayName()){
			this.ballName = hand.getItemMeta().getDisplayName();
		}
		this.velocity = velocity;
		this.spinVector = spinVector;
		this.acceleration = acceleration;
		this.random = random;
		this.tracker = tracker;
		this.rPoint = rPoint;
		this.vModifier = vModifier;
	}

	public ItemStack getItemBall(){
		return hand;
	}


	public String getBallType(){
		return ballType;
	}

	public void setBallType(String ballType){
		this.ballType = ballType;
	}

	public String getBallName(){
		return ballName;
	}

	public void setBallName(String ballName){
		this.ballName = ballName;
	}

	public Vector getVelocity(){
		return velocity;
	}

	public void setVelocity(Vector velocity){
		this.velocity = velocity;
	}

	public Vector getSpinVector(){
		return spinVector;
	}

	public void setSpinVector(Vector spinVector){
		this.spinVector = spinVector;
	}

	public Vector getVModifier(){
		return vModifier;
	}

	public void setVModifier(Vector vModifier){
		this.vModifier = vModifier;
	}

	public double getAcceleration(){
		return acceleration;
	}

	public void setAcceleration(double acceleration){
		this.acceleration = acceleration;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	public double getRandom(){
		return random;
	}

	public void setRandom(double random){
		this.random = random;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;

	}

	public Location getRPoint(){
		return rPoint;
	}

	public void setRPoint(Location rPoint){
		this.rPoint = rPoint;
	}

	public Particle getTracker(){
		return tracker;
	}

	public void setTracker(Particle tracker){
		this.tracker = tracker;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
