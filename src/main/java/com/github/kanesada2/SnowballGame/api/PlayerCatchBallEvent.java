package com.github.kanesada2.SnowballGame.api;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerCatchBallEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Projectile ball;
	private ItemStack itemBall;
	private boolean isDirect;
	public PlayerCatchBallEvent(Player who, Projectile ball, ItemStack itemBall, boolean isDirect) {
		super(who);
		this.ball = ball;
		this.itemBall = itemBall;
		this.isDirect = isDirect;
	}

	public Projectile getBall(){
		return ball;
	}

	public void setBall(Projectile ball){
		this.ball = ball;
	}

	public ItemStack getItemBall(){
		return itemBall;
	}
	public void setItemBall(ItemStack itemBall){
		this.itemBall = itemBall;
	}

	public boolean isDirect(){
		return isDirect;
	}
	public void setDirect(boolean isDirect){
		this.isDirect = isDirect;
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