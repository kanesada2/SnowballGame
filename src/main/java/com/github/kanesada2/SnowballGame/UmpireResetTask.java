package com.github.kanesada2.SnowballGame;

import org.bukkit.entity.Snowman;
import org.bukkit.scheduler.BukkitRunnable;

public class UmpireResetTask extends BukkitRunnable {
	private Snowman pl;

	public UmpireResetTask(Snowman pl) {
        this.pl = pl;
    }
    @Override
    public void run() {
    	pl.setCollidable(true);
    }

}
