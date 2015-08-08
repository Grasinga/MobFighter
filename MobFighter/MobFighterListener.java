package net.grasinga.MobFighter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.milkbowl.vault.economy.Economy;

public class MobFighterListener implements Listener {
	
	// Variable for the plugin.
	private JavaPlugin mobfighter = null;
	
	// Economy:
	private static Economy econ = VaultEco.getEconomy();
	
	// Variables for drop items and sell values:
	public static ArrayList<String> mobDrops = new ArrayList<String>();
    public static ArrayList<String> mobDropPrices = new ArrayList<String>();
	
	// Variables to help regenerate blocks during the day:
	private ArrayList<BlockState> blockStates = new ArrayList<BlockState>();
	private int fixBlockDelay = 0;
	
	// Constructor to pass in important variables.
	public MobFighterListener(JavaPlugin p){
		mobfighter = p;
		
		// Gets the drops and their prices.
		fillDrops();
		fillDropPrices();
	}
	
	// Gets the list of drops from the configuration file.
	private void fillDrops() {
		List<String> list = mobfighter.getConfig().getStringList("MobDrops");
		String[] a = list.toArray(new String[0]);
		for(int i=0;i<a.length;i++)
		{
			mobDrops.add(a[i]);
		}
	}
	
	// Gets the list of prices for the drops from the configuration file.
	private void fillDropPrices() {
		List<String> list = mobfighter.getConfig().getStringList("MobDropPrice");
		String[] a = list.toArray(new String[0]);
		for(int i=0;i<a.length;i++)
		{
			mobDropPrices.add(a[i]);
		}
	}
	
	// Boosts the player's health scale based on the configuration file's info.
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void logIn(PlayerLoginEvent e)
	{
		Player p = e.getPlayer();
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"scoreboard players set " + p.getDisplayName() + " Money_Top " + (int) econ.getBalance(p.getDisplayName()));
		try
		{
			p.setHealthScale(mobfighter.getConfig().getDouble("HealthScale." + p.getDisplayName()));
		}
		catch(Exception ex){return;}
	}
	
	// Gives a player who has joined for the first time the shop item.
	@EventHandler(priority = EventPriority.NORMAL)
	public void playerJoin(PlayerJoinEvent e)
	{	
		Player p = e.getPlayer();
		
		// Player has logged in before.
		if(p.hasPlayedBefore()){ return; }
		
		// Give starter kit and shop to player who joins for the first time.
		ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
		ItemStack leatherHat = new ItemStack(Material.LEATHER_HELMET);
		ItemStack leatherChest = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack leatherLegs = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack leatherBoots = new ItemStack(Material.LEATHER_BOOTS);
		ItemStack bow = new ItemStack(Material.BOW);
		ItemStack arrow = new ItemStack(Material.ARROW,32);
		ItemStack beef = new ItemStack(Material.COOKED_BEEF,16);
		p.getInventory().setHelmet(leatherHat);
		p.getInventory().setChestplate(leatherChest);
		p.getInventory().setLeggings(leatherLegs);
		p.getInventory().setBoots(leatherBoots);
		p.getInventory().addItem(stoneSword);
		p.getInventory().addItem(bow);
		p.getInventory().addItem(arrow);
		p.getInventory().addItem(beef);
		ItemStack paper = new ItemStack(Material.PAPER);
		ItemMeta paperMeta = paper.getItemMeta();
		paperMeta.setDisplayName(ChatColor.BLUE + "Shop");
		paper.setItemMeta(paperMeta);
		p.getInventory().addItem(paper);
		p.sendMessage(ChatColor.BLUE + "Right-click the piece of paper to open the shop!");
	}
	
	// Sets the configuration file's info in regards to the player's health scale when they log off.
	@EventHandler(priority = EventPriority.NORMAL)
	public void logOff(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		if(p.getHealthScale() > 20)
		{
			mobfighter.getConfig().set("HealthScale." + p.getDisplayName(), p.getHealthScale());
			mobfighter.saveConfig();
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void playerClick(PlayerInteractEvent event)
	{
		final Player player = event.getPlayer();
		
		//Sell Drops:
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			for(int a=0;a<mobDrops.size();a++)
			{
				if(player.getItemInHand().getType().toString().equals(mobDrops.get(a).toString()))
				{
					double dropPrice = Double.parseDouble(mobDropPrices.get(a));
					dropPrice *= player.getItemInHand().getAmount();
					player.setItemInHand(new ItemStack(Material.AIR));
					econ.depositPlayer(player.getDisplayName(),dropPrice);
					player.sendMessage(ChatColor.GREEN + "Sold " + mobDrops.get(a).toString() + " for: $" + dropPrice);
				}
			}
		}
		
		// Creative mode switch
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(player.getItemInHand().getType().equals(Material.BRICK))
			{
				for(int i=0;i<mobfighter.getConfig().getList("Creative Immunity").size();i++)
					if(player.getDisplayName().equalsIgnoreCase(mobfighter.getConfig().getList("Creative Immunity").get(i).toString()))
						return;
				if(player.getLevel() >= 60)
				{
					player.setItemInHand(new ItemStack(Material.AIR));
					player.setGameMode(GameMode.CREATIVE);
				}
				else
					player.sendMessage(ChatColor.RED + "You need to be at least level 60 to get creative!");
			}
		}
		
		// Pet Wolf spawn
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(player.getItemInHand().getType().equals(Material.MONSTER_EGG)){
				if(player.getItemInHand().getDurability() == EntityType.WOLF.getTypeId())
					player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
				player.setItemInHand(new ItemStack(Material.AIR));
			}
		}
		
		// Health Boost, Stat Boost, and Crafting 
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			if(player.getItemInHand().getType().equals(Material.RED_MUSHROOM))
			{
				int amount = player.getItemInHand().getAmount();
				player.setItemInHand(new ItemStack(Material.AIR));
				player.setHealthScale(player.getHealthScale() + 4 * amount);
				if(player.getHealthScale() > 40)
				{
					player.setHealthScale(40);
					player.sendMessage(ChatColor.RED + "Your health is already boosted to the max!");
				}
			}
			else if(player.getItemInHand().getType().equals(Material.STONE_BUTTON))
			{
				player.setItemInHand(new ItemStack(Material.AIR));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0));
				player.sendMessage(ChatColor.GREEN + "Stat Boost Activated!");
			}
			else if(player.getItemInHand().getType().equals(Material.WORKBENCH))
			{
				for(int i=0;i<mobfighter.getConfig().getList("Creative Immunity").size();i++)
					if(player.getDisplayName().equalsIgnoreCase(mobfighter.getConfig().getList("Creative Immunity").get(i).toString()))
						return;
				player.sendMessage(ChatColor.GRAY + "Type: /craft");
			}
		
		// Get special items for end-game armor set.
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(player.getItemInHand().getType().equals(Material.SPONGE))
			{
				player.getInventory().clear();
				ItemStack coal = new ItemStack(Material.COAL, 64);
				ItemMeta meta = coal.getItemMeta();
				meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Undead Heart");
				coal.setItemMeta(meta);
				int i = 0;
				while(i<36)
				{
					player.getInventory().addItem(coal);
					i++;
				}
				player.updateInventory();
			}
		}
		else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			if(player.getItemInHand().getType().equals(Material.SPONGE))
			{
				player.getInventory().clear();
				ItemStack emerald = new ItemStack(Material.EMERALD, 64);
				ItemMeta meta = emerald.getItemMeta();
				meta.setDisplayName(ChatColor.DARK_GREEN + "Tainted Soul");
				emerald.setItemMeta(meta);
				int n = 0;
				while(n<36)
				{
					player.getInventory().addItem(emerald);
					n++;
				}
				player.updateInventory();
			}
		}
		
		// Allows people with creative immunity to place blocks.
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(player.getGameMode().equals(GameMode.CREATIVE))
			{
				for(int i=0;i<mobfighter.getConfig().getList("Creative Immunity").size();i++)
					if(!(player.getDisplayName().equalsIgnoreCase(mobfighter.getConfig().getList("Creative Immunity").get(i).toString())))
						if(!(player.getItemInHand().getType().equals(Material.AIR)))
							event.setCancelled(true);
			}
		}
		
		// Opens the main shop by right-clicking the shop paper.
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			if(player.getItemInHand().getType().equals(Material.PAPER))
				player.openInventory(ShopMenu.getShop());
		
	}
	
	// Handles buying items.
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBuyClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		if(player != null)
		{
			ItemStack clicked = event.getCurrentItem();
			Inventory inventory = event.getInventory();
			if(inventory.getName().equals(ShopMenu.shopName()))
			{
				// Items can't be taken from the shop.
				event.setCancelled(true);
				
				if(clicked.getType() == Material.STONE_SWORD)
				{
					if(VaultEco.getEconomy().getBalance(player) < 10)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 10);
					player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
				}
				else if(clicked.getType() == Material.LEATHER_HELMET)
				{
					if(VaultEco.getEconomy().getBalance(player) < 5)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 5);
					player.getInventory().addItem(new ItemStack(Material.LEATHER_HELMET));
				}
				else if(clicked.getType() == Material.LEATHER_CHESTPLATE)
				{
					if(VaultEco.getEconomy().getBalance(player) < 10)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 10);
					player.getInventory().addItem(new ItemStack(Material.LEATHER_CHESTPLATE));
				}
				else if(clicked.getType() == Material.LEATHER_LEGGINGS)
				{
					if(VaultEco.getEconomy().getBalance(player) < 10)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 10);
					player.getInventory().addItem(new ItemStack(Material.LEATHER_LEGGINGS));
				}
				else if(clicked.getType() == Material.LEATHER_BOOTS)
				{
					if(VaultEco.getEconomy().getBalance(player) < 5)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 5);
					player.getInventory().addItem(new ItemStack(Material.LEATHER_BOOTS));
				}
				else if(clicked.getType() == Material.BOW)
				{
					if(VaultEco.getEconomy().getBalance(player) < 10)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 10);
					player.getInventory().addItem(new ItemStack(Material.BOW));
				}
				else if(clicked.getType() == Material.GOLD_SWORD)
				{
					if(VaultEco.getEconomy().getBalance(player) < 5)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 5);
					player.getInventory().addItem(new ItemStack(Material.GOLD_SWORD));
				}
				else if(clicked.getType() == Material.GOLD_HELMET)
				{
					if(VaultEco.getEconomy().getBalance(player) < 10)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 10);
					player.getInventory().addItem(new ItemStack(Material.GOLD_HELMET));
				}
				else if(clicked.getType() == Material.GOLD_CHESTPLATE)
				{
					if(VaultEco.getEconomy().getBalance(player) < 15)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 15);
					player.getInventory().addItem(new ItemStack(Material.GOLD_CHESTPLATE));
				}
				else if(clicked.getType() == Material.GOLD_LEGGINGS)
				{
					if(VaultEco.getEconomy().getBalance(player) < 15)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 15);
					player.getInventory().addItem(new ItemStack(Material.GOLD_LEGGINGS));
				}
				else if(clicked.getType() == Material.GOLD_BOOTS)
				{
					if(VaultEco.getEconomy().getBalance(player) < 10)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 10);
					player.getInventory().addItem(new ItemStack(Material.GOLD_BOOTS));
				}
				else if(clicked.getType() == Material.ARROW)
				{
					if(VaultEco.getEconomy().getBalance(player) < 10)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 10);
					player.getInventory().addItem(new ItemStack(Material.ARROW,16));
				}
				else if(clicked.getType() == Material.IRON_SWORD)
				{
					if(VaultEco.getEconomy().getBalance(player) < 50)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 50);
					player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
				}
				else if(clicked.getType() == Material.IRON_HELMET)
				{
					if(VaultEco.getEconomy().getBalance(player) < 100)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 100);
					player.getInventory().addItem(new ItemStack(Material.IRON_HELMET));
				}
				else if(clicked.getType() == Material.IRON_CHESTPLATE)
				{
					if(VaultEco.getEconomy().getBalance(player) < 400)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 400);
					player.getInventory().addItem(new ItemStack(Material.IRON_CHESTPLATE));
				}
				else if(clicked.getType() == Material.IRON_LEGGINGS)
				{
					if(VaultEco.getEconomy().getBalance(player) < 300)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 300);
					player.getInventory().addItem(new ItemStack(Material.IRON_LEGGINGS));
				}
				else if(clicked.getType() == Material.IRON_BOOTS)
				{
					if(VaultEco.getEconomy().getBalance(player) < 100)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 100);
					player.getInventory().addItem(new ItemStack(Material.IRON_BOOTS));
				}
				else if(clicked.getType() == Material.COOKED_BEEF)
				{
					if(VaultEco.getEconomy().getBalance(player) < 10)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 10);
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF,8));
				}
				else if(clicked.getType() == Material.DIAMOND_SWORD)
				{
					if(VaultEco.getEconomy().getBalance(player) < 100)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 100);
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
				}
				else if(clicked.getType() == Material.DIAMOND_HELMET)
				{
					if(VaultEco.getEconomy().getBalance(player) < 300)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 300);
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_HELMET));
				}
				else if(clicked.getType() == Material.DIAMOND_CHESTPLATE)
				{
					if(VaultEco.getEconomy().getBalance(player) < 2600)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 2600);
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_CHESTPLATE));
				}
				else if(clicked.getType() == Material.DIAMOND_LEGGINGS)
				{
					if(VaultEco.getEconomy().getBalance(player) < 1000)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 1000);
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_LEGGINGS));
				}
				else if(clicked.getType() == Material.DIAMOND_BOOTS)
				{
					if(VaultEco.getEconomy().getBalance(player) < 300)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 300);
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_BOOTS));
				}
				else if(clicked.getType() == Material.APPLE)
				{
					if(VaultEco.getEconomy().getBalance(player) < 20)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 20);
					player.getInventory().addItem(new ItemStack(Material.APPLE,10));
				}
				else if(clicked.getType() == Material.BOOK)
				{
					if(VaultEco.getEconomy().getBalance(player) < 100)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 100);
					player.getInventory().addItem(new ItemStack(Material.BOOK));
				}
				else if(clicked.getType() == Material.EXP_BOTTLE)
				{
					if(VaultEco.getEconomy().getBalance(player) < 100)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 100);
					player.getInventory().addItem(new ItemStack(Material.EXP_BOTTLE,10));
				}
				else if(clicked.getType() == Material.LAPIS_BLOCK)
				{
					if(VaultEco.getEconomy().getBalance(player) < 50)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 50);
					player.getInventory().addItem(new ItemStack(Material.LAPIS_BLOCK));
				}
				else if(clicked.getType() == Material.GOLD_INGOT)
				{
					if(VaultEco.getEconomy().getBalance(player) < 100)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 100);
					player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,10));
				}
				else if(clicked.getType() == Material.IRON_INGOT)
				{
					if(VaultEco.getEconomy().getBalance(player) < 1000)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 1000);
					player.getInventory().addItem(new ItemStack(Material.IRON_INGOT,10));
				}
				else if(clicked.getType() == Material.DIAMOND)
				{
					if(VaultEco.getEconomy().getBalance(player) < 2000)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 2000);
					player.getInventory().addItem(new ItemStack(Material.DIAMOND,10));
				}
				else if(clicked.getType() == Material.BRICK)
				{
					if(VaultEco.getEconomy().getBalance(player) < 200)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 200);
					player.getInventory().addItem(clicked);
				}
				else if(clicked.getType() == Material.WORKBENCH)
				{
					if(VaultEco.getEconomy().getBalance(player) < 1000)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 1000);
					player.getInventory().addItem(clicked);
				}
				else if(clicked.getType() == Material.POTION)
				{
					if(VaultEco.getEconomy().getBalance(player) < 20)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 20);
					player.getInventory().addItem(clicked);
				}
				else if(clicked.getType() == Material.WRITTEN_BOOK)
				{
					player.getInventory().addItem(clicked);
				}
			}// End of main shop.
			if(inventory.getName().equals(EliteShop.shopName()))
			{
				// No items can be taken from the elite shop.
				event.setCancelled(true);

				if(clicked.getType() == Material.MONSTER_EGG)
				{
					if(VaultEco.getEconomy().getBalance(player) < 500)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 500);
					player.getInventory().addItem(clicked);
				}
				else if(clicked.getType() == Material.STONE_BUTTON)
				{
					if(VaultEco.getEconomy().getBalance(player) < 10000)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 10000);
					player.getInventory().addItem(clicked);
				}
				else if(clicked.getType() == Material.RED_MUSHROOM)
				{
					if(VaultEco.getEconomy().getBalance(player) < 2000)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 2000);
					player.getInventory().addItem(clicked);
				}
				else if(clicked.getType() == Material.RED_ROSE)
				{
					if(VaultEco.getEconomy().getBalance(player) < 1000)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 1000);
					player.getInventory().addItem(clicked);
				}
				else if(clicked.getType() == Material.NETHER_STAR)
				{
					if(VaultEco.getEconomy().getBalance(player) < 200000)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 200000);
					ItemStack star = new ItemStack(Material.NETHER_STAR);
					ItemMeta meta = star.getItemMeta();
					meta.setDisplayName(ChatColor.GREEN + "Swirling Souls");
					star.setItemMeta(meta);
					player.getInventory().addItem(star);
				}
				else if(clicked.getType() == Material.FIREWORK_CHARGE)
				{
					if(VaultEco.getEconomy().getBalance(player) < 200000)
					{
						player.sendMessage(ChatColor.RED + "You do not have enough money!");
						return;
					}
					VaultEco.getEconomy().withdrawPlayer(player, 200000);
					ItemStack fCharge = new ItemStack(Material.FIREWORK_CHARGE);
					ItemMeta meta = fCharge.getItemMeta();
					meta.setDisplayName(ChatColor.DARK_PURPLE + "Festering Darkness");
					fCharge.setItemMeta(meta);
					player.getInventory().addItem(fCharge);
				}
			}// End of elite shop.
		}// End of null check.
	}// End of inventory click.
	
	// No building allowed on server.
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlaceBlock(BlockPlaceEvent event){
		Player player = event.getPlayer();
		
		// Building allowed if the player has creative immunity.
		for(int i=0;i<mobfighter.getConfig().getList("Creative Immunity").size();i++)
			if(player.getDisplayName().equalsIgnoreCase(mobfighter.getConfig().getList("Creative Immunity").get(i).toString()))
					return;
		
		event.setCancelled(true);
	}
	
	// No block breaking allowed on server.
		@EventHandler(priority = EventPriority.NORMAL)
		public void onPlaceBreak(BlockBreakEvent event){
			Player player = event.getPlayer();
			
			// Block breaking allowed if the player has creative immunity.
			for(int i=0;i<mobfighter.getConfig().getList("Creative Immunity").size();i++)
				if(player.getDisplayName().equalsIgnoreCase(mobfighter.getConfig().getList("Creative Immunity").get(i).toString()))
						return;
			
			event.setCancelled(true);
		}
	
	// Handles player interaction with anvil while in or out of creative.
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void anvilFix(final InventoryClickEvent e)
	{
		if(!e.isCancelled())
		{
			HumanEntity ent = e.getWhoClicked();
			
			// Anvil used with survival
			if(ent instanceof Player)
			{
				final Player player = (Player)ent;
				Inventory inv = e.getInventory();
				
				// Anvil used with creative:
				if(player.getGameMode() == GameMode.CREATIVE)
				{
					if(inv instanceof AnvilInventory)
					{
						AnvilInventory anvil = (AnvilInventory)inv;
						InventoryView view = e.getView();
						int rawSlot = e.getRawSlot();
						if(rawSlot == view.convertSlot(rawSlot))
						{
							if(rawSlot == 2)
							{
								ItemStack[] items = anvil.getContents();
								ItemStack item1 = items[0];
								ItemStack item2 = items[1];
								if(item1 != null && item2 != null)
								{
									int id1 = item1.getTypeId();
									int id2 = item2.getTypeId();
									if(id1 != 0 && id2 != 0)
									{
										ItemStack item3 = e.getCurrentItem();
										if(item3 != null)
										{
											ItemMeta meta = item3.getItemMeta();
											if(meta != null)
											{
												// Player has to have 60 or more levels to use the creative combine.
												if(player.getLevel() >= 60)
												{
													player.setLevel(player.getLevel()-60);
													player.sendMessage(ChatColor.GREEN + "Repair/Combine Successful!");
													Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mobfighter, new Runnable() 
													{
														public void run() 
														{
															// Bypass being set to survival after using anvil if the player has creative immunity.
															for(int i=0;i<mobfighter.getConfig().getList("Creative Immunity").size();i++)
																if(player.getDisplayName().equalsIgnoreCase(mobfighter.getConfig().getList("Creative Immunity").get(i).toString()))
																	return;
															// Sets the player back to survival once the anvil is used.
															e.getWhoClicked().getOpenInventory().close();
															player.setGameMode(GameMode.ADVENTURE);
														}
													}, 20*2);
												}// End of creative combine.	
											}// Didn't have enough levels to combine/finishing.
										}// Item 3 was/wasn't null.
									}// Item 1 & 2 id's were/weren't null.
								}// Item 1 & 2 was/wasn't null.
							}// End of (rawSlot == 2)
						}// End of convert view
					}// End of anvil inventory
					else
					{
						// Bypass having the player's inventory closed if they have creative immunity.
						for(int i=0;i<mobfighter.getConfig().getList("Creative Immunity").size();i++)
							if(player.getDisplayName().equalsIgnoreCase(mobfighter.getConfig().getList("Creative Immunity").get(i).toString()))
								e.setCancelled(false);
						// Closes the player's inventory after the anvil is used.
							else
							{
								e.setCancelled(true);
								player.closeInventory();
							}
					}
				}// End of anvil with creative
			}// End of anvil with survival
		}// End of !isCancelled
	}// End of anvilFix
	
	// Handles closing a player's inventory after they have crafted an item.
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCraft(final CraftItemEvent event)
	{
		event.getWhoClicked().getInventory().remove(Material.WORKBENCH);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mobfighter, new Runnable() {
			 
		    public void run() 
		    {
				event.getWhoClicked().getOpenInventory().close();
		    }
		 
		}, 20*1);
	}
	
	// Handles death related events.
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onDeath(EntityDeathEvent event){
		
		if(event.getEntity() instanceof Player)
		{
			Player player = (Player)event.getEntity();
			
			// Players lose 5% of their money on death.
			Double loss = 0.0;
			Double amount = econ.getBalance(player.getDisplayName());
			loss = amount * 0.05;
			econ.withdrawPlayer(player.getDisplayName(),loss);
		}
		
		// Powered Creepers drop diamond when killed.
		if(event.getEntity() instanceof Creeper)
		{
			if(((Creeper) event.getEntity()).isPowered())
			{
				ItemStack item = new ItemStack(Material.DIAMOND);
				event.getDrops().add(item);
			}
		}
	}
	
	// Regenerates the blocks that were added to blockStates.
	@EventHandler (priority = EventPriority.HIGH)
	public void chunkLoad(ChunkLoadEvent event){
		
		// Only regenerates during the day.
		if(!MobFighter.isNight)
			for(int i=0;i<blockStates.size();i++)
			{
				final BlockState state = blockStates.get(i);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mobfighter, new Runnable()
					{
						public void run()
					{
							state.update(true, false);
						}
					}, fixBlockDelay);
				blockStates.remove(i);
			}
	}
	
	// Adds blown up blocks into blockStates.
	@EventHandler (priority = EventPriority.HIGH)
	public void onExplode(final EntityExplodeEvent e)
	{
        for(Block b : e.blockList())
        {
        	final BlockState state = b.getState();
        	b.setType(Material.AIR);
        	blockStates.add(state);

			int delay = 5;
	        if((b.getType() == Material.SAND || b.getType() == Material.GRAVEL))
	        	delay++;
	        
	        fixBlockDelay = delay;
        }
	}
	
	// Enderdragon does not spawn a portal when killed.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityCreatePortalEvent(EntityCreatePortalEvent event) 
	{
		if (event.getEntity() instanceof EnderDragon)
		{
			event.setCancelled(true);
		}
	}
}
