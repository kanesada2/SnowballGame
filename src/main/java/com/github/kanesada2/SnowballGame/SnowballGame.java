package com.github.kanesada2.SnowballGame;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SnowballGame extends JavaPlugin implements Listener{

	private SnowballGameListener listener;
	private SnowballGameCommandExecutor commandExecutor;
	public final HashSet<UUID> notifyDisabled = new HashSet<>();

	@Override
    public void onEnable() {
		this.saveDefaultConfig();
        listener = new SnowballGameListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        registerCustomRecipes();
        commandExecutor = new SnowballGameCommandExecutor(this);
        getCommand("SnowballGame").setExecutor(commandExecutor);
        new Data();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        loadNotifyDisabled();
        getLogger().info("SnowballGame Enabled!");
    }

    @Override
    public void onDisable() {
    	List <World> worlds = getServer().getWorlds();
    	worlds.forEach(world -> Util.deleteBalls(world));
    	saveNotifyDisabled();
    }
    private void registerCustomRecipes() {
    	getServer().addRecipe(Util.getBallRecipe("highest"));
    	getServer().addRecipe(Util.getBallRecipe("higher"));
    	getServer().addRecipe(Util.getBallRecipe("normal"));
    	getServer().addRecipe(Util.getBallRecipe("lower"));
    	getServer().addRecipe(Util.getBallRecipe("lowest"));
    	getServer().addRecipe(Util.getBatRecipe());
    	getServer().addRecipe(Util.getGloveRecipe());
    	getServer().addRecipe(Util.getUmpireRecipe());
    	getServer().addRecipe(Util.getBaseRecipe());
    	getServer().addRecipe(Util.getCoachRecipe());
    	getLogger().info("Custom Recipe Enabled!");
    }

    public void loadNotifyDisabled() {
		try {
			notifyDisabled.clear();
			for (String line: Files.readAllLines(new File(getDataFolder(), "notify-disabled.txt").toPath())) {
				try {
					notifyDisabled.add(UUID.fromString(line));
				} catch (IllegalArgumentException e) {
				}
			}
		} catch (IOException e) {
		}
	}

	public void saveNotifyDisabled() {
		try {
			File notifyDisabledFile = new File(getDataFolder(), "notify-disabled.txt");
			if (!notifyDisabledFile.exists())
				notifyDisabledFile.createNewFile();
			try (PrintWriter writer = new PrintWriter(notifyDisabledFile, "UTF-8")) {
				writer.println("# The following players disabled SBG's nortification for themselves");
				for (UUID uuid : notifyDisabled) {
					writer.println(uuid.toString());
				}
			}
		} catch (IOException e) {
		}
	}
}
