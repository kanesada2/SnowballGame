package com.github.kanesada2.SnowballGame;

import java.util.ArrayList;
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
		if (!cmd.getName().equalsIgnoreCase("SnowballGame")) {
            return null;
        }
		ArrayList<String> completions = new ArrayList<String>();
		switch(args.length){
		case 1:
			if (args[0].length() == 0) {
	            completions.add("reload");
	            completions.add("get");
			}else {
	            if ("reload".startsWith(args[0])) {
	                completions.add("reload");
	            }else if ("get".startsWith(args[0])) {
	            	completions.add("reload");
	            }
	        }
			break;
		case 2:
			if(args[0].equalsIgnoreCase("get")){
				if (args[1].length() == 0) {
					completions.add("Ball");
					completions.add("Bat");
		            completions.add("Glove");
				}else {
		            if("Ball".startsWith(args[1])){
		            	completions.add("Ball");
		            }
		            if("Bat".startsWith(args[1])){
		            	completions.add("Bat");
		            }
		            if("Glove".startsWith(args[1])){
		            	completions.add("Glove");
		            }
		        }
		       }
			break;
		case 3:
			if(args[1].equalsIgnoreCase("ball")){
				if (args[2].length() == 0) {
					completions.add("Highest");
					completions.add("Higher");
		            completions.add("Normal");
		            completions.add("Lower");
		            completions.add("Lowest");
				}else {
		            if("Highest".startsWith(args[2])){
		            	completions.add("Highest");
		            }
		            if("Higher".startsWith(args[2])){
		            	completions.add("higher");
		            }
		            if("Normal".startsWith(args[2])){
		            	completions.add("Normal");
		            }
		            if("Lower".startsWith(args[2])){
		            	completions.add("Lower");
		            }
		            if("Lowest".startsWith(args[2])){
		            	completions.add("Lowest");
		            }
		        }
		       }
			break;
		}
		return completions;
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
				msgs[2] = "/sbg get [Ball|Bat|Glove] <Highest|Higher|Normal|Lower|Lowest>" + ChatColor.YELLOW + "get SnowballGame's custom item.";
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
					item = Util.getBall("normal");
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
				sender.sendMessage("You got a SnowballGame's item!");
				return true;
			case 3:
				if(!(sender instanceof Player)){
					sender.sendMessage("Please send this command in game.");
					return false;
				}
				if(!args[0].equalsIgnoreCase("get")){
					Bukkit.getLogger().info(args[0]);
					sender.sendMessage("Unknown command. Please check /sbg");
					return false;
				}
				if(!args[1].equalsIgnoreCase("ball")){
					Bukkit.getLogger().info(args[0]);
					sender.sendMessage("You can't choice the type of such a item.");
					return false;
				}
				if(!sender.hasPermission("SnowballGame.get")){
					sender.sendMessage("You don't have permisson.");
					return false;
				}
				ItemStack ball;
				if(args[2].equalsIgnoreCase("Highest")){
					ball = Util.getBall("highest");
				}else if(args[2].equalsIgnoreCase("Higher")){
					ball = Util.getBall("higher");
				}else if(args[2].equalsIgnoreCase("Normal")){
					ball = Util.getBall("normal");
				}else if(args[2].equalsIgnoreCase("Lower")){
					ball = Util.getBall("lower");
				}else if(args[2].equalsIgnoreCase("Lowest")){
					ball = Util.getBall("lowest");
				}else{
					sender.sendMessage("SnowballGame can't provide such a item.");
					return false;
				}
				Player pler = (Player)sender;
				Inventory inv = pler.getInventory();
				if(inv.containsAtLeast(ball,1) || inv.firstEmpty() != -1){
					inv.addItem(ball);
				}else{
					pler.getWorld().dropItem(pler.getLocation(), ball);
				}
				sender.sendMessage("You got a SnowballGame's item!");
				return true;
			default:
				sender.sendMessage("Unknown command. Please check /sbg");
				return false;
		}
	}

}
