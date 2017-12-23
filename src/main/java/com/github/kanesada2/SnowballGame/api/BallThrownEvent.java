package com.github.kanesada2.SnowballGame.api;

import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class BallThrownEvent extends EntityEvent{
	private static final HandlerList handlers = new HandlerList();
	public BallThrownEvent(Projectile what){
		super(what);
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
