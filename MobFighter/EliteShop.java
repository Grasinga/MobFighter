package net.grasinga.MobFighter;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// Main shop for MobFighter
public class EliteShop {
	public static Inventory eliteShop = Bukkit.createInventory(null, 9, ChatColor.AQUA + "MobFighter - Elite Shop");
	static
	{
		// Health Booster for $2,000
		ItemStack health = new ItemStack(Material.RED_MUSHROOM);
		ItemMeta healthMeta = health.getItemMeta();
		ArrayList<String> healthLore = new ArrayList<String>();
		healthLore.add(ChatColor.WHITE + "Price: $2,000.00");
		healthMeta.setDisplayName("Health Boost");
		healthMeta.setLore(healthLore);
		health.setItemMeta(healthMeta);
		eliteShop.setItem(0, health);
		
		// Stat Boost for $10,000 (this is permanent)
		ItemStack button = new ItemStack(Material.STONE_BUTTON);
		ItemMeta buttonMeta = button.getItemMeta();
		ArrayList<String> buttonLore = new ArrayList<String>();
		buttonLore.add(ChatColor.WHITE + "Price: $10,000.00");
		buttonMeta.setDisplayName("Stat Boost");
		buttonMeta.setLore(buttonLore);
		button.setItemMeta(buttonMeta);
		eliteShop.setItem(1, button);
	} 
	
	// Method to pass shop name to other classes.
	public static String shopName(){return eliteShop.getName();}
	
	// Method to pass the shop itself to other classes.
	public static Inventory getShop(){return eliteShop;}
}

