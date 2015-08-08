package net.grasinga.MobFighter;

import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;

// Main class
public class MobFighter extends JavaPlugin {
	
	// Used for short handing the server.
	private Server server;
	
	// Used for short handing the world name and for getting specific a world name other that 'world' in configuration file.
	private String worldName;
	
	// Used for short handing the world.
    private World world;
	
	// Every number or nights which an event can occur. (If it's 5 then an event happens every 5 nights.)
	private int eventNight = getConfig().getInt("Event Night");
	
	// Variable to keep track on how many nights have passed. (Includes the current night if it's night time.)
	private int night = getConfig().getInt("Current Night");
	
	// Variable to use night in other classes.
	public static int nights = 0;
	
	// Variable to keep track of when it first becomes day time. (true = just became day time) (false = has been day time or is night time)
	private boolean startDay = true;
	
	// Variable to keep track of when it first becomes night time. (true = just became night time) (false = has been night time or is day time)
	private boolean startNight = false;
	
	// Variable to keep track of when it's night time. Also used for other classes. (true = night time) (false = day time)
	public static boolean isNight = false;
	
	// Variable to use events.
	SpecialEvents event;
	
	// Variable that allows only one pass of spawnProtection per 30 seconds. (true = event is running) (false = event is not running)
	private boolean runningProtect = false;
	
	// Variable to get players who have readied.
	public static PlayerNames playerNames = new PlayerNames();
	
	// Variable to pick the event.
	public static int eventPicker = 0;
	
	// Variable to keep track or fixed or random events. (true = random event) (false = specified event)
	public static boolean anyEvent = true;
	
	// When plugin is starts:
	public void onEnable() {
		log("[MobFighter] Enabled!" 
				+ "\n--------------------------------------------------"
				+ "\nEthereal Network's MobFighter"
				+ "\nMade by: Grasinga"
				+ "\n--------------------------------------------------");
		
		// Sets values for variables (VERY IMPORTANT TO SET HERE):
		server = Bukkit.getServer();
		worldName = getConfig().getString("World Name");
		world = server.getWorld(worldName);
		
		// Allows the use of game and special events.
		PluginManager pluginmanager;
		pluginmanager = this.getServer().getPluginManager();
		Listener regularListener = new MobFighterListener(this);
		pluginmanager.registerEvents(regularListener, this);
		
		// Creates special events.
		event = new SpecialEvents(this, pluginmanager, world);
		
		// Allows the use of commands.
        getCommand("mfreload").setExecutor(new MobFighterCommands(this));
		getCommand("setboards").setExecutor(new MobFighterCommands(this));
		getCommand("night").setExecutor(new MobFighterCommands(this));
		getCommand("setnight").setExecutor(new MobFighterCommands(this));
		getCommand("eventnight").setExecutor(new MobFighterCommands(this));
		getCommand("setevent").setExecutor(new MobFighterCommands(this));
		getCommand("ready").setExecutor(new MobFighterCommands(this));
		getCommand("getshop").setExecutor(new MobFighterCommands(this));
		getCommand("getbook").setExecutor(new MobFighterCommands(this));
		getCommand("getshop").setExecutor(new MobFighterCommands(this));
		getCommand("eliteshop").setExecutor(new MobFighterCommands(this));
		getCommand("craft").setExecutor(new MobFighterCommands(this));
		getCommand("exchange").setExecutor(new MobFighterCommands(this));
		
		// Load the configuration file:
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		// Set Scoreboard, gamerule, and flags if this plugin was just added to the server.
		if(getConfig().getBoolean("Just Added"))
			runStartVariables();
		
		// Runs the Repeating Task that determines whether is it day or night. (Checks every Minecraft tick.)
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				
				// Day Time --------------------------------------------------------------------------------------
				
				if(world.getTime() >= 0 && world.getTime() < 13700){
					
					// Make sure amount of nights match.
					night = nights;
    				
					// It's now day time.
					isNight = false;
					
					// When the day first starts: change money to tab view, set players money on tab view, teleport players to spawn, and butcher all mobs.
					if(startDay)
            		{						
						// Gets the top player for Elite Shop
						getConfig().set("Top Player", topPlayer());
						saveConfig();
						reloadConfig();
						
						// Clears ready list.
						playerNames.removeAll();
						
						// Prevents Slimes and animals from spawning and resets global flags.
						server.dispatchCommand(Bukkit.getConsoleSender(),"region flag __global__" + " -w "+ worldName + " deny-spawn Slime, Pig, Chicken, Sheep, Cow, Horse, Rabbit");
						
        				server.dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives setDisplay list Money_Top");
            			Collection<? extends Player> list = Bukkit.getOnlinePlayers();
            			for(Player p : list)
            			{
        					server.dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set " + p.getDisplayName() + " Money_Top " + (int) VaultEco.getEconomy().getBalance(p.getDisplayName()));
            				p.teleport(world.getSpawnLocation());
            			}
        				Bukkit.broadcastMessage(ChatColor.GOLD + "It's day time! Get prepared for the next night!");
        				server.dispatchCommand(Bukkit.getConsoleSender(),"butcher -a");
    					
    					// Allows PvP during the day. (Only outside of spawn.)
    					server.getWorld(worldName).setPVP(true);
        				
        				// End all Special Events:
        				
        				if(SpecialEvents.lightning)
                		{
                    		Bukkit.broadcastMessage(ChatColor.YELLOW + "The Storm seems to have subsided.");
                    		world.setStorm(false);
                    		SpecialEvents.lightning = false;
                		}
        				
                		if(SpecialEvents.explosive)
                		{
                			Bukkit.broadcastMessage(ChatColor.GOLD + "The air feels normal again.");
                			SpecialEvents.explosive = false;
                		}
                		
                		if(SpecialEvents.potato)
                		{
                			for(int i=0;i<Bukkit.getServer().getWorld(worldName).getPlayers().size();i++)
                			{
                				if(world.getPlayers().get(i).getInventory().contains(Material.POISONOUS_POTATO))
                				{
                					world.getPlayers().get(i).getInventory().remove(Material.POISONOUS_POTATO);
    								world.getPlayers().get(i).damage(20);
                				}
                			}
                			SpecialEvents.potato = false;
                		}
                		
        				SpecialEvents.babyZombies = false;
        				
        				SpecialEvents.wolves = false;
    					
    					// Resets the night counter to 0 after one million nights have passed.
    					if(night > 1000000){
    						night = 0;
    						nights = 0;
    					}    					
    					
    					// Alternates from day to night variable.
        				startNight = true;
        				startDay = false;
            		}// End of day start.
					
					// Gives the players a heads up before it becomes night time.
					if(!world.getPlayers().isEmpty())
						if(world.getTime() == 12700)
	    					Bukkit.broadcastMessage(ChatColor.GOLD + "It will be night in 50 seconds!");
					
				}// End of day time.
				
				// Night Time --------------------------------------------------------------------------------------------
				
				else if(world.getTime() > 13700 && world.getTime() < 22700){
					
					// Make sure amount of nights match.
					night = nights;
					
					// It's now night time.
					isNight = true;
					
					// Checks to see if there are any players in the world. (true = runs following code) (false = there are no players and code isn't executed)
					if(!world.getPlayers().isEmpty()){			
					
						// Makes sure a player cannot be in creative at night. (Unless they have immunity in the configuration file.)
						Collection<? extends Player> list = Bukkit.getOnlinePlayers();
	    				for(Player a : list)
	    				{
	    					if(a.getGameMode().equals(GameMode.CREATIVE))
	    						for(int i=0;i<getConfig().getList("Creative Immunity").size();i++)
	    							if(!(a.getDisplayName().equalsIgnoreCase(getConfig().getList("Creative Immunity").get(i).toString())))
	    								a.setGameMode(GameMode.SURVIVAL);
	    				}
	    				
	    				// Stuff to run when the night begins:
	    				if(startNight){
	    					
	    					// Prevents Slimes and animals from spawning and resets global flags.
							server.dispatchCommand(Bukkit.getConsoleSender(),"region flag __global__" + " -w "+ worldName + " deny-spawn Slime, Pig, Chicken, Sheep, Cow, Horse, Rabbit");
	    					
							// Clears ready list.
							playerNames.removeAll();
							
	    					// Increase the number of nights that have passed.
	    					night++;
	    					nights++;
	    					
	    					// Sets the tab view score to player kills
	    					server.dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives setDisplay list Player-Kills");
	    					
	    					// Teleports players to spawn.
	        				for(Player a : list)
	        				{
	        					a.teleport(server.getWorld(worldName).getSpawnLocation());
	        				}
	        				
	        				// Lets a player know the following: that it's night time, what night it is, and that spawn protection will only last 30 seconds.
	        				Bukkit.broadcastMessage(ChatColor.BLUE + "It's night time! Go out and earn your keep!");
	        				Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "Night: " + night);
	        				server.dispatchCommand(Bukkit.getConsoleSender(),"region flag spawn" + " -w "+ worldName + " mob-damage deny");
	        				Bukkit.broadcastMessage(ChatColor.GREEN + (ChatColor.ITALIC + "Spawn protection from mobs will be disabled in 30 seconds!"));
	        				if(!runningProtect)
	        					spawnProtection();
	    					
	    					// Disables PvP during the night. (Can be enabled by PvP event.)
	    					server.getWorld(worldName).setPVP(false);
	        				
	    					// Picks an event if the night is an evenNight.
	        				if(night%eventNight == 0){
	        					
	        					// The "random" event picker.
	        					if(anyEvent)
	        						eventPicker = (int)(Math.random()*100+1);
	        					
	        					// Enderdragon with 20% Chance
	        					if(eventPicker>0 && eventPicker<20) {event.setEvent("Enderdragon");}
	        					
	        					// Wither with 10% Chance
	        					else if(eventPicker>20 && eventPicker<30){event.setEvent("Wither");}
	        					
	        					// Field of Flowers with 10% Chance
	        					else if(eventPicker>=30 && eventPicker<40){event.setEvent("Field of Flowers");}
	        					
	        					// Lightning Storm 10% Chance
	        					else if(eventPicker>=40 && eventPicker<50){event.setEvent("Lightning Storm");}
	        					
	        					// Explosive Drops 10% Chance
	        					else if(eventPicker>=50 && eventPicker<60){event.setEvent("Explosive Drops");}
	        					
	        					// Hot Potato 5% Chance
	        					else if(eventPicker>=60 && eventPicker<65){event.setEvent("Hot Potato");}
	        					
	        					// PvP On 20% Chance
	        					else if(eventPicker>=65 && eventPicker<85){event.setEvent("PvP On");}
	        					
	        					// Giants 5% Chance
	        					else if(eventPicker>=85 && eventPicker<90){event.setEvent("Giants");}
	        					
	        					// Baby Zombie Swarm 5% Chance
	        					else if(eventPicker>=90 && eventPicker<95){event.setEvent("Baby Zombie Swarm");}
	        					
	        					// Corrupted Wolf Pack 5% Chance
	        					else if(eventPicker>=95 && eventPicker<=100){event.setEvent("Corrupted Wolf Pack");}
	        					
	        				}// End of eventNight
	        				
	    					// Alternates from night to day variable.
	    					startDay = true;
	    					startNight = false;
	    					
	    				}// End of startNight
					}// End of player check
				}// End of night time
				
				// ----------------- Anything that needs to be running whether it's day or night. ------------------------
				
				// This allows the /eventnight command to update and take.
				eventNight = getConfig().getInt("Event Night");
				
			}// End of Run
        }, 0L, 1L);// End of Repeating Task
        
	}// End of onEnable()   
	
	// When plugin stops:
	public void onDisable() {
		
		// Save any changes to configuration file:
		getConfig().set("Current Night", night);
		saveConfig();
		
		log("[MobFighter] Disabled!");
		
	}// End of onDisable()
	
	// Delay these commands by 5 seconds to allow the plugin to fully load.
	private void runStartVariables(){
		server.getScheduler().scheduleSyncDelayedTask(this, new Runnable() 
		{
			public void run() 
			{
				server.dispatchCommand(Bukkit.getConsoleSender(),"setboards");
				server.dispatchCommand(Bukkit.getConsoleSender(),"gamerule keepInventory true");
			}
		}, 20*5);
		getConfig().set("Just Added", false);
		saveConfig();
		reloadConfig();
	}
	
	// 30 Seconds of protection from monsters when the night begins.
	private void spawnProtection()
	{
		runningProtect = true;
		
		// Only 1 instance per 30 seconds
		if(runningProtect){
			server.getScheduler().scheduleSyncDelayedTask(this, new Runnable() 
			{
				public void run() 
				{
					if(isNight)
					{
						server.dispatchCommand(Bukkit.getConsoleSender(),"region flag spawn" + " -w "+ worldName + " mob-damage");
						Bukkit.broadcastMessage(ChatColor.RED + (ChatColor.ITALIC + "Spawn protection from mobs is now disabled!"));
					}
					// Time was set to day.
					else
						Bukkit.broadcastMessage(ChatColor.RED + (ChatColor.ITALIC + "Spawn protection was not disabled because it's daytime."));

					runningProtect = false;
				}
			}, 20*30);
		}
	}// End of spawnProtection.
	
	// Gets the highest scores from both offline and online players and compares them to find the top player.
	@SuppressWarnings("deprecation")
	private String topPlayer(){
		Objective obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("Total_Kills");
		String topPlayer = "";
		
		OfflinePlayer[] players = Bukkit.getOfflinePlayers();
		OfflinePlayer playerOff = null;
		if(players.length > 0)
			playerOff = players[0];
		
		// Find top player of offline players.
		for(int p=(players.length - 1); p>0;p--)
			if(playerOff != null)
				if(obj.getScore(playerOff).getScore() < obj.getScore(players[p]).getScore())
					playerOff = players[p];
		
		Player[] playersOn = new Player[server.getOnlinePlayers().size()];
		int i = 0;
		for(Player p : server.getOnlinePlayers()){
			playersOn[i] = p;
			i++;
		}
		
		Player playerOn = null;
		if(playersOn.length > 0)
			playerOn =  playersOn[0];
		
		// Find top player of online players.
		for(int p=(playersOn.length - 1); p>0; p--)
			if(playerOn != null)
				if(obj.getScore(playerOn).getScore() < obj.getScore(playersOn[p]).getScore())
					playerOn = playersOn[p];
		
		// Checks all cases of null players.
		if(playerOff == null && playerOn == null)
			return "";
		else if(playerOff == null)
			return playerOn.getName().toString();
		else if(playerOn == null)
			return playerOff.getName().toString();
		
		// Find the top player between offline's top player & online's top player.
		else if(obj.getScore(playerOff).getScore() < obj.getScore(playerOn).getScore())
			topPlayer = playerOn.getName().toString();
		else
			topPlayer = playerOff.getName().toString();
		
		return topPlayer;
	}
	
	// Simple log method for short handing.
	private void log(String s){System.out.println(s);}
}
