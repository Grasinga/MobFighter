package net.grasinga.MobFighter;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class SpecialEvents{
	
	// Variable to get the world the plugin is running on.
	private World world;
	
	// Variable for PluginManager.
	PluginManager pluginmanager = null;
	
	// Event variables:
	public static boolean flowers = false;
	public static boolean lightning = false;
	public static boolean explosive = false;
	public static boolean potato = false;
	public static boolean giants = false;
	public static int giantsKilled = 0;
	public static boolean babyZombies = false;
	public static boolean wolves = false;
	
	// Constructor to get important variables.
	public SpecialEvents(MobFighter mobfighter, PluginManager pm, World w){
		
		// Allows the use of Special Events
		Listener specialListener = new SpecialEventsListener(mobfighter);
		pm.registerEvents(specialListener, mobfighter);
		
		world = w;
	}
	
	// Public class to be used by outside classes. Sets the event.
	public void setEvent(String event){startEvent(event);}
	
	// Starts the specified event.
	private void startEvent(String event){
		if(event.equalsIgnoreCase("Enderdragon"))
			eventEnderdragon();
		else if(event.equalsIgnoreCase("Wither"))
			eventWither();
		else if(event.equalsIgnoreCase("Field of Flowers"))
			eventFlowers();
		else if(event.equalsIgnoreCase("Lightning Storm"))
			eventLightningDrops();
		else if(event.equalsIgnoreCase("Explosive Drops"))
			eventExplosiveDrops();
		else if(event.equalsIgnoreCase("Hot Potato"))
			eventHotPotato();
		else if(event.equalsIgnoreCase("PvP On"))
			eventPVP();
		else if(event.equalsIgnoreCase("Giants"))
			eventGiants();
		else if(event.equalsIgnoreCase("Baby Zombie Swarm"))
			eventBabyZombies();
		else if(event.equalsIgnoreCase("Corrupted Wolf Pack"))
			eventCorruptedWolves();
		else // All other cases
			log("No event found!");
	}
	
	// Enderdragon event:
	private void eventEnderdragon(){
		Location spawnLoc = new Location(world,world.getSpawnLocation().getX(),(world.getSpawnLocation().getY() + 35),world.getSpawnLocation().getZ());
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + (ChatColor.BOLD + "The Ender Dragon has Appeared!"));
		world.spawnEntity(spawnLoc, EntityType.ENDER_DRAGON);
	}
	
	// Wither event:
	private void eventWither(){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"region flag __global__" + " -w " + world.getName() + " deny-spawn Creeper, Witch, Skeleton, Spider, Zombie, Slime, Enderman, Pig, Chicken, Sheep, Cow, Horse, Rabbit");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"butcher");
		Location spawnLoc = new Location(world,(world.getSpawnLocation().getX() - 20),(world.getSpawnLocation().getY() + 10),world.getSpawnLocation().getZ());
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + (ChatColor.BOLD + "The Wither has Appeared!"));
		world.spawnEntity(spawnLoc, EntityType.WITHER);
	}
	
	// Field of Flowers event:
	private void eventFlowers(){
		flowers = true;
	}
	
	// Lightning Drops event:
	private void eventLightningDrops(){
		Bukkit.broadcastMessage(ChatColor.YELLOW + "A Storm is brewing!");
		world.setStorm(true);
		lightning = true;
	}
	
	// Explosive Drops event:
	private void eventExplosiveDrops(){
		Bukkit.broadcastMessage(ChatColor.GOLD + "There is a \"Primed Feeling\" in the air...");
		explosive = true;
	}
	
	// Hot Potato event:
	private void eventHotPotato(){
		Bukkit.broadcastMessage(ChatColor.DARK_RED + "The God of Death has handed down his potato!");
		int size = world.getPlayers().size();
		int random = (int)(Math.random()*size);
		world.getPlayers().get(random).getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
		potato = true;
	}
	
	// PvP at night event:
	private void eventPVP(){
		Bukkit.broadcastMessage(ChatColor.DARK_RED + "PvP Enabled!");
		world.setPVP(true);
	}
	
	// Giant event:
	private void eventGiants(){
		giants = true;
		Bukkit.broadcastMessage(ChatColor.DARK_RED + "The four Giants have risen!");
		int x = 15;
		int z = 15;
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"region flag __global__" + " -w " + world.getName() + " deny-spawn Creeper, Witch, Skeleton, Spider, Slime, Enderman, Pig, Chicken, Sheep, Cow, Horse, Rabbit");
		// First
		Location loc1 = new Location(world,(world.getSpawnLocation().getX()-x),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()-z));
		world.spawnEntity(loc1, EntityType.GIANT);
		// Second
		Location loc2 = new Location(world,(world.getSpawnLocation().getX()+x),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()-z));
		world.spawnEntity(loc2, EntityType.GIANT);
		// Third
		Location loc3 = new Location(world,(world.getSpawnLocation().getX()+x),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()+z));
		world.spawnEntity(loc3, EntityType.GIANT);
		// Fourth
		Location loc4 = new Location(world,(world.getSpawnLocation().getX()-x),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()+z));
		world.spawnEntity(loc4, EntityType.GIANT);
	}
	
	// Baby Zombie Swarm event:
	private void eventBabyZombies(){
		
		babyZombies = true;
		
		Bukkit.broadcastMessage(ChatColor.DARK_RED + "The Undead Horde has come!");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"region flag __global__" + " -w " + world.getName() + " deny-spawn Creeper, Witch, Skeleton, Spider, Slime, Enderman, Giant, Pig, Chicken, Sheep, Cow, Horse, Rabbit");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"butcher");
		Location a = new Location(world,(world.getSpawnLocation().getBlockX()-20),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()-20));
		Location b = new Location(world,(world.getSpawnLocation().getBlockX()+20),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()-15));
		int xMin = Math.min(a.getBlockX(),b.getBlockX());
		int yMin = Math.min(a.getBlockY(),b.getBlockY());
		int zMin = Math.min(a.getBlockZ(),b.getBlockZ());
		int xMax = Math.max(a.getBlockX(),b.getBlockX());
		int yMax = Math.max(a.getBlockY(),b.getBlockY());
		int zMax = Math.max(a.getBlockZ(),b.getBlockZ());
		for (int x = xMin; x<=xMax; x++)
			for (int y = yMin; y<=yMax; y++)
				for (int z = zMin; z<=zMax; z++)
				{
					Location spawnLoc = new Location(world,world.getBlockAt(x,y,z).getX(),world.getBlockAt(x,y,z).getY(),world.getBlockAt(x,y,z).getZ());
					Zombie zombie = (Zombie) world.spawnEntity(spawnLoc, EntityType.ZOMBIE);
					zombie.setBaby(true);
				}
	}
	
	// Corrupt Wolf Pack event:
	private void eventCorruptedWolves(){
		
		wolves = true;
		
		Bukkit.broadcastMessage(ChatColor.WHITE + "The Corrupted Wolves have come!");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"region flag __global__" + " -w " + world.getName() + " deny-spawn Creeper, Witch, Skeleton, Zombie, Spider, Slime, Enderman, Giant, Pig, Chicken, Sheep, Cow, Horse, Rabbit");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"butcher");
		Location a = new Location(world,(world.getSpawnLocation().getBlockX()-20),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()-20));
		Location b = new Location(world,(world.getSpawnLocation().getBlockX()+20),(world.getSpawnLocation().getY()),(world.getSpawnLocation().getZ()-10));
		int xMin = Math.min(a.getBlockX(),b.getBlockX());
		int yMin = Math.min(a.getBlockY(),b.getBlockY());
		int zMin = Math.min(a.getBlockZ(),b.getBlockZ());
		int xMax = Math.max(a.getBlockX(),b.getBlockX());
		int yMax = Math.max(a.getBlockY(),b.getBlockY());
		int zMax = Math.max(a.getBlockZ(),b.getBlockZ());
		for (int x = xMin; x<=xMax; x++)
			for (int y = yMin; y<=yMax; y++)
				for (int z = zMin; z<=zMax; z++)
				{
					Location spawnLoc = new Location(world,world.getBlockAt(x,y,z).getX(),world.getBlockAt(x,y,z).getY(),world.getBlockAt(x,y,z).getZ());
					Wolf w = (Wolf) world.spawnEntity(spawnLoc, EntityType.WOLF);
					w.setAngry(true);
					List<Entity> entities = w.getNearbyEntities(10, 10, 10);
					for(Entity e : entities)
						if(e.getType().equals(EntityType.PLAYER))
							w.setTarget((LivingEntity) e);
				}
	}
	
	// Simple log method for short handing.
	private void log(String s){System.out.println(s);}
}
