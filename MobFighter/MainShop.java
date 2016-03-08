package net.grasinga.MobFighter;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

// Main shop for MobFighter
public class MainShop {
	private static Inventory shopInv = Bukkit.createInventory(null, 54, "Main Shop");
	private static PluginManager pm = Bukkit.getServer().getPluginManager();
	private static ConfigurationSection shop = pm.getPlugin("MobFighter").getConfig().getConfigurationSection("Shops").getConfigurationSection("Main Shop");
	static
	{
		ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
		ItemMeta stoneSwordMeta = stoneSword.getItemMeta();
		stoneSwordMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("STONE_SWORD")));
		stoneSword.setItemMeta(stoneSwordMeta);
		shopInv.setItem(0, stoneSword);
		
		ItemStack leatherHat = new ItemStack(Material.LEATHER_HELMET);
		ItemMeta leatherHatMeta = leatherHat.getItemMeta();
		leatherHatMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("LEATHER_HELMET")));
		leatherHat.setItemMeta(leatherHatMeta);
		shopInv.setItem(9, leatherHat);
	
		ItemStack leatherChest = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemMeta leatherChestMeta = leatherChest.getItemMeta();
		leatherChestMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("LEATHER_CHESTPLATE")));
		leatherChest.setItemMeta(leatherChestMeta);
		shopInv.setItem(18, leatherChest);

		ItemStack leatherLegs = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemMeta leatherLegsMeta = leatherLegs.getItemMeta();
		leatherLegsMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("LEATHER_LEGGINGS")));
		leatherLegs.setItemMeta(leatherLegsMeta);
		shopInv.setItem(27, leatherLegs);

		ItemStack leatherBoots = new ItemStack(Material.LEATHER_BOOTS);
		ItemMeta leatherBootsMeta = leatherBoots.getItemMeta();
		leatherBootsMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("LEATHER_BOOTS")));
		leatherBoots.setItemMeta(leatherBootsMeta);
		shopInv.setItem(36, leatherBoots);

		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta bowMeta = bow.getItemMeta();
		bowMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("BOW")));
		bow.setItemMeta(bowMeta);
		shopInv.setItem(45, bow);

		ItemStack goldSword = new ItemStack(Material.GOLD_SWORD);
		ItemMeta goldSwordMeta = goldSword.getItemMeta();
		goldSwordMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("GOLD_SWORD")));
		goldSword.setItemMeta(goldSwordMeta);
		shopInv.setItem(1, goldSword);

		ItemStack goldHat = new ItemStack(Material.GOLD_HELMET);
		ItemMeta goldHatMeta = goldHat.getItemMeta();
		goldHatMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("GOLD_HELMET")));
		goldHat.setItemMeta(goldHatMeta);
		shopInv.setItem(10, goldHat);

		ItemStack goldChest = new ItemStack(Material.GOLD_CHESTPLATE);
		ItemMeta goldChestMeta = goldChest.getItemMeta();
		goldChestMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("GOLD_CHESTPLATE")));
		goldChest.setItemMeta(goldChestMeta);
		shopInv.setItem(19, goldChest);

		ItemStack goldLegs = new ItemStack(Material.GOLD_LEGGINGS);
		ItemMeta goldLegsMeta = goldLegs.getItemMeta();
		goldLegsMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("GOLD_LEGGINGS")));
		goldLegs.setItemMeta(goldLegsMeta);
		shopInv.setItem(28, goldLegs);

		ItemStack goldBoots = new ItemStack(Material.GOLD_BOOTS);
		ItemMeta goldBootsMeta = goldBoots.getItemMeta();
		goldBootsMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("GOLD_BOOTS")));
		goldBoots.setItemMeta(goldBootsMeta);
		shopInv.setItem(37, goldBoots);

		ItemStack arrow = new ItemStack(Material.ARROW,16);
		ItemMeta arrowMeta = arrow.getItemMeta();
		arrowMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("ARROW")));
		arrow.setItemMeta(arrowMeta);
		shopInv.setItem(46, arrow);

		ItemStack ironSword = new ItemStack(Material.IRON_SWORD);
		ItemMeta ironSwordMeta = ironSword.getItemMeta();
		ironSwordMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("IRON_SWORD")));
		ironSword.setItemMeta(ironSwordMeta);
		shopInv.setItem(2, ironSword);

		ItemStack ironHat = new ItemStack(Material.IRON_HELMET);
		ItemMeta ironHatMeta = ironHat.getItemMeta();
		ironHatMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("IRON_HELMET")));
		ironHat.setItemMeta(ironHatMeta);
		shopInv.setItem(11, ironHat);

		ItemStack ironChest = new ItemStack(Material.IRON_CHESTPLATE);
		ItemMeta ironChestMeta = ironChest.getItemMeta();
		ironChestMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("IRON_CHESTPLATE")));
		ironChest.setItemMeta(ironChestMeta);
		shopInv.setItem(20, ironChest);

		ItemStack ironLegs = new ItemStack(Material.IRON_LEGGINGS);
		ItemMeta ironLegsMeta = ironLegs.getItemMeta();
		ironLegsMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("IRON_LEGGINGS")));
		ironLegs.setItemMeta(ironLegsMeta);
		shopInv.setItem(29, ironLegs);

		ItemStack ironBoots = new ItemStack(Material.IRON_BOOTS);
		ItemMeta ironBootsMeta = ironBoots.getItemMeta();
		ironBootsMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("IRON_BOOTS")));
		ironBoots.setItemMeta(ironBootsMeta);
		shopInv.setItem(38, ironBoots);

		ItemStack beef = new ItemStack(Material.COOKED_BEEF,8);
		ItemMeta beefMeta = beef.getItemMeta();
		beefMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("COOKED_BEEF")));
		beef.setItemMeta(beefMeta);
		shopInv.setItem(47, beef);

		ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta diamondSwordMeta = diamondSword.getItemMeta();
		diamondSwordMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("DIAMOND_SWORD")));
		diamondSword.setItemMeta(diamondSwordMeta);
		shopInv.setItem(3, diamondSword);

		ItemStack diamondHat = new ItemStack(Material.DIAMOND_HELMET);
		ItemMeta diamondHatMeta = diamondHat.getItemMeta();
		diamondHatMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("DIAMOND_HELMET")));
		diamondHat.setItemMeta(diamondHatMeta);
		shopInv.setItem(12, diamondHat);

		ItemStack diamondChest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemMeta diamondChestMeta = diamondChest.getItemMeta();
		diamondChestMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("DIAMOND_CHESTPLATE")));
		diamondChest.setItemMeta(diamondChestMeta);
		shopInv.setItem(21, diamondChest);

		ItemStack diamondLegs = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemMeta diamondLegsMeta = diamondLegs.getItemMeta();
		diamondLegsMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("DIAMOND_LEGGINGS")));
		diamondLegs.setItemMeta(diamondLegsMeta);
		shopInv.setItem(30, diamondLegs);

		ItemStack diamondBoots = new ItemStack(Material.DIAMOND_BOOTS);
		ItemMeta diamondBootsMeta = diamondBoots.getItemMeta();
		diamondBootsMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("DIAMOND_BOOTS")));
		diamondBoots.setItemMeta(diamondBootsMeta);
		shopInv.setItem(39, diamondBoots);

		ItemStack apple = new ItemStack(Material.APPLE,10);
		ItemMeta appleMeta = apple.getItemMeta();
		appleMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("APPLE")));
		apple.setItemMeta(appleMeta);
		shopInv.setItem(48, apple);

		ItemStack brick = new ItemStack(Material.BRICK);
		ItemMeta brickMeta = brick.getItemMeta();
		ArrayList<String> brickLore = new ArrayList<String>();
		brickLore.add(ChatColor.AQUA + "Price: $200 " + ChatColor.ITALIC + (ChatColor.WHITE + "(Allows for bypass with anvil)"));
		brickMeta.setDisplayName("Creative");
		brickMeta.setLore(brickLore);
		brick.setItemMeta(brickMeta);
		shopInv.setItem(6, brick);

		ItemStack bench = new ItemStack(Material.WORKBENCH);
		ItemMeta benchMeta = brick.getItemMeta();
		ArrayList<String> benchLore = new ArrayList<String>();
		benchLore.add(ChatColor.AQUA + "Price: $1000 " + ChatColor.ITALIC + (ChatColor.WHITE + "(Allows player to craft 1 item.)"));
		benchMeta.setDisplayName("Crafting (With item in hand, use /craft)");
		benchMeta.setLore(benchLore);
		bench.setItemMeta(benchMeta);
		shopInv.setItem(7, bench);

		ItemStack book = new ItemStack(Material.BOOK);
		ItemMeta bookMeta = book.getItemMeta();
		bookMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("BOOK")));
		book.setItemMeta(bookMeta);
		shopInv.setItem(24, book);

		ItemStack expBottle = new ItemStack(Material.EXP_BOTTLE,10);
		ItemMeta expBottleMeta = expBottle.getItemMeta();
		expBottleMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("EXP_BOTTLE")));
		expBottle.setItemMeta(expBottleMeta);
		shopInv.setItem(25, expBottle);
		
		Potion regen = new Potion(PotionType.REGEN);
		Potion swift = new Potion(PotionType.SPEED);
		Potion night = new Potion(PotionType.NIGHT_VISION);
		Potion strength = new Potion(PotionType.STRENGTH);

		ItemStack pot1 = regen.toItemStack(1);
		ItemMeta pot1Meta = pot1.getItemMeta();
		pot1Meta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("POTION")));
		pot1Meta.setDisplayName("Potions Currently Unavailable");
		pot1.setItemMeta(pot1Meta);
		shopInv.setItem(32, pot1);

		ItemStack pot2 = swift.toItemStack(1);
		ItemMeta pot2Meta = pot2.getItemMeta();
		pot2Meta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("POTION")));
		pot2Meta.setDisplayName("Potions Currently Unavailable");
		pot2.setItemMeta(pot2Meta);
		shopInv.setItem(33, pot2);

		ItemStack pot3 = night.toItemStack(1);
		ItemMeta pot3Meta = pot3.getItemMeta();
		pot3Meta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("POTION")));
		pot3Meta.setDisplayName("Potions Currently Unavailable");
		pot3.setItemMeta(pot3Meta);
		shopInv.setItem(34, pot3);

		ItemStack pot4 = strength.toItemStack(1);
		ItemMeta pot4Meta = pot4.getItemMeta();
		pot4Meta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("POTION")));
		pot4Meta.setDisplayName("Potions Currently Unavailable");
		pot4.setItemMeta(pot4Meta);
		shopInv.setItem(35, pot4);

		ItemStack gold = new ItemStack(Material.GOLD_INGOT,10);
		ItemMeta goldMeta = gold.getItemMeta();
		goldMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("GOLD_INGOT")));
		gold.setItemMeta(goldMeta);
		shopInv.setItem(51, gold);

		ItemStack iron = new ItemStack(Material.IRON_INGOT,10);
		ItemMeta ironMeta = iron.getItemMeta();
		ironMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("IRON_INGOT")));
		iron.setItemMeta(ironMeta);
		shopInv.setItem(52, iron);

		ItemStack diamond = new ItemStack(Material.DIAMOND,10);
		ItemMeta diamondMeta = diamond.getItemMeta();
		diamondMeta.setLore(Arrays.asList(ChatColor.AQUA + "Price: $" + shop.getString("DIAMOND")));
		diamond.setItemMeta(diamondMeta);
		shopInv.setItem(50, diamond);

		ItemStack lapis = new ItemStack(Material.LAPIS_BLOCK);
		ItemMeta lapisMeta = lapis.getItemMeta();
		ArrayList<String> lapisLore = new ArrayList<String>();
		lapisLore.add(ChatColor.AQUA + "Price: $50 " + ChatColor.ITALIC + (ChatColor.WHITE + "(Used for enchanting!)"));
		lapisMeta.setLore(lapisLore);
		lapis.setItemMeta(lapisMeta);
		shopInv.setItem(53, lapis);
	}
	
	// Method to pass shop name to other classes.
	public static String shopName(){return shopInv.getName();}
	
	// Method to pass the shop itself to other classes.
	public static Inventory getShop(){
		return shopInv;
	}
}

