package com.github.kanesada2.SnowballGame.api;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class UmpireCallEvent extends EntityEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Projectile ball;
	private String msg;
	public UmpireCallEvent(ArmorStand who, Projectile ball, String msg){
		super(who);
		this.ball = ball;
		this.msg = msg;
	}

	public Projectile getBall(){
		return ball;
	}

	public void setBall(Projectile ball){
		this.ball = ball;
	}

	public String getMsg(){
		return msg;
	}

	public void setMsg(String msg){
		this.msg = msg;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;

	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}


}
