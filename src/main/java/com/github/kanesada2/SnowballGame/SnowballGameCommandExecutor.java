package com.github.kanesada2.SnowballGame;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SnowballGameCommandExecutor implements CommandExecutor, TabCompleter {

	private SnowballGame plugin;

    public SnowballGameCommandExecutor(SnowballGame plugin) {
        this.plugin = plugin;
    }
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("SnowballGame")) {
            return false;
        }
		switch(args.length){
			case 0:
				String [] msgs = new String[3];
				msgs[0] = "/sbg " + ChatColor.YELLOW + "show all SnowballGame commands.";
				msgs[1] = "/sbg reload " + ChatColor.YELLOW + "reload SnowballGame's config file";
				msgs[2] = "/sbg get [Ball|Bat|Glove] " + ChatColor.YELLOW + "get SnowballGame's custom item.";
				sender.sendMessage(msgs);
				return true;
			case 1:
				if(args[0].equalsIgnoreCase("reload")){
					if(sender instanceof Player){
						if(!sender.hasPermission("SnowballGame.reload")){
							sender.sendMessage("You don't have permisson.");
							return false;
						}
					}else if(!(sender instanceof ConsoleCommandSender)){
						return false;
					}
					Bukkit.getLogger().info("Reloading SnowballGame...");
					plugin.getPluginLoader().disablePlugin(plugin);
					plugin.getPluginLoader().enablePlugin(plugin);
					return true;
				}else{
					sender.sendMessage("Unknown command. Please check /sbg");
					return false;
				}
			case 2:
				if(!(sender instanceof Player)){
					sender.sendMessage("Please send this command in game.");
					return false;
				}
				if(!args[0].equalsIgnoreCase("get")){
					Bukkit.getLogger().info(args[0]);
					sender.sendMessage("Unknown command. Please check /sbg");
					return false;
				}
				if(!sender.hasPermission("SnowballGame.get")){
					sender.sendMessage("You don't have permisson.");
					return false;
				}
				ItemStack item;
				if(args[1].equalsIgnoreCase("Ball")){
					item = Util.getBall();
				}else if(args[1].equalsIgnoreCase("Bat")){
					item = Util.getBat();
				}else if(args[1].equalsIgnoreCase("Glove")){
					item = Util.getGlove();
				}else{
					sender.sendMessage("SnowballGame can't provide such a item.");
					return false;
				}
				Player player = (Player)sender;
				Inventory inventory = player.getInventory();
				if(inventory.containsAtLeast(item,1) || inventory.firstEmpty() != -1){
					inventory.addItem(item);
				}else{
					player.getWorld().dropItem(player.getLocation(), item);
				}
				return true;
			default:
				sender.sendMessage("Unknown command. Please check /sbg");
				return false;
		}
	}

}
