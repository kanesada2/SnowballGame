package com.github.kanesada2.SnowballGame.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class BallBounceEvent extends EntityEvent{

	private static final HandlerList handlers = new HandlerList();
	private Block block;
	private Projectile before;
	private boolean first;
	public BallBounceEvent(Projectile what, Block block, Projectile before, boolean first) {
		super(what);
		this.block = block;
		this.before = before;
		this.first = first;
	}

	public Projectile getBeforeBounce(){
		return before;
	}
	public Block getBlock(){
		return block;
	}
	public void setBlock(Block block){
		this.block = block;
	}

	public boolean isFirst(){
		return first;
	}
	public void setFirst(boolean first){
		this.first = first;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}


}
