package com.github.kanesada2.SnowballGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.github.kanesada2.SnowballGame.api.SnowballGameAPI;

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
	            completions.add("sweep");
			}else {
	            if ("reload".startsWith(args[0])) {
	                completions.add("reload");
	            }else if ("get".startsWith(args[0])) {
	            	completions.add("get");
	            }else if ("please".startsWith(args[0])) {
	            	completions.add("please");
	            }else if ("sweep".startsWith(args[0])) {
	            	completions.add("sweep");
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
		            completions.add("CoachManager");
		            completions.add("Base");
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
		            if("CoachManager".startsWith(args[1])){
		            	completions.add("CoachManager");
		            }
		            if("Ball".startsWith(args[1])){
		            	completions.add("Base");
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
			case 0: {
				String [] msgs = new String[6];
				msgs[0] = "/sbg " + ChatColor.YELLOW + "show all SnowballGame commands.";
				msgs[1] = "/sbg reload " + ChatColor.YELLOW + "reload SnowballGame's config file";
				msgs[2] = "/sbg get [Ball|Bat|Glove] <Highest|Higher|Normal|Lower|Lowest>" + ChatColor.YELLOW + "get SnowballGame's custom item.";
				msgs[3] = "/sbg please " + ChatColor.YELLOW + "CoachManager hit the ball for your fielding practice.";
				msgs[4] = "/sbg sweep " + ChatColor.YELLOW + "clean up floationg balls arround you(in 3 blocks).";
				msgs[5] = "/sbg msg [on|off] " + ChatColor.YELLOW + "enable|disable SBG's nortification to you";
				sender.sendMessage(msgs);
				return true;
			}
			case 1: {
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
					Inventory inventory = player.getInventory();
					ItemStack item = Util.getBall("normal");
					for(Iterator<ItemStack> iterator = inventory.iterator(); iterator.hasNext();){
						item = iterator.next();
						if(item != null && Util.isBall(item)){
							if(player.getGameMode() != GameMode.CREATIVE){
								ItemStack ball = item.clone();
								item.setAmount(item.getAmount() - 1);
								item = ball;
							}
							break;
						}else if(!iterator.hasNext()){
							player.sendMessage("You must have at least one ball to send this command.");
							return false;
						}
					}
					if(player.hasMetadata("onMotion")){
						player.sendMessage("Your coach can't hit the ball so quickly.");
						return false;
					}
					int range = plugin.getConfig().getInt("CoachManager.Coach_Range",120);
					Collection <Entity> entities = player.getNearbyEntities(range, 10, range);
					if(entities.isEmpty()){
						player.sendMessage("You are too far from your coach to practice.");
						return false;
					}
					for(Iterator<Entity> eitr = entities.iterator(); eitr.hasNext();){
						Entity entity = eitr.next();
						if(entity instanceof ArmorStand && entity.getCustomName() != null && entity.getCustomName().equalsIgnoreCase(plugin.getConfig().getString("CoachManager.Coach_Name"))){
							SnowballGameAPI.playWithCoach(player, (ArmorStand)entity, Util.getBallType(item.getItemMeta().getLore()));
							break;
						}else if(!eitr.hasNext()){
							player.sendMessage("You are too far from your coach to practice.");
							return false;
						}
					}
					player.setMetadata("onMotion", new FixedMetadataValue(plugin, true));
					new PlayerCoolDownTask(plugin, player).runTaskLater(plugin, plugin.getConfig().getInt("Ball.Cool_Time", 30));
					return true;
				}else if(args[0].equalsIgnoreCase("sweep")){
					if(!(sender instanceof Player)){
						sender.sendMessage("Please send this command in game.");
						return false;
					}else if(!sender.hasPermission("SnowballGame.sweep")){
						sender.sendMessage("You don't have permisson.");
						return false;
					}
					Player player = (Player)sender;
					Collection <Entity> entities = player.getNearbyEntities(3, 3, 3);
					int count = 0;
					for (Entity entity : entities){
						if(entity instanceof Snowball && !entity.hasGravity()){
							entity.remove();
							count++;
						}
					}
					if(count == 0){
						sender.sendMessage("No balls was found arround you.");
						return false;
					}
					sender.sendMessage(count + " balls successfully cleaned up!");
					return true;
				}
			break;
			}
			case 2: {
				if(args[0].equalsIgnoreCase("msg")){
					if(!(sender instanceof Player)){
						sender.sendMessage("Please send this command in game.");
						return false;
					}
					Player player = (Player) sender;
					if (args[1].equalsIgnoreCase("off")) {
						if(plugin.notifyDisabled.contains(player.getUniqueId())){
							player.sendMessage("Your setting is already off.");
							return false;
						}
						plugin.notifyDisabled.add(player.getUniqueId());
						player.sendMessage("Disabled SBG's message to you.");
						return true;
					}else if (args[1].equalsIgnoreCase("on")) {
						if(!plugin.notifyDisabled.contains(player.getUniqueId())){
							player.sendMessage("Your setting is already on.");
							return false;
						}
						plugin.notifyDisabled.remove(player.getUniqueId());
						player.sendMessage("You can enjoy sounds of baseball again!");
						return true;
					}
				}else if(args[0].equalsIgnoreCase("get")){
					if(!(sender instanceof Player)){
						sender.sendMessage("Please send this command in game.");
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
					}else if(args[1].equalsIgnoreCase("CoachManager")){
						item = Util.getCoach();
					}else if(args[1].equalsIgnoreCase("Base")){
						item = Util.getBase();
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
				}
			break;
			}
			case 3: {
				if(args[0].equalsIgnoreCase("get")){
					if(!(sender instanceof Player)){
						sender.sendMessage("Please send this command in game.");
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
					Player player = (Player)sender;
					Inventory inv = player.getInventory();
					if(inv.containsAtLeast(ball,1) || inv.firstEmpty() != -1){
						inv.addItem(ball);
					}else{
						player.getWorld().dropItem(player.getLocation(), ball);
					}
					sender.sendMessage("You got a SnowballGame's item!");
					return true;
				}
			}
		}
		sender.sendMessage("Unknown command. Please check /sbg");
		return false;
	}

}
