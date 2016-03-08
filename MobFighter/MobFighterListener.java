package net.grasinga.MobFighter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
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
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.milkbowl.vault.economy.Economy;

public class MobFighterListener implements Listener {
	
	// Variable for the plugin.
	private static JavaPlugin mobfighter = null;
	
	// Economy:
	public static Economy econ = VaultEco.getEconomy();
    
    private static Map<String, Object> sellingItems;
    private Map<String, Object> shopItems;
	
	// Variables to help regenerate blocks during the day:
	public static ArrayList<BlockState> blockStates = new ArrayList<BlockState>();
	public static int fixBlockDelay = 0;
	
	// Constructor to pass in important variables.
	public MobFighterListener(JavaPlugin p){
		mobfighter = p;
		
		// Gets the sellable items.
		fillSellables();
	}
	
	// Gets the list of sellable items from the configuration file.
	public static void fillSellables() {
		sellingItems = mobfighter.getConfig().getConfigurationSection("Sellable Items").getValues(false);
	}
	
	// Boosts the player's health based on the configuration file's info.
	@EventHandler(priority = EventPriority.NORMAL)
	public void logIn(PlayerLoginEvent e)
	{
		Player p = e.getPlayer();
		try
		{
			p.setMaxHealth(mobfighter.getConfig().getDouble("HealthBoost." + p.getDisplayName()));
			return;
		}
		// If the player isn't in the config file.
		catch(Exception ex){p.resetMaxHealth();}
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
		paperMeta.setLore(Arrays.asList(ChatColor.AQUA + "Right-click to open the shop!"));
		paper.setItemMeta(paperMeta);
		p.getInventory().addItem(paper);
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bm = (BookMeta) book.getItemMeta();
		// Book Page Per Line
		bm.setPages(Arrays.asList(
				"Welcome to:\nMobFighter!\n\nThis server is a\nmob fighting game!\nYou kill mobs and\npick up their loot\nat night so you\ncan sell it\nduring the day to\nget better gear!\nAs you play you\nmay notice a\n",
				"message saying \n\"Night: #\" this\nlets you know the\ncurrent night and\nhelps you prepare\nfor the events!\nEvents happen every\nfive waves varying\nfrom fighting the\nEnderdragon, to\nstrolling in a\nmeadow of flowers.\n",
				"Commands:\n/getshop\n\n/ready\n(Toggles ready status!)\n\n/ready list\n/night\n/craft\n/call <username>\n/bring <username>\n/exchange\n/getbook <name>",
				"See the full list of commands with /help\n\n Thanks for reading!"));
		bm.setAuthor("Mob Fighter");
		bm.setTitle("Mob Fighter Info");
		book.setItemMeta(bm);
		p.getInventory().addItem(book);
	}
	
	// Sets the configuration file's info in regards to the player's max health when they log off.
	@EventHandler(priority = EventPriority.NORMAL)
	public void logOff(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		if(p.getMaxHealth() > 20)
		{
			mobfighter.getConfig().set("HealthBoost." + p.getDisplayName(), p.getMaxHealth());
			mobfighter.saveConfig();
		}
	}

	// Handles multiple instances of player clicks.
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void playerClick(PlayerInteractEvent event)
	{
		final Player player = event.getPlayer();
		
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			// Creative mode switch
			if(player.getItemInHand().getType().equals(Material.BRICK))
			{
				// Bypass switch and level loss.
				if(mobfighter.getConfig().getList("Creative Immunity").contains(player.getDisplayName()))
					return;
				if(player.getLevel() >= 60)
				{
					player.setItemInHand(new ItemStack(Material.AIR));
					player.setLevel(player.getLevel() - 60);
					player.setGameMode(GameMode.CREATIVE);
				}
				else
					player.sendMessage(ChatColor.RED + "You need to be at least level 60 to get creative!");
			}
		
			// Health Boost, Stat Boost, and Crafting 
			if(player.getItemInHand().getType().equals(Material.RED_MUSHROOM) && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "Health Boost"))
			{
				int amount = player.getItemInHand().getAmount();
				player.setItemInHand(new ItemStack(Material.AIR));
				while(amount > 0) // Add health and refunds the money spent buying it if the player has used 5 already.
				{
					if(player.getMaxHealth() >= 40){
						player.setMaxHealth(40);
						player.sendMessage(ChatColor.RED + "Your health is already boosted to the max!");
						VaultEco.getEconomy().depositPlayer(player, Double.parseDouble(EliteShop.shop.getString("RED_MUSHROOM")));
						player.sendMessage(ChatColor.GREEN + "Refunded: $" + EliteShop.shop.getString("RED_MUSHROOM"));
					}
					else
						player.setMaxHealth(player.getMaxHealth() + 4);
					amount--;
				}
				if(player.getMaxHealth() > 20) // Sets max health in config and then reloads to apply.
				{
					mobfighter.getConfig().set("HealthBoost." + player.getDisplayName(), player.getMaxHealth());
					mobfighter.saveConfig();
				}
			}
			else if(player.getItemInHand().getType().equals(Material.STONE_BUTTON) && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Stat Boost"))
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
				if(mobfighter.getConfig().getList("Creative Immunity").contains(player.getDisplayName()))
					return;
				player.sendMessage(ChatColor.GRAY + "Type: /craft");
			}
		
			// Get special items for end-game armor set.
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
			
			// Opens the main shop by right-clicking the shop paper.
			if(player.getItemInHand().getType().equals(Material.PAPER))
				player.openInventory(MainShop.getShop());
		
		}// End of Right-click
		
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
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
	}
	
	// Handles selling items with left-click.
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void sellClick(PlayerInteractEvent event){
		
		final Player player = event.getPlayer();
		
		// Sell Items:
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			for(int a=0;a<sellingItems.size();a++)
				if(sellingItems.containsKey(player.getItemInHand().getType().toString()))
				{
					// If the item is a custom or renamed item.
					if(player.getItemInHand().hasItemMeta())
						return;
					
					String itemName = player.getItemInHand().getType().toString();
					double dropPrice = Double.parseDouble(sellingItems.get(itemName).toString());
					dropPrice *= player.getItemInHand().getAmount();
					player.setItemInHand(new ItemStack(Material.AIR));
					econ.depositPlayer(player.getDisplayName(),dropPrice);
					player.sendMessage(ChatColor.GREEN + "Sold " + itemName + " for: $" + dropPrice);
				}
		}
	}
	
	// Handles buying items.
	@EventHandler(priority = EventPriority.NORMAL)
	public void buyClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		
		if(player != null)
		{
			// Makes sure there is an item that was clicked.
			if(event.getCurrentItem() == null)
				return;
			
			ItemStack clicked = event.getCurrentItem();
			
			boolean checks = false;
			
			if(event.getInventory().getName() != null)
				if(!(event.getClickedInventory().getName().toString().equalsIgnoreCase("container.inventory")))
					checks = itemChecker(event.getInventory().getName(), player, clicked);
			
			// Make sure items can't be take from the shop if the item clicked checks (is part of the shop).
			if(checks)
				event.setCancelled(true);
			
		}// End of null check.
	}// End of inventory click.
	
	// Checks the item to the list in the config, gets the price, sees if the player has enough money, gives the item to the player.
	private boolean itemChecker(String shop, Player player, ItemStack clickedItem){
		
		// Checks to make sure the shop exists.
		if(mobfighter.getConfig().getConfigurationSection("Shops").isConfigurationSection(shop))	
			shopItems = mobfighter.getConfig().getConfigurationSection("Shops").getConfigurationSection(shop).getValues(false);
		else return false;
		
		double price = 0.0;
		
		// Double check that the shop has items then runs the rest.
		if(shopItems != null)
				for(String s : shopItems.keySet())
					if(clickedItem.getType().toString().equalsIgnoreCase(s))
					{
						price = Double.parseDouble(shopItems.get(s).toString());
						if(VaultEco.getEconomy().getBalance(player) < price)
						{
							player.sendMessage(ChatColor.RED + "You do not have enough money!");
							return true;
						}
						VaultEco.getEconomy().withdrawPlayer(player, price);
						ItemStack item = new ItemStack(clickedItem);
						ItemMeta itemMeta = item.getItemMeta();
						itemMeta.setLore(null);
						item.setItemMeta(itemMeta);
						player.getInventory().addItem(item);
						player.updateInventory();
					}
		
		return true;
	}
	
	// No building allowed on server.
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer();
		
		// Building allowed if the player has creative immunity.
		if(mobfighter.getConfig().getList("Creative Immunity").contains(player.getDisplayName()))
				return;
		
		event.setCancelled(true);
	}
	
	// No block breaking allowed on server.
		@EventHandler(priority = EventPriority.NORMAL)
		public void onBlockBreak(BlockBreakEvent event){
			Player player = event.getPlayer();
			
			// Block breaking allowed if the player has creative immunity.
			if(mobfighter.getConfig().getList("Creative Immunity").contains(player.getDisplayName()))
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
															if(mobfighter.getConfig().getList("Creative Immunity").contains(player.getDisplayName()))
																return;
															// Sets the player back to survival once the anvil is used.
															e.getWhoClicked().getOpenInventory().close();
															player.setGameMode(GameMode.SURVIVAL);
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
						if(mobfighter.getConfig().getList("Creative Immunity").contains(player.getDisplayName()))
							return;
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
	
	// Adds blown up blocks into blockStates.
	@EventHandler (priority = EventPriority.HIGH)
	public void onExplode(final EntityExplodeEvent e){
        for(Block b : e.blockList()){
        	final BlockState state = b.getState();
        	b.setType(Material.AIR);
        	blockStates.add(state);

			int delay = 5;
	        if((b.getType() == Material.SAND || b.getType() == Material.GRAVEL))
	        	delay++;
	        
	        fixBlockDelay = delay;
        }// End of for.
	}// End of onExplode().
	
	// Enderdragon does not spawn a portal when killed.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityCreatePortalEvent(EntityCreatePortalEvent event){
		if (event.getEntity() instanceof EnderDragon)
			event.setCancelled(true);
	}// End of portalCreate
}
