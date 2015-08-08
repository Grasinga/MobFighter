package net.grasinga.MobFighter;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class MobFighterCommands implements CommandExecutor {
	
	private MobFighter mobfighter;
	
	// Used for short handing the server.
	private Server server = null;
	
	// Used for short handing the world name and for getting specific a world name other that 'world' in configuration file.
	private String worldName = "";
	
	// Used for short handing the world.
	private World world = null;
	
	// Important variables for MobFighterCommands.
	private int countDay = 0;
	private int countNight = 0;
	private boolean day = false;
	private boolean night = false;
	private List<Player> playersOnServer = null;
	private int numberOfPlayers = 0;
	
	// Constructor to pass in any needed variables.
	public MobFighterCommands(MobFighter plugin){
		mobfighter = plugin;
		
		// Sets values
		server = Bukkit.getServer();
		worldName = mobfighter.getConfig().getString("World Name");
		world = server.getWorld(worldName);
		
		// Get number of players on the server.
		numberOfPlayers = getNumberOfPlayers();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		// Commands for anyone (some have permissions).
		
			// Command to reload the plugin's configuration file.
			if(commandLabel.equalsIgnoreCase("mfreload")){
				try
				{
					sender.sendMessage(ChatColor.GREEN + "Reloading MobFighter's configuration file . . .");
					mobfighter.getConfig().set("Current Night", MobFighter.nights);
					mobfighter.saveConfig();
					mobfighter.reloadConfig();
					sender.sendMessage(ChatColor.GREEN + "MobFighter's configuration file has been reloaded!");
				}
				catch(Exception e){sender.sendMessage(ChatColor.RED + "Error: " + e); return false;} // Fails to reload.
				return true;
			}
			
			// Sets the scoreboards that are to be used ingame.
			else if(commandLabel.equalsIgnoreCase("setboards"))
			{			
				server.dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives add Player-Kills playerKillCount");
				server.dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives add Total_Kills totalKillCount");
				server.dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives add Health health");
				server.dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives add Money_Top dummy");
				server.dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives setDisplay list Player-Kills");
				server.dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives setDisplay sidebar Total_Kills");
				server.dispatchCommand(Bukkit.getConsoleSender(),"scoreboard objectives setDisplay belowName Health");
				return true;
			}
			
			// Displays the amount of nights that have passed.
			else if(commandLabel.equalsIgnoreCase("night"))
			{
				sender.sendMessage(ChatColor.DARK_AQUA + ("Night: " + MobFighter.nights));
				return true;
			}			

			// Allows the sender to set the amount of nights that have passed.
			else if(commandLabel.equalsIgnoreCase("setnight")){
				if(!(args.length<=0)){
					try{Integer.parseInt(args[0]);}
					// Was something other than a number trying to be parsed.
					catch(Exception e){sender.sendMessage(ChatColor.RED + "Number has to be between 0 and 1000000!"); return false;}
				}				
				if(Integer.parseInt(args[0]) >= 0 && Integer.parseInt(args[0]) <= 1000000){
					
						MobFighter.nights = Integer.parseInt(args[0]);
						mobfighter.getConfig().set("Current Night", Integer.parseInt(args[0]));
						mobfighter.saveConfig();
						mobfighter.reloadConfig();
						sender.sendMessage(ChatColor.BLUE + "Night set to: " + Integer.parseInt(args[0]));
						return true;					
				}
				// Number was not between 0 and 1000000.
				else
					sender.sendMessage(ChatColor.RED + "Number has to be between 0 and 1000000!");
				return false;
			}
			
			// Allows the sender to set what nights the events start.
			else if(commandLabel.equalsIgnoreCase("eventnight")){
				if(!(args.length <= 0)){
					try{Integer.parseInt(args[0]);}
					// Was something other than a number trying to be parsed.
					catch(Exception e){sender.sendMessage(ChatColor.RED + "Number has to be between positive!"); return false;}
				}				
				if(Integer.parseInt(args[0]) >= 1){
					
						mobfighter.getConfig().set("Event Night", Integer.parseInt(args[0]));
						mobfighter.saveConfig();
						mobfighter.reloadConfig();
						sender.sendMessage(ChatColor.BLUE + "Event night set to: " + Integer.parseInt(args[0]));
						return true;					
				}
				// Number was not positive.
				else
					sender.sendMessage(ChatColor.RED + "Number has to be positive!");
				return false;
			}
		
		// Commands for players.
		if(sender instanceof Player){
			
			Player player = (Player) sender;
			
			if(commandLabel.equalsIgnoreCase("ready"))
			{				
				// Tells whether it is day or night.
				if(world.getTime() > 0 && world.getTime() < 13700) // Day
				{
					day = true;
					night = false;
				}
				else if(world.getTime() > 13700 && world.getTime() < 24000) // Night
				{
					day = false;
					night = true;
				}
				
				// Sets values for variables to use.
				playersOnServer = world.getPlayers();
				numberOfPlayers = getNumberOfPlayers();
				
				// Checks to see how many people have readied up.
				if(!(args.length<=0) && args[0].equalsIgnoreCase("list"))
				{
					
					player.sendMessage("Players Readied: " + ChatColor.GREEN + MobFighter.playerNames.getAllNames());
					return true;
				}
				
				else if(!(getHasReadied(player.getDisplayName())))
				{
					if(args.length <= 0)
					{
						// Ready for day time.
						if(night)
						{
							countDay++;
							player.chat(ChatColor.GREEN + "Readied for Day!");
							MobFighter.playerNames.addName(player.getDisplayName());
							if(countDay >= numberOfPlayers)
							{
								countDay = 0;
								countNight = 0;
								world.setTime(0);
								MobFighter.playerNames.removeAll();
								return true;
							}
							return true;
						}
						
						// Ready for night time.
						else if(day)
						{
							countNight++;
							player.chat(ChatColor.GREEN + "Readied for Night!");
							MobFighter.playerNames.addName(player.getDisplayName());
							if(countNight >= numberOfPlayers)
							{
								countDay = 0;
								countNight = 0;
								world.setTime(13700);
								MobFighter.playerNames.removeAll();
								return true;
							}
							return true;
						}
							
					}
					
					// If /ready is used with more than one argument.
					else if(args.length >= 1)
					{
							player.sendMessage("Please type: /ready or /ready list");
							return false;
					}
					
					else
						return false;
				}// End of has not readied
				
				// Unready by using /ready after readying up.
				else if((getHasReadied(player.getDisplayName())))
				{
					MobFighter.playerNames.removeName(player.getDisplayName());
					if(countDay > 0)
						countDay--;
					else if(countNight > 0)
						countNight--;
					player.chat(ChatColor.RED + "Unreadied");
					return true;
				}
				
				return true;
			}
			
			// Command to get the main shop.
			else if(commandLabel.equalsIgnoreCase("getshop"))
			{
				ItemStack paper = new ItemStack(Material.PAPER);
				ItemMeta paperMeta = paper.getItemMeta();
				paperMeta.setDisplayName(ChatColor.BLUE + "Shop");
				paper.setItemMeta(paperMeta);
				player.getInventory().addItem(paper);
				player.sendMessage(ChatColor.BLUE + "Right-click the piece of paper to open the shop!");
			}
			
			// Command to get the main shop.
			else if(commandLabel.equalsIgnoreCase("eliteshop"))
			{
				if(sender.getName().toString().equalsIgnoreCase(mobfighter.getConfig().getString("Top Player")))
					player.openInventory(EliteShop.getShop());
				else
					sender.sendMessage(ChatColor.RED + "Sorry, you are not the top player!");
			}
			
			// Command to get helpful books.
			else if(commandLabel.equalsIgnoreCase("getbook"))
			{
				// Commands arguments lead to the start book:
				if(!(args.length<=0)&&(!(args.length>=2))&&args[0].equalsIgnoreCase("starter"))
				{
					ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
					BookMeta bm = (BookMeta) book.getItemMeta();
					// Book Page Per Line
					bm.setPages(Arrays.asList(
							"Welcome to:\nMobFighter!\n\nThis server is a\nmob fighting game!\nYou kill mobs and\npick up their loot\nat night so you\ncan sell it\nduring the day to\nget better gear!\nAs you play you\nmay notice a\n",
							"message saying \n\"Night: #\" this\nlets you know the\ncurrent night and\nhelps you prepare\nfor the events!\nEvents happen every\nfive waves varying\nfrom fighting the\nEnderdragon, to\nstrolling in a\nmeadow of flowers.\n",
							"Commands:\n/getshop\n\n/ready : Toggles ready status!\n\n/ready list\n/night\n/craft\n/call <username>\n/bring <username>\n/exchange\n/getbook <name>",
							"Emojies Spice up\nchat!\n\nType:\n/help mmemoji\n/help mmemoji 2\n/help mmemoji 3\n\nSee what emojies\nyou can use!\n\nThanks for reading!"));
					bm.setAuthor("Mob Fighter");
					bm.setTitle("Mob Fighter Info");
					book.setItemMeta(bm);
					player.getInventory().addItem(book);
				}
				
				// Command arguments lead to the anvil book.
				else if(!(args.length<=0)&&(!(args.length>=2))&&args[0].equalsIgnoreCase("anvil"))
				{
					ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
					BookMeta bm = (BookMeta) book.getItemMeta();
					// Book Page Per Line
					bm.setPages(Arrays.asList(
							"The anvil has a restriction set on any enchament combines that are over level 40.\n\nMinecraft Wiki:\n\nIf the job would cost 40 or more levels, it will be rejected as \"Too Expensive!\". (This does not apply in creative mode.)",
							"So in order to combine items that are \"Too Expensive!\" a player can buy creative in order to combine the two items. The player also has to have at least 60 levels!"));
					bm.setAuthor("Mob Fighter");
					bm.setTitle("Anvil - Creative");
					book.setItemMeta(bm);
					player.getInventory().addItem(book);
				}
				
				// Command arguments were invalid.
				else
					player.sendMessage("Current Books: starter and anvil.");
			}
			
			// Command to use the workbench to craft a single item.
			else if(commandLabel.equalsIgnoreCase("craft"))
			{
				if(player.getItemInHand().getType().equals(Material.WORKBENCH))
				{
					Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.WORKBENCH);
					player.openInventory(inv);
					player.openWorkbench(null, true);
					return true;
				}
				else
					player.sendMessage("You need a Workbench in your hand in order to craft!");
				return false;
			}
			
			// Command to get demon or sage armor.
			else if(commandLabel.equalsIgnoreCase("exchange"))
			{
				// Command arguments lead to demon path:
				if(!(args.length<=0)&&(!(args.length>=2))&&args[0].equalsIgnoreCase("demon"))
				{
					ItemStack coal = new ItemStack(Material.COAL);
					coal.setItemMeta(SpecialEventsListener.undeadHeart.getItemMeta());
					ItemStack fCharge = new ItemStack(Material.FIREWORK_CHARGE);
					ItemMeta meta = fCharge.getItemMeta();
					meta.setDisplayName(ChatColor.DARK_PURPLE + "Festering Darkness");
					fCharge.setItemMeta(meta);
					
					// Get item needed by trading in coal (undeadHearts).
					if(player.getInventory().containsAtLeast(coal, 1728))
					{
						player.getInventory().clear();
						player.getInventory().addItem(fCharge);
					}
					
					// Get the armor set if there are five Festering Darkness in the player's inventory.
					else if(player.getInventory().containsAtLeast(fCharge, 5))
					{
						player.getInventory().clear();
						ItemStack button = new ItemStack(Material.STONE_BUTTON);
						ItemStack workbench = new ItemStack(Material.WORKBENCH);
						ItemStack brick = new ItemStack(Material.BRICK);
						ItemStack dh = new ItemStack(Material.DIAMOND_HELMET);
						ItemMeta meta1 = dh.getItemMeta();
						meta1.setDisplayName(ChatColor.DARK_RED + "Minotuar's Horns");
						dh.setItemMeta(meta1);
						dh.addUnsafeEnchantment(new EnchantmentWrapper(0), 10);
						dh.addUnsafeEnchantment(new EnchantmentWrapper(7), 20);
						dh.addUnsafeEnchantment(new EnchantmentWrapper(34), 20);
						ItemStack dc = new ItemStack(Material.DIAMOND_CHESTPLATE);
						ItemMeta meta2 = dc.getItemMeta();
						meta2.setDisplayName(ChatColor.DARK_RED + "Cerberus' Breastplate");
						dc.setItemMeta(meta2);
						dc.addUnsafeEnchantment(new EnchantmentWrapper(0), 10);
						dc.addUnsafeEnchantment(new EnchantmentWrapper(3), 20);
						dc.addUnsafeEnchantment(new EnchantmentWrapper(34), 20);
						ItemStack dl = new ItemStack(Material.DIAMOND_LEGGINGS);
						ItemMeta meta3 = dc.getItemMeta();
						meta3.setDisplayName(ChatColor.DARK_RED + "Hades' Guard");
						dl.setItemMeta(meta3);
						dl.addUnsafeEnchantment(new EnchantmentWrapper(0), 10);
						dl.addUnsafeEnchantment(new EnchantmentWrapper(1), 20);
						dl.addUnsafeEnchantment(new EnchantmentWrapper(34), 20);
						ItemStack db = new ItemStack(Material.DIAMOND_BOOTS);
						ItemMeta meta4 = dc.getItemMeta();
						meta4.setDisplayName(ChatColor.DARK_RED + "Titan's Fortitude");
						db.setItemMeta(meta4);
						db.addUnsafeEnchantment(new EnchantmentWrapper(0), 10);
						db.addUnsafeEnchantment(new EnchantmentWrapper(4), 20);
						db.addUnsafeEnchantment(new EnchantmentWrapper(34), 20);
						ItemStack ds = new ItemStack(Material.DIAMOND_SWORD);
						ItemMeta meta5 = ds.getItemMeta();
						meta5.setDisplayName(ChatColor.DARK_RED + "Pandemonium");
						ds.setItemMeta(meta5);
						ds.addUnsafeEnchantment(new EnchantmentWrapper(16), 20);
						ds.addUnsafeEnchantment(new EnchantmentWrapper(20), 20);
						ds.addUnsafeEnchantment(new EnchantmentWrapper(34), 20);
						player.getInventory().addItem(dh);
						player.getInventory().addItem(dc);
						player.getInventory().addItem(dl);
						player.getInventory().addItem(db);
						player.getInventory().addItem(ds);
						player.getInventory().addItem(button);
						player.getInventory().addItem(workbench);
						player.getInventory().addItem(brick);
					}
					
					// Command was missing the substance that's needed.
					else
						player.sendMessage(ChatColor.GRAY + "You seem to be lacking substance.");
					
					return true;
				}
				// Command arguments lead to sage path:
				else if(!(args.length<=0)&&(!(args.length>=2))&&args[0].equalsIgnoreCase("sage"))
				{
					// Sets meta data for Tainted Souls and Swirling Souls
					ItemStack emerald = new ItemStack(Material.EMERALD);
					emerald.setItemMeta(SpecialEventsListener.taintedSoul.getItemMeta());
					ItemStack star = new ItemStack(Material.NETHER_STAR);
					ItemMeta meta = star.getItemMeta();
					meta.setDisplayName(ChatColor.GREEN + "Swirling Souls");
					star.setItemMeta(meta);
					
					// Get the item needed by trading in emeralds (taintedSouls).
					if(player.getInventory().containsAtLeast(emerald, 1728))
					{
						player.getInventory().clear();
						player.getInventory().addItem(star);
					}
					
					// Gets the armor set if there are 5 Swirling Souls in the players inventory.
					else if(player.getInventory().containsAtLeast(star, 5))
					{
						player.getInventory().clear();
						ItemStack button = new ItemStack(Material.STONE_BUTTON);
						ItemStack workbench = new ItemStack(Material.WORKBENCH);
						ItemStack brick = new ItemStack(Material.BRICK);
						ItemStack dh = new ItemStack(Material.DIAMOND_HELMET);
						ItemMeta meta1 = dh.getItemMeta();
						meta1.setDisplayName(ChatColor.AQUA + "Posiden's Helm");
						dh.setItemMeta(meta1);
						dh.addUnsafeEnchantment(new EnchantmentWrapper(0), 10);
						dh.addUnsafeEnchantment(new EnchantmentWrapper(7), 20);
						dh.addUnsafeEnchantment(new EnchantmentWrapper(34), 20);
						ItemStack dc = new ItemStack(Material.DIAMOND_CHESTPLATE);
						ItemMeta meta2 = dc.getItemMeta();
						meta2.setDisplayName(ChatColor.AQUA + "Zeus' Might");
						dc.setItemMeta(meta2);
						dc.addUnsafeEnchantment(new EnchantmentWrapper(0), 10);
						dc.addUnsafeEnchantment(new EnchantmentWrapper(3), 20);
						dc.addUnsafeEnchantment(new EnchantmentWrapper(34), 20);
						ItemStack dl = new ItemStack(Material.DIAMOND_LEGGINGS);
						ItemMeta meta3 = dc.getItemMeta();
						meta3.setDisplayName(ChatColor.AQUA + "Anthena's Wrath");
						dl.setItemMeta(meta3);
						dl.addUnsafeEnchantment(new EnchantmentWrapper(0), 10);
						dl.addUnsafeEnchantment(new EnchantmentWrapper(1), 20);
						dl.addUnsafeEnchantment(new EnchantmentWrapper(34), 20);
						ItemStack db = new ItemStack(Material.DIAMOND_BOOTS);
						ItemMeta meta4 = dc.getItemMeta();
						meta4.setDisplayName(ChatColor.AQUA + "Hermes' Boots");
						db.setItemMeta(meta4);
						db.addUnsafeEnchantment(new EnchantmentWrapper(0), 10);
						db.addUnsafeEnchantment(new EnchantmentWrapper(4), 20);
						db.addUnsafeEnchantment(new EnchantmentWrapper(34), 20);
						ItemStack ds = new ItemStack(Material.DIAMOND_SWORD);
						ItemMeta meta5 = ds.getItemMeta();
						meta5.setDisplayName(ChatColor.AQUA + "Nirvana");
						ds.setItemMeta(meta5);
						ds.addUnsafeEnchantment(new EnchantmentWrapper(16), 20);
						ds.addUnsafeEnchantment(new EnchantmentWrapper(19), 20);					
						ds.addUnsafeEnchantment(new EnchantmentWrapper(34), 20);
						player.getInventory().addItem(dh);
						player.getInventory().addItem(dc);
						player.getInventory().addItem(dl);
						player.getInventory().addItem(db);
						player.getInventory().addItem(ds);
						player.getInventory().addItem(button);
						player.getInventory().addItem(workbench);
						player.getInventory().addItem(brick);
					}
					// Command was missing the substance that's needed.
					else
						player.sendMessage(ChatColor.GRAY + "You seem to be lacking substance.");
					
					return true;
				}
				
				// Command was missing a path argument.
				else
					player.sendMessage(ChatColor.GRAY + "You seem to be lacking a path.");
					
				return true;
			}
		}
		
		// Commands for console.
		else if(sender instanceof ConsoleCommandSender){
			log("No extra console commands right now.");
			return false;
		}
		
		return false;
	}
	
	// Method that gets the number of players online.
	public int getNumberOfPlayers()
	{
		// First load/reload instance
		if(playersOnServer == null)
			return 0;
		
		int players = 0;
		for(int i=0; i<playersOnServer.size();i++)
			players++;
		return players;
	}
	
	// Method used to check if a player has done /ready
	public boolean getHasReadied(String name)
	{
		for(int i=0;i<MobFighter.playerNames.getSize();i++)
			if(MobFighter.playerNames.getName(i).equalsIgnoreCase(name))
				return true;
		return false;
	}
	
	// Simple log method for short handing.
	private void log(String s){System.out.println(s);}
}
