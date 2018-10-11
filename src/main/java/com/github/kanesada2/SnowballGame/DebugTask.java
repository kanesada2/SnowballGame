package com.github.kanesada2.SnowballGame;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.kanesada2.SnowballGame.api.SnowballGameAPI;

public class DebugTask extends BukkitRunnable {
	private SnowballGame plugin;
	private Player player;
	public DebugTask(SnowballGame plugin, Player player){
		this.plugin = plugin;
		this.player = player;
	}
	@Override
	public void run() {
		Location loc = player.getEyeLocation();
		loc.setYaw((float) (loc.getYaw() + 90 * (Math.random() * 2 - 1)));
		loc.setPitch((float) (90 * (Math.random() * 2 - 1)));
		loc.add(loc.getDirection().multiply(Math.random() * 1.6));

		Location loc4vel = new Location(player.getWorld(), 0,0,0);
		loc4vel.setYaw((float) (-90 + 90 * (Math.random() * 2 - 1)));
		loc4vel.setPitch((float) (Math.random() * 90));
		Vector vel = loc4vel.getDirection().normalize().multiply(2 * Math.random());
		Projectile ball = SnowballGameAPI.launch(player, null, false, "normal", "", vel, new Vector(0,0,0), 0, 0,null, loc, new Vector(0,0,0));

		List<String> batNames = plugin.getConfig().getStringList("Bat.Swing.Type");
		String batName = batNames.get((int)(Math.random() * batNames.size()));
		player.sendMessage(batName);
		Vector batMove = SnowballGameAPI.getBatPositionFromName(player.getEyeLocation(), (Math.PI / 2 + 0.01) * -1, -1, batName).subtract(SnowballGameAPI.getBatPositionFromName(player.getEyeLocation(), Math.PI / 2 * -1, -1, batName)).toVector().normalize();

		Location swingLoc = player.getEyeLocation();
		swingLoc.setYaw((float) (loc.getYaw() + 90 * (Math.random() * 2 - 1)));
		swingLoc.setPitch((float) (90 * (Math.random() * 2 - 1)));
		swingLoc.add(swingLoc.getDirection().multiply(Math.random() * 1.6));

		SnowballGameAPI.tryHit(player, swingLoc, new Vector(1.2, 1.2, 1.2), 1, 1.3, batMove, 1);
	}

}
