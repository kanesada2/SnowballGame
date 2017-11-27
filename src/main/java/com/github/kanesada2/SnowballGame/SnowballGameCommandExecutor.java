package com.github.kanesada2.SnowballGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

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
	            completions.add("please");
			}else {
	            if ("reload".startsWith(args[0])) {
	                completions.add("reload");
	            }else if ("get".startsWith(args[0])) {
	            	completions.add("get");
	            }else if ("please".startsWith(args[0])) {
	            	completions.add("please");
	            }
	        }
			break;
		case 2:
			if(args[0].equalsIgnoreCase("get")){
				if (args[1].length() == 0) {
					completions.add("Ball");
					completions.add("Bat");
		            completions.add("Glove");
		            completions.add("Umpire");
		            completions.add("Coach");
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
		            if("Umpire".startsWith(args[1])){
		            	completions.add("Umpire");
		            }
		            if("Coach".startsWith(args[1])){
		            	completions.add("Coach");
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
				String [] msgs = new String[4];
				msgs[0] = "/sbg " + ChatColor.YELLOW + "show all SnowballGame commands.";
				msgs[1] = "/sbg reload " + ChatColor.YELLOW + "reload SnowballGame's config file";
				msgs[2] = "/sbg get [Ball|Bat|Glove] <Highest|Higher|Normal|Lower|Lowest>" + ChatColor.YELLOW + "get SnowballGame's custom item.";
				msgs[3] = "/sbg please " + ChatColor.YELLOW + "Coach hit the ball for your fielding practice.";
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
					plugin.reloadConfig();
					Bukkit.getLogger().info("SnowballGame Reloaded!");
					return true;
				}else if(args[0].equalsIgnoreCase("please")){
					if(!(sender instanceof Player)){
						sender.sendMessage("Please send this command in game.");
						return false;
					}else if(!sender.hasPermission("SnowballGame.please")){
						sender.sendMessage("You don't have permisson.");
						return false;
					}
					Player player = (Player)sender;
					if(!player.getInventory().containsAtLeast(Util.getBall("normal"), 1)){
						player.sendMessage("You must have at least one normal-ball to send this command.");
						return false;
					}else{
						if(player.getGameMode() != GameMode.CREATIVE){
							ItemStack[] inventory = player.getInventory().getContents();
							for(ItemStack item : inventory){
								if(item != null && Util.isBall(item) && item.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getConfig().getString("Ball.Ball_Name")) && item.getItemMeta().getLore().size() == 2){
									item.setAmount(item.getAmount() - 1);
									break;
								}
							}
						}
						if(player.hasMetadata("onMotion")){
							player.sendMessage("Your coach can't hit the ball so quickly.");
							return false;
						}
						Collection <Entity> entities = player.getNearbyEntities(100, 10, 100);
						if(entities.isEmpty()){
							player.sendMessage("You are too far from your coach to practice.");
							return false;
						}
						for(Entity entity : entities){
							if(entity instanceof ArmorStand && entity.getCustomName() != null && entity.getCustomName().equalsIgnoreCase(plugin.getConfig().getString("Coach.Coach_Name"))){
								SnowballGameAPI.playWithCoach(player, (ArmorStand)entity, "normal");
								break;
							}
						}
						player.setMetadata("onMotion", new FixedMetadataValue(plugin, true));
						new PlayerCoolDownTask(plugin, player).runTaskLater(plugin, plugin.getConfig().getInt("Ball.Cool_Time", 30));
						return true;
					}
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
				}else if(args[1].equalsIgnoreCase("Umpire")){
					item = Util.getUmpire();
				}else if(args[1].equalsIgnoreCase("Coach")){
					item = Util.getCoach();
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
					sender.sendMessage("Unknown command. Please check /sbg");
					return false;
				}
				if(!args[1].equalsIgnoreCase("ball")){
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
