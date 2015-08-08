package net.grasinga.MobFighter;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// Elite shop for MobFighter
public class EliteShop {
	public static Inventory eliteShop = Bukkit.createInventory(null, 9, ChatColor.AQUA + "MobFighter - Elite Shop");
	static
	{		
		// Pet Egg for $500
		@SuppressWarnings("deprecation")
		ItemStack egg = new ItemStack(383, 1, (short) 95);
		ItemMeta eggMeta = egg.getItemMeta();
		ArrayList<String> eggLore = new ArrayList<String>();
		eggLore.add(ChatColor.WHITE + "Price $500.00");
		eggMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Pet Wolf");
		eggMeta.setLore(eggLore);
		egg.setItemMeta(eggMeta);
		eliteShop.setItem(0, egg);
		
		// Stat Boost for $10,000 (this is permanent)
		ItemStack button = new ItemStack(Material.STONE_BUTTON);
		ItemMeta buttonMeta = button.getItemMeta();
		ArrayList<String> buttonLore = new ArrayList<String>();
		buttonLore.add(ChatColor.WHITE + "Price: $10,000.00");
		buttonMeta.setDisplayName("Stat Boost");
		buttonMeta.setLore(buttonLore);
		button.setItemMeta(buttonMeta);
		eliteShop.setItem(2, button);
		
		// Health Booster for $2,000
		ItemStack health = new ItemStack(Material.RED_MUSHROOM);
		ItemMeta healthMeta = health.getItemMeta();
		ArrayList<String> healthLore = new ArrayList<String>();
		healthLore.add(ChatColor.WHITE + "Price: $2,000.00");
		healthMeta.setDisplayName("Health Boost");
		healthMeta.setLore(healthLore);
		health.setItemMeta(healthMeta);
		eliteShop.setItem(3, health);
		
		// Power Flower for $1,000
		ItemStack flower = new ItemStack(Material.RED_ROSE);
		ItemMeta flowerMeta = flower.getItemMeta();
		ArrayList<String> flowerLore = new ArrayList<String>();
		flowerLore.add(ChatColor.WHITE + "Price $1,000.00");
		flowerMeta.setDisplayName(ChatColor.DARK_PURPLE + "Power Flower");
		flowerMeta.setLore(flowerLore);
		flower.setItemMeta(flowerMeta);
		flower.addUnsafeEnchantment(new EnchantmentWrapper(16), 5);
		flower.addUnsafeEnchantment(new EnchantmentWrapper(21), 5);
		flower.addUnsafeEnchantment(new EnchantmentWrapper(19), 1);
		eliteShop.setItem(5, flower);
		
		// 5x Swirling Souls for $1,000,000
		ItemStack star = new ItemStack(Material.NETHER_STAR,5);
		ItemMeta starMeta = star.getItemMeta();
		ArrayList<String> starLore = new ArrayList<String>();
		starLore.add(ChatColor.WHITE + "Price: $1,000,000");
		starMeta.setDisplayName(ChatColor.GREEN + "Swirling Souls");
		starMeta.setLore(starLore);
		star.setItemMeta(starMeta);
		eliteShop.setItem(7, star);
		
		// 5x Festering Darkness for $1,000,000
		ItemStack fCharge = new ItemStack(Material.FIREWORK_CHARGE,5);
		ItemMeta fChargeMeta = fCharge.getItemMeta();
		ArrayList<String> fChargeLore = new ArrayList<String>();
		fChargeLore.add(ChatColor.WHITE + "Price: $1,000,000");
		fChargeMeta.setDisplayName(ChatColor.DARK_PURPLE + "Festering Darkness");
		fChargeMeta.setLore(fChargeLore);
		fCharge.setItemMeta(fChargeMeta);
		eliteShop.setItem(8, fCharge);
	} 
	
	// Method to pass shop name to other classes.
	public static String shopName(){return eliteShop.getName();}
	
	// Method to pass the shop itself to other classes.
	public static Inventory getShop(){return eliteShop;}
}

