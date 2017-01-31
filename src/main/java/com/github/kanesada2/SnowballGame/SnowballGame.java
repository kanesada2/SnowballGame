package com.github.kanesada2.SnowballGame;

import java.util.List;

import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SnowballGame extends JavaPlugin implements Listener{

	private SnowballGameListener listener;
	private SnowballGameCommandExecutor commandExecutor;

	@Override
    public void onEnable() {
		this.saveDefaultConfig();
        listener = new SnowballGameListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        registerCustomRecipes();
        commandExecutor = new SnowballGameCommandExecutor(this);
        getCommand("SnowballGame").setExecutor(commandExecutor);
        getLogger().info("SnowballGame Enabled!");
    }

    @Override
    public void onDisable() {
    	List <World> worlds = getServer().getWorlds();
    	worlds.forEach(world -> Util.deleteBalls(world));
    }
    private void registerCustomRecipes() {
    	getServer().addRecipe(Util.getBallRecipe("highest"));
    	getServer().addRecipe(Util.getBallRecipe("higher"));
    	getServer().addRecipe(Util.getBallRecipe("normal"));
    	getServer().addRecipe(Util.getBallRecipe("lower"));
    	getServer().addRecipe(Util.getBallRecipe("lowest"));
    	getServer().addRecipe(Util.getBatRecipe());
    	getServer().addRecipe(Util.getGloveRecipe());
    	getLogger().info("Custom Recipe Enabled!");
    }
}
