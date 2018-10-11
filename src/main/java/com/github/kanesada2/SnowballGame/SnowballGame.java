package com.github.kanesada2.SnowballGame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SnowballGame extends JavaPlugin implements Listener{

	private SnowballGameListener listener;
	private SnowballGameCommandExecutor commandExecutor;
	private Connection connection;
	public Statement statement;

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
        getLogger().info("SnowballGame Enabled!");

        try {
            openConnection();
            this.statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
    	getServer().addRecipe(Util.getUmpireRecipe());
    	getServer().addRecipe(Util.getBaseRecipe());
    	getServer().addRecipe(Util.getCoachRecipe());
    	getLogger().info("Custom Recipe Enabled!");
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            String host = this.getConfig().getString("DB.host");
            String database = this.getConfig().getString("DB.database");
            String port = this.getConfig().getString("DB.port");
            String username = this.getConfig().getString("DB.username");
            String password = this.getConfig().getString("DB.password");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        }
    }
}
