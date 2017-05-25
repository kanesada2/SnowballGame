package com.github.kanesada2.SnowballGame;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerCoolDownTask extends BukkitRunnable {
	private SnowballGame plugin;
	private Player player;
	public PlayerCoolDownTask(SnowballGame plugin, Player player){
		this.plugin = plugin;
		this.player = player;
	}
	@Override
	public void run() {
		if(player.hasMetadata("onMotion")){
			player.removeMetadata("onMotion", plugin);
		}
		if(player.hasMetadata("onSlide")){
			player.removeMetadata("onSlide", plugin);
		}
	}

}
