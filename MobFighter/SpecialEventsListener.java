package net.grasinga.MobFighter;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpecialEventsListener implements Listener{
	
	private MobFighter mobfighter;
	
	private boolean initialFlowers = true;
	
	// Important variables to be used in other classes.
	public static ItemStack taintedSoul = new ItemStack(Material.EMERALD);
	public static ItemStack undeadHeart = new ItemStack(Material.COAL);
	
	public SpecialEventsListener(MobFighter p){
		mobfighter = p;
		
		ItemMeta meta = taintedSoul.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GREEN + "Tainted Soul");
		taintedSoul.setItemMeta(meta);
		ItemMeta meta2 = undeadHeart.getItemMeta();
		meta2.setDisplayName(ChatColor.LIGHT_PURPLE + "Undead Heart");
		undeadHeart.setItemMeta(meta2);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void playerClick(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(SpecialEvents.flowers)
			if(event.getAction() == Action.LEFT_CLICK_BLOCK)
				if(player.getItemInHand().getType().equals(Material.DIAMOND_SPADE))
				{
					player.getInventory().remove(Material.DIAMOND_SPADE);
					Block block = event.getClickedBlock();
					if(block.getType().equals(Material.RED_ROSE))
						block.breakNaturally();
					ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
					BookMeta bm = (BookMeta) book.getItemMeta();
					bm.setPages(Arrays.asList("The Lords and other Dwellers of the Overworld have recognized you. They will be willing to give you more power if you can gather enough Tainted Souls."));
					bm.setAuthor("Mob Fighter");
					bm.setTitle("Sage Path");
					book.setItemMeta(bm);
					player.getInventory().addItem(book);
					player.updateInventory();
				}
	}
	
	// Handles events that deal with picking up items. (Lightning, Explosive, and Field of Flowers)
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickUp(PlayerPickupItemEvent event){
		final Player player = event.getPlayer();
		World world = player.getWorld();
		
		// Special Flower:
		if(SpecialEvents.flowers){
			ItemStack theFlower = new ItemStack(Material.RED_ROSE);
			if(event.getItem().getItemStack().getData().equals(theFlower.getData()))
			{
				ItemStack flower = event.getItem().getItemStack();
				ItemMeta itemMeta = flower.getItemMeta();
				itemMeta.setDisplayName("Power Flower");
				flower.setItemMeta(itemMeta);
				flower.addUnsafeEnchantment(new EnchantmentWrapper(16), 5);
				flower.addUnsafeEnchantment(new EnchantmentWrapper(21), 5);
				flower.addUnsafeEnchantment(new EnchantmentWrapper(19), 1);
			}
		}
		
		// For lightning drops event.
		if(SpecialEvents.lightning)
		{
			int x = (player.getLocation().getBlockX() + ((int)(Math.random()*11)));
			int y = player.getLocation().getBlockY();
			int z = (player.getLocation().getBlockZ() + ((int)(Math.random()*11)));
			Location strike = new Location(world, x, y, z);
			world.strikeLightning(strike);
		}
		
		// For explosive drops event.
		if(SpecialEvents.explosive)
		{
			int x = (player.getLocation().getBlockX() + ((int)(Math.random()*11)));
			int y = player.getLocation().getBlockY();
			int z = (player.getLocation().getBlockZ() + ((int)(Math.random()*11)));
			Location strike = new Location(world, x, y, z);
			Entity tnt = world.spawnEntity(strike, EntityType.PRIMED_TNT);
			((TNTPrimed)tnt).setFuseTicks(0);
		}
	}
	
	// Handles passing on the potato to another player in the hot potato special event.
	@EventHandler(priority = EventPriority.NORMAL)
	public void hotPotatoTransfer(PlayerInteractEntityEvent event)
	{
		if(SpecialEvents.potato)
		{
			final Player player = event.getPlayer();
			final Player targetPlayer = (Player) event.getRightClicked();
			if(targetPlayer instanceof Player)
				if(player.getItemInHand().getType().equals(Material.POISONOUS_POTATO))
				{
					ItemStack item = player.getItemInHand();
					targetPlayer.getInventory().addItem(item);
					player.getInventory().remove(item);
				}
		}
	}
	
	// Handles a player trying to throw the potato in the hot potato special even.
	@EventHandler(priority = EventPriority.NORMAL)
	public void playerItemDrop(PlayerDropItemEvent event)
	{
		ItemStack potato = new ItemStack(Material.POISONOUS_POTATO);
		if(event.getItemDrop().getItemStack().equals(potato))
		{
			final Player player = event.getPlayer();
			Location loc = player.getLocation();
			player.getWorld().strikeLightning(loc);
			event.setCancelled(true);
			Bukkit.broadcastMessage(player.getDisplayName() + " you shall not throw the potato.");
			player.setHealth(0);
		}
	}
	
	// Deals with monster spawns for special events. (Field of Flowers, and Corrupted Wolf Pack)
	@EventHandler(priority = EventPriority.NORMAL)
	public void spawningMobs(CreatureSpawnEvent event)
	{
		// Prevents creepers from spawning on the Field of Flowers special event.
		if(event.getEntity() instanceof Creeper)
			if(SpecialEvents.flowers)
				event.setCancelled(true);
		
		// Makes wolves target the nearest player on the Corrupt Wolf Pack special event.
		if(event.getEntity() instanceof Wolf)
		{
			Wolf w = (Wolf)event.getEntity();
			if(SpecialEvents.wolves)
			{
				w.setAngry(true);
				List<Entity> entities = w.getNearbyEntities(10, 10, 10);
				for(int i=0;i<w.getNearbyEntities(10,10,10).size();i++)
				{
					if(entities.get(i).getType().equals(EntityType.WOLF))
					{
						Wolf other = (Wolf) entities.get(i);
						for(Entity e : entities)
							if(e.getType().equals(EntityType.PLAYER))
								other.setTarget((LivingEntity) e);
					}
				}
			}
		}
		
		// Gives zombies a boost in speed on the Baby Zombie Swarm special event.
		if(SpecialEvents.babyZombies)
			if(event.getEntity() instanceof Zombie)
			{
				Zombie z = (Zombie)event.getEntity();
				if(!(z.isBaby()))
					((LivingEntity)z).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
			}
		
		if(SpecialEvents.giants){
			if(event.getEntity() instanceof Giant)
			{
				Giant g = (Giant)event.getEntity();
				((LivingEntity)g).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE,1));
			}
		}
	}
	
	// Makes the Giants teleport when damaged.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent event){
		
		if(SpecialEvents.giants){			
			if(event.getEntity() instanceof Giant){
				
				// Do damage to attacker.
				if(event.getDamager() instanceof Arrow)
					((LivingEntity)((Arrow) event.getDamager()).getShooter()).damage(2);
				else
					((LivingEntity) event.getDamager()).damage(2);
				
				// Teleport a little ways away.
				World world = event.getEntity().getWorld();				
				int x = (event.getEntity().getLocation().getBlockX() + ((int)(Math.random()*10)));
				int y = event.getEntity().getLocation().getBlockY();
				int z = (event.getEntity().getLocation().getBlockZ() + ((int)(Math.random()*10)));
				Location tp = new Location(world, x, y, z);
				
				// Make it look like the Giant launch towards its teleport.
				doEffect(event.getEntity().getWorld(), event.getEntity().getLocation(), Effect.EXPLOSION_LARGE);
				event.getEntity().teleport(tp);
			}
			else
				return;
		}
		
	}
	
	
	// Handles special event deaths.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(EntityDeathEvent event){
		
		World world = event.getEntity().getWorld();
		
		// For enderdragon event.
		if(event.getEntity() instanceof EnderDragon)
		{
			Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + (ChatColor.BOLD + "The Ender Dragon has been defeated!"));
			ItemStack dEgg = new ItemStack(Material.DRAGON_EGG);
			event.getDrops().clear();
			event.getDrops().add(dEgg);
		}
		
		// For wither event.
		if(event.getEntity() instanceof Wither)
		{
			Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + (ChatColor.BOLD + "The Wither has been defeated!"));
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 1);
			ItemStack item2 = new ItemStack(Material.SKULL_ITEM, 1, (byte) 1);
			ItemStack item3 = new ItemStack(Material.SKULL_ITEM, 1, (byte) 1);
			ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bm = (BookMeta) book.getItemMeta();
			bm.setPages(Arrays.asList("The Lords and Other powerful Dwellers of the Underworld are up to giving you more power if you can collect enough Undead Hearts."));
			bm.setAuthor("Mob Fighter");
			bm.setTitle("Demon Path");
			book.setItemMeta(bm);
			event.getDrops().clear();
			event.getDrops().add(item);
			event.getDrops().add(item2);
			event.getDrops().add(item3);
			event.getDrops().add(book);
			event.setDroppedExp(200000);
		}	
		
		// For giant event.
		if(event.getEntity() instanceof Giant)
		{
			ItemStack item = new ItemStack(Material.DIAMOND_BLOCK);
			event.getDrops().add(item);
			event.setDroppedExp(10000);
			SpecialEvents.giantsKilled++;
			
			Bukkit.broadcastMessage(ChatColor.RED + "Giants left this night: " + (4 - SpecialEvents.giantsKilled));
			if(SpecialEvents.giantsKilled % 4 == 0){
				Bukkit.broadcastMessage(ChatColor.GREEN + "Total Giants defeated: " + SpecialEvents.giantsKilled);
				Bukkit.broadcastMessage(ChatColor.GOLD + "Now that all of the Giants have been defeated, it will become day in 10 seconds!");
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mobfighter, new Runnable() 
				{
					public void run() 
					{
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"time "+ world.getName() +" day");
					}
				}, 20*10);	
			}
		}
		
		// For Zombie Swarm event.
		if(event.getEntity() instanceof Zombie)
		{
			Zombie z = (Zombie)event.getEntity();
			if(SpecialEvents.babyZombies)
			{
				if(z.isBaby())
				{
					event.getDrops().clear();
					ItemStack coal1 = new ItemStack(Material.COAL);
					ItemStack coal2 = new ItemStack(Material.COAL);
					ItemMeta meta = coal1.getItemMeta();
					meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Undead Heart");
					coal1.setItemMeta(meta);
					coal2.setItemMeta(meta);
					event.getDrops().add(coal1);
					event.getDrops().add(coal2);
				}
			}
		}
		
		// For Wolf Pack event.
		if(event.getEntity() instanceof Wolf)
		{
			Wolf w = (Wolf)event.getEntity();
			if(SpecialEvents.wolves)
			{
				w.setAngry(true);
				List<Entity> entities = w.getNearbyEntities(10, 10, 10);
				for(int i=0;i<w.getNearbyEntities(10,10,10).size();i++)
				{
					if(entities.get(i).getType().equals(EntityType.WOLF))
					{
						Wolf other = (Wolf) entities.get(i);
						for(Entity e : entities)
						{
							if(e.getType().equals(EntityType.PLAYER))
								other.setTarget((LivingEntity) e);
							break;
						}
					}
				}
				ItemStack emerald = new ItemStack(Material.EMERALD);
				ItemMeta meta = emerald.getItemMeta();
				meta.setDisplayName(ChatColor.DARK_GREEN + "Tainted Soul");
				emerald.setItemMeta(meta);
				event.getDrops().clear();
				event.getDrops().add(emerald);
			}
		}		
	}
	
	// Used to load/unload the flowers on the world.
		@SuppressWarnings("deprecation")
		@EventHandler(priority = EventPriority.HIGHEST)
		public void chunkLoad(ChunkLoadEvent event){
			
			World world = event.getWorld();
			
			// Flowers only spawn in once.
			
				if(SpecialEvents.flowers){
					if(initialFlowers){
						Bukkit.broadcastMessage(ChatColor.GREEN + "A field of flowers appears!");
						
						// Sets the positions to spawn in flowers.
						Location a = new Location(world,(world.getSpawnLocation().getBlockX()-150),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()-150));
						Location b = new Location(world,(world.getSpawnLocation().getBlockX()+150),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()+150));
						int xMin = Math.min(a.getBlockX(),b.getBlockX());
						int yMin = Math.min(a.getBlockY(),b.getBlockY());
						int zMin = Math.min(a.getBlockZ(),b.getBlockZ());
						int xMax = Math.max(a.getBlockX(),b.getBlockX());
						int yMax = Math.max(a.getBlockY(),b.getBlockY());
						int zMax = Math.max(a.getBlockZ(),b.getBlockZ());
						
						// Gets the spot to set the special flower.
						int r = (int)(Math.random()*90000+1);
						int sFlower = 0;
						
						for (int x = xMin; x<=xMax; x++)
						{
							for (int y = yMin; y<=yMax; y++)
							{
								for (int z = zMin; z<=zMax; z++)
							    {
									// Places a flower within the positions given. (Only if that block is air.)
									Location blockLoc = new Location(world,(world.getBlockAt(x,y,z).getX()), (world.getBlockAt(x,y,z).getY()), (world.getBlockAt(x,y,z).getZ()));
									if(blockLoc.getBlock().getType().equals(Material.AIR))
										blockLoc.getBlock().setTypeIdAndData(38, (byte) 0x4, false);
									sFlower++;
									
									// Checks to see if the spot the flower was just placed in needs to be replaced with a rose.
									if(sFlower==r)
									{
										if(blockLoc.getBlock().getType().equals(Material.RED_ROSE))
											blockLoc.getBlock().setTypeIdAndData(38, (byte) 0x0, false);
									}
							    }
							}
						}
						
						// Sets the positions to remove flowers near spawn.
						Location c = new Location(world,(world.getSpawnLocation().getBlockX()-100),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()-100));
						Location d = new Location(world,(world.getSpawnLocation().getBlockX()+100),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()+100));
						int xMin2 = Math.min(c.getBlockX(),d.getBlockX());
						int yMin2 = Math.min(c.getBlockY(),d.getBlockY());
						int zMin2 = Math.min(c.getBlockZ(),d.getBlockZ());
						int xMax2 = Math.max(c.getBlockX(),d.getBlockX());
						int yMax2 = Math.max(c.getBlockY(),d.getBlockY());
						int zMax2 = Math.max(c.getBlockZ(),d.getBlockZ());
						for (int x = xMin2; x<=xMax2; x++)
						{
							for (int y = yMin2; y<=yMax2; y++)
							{
								for (int z = zMin2; z<=zMax2; z++)
							    {
									// Removes flowers around spawn using the above positions.
									Location blockLoc = new Location(world,(world.getBlockAt(x,y,z).getX()), (world.getBlockAt(x,y,z).getY()), (world.getBlockAt(x,y,z).getZ()));
									if(blockLoc.getBlock().getType().equals(Material.RED_ROSE))
										blockLoc.getBlock().setType(Material.AIR);
							    }
							}
						}
						
						// Gets rid of any flowers that dropped when they were being spawned in. (So there isn't lag from the drops.)
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"stoplag");
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"stoplag -c");
						
						initialFlowers = false;
						
					}// End of initialFlowers.
					
					// Ends event.
					if(!MobFighter.isNight)
					{
						Location a = new Location(world,(world.getSpawnLocation().getBlockX()-150),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()-150));
						Location b = new Location(world,(world.getSpawnLocation().getBlockX()+150),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()+150));
						int xMin = Math.min(a.getBlockX(),b.getBlockX());
						int yMin = Math.min(a.getBlockY(),b.getBlockY());
						int zMin = Math.min(a.getBlockZ(),b.getBlockZ());
						int xMax = Math.max(a.getBlockX(),b.getBlockX());
						int yMax = Math.max(a.getBlockY(),b.getBlockY());
						int zMax = Math.max(a.getBlockZ(),b.getBlockZ());
						for (int x = xMin; x<=xMax; x++)
						{
							for (int y = yMin; y<=yMax; y++)
							{
								for (int z = zMin; z<=zMax; z++)
							    {
									Location blockLoc = new Location(world,(world.getBlockAt(x,y,z).getX()), (world.getBlockAt(x,y,z).getY()), (world.getBlockAt(x,y,z).getZ()));
									if(blockLoc.getBlock().getType().equals(Material.RED_ROSE))
										blockLoc.getBlock().setType(Material.AIR);
							    }
							}
						}
						Bukkit.broadcastMessage(ChatColor.GREEN + "The flowers seem to have disappeared!");
						
						initialFlowers = true;
						
						SpecialEvents.flowers = false;
						
					}// End of night.
					
				}// End of flowers.			
				
		}// End of Chunk Load event.
		
		// Used for playing effects.
		private void doEffect(World w, Location l, Effect e){
			l.setY(l.getY() + 2);
			w.playEffect(l, e, 1000);
		}
}
