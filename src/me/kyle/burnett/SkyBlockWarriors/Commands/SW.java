package me.kyle.burnett.SkyBlockWarriors.Commands;

import java.io.IOException;

import me.kyle.burnett.SkyBlockWarriors.ArenaState;
import me.kyle.burnett.SkyBlockWarriors.ChestType;
import me.kyle.burnett.SkyBlockWarriors.Game;
import me.kyle.burnett.SkyBlockWarriors.GameManager;
import me.kyle.burnett.SkyBlockWarriors.Main;
import me.kyle.burnett.SkyBlockWarriors.Events.PlayerLeaveArenaEvent;
import me.kyle.burnett.SkyBlockWarriors.Utils.ChestFiller;
import me.kyle.burnett.SkyBlockWarriors.Utils.WorldEditUtility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;

public class SW implements CommandExecutor{

	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("sw") || cmd.getName().equalsIgnoreCase("skyblockw")){
			
			Player p = (Player) sender;
			
			GameManager gm = GameManager.getInstance();
			
			if(args.length == 0){
				
				p.sendMessage(ChatColor.GOLD + "----------" + ChatColor.GREEN +"Sky Block War's Command's" + ChatColor.GOLD  +"----------");
				p.sendMessage(ChatColor.GOLD + "/sw" + ChatColor.GREEN +"Show's this help screen.");
				p.sendMessage(ChatColor.GOLD + "/sw join [arena number]" + ChatColor.GREEN +"Join's an arena if specified or goes to the lobby.");
				p.sendMessage(ChatColor.GOLD + "/sw leave" + ChatColor.GREEN +"Leave's the arena and teleports you to the lobby.");
				
				return true;
			}
			
			if(args.length == 1){
				
				if(args[0].equalsIgnoreCase("join")){
					
					if(Main.getInstance().teleportToLobby(p)){
						
						p.sendMessage(ChatColor.GREEN + "Arena not specifed teleporting to lobby.");
					
					} else if(!Main.getInstance().teleportToLobby(p)){
						
						p.sendMessage(ChatColor.RED + "Tryed to teleport to lobby but it was not found. Please tell server staff.");
					}
					
					return true;
				}
				
				if(args[0].equalsIgnoreCase("leave")){
					
					if(gm.isPlayerInGame(p)){
						
						Game game = gm.getPlayerGame(p);
						
						PlayerLeaveArenaEvent event = new PlayerLeaveArenaEvent(p, gm.getPlayerGame(p));
						
						Bukkit.getServer().getPluginManager().callEvent(event);
					
						gm.leaveGame(p);
						
						p.sendMessage(ChatColor.GREEN + "You have left the arena.");
												
						game.broadCastGame(ChatColor.GREEN +"Player " + p.getDisplayName() + ChatColor.GREEN + " has left the game.");
						
					}else if(!gm.isPlayerInGame(p)){
						
						p.sendMessage(ChatColor.RED + "You are not in a game.");
					}
					
					return true;
				}
				
				if(args[0].equalsIgnoreCase("list")){
						
					if(gm.isPlayerInGame(p)){
						
						gm.getPlayerGame(p).getPlayersAsList();
					
					}else if(!gm.isPlayerInGame(p)){
						p.sendMessage(ChatColor.RED + "You are not in a game.");
					}
				}
				
				if(args[0].equalsIgnoreCase("listgames")){
					
					String arenas = gm.listGames();
					
					p.sendMessage(ChatColor.GOLD + "Arena List:");
					p.sendMessage(ChatColor.GRAY + arenas);
				}
				
				if(args[0].equalsIgnoreCase("create")){
					
					if(WorldEditUtility.getInstance().doesSelectionExist(p)){
						
						int arena = gm.createGame(p);
						p.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.GOLD + arena + ChatColor.GREEN + " created.");
					
					}else if(!WorldEditUtility.getInstance().doesSelectionExist(p)){
						p.sendMessage(ChatColor.RED + "Please make a selection of the arena first.");
					}
				}
				
				if(args[0].equalsIgnoreCase("confirm")){
					
					if(gm.getConfirming().containsKey(p.getName())){
						
						gm.overrideArena(p, gm.getConfirming().get(p.getName()));
						
					}else if(!gm.getConfirming().containsKey(p.getName())){
						
						p.sendMessage(ChatColor.RED + "You are not waiting to confirm anything.");
					}
				}
				
				if(args[0].equalsIgnoreCase("setlobby")){
					
					Main.getInstance().setLobby(p);
					p.sendMessage(ChatColor.GREEN + "Lobby set succesfully.");
					
				}
				
				if(args[0].equalsIgnoreCase("finish")){
					
					if(gm.isEditing(p)){
						
						p.sendMessage(ChatColor.GREEN + "Finished editing arena " + ChatColor.GOLD + gm.getEditing().get(p.getName()) + ChatColor.GREEN + ". Remember to '/sw save arena' if you edited the blocks.");
						 
						gm.removeEditor(p);
						
					}else if(!gm.isEditing(p)){
						
						p.sendMessage(ChatColor.RED + "You are not editing an arena.");
					}
					
				}
				
				return true;
			}
			
			if(args.length == 2){
				
/*				if(args[0].equalsIgnoreCase("fill")){
					
					ChestFiller.loadChests(Integer.parseInt(args[1]));
					p.sendMessage("Filled");
					
				}*/
					
				if(args[0].equalsIgnoreCase("create")){
					
					if(gm.isInteger(args[1])){
						
						int id = Integer.parseInt(args[1]);
						
						if(!(id > gm.getArenaAmount())){
							
							p.sendMessage(ChatColor.RED + "You are away to override a previous arena. Do '/sw confirm' to confirm this action.");
							
							gm.getConfirming().put(p.getName(), id);
							
						
						}else if(id > gm.getArenaAmount()){
							
							p.sendMessage(ChatColor.RED + "That number is bigger than your amount of arenas. Use '/sw create' to add arenas.");
						}
					}
				
				}
				
				if(args[0].equalsIgnoreCase("join")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						Game game = gm.getGameByID(Integer.parseInt(args[1]));
						
							if(game.state.equals(ArenaState.WAITING)){
								
								game.addPlayer(p);
								gm.setPlayerGame(p, game);
																								
							}else if(game.state != ArenaState.WAITING){
								
								p.sendMessage(ChatColor.RED + "Can not join the arena because it is " + game.getState().toString() + ".");
							}
	
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED + "That game does not exist.");
					}
					return true;
				}
				
				if(args[0].equalsIgnoreCase("edit")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						gm.addEditor(p, Integer.parseInt(args[1]));
						
						gm.getGameByID(Integer.parseInt(args[1])).setState(ArenaState.GETTING_EDITED);
						
						p.sendMessage(ChatColor.GREEN + "Now editing arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + ".");
						
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED + "That arena does not exist.");
					}
					

				}
				
				if(args[0].equalsIgnoreCase("addchest")){
					
					if(gm.isEditing(p)){
						
						if(WorldEditUtility.getInstance().doesSelectionExist(p)){
						
							if(WorldEditUtility.getInstance().isChest(p)){
								
								Location loc = WorldEditUtility.getInstance().getChestLocation(p);
								
								if(loc.equals(null)){
									
									p.sendMessage(ChatColor.RED + "An error occured. Please try again.");
									
									return true;
								}
							
								if(args[1].equalsIgnoreCase("side")){
									
									gm.getGameEditing(p).addChest(ChestType.SIDE, loc);
								
									p.sendMessage(ChatColor.GREEN + "You have added a " + ChatColor.GOLD + "side " + ChatColor.GREEN + "chest to arena " + ChatColor.GOLD + gm.getPlayerEditing(p) + ChatColor.GREEN + ".");
									
								}else if(args[1].equalsIgnoreCase("center")){
									
									gm.getGameEditing(p).addChest(ChestType.CENTER, loc);
									
									p.sendMessage(ChatColor.GREEN + "You have added a " + ChatColor.GOLD + "center " + ChatColor.GREEN + "chest to arena " + ChatColor.GOLD + gm.getPlayerEditing(p) + ChatColor.GREEN + ".");
									
								}else if(args[1].equalsIgnoreCase("spawn")){
								
									gm.getGameEditing(p).addChest(ChestType.SPAWN, loc);
									
									p.sendMessage(ChatColor.GREEN + "You have added a " + ChatColor.GOLD + "spawn " + ChatColor.GREEN + "chest to arena " + ChatColor.GOLD + gm.getPlayerEditing(p) + ChatColor.GREEN + ".");
									
								}
								
							}else if(!WorldEditUtility.getInstance().isChest(p)){
								
								p.sendMessage(ChatColor.RED + "Your selection is either more than one block or is not a chest.");
							}
							
						}else if(!WorldEditUtility.getInstance().doesSelectionExist(p)){
							
							p.sendMessage(ChatColor.RED + "You do not have a selection of a chest.");
						}
						
					}else if(!gm.isEditing(p)){
						p.sendMessage(ChatColor.RED  + "You are not editing an arena.");
					}
					
				}
				
				if(args[0].equalsIgnoreCase("enable")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						if(!gm.isEnabled(Integer.parseInt(args[1]))){
							
							gm.enableGame(Integer.parseInt(args[1]));
							
							p.sendMessage(ChatColor.GREEN + "You enabled arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + ".");
							
						}else if(gm.isEnabled(Integer.parseInt(args[1]))){
							
							p.sendMessage(ChatColor.RED + "Arena is already enabled.");
						}
					
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED +"That is not an arena.");
					}

				}
				
				if(args[0].equalsIgnoreCase("disable")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						if(gm.isEnabled(Integer.parseInt(args[1]))){
							
							gm.disableGame(Integer.parseInt(args[1]));
							
							p.sendMessage(ChatColor.GREEN + "You disabled arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + ".");
							
						}else if(!gm.isEnabled(Integer.parseInt(args[1]))){
							
							p.sendMessage(ChatColor.RED + "Arena is already disabled.");
						}
					
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED +"That is not an arena.");
					}
					
				}
				
				if(args[0].equalsIgnoreCase("save")){
	
					if(gm.checkGameByID(Integer.parseInt(args[1]))){	
						
						WorldEditUtility.getInstance().resaveArena(Integer.parseInt(args[1]), p);
						
						p.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " has been saved.");

					
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED + "That arena does not exist.");
					}
				}
				
				if(args[0].equalsIgnoreCase("load")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						try {
							
							WorldEditUtility.getInstance().loadIslandSchematic(Integer.parseInt(args[1]));
						
						} catch (NumberFormatException| MaxChangedBlocksException | DataException| IOException e) {
							
							e.printStackTrace();
						}
						
						p.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " has been loaded.");
						
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED + "That arena does not exist.");
					}

				}
				
				if(args[0].equalsIgnoreCase("ready")){
					
					if(gm.isEditing(p)){
						
						gm.getGameEditing(p).setState(ArenaState.WAITING);
						
						p.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.GOLD + gm.getEditing().get(p.getName() + ChatColor.GREEN + " is ready to use."));
						
						ChestFiller.loadChests(gm.getEditing().get(p.getName()));
						
					}else if(!gm.isEditing(p)){
						
						p.sendMessage(ChatColor.RED  + "You are not editing an arena.");
					}
					
				}
			
				return true;
			}
			
			if(args.length == 3){
				
				return true;
			}
			
			return true;
		}
		return false;
	}
	
}
