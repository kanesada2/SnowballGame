package com.github.kanesada2.SnowballGame;


import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

public class PlayerHitBallEvent extends PlayerEvent{

	private static final HandlerList handlers = new HandlerList();
	private Projectile ball;
	private Vector spinVector;
	public PlayerHitBallEvent(Player who, Projectile ball, Vector spinVector) {
		super(who);
		this.ball = ball;
		this.spinVector = spinVector;

	}
	public Projectile getBall(){
		return ball;
	}
	public Vector getSpinVector(){
		return spinVector;
	}
	public void setBall(Projectile ball){
		this.ball = ball;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}

}