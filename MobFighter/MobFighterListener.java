package net.grasinga.MobFighter;

import java.util.ArrayList;
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
import org.bukkit.event.entity.EntityDamageEvent;
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
    
    private Map<String, Object> sellingItems;
    private Map<String, Object> shopItems;
	
	// Variables to help regenerate blocks during the day:
	private ArrayList<BlockState> blockStates = new ArrayList<BlockState>();
	private int fixBlockDelay = 0;
	
	// Constructor to pass in important variables.
	public MobFighterListener(JavaPlugin p){
		mobfighter = p;
		
		// Gets the sellable items.
		fillSellables();
	}
	
	// Gets the list of sellable items from the configuration file.
	private void fillSellables() {
		sellingItems = mobfighter.getConfig().getConfigurationSection("Selling Items").getValues(false);
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
			mobfighter.reloadConfig();
		}
	}
	
	// The magic behind the health boost. Reduces damage by 10% for each boost used. (All 5 = Half Damage)
	@EventHandler(priority = EventPriority.NORMAL)
	public void playerHurt(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			if(mobfighter.getConfig().getDouble("HealthScale." + ((Player) event.getEntity()).getDisplayName()) != 0)
				if(mobfighter.getConfig().getDouble("HealthScale." + ((Player) event.getEntity()).getDisplayName()) == 24.0)
					event.setDamage(event.getDamage() - (event.getDamage() / 6));
				else if(mobfighter.getConfig().getDouble("HealthScale." + ((Player) event.getEntity()).getDisplayName()) == 28.0)
					event.setDamage(event.getDamage() - (event.getDamage() / 5));
				else if(mobfighter.getConfig().getDouble("HealthScale." + ((Player) event.getEntity()).getDisplayName()) == 32.0)
					event.setDamage(event.getDamage() - (event.getDamage() / 4));
				else if(mobfighter.getConfig().getDouble("HealthScale." + ((Player) event.getEntity()).getDisplayName()) == 36.0)
					event.setDamage(event.getDamage() - (event.getDamage() / 3));
				else if(mobfighter.getConfig().getDouble("HealthScale." + ((Player) event.getEntity()).getDisplayName()) == 40.0)
					event.setDamage(event.getDamage() / 2);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void playerClick(PlayerInteractEvent event)
	{
		final Player player = event.getPlayer();
		
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
		
		// Health Boost, Stat Boost, and Crafting 
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			if(player.getItemInHand().getType().equals(Material.RED_MUSHROOM))
			{
				int amount = player.getItemInHand().getAmount();
				player.setItemInHand(new ItemStack(Material.AIR));
				player.setHealthScale(player.getHealthScale() + 4 * amount);
				while(amount > 0) // Refunds the money spent buying it if the player has used 5 already.
				{
					if(player.getHealthScale() > 40){
						player.setHealthScale(40);
						player.sendMessage(ChatColor.RED + "Your health is already boosted to the max!");
						VaultEco.getEconomy().depositPlayer(player, 2000);
						player.sendMessage(ChatColor.GREEN + "Refunded: $2,000.00");
					}
					else
						player.setHealthScale(player.getHealthScale() + 4);
					amount--;
				}
				if(player.getHealthScale() > 20) // Sets scale in config and then reloads to apply.
				{
					mobfighter.getConfig().set("HealthScale." + player.getDisplayName(), player.getHealthScale());
					mobfighter.saveConfig();
					mobfighter.reloadConfig();
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
		
		// Opens the main shop by right-clicking the shop paper.
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			if(player.getItemInHand().getType().equals(Material.PAPER))
				player.openInventory(MainShop.getShop());
		
	}
	
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
				if(!(event.getClickedInventory().getName().toString().equalsIgnoreCase("container.inventory"))){
					checks = itemChecker(event.getInventory().getName(), player, clicked);
				}
			
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
		else
			return false;
		
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
						ItemMeta clickedMeta = clickedItem.getItemMeta();
						clickedMeta.setLore(null);
						clickedItem.setItemMeta(clickedMeta);
						player.getInventory().addItem(clickedItem);
						player.updateInventory();
					}
		return true;
	}
	
	// No building allowed on server.
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer();
		
		// Building allowed if the player has creative immunity.
		for(int i=0;i<mobfighter.getConfig().getList("Creative Immunity").size();i++)
			if(player.getDisplayName().equalsIgnoreCase(mobfighter.getConfig().getList("Creative Immunity").get(i).toString()))
					return;
		
		event.setCancelled(true);
	}
	
	// No block breaking allowed on server.
		@EventHandler(priority = EventPriority.NORMAL)
		public void onBlockBreak(BlockBreakEvent event){
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
