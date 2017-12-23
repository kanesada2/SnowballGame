package com.github.kanesada2.SnowballGame.api;


import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

public class PlayerHitBallEvent extends PlayerEvent{

	private static final HandlerList handlers = new HandlerList();
	private Projectile ball;
	private Projectile beforeHit;
	private Vector spinVector;
	public PlayerHitBallEvent(Player who, Projectile ball, Projectile beforeHit, Vector spinVector) {
		super(who);
		this.ball = ball;
		this.beforeHit = beforeHit;
		this.spinVector = spinVector;

	}
	public Projectile getBeforeHit(){
		return beforeHit;
	}
	public Projectile getBall(){
		return ball;
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

}