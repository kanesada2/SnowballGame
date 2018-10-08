package com.github.kanesada2.SnowballGame.api;

import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class BallHitEvent extends EntityEvent {
	private static final HandlerList handlers = new HandlerList();
	private Projectile beforeHit;
	public BallHitEvent(Projectile what, Projectile beforeHit){
		super(what);
		this.beforeHit = beforeHit;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
	public Projectile getBeforeHit() {
		return beforeHit;
	}
}
