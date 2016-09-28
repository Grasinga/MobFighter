package net.grasinga.MobFighter;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

// Elite shop for MobFighter
class EliteShop {
    private static Inventory eliteShop = Bukkit.createInventory(null, 9, "Elite Shop");
    private static PluginManager pm = Bukkit.getServer().getPluginManager();
    static ConfigurationSection shop = pm.getPlugin("MobFighter").getConfig().getConfigurationSection("Shops").getConfigurationSection("Elite Shop");
    static
    {
        ItemStack health = new ItemStack(Material.RED_MUSHROOM);
        ItemMeta healthMeta = health.getItemMeta();
        ArrayList<String> healthLore = new ArrayList<String>();
        healthLore.add(ChatColor.WHITE + "Price: $" + shop.getString("RED_MUSHROOM"));
        healthMeta.setDisplayName(ChatColor.DARK_RED + "Health Boost");
        healthMeta.setLore(healthLore);
        health.setItemMeta(healthMeta);
        eliteShop.setItem(0, health);

        ItemStack button = new ItemStack(Material.STONE_BUTTON);
        ItemMeta buttonMeta = button.getItemMeta();
        ArrayList<String> buttonLore = new ArrayList<String>();
        buttonLore.add(ChatColor.WHITE + "Price: $" + shop.getString("STONE_BUTTON"));
        buttonMeta.setDisplayName(ChatColor.GREEN + "Stat Boost");
        buttonMeta.setLore(buttonLore);
        button.setItemMeta(buttonMeta);
        eliteShop.setItem(1, button);

        ItemStack godApple = new ItemStack(Material.GOLDEN_APPLE, 1, (short)1);
        ItemMeta appleMeta = godApple.getItemMeta();
        ArrayList<String> appleLore = new ArrayList<String>();
        appleLore.add(ChatColor.WHITE + "Price: $" + shop.getString("GOLDEN_APPLE"));
        appleMeta.setDisplayName(ChatColor.GOLD + "God Apple");
        appleMeta.setLore(appleLore);
        godApple.setItemMeta(appleMeta);
        eliteShop.setItem(2, godApple);

        ItemStack bat = new ItemStack(Material.BLAZE_ROD);
        ItemMeta batMeta = bat.getItemMeta();
        ArrayList<String> batLore = new ArrayList<String>();
        batLore.add(ChatColor.WHITE + "Price: $" + shop.getString("BLAZE_ROD"));
        batMeta.setDisplayName(ChatColor.YELLOW + "Baseball Bat");
        batMeta.setLore(batLore);
        bat.setItemMeta(batMeta);
        bat.addUnsafeEnchantment(new EnchantmentWrapper(19), 35);
        eliteShop.setItem(4, bat);

        ItemStack flower = new ItemStack(Material.RED_ROSE);
        ItemMeta flowerMeta = flower.getItemMeta();
        ArrayList<String> flowerLore = new ArrayList<String>();
        flowerLore.add(ChatColor.WHITE + "Price $" + shop.getString("RED_ROSE"));
        flowerMeta.setDisplayName(ChatColor.DARK_PURPLE + "Power Flower");
        flowerMeta.setLore(flowerLore);
        flower.setItemMeta(flowerMeta);
        flower.addUnsafeEnchantment(new EnchantmentWrapper(16), 5);
        flower.addUnsafeEnchantment(new EnchantmentWrapper(21), 5);
        flower.addUnsafeEnchantment(new EnchantmentWrapper(19), 1);
        eliteShop.setItem(5, flower);

        ItemStack star = new ItemStack(Material.NETHER_STAR);
        ItemMeta starMeta = star.getItemMeta();
        ArrayList<String> starLore = new ArrayList<String>();
        starLore.add(ChatColor.WHITE + "Price: $" + shop.getString("NETHER_STAR"));
        starMeta.setDisplayName(ChatColor.GREEN + "Swirling Souls");
        starMeta.setLore(starLore);
        star.setItemMeta(starMeta);
        eliteShop.setItem(7, star);

        ItemStack fCharge = new ItemStack(Material.FIREWORK_CHARGE);
        ItemMeta fChargeMeta = fCharge.getItemMeta();
        ArrayList<String> fChargeLore = new ArrayList<String>();
        fChargeLore.add(ChatColor.WHITE + "Price: $" + shop.getString("FIREWORK_CHARGE"));
        fChargeMeta.setDisplayName(ChatColor.DARK_PURPLE + "Festering Darkness");
        fChargeMeta.setLore(fChargeLore);
        fCharge.setItemMeta(fChargeMeta);
        eliteShop.setItem(8, fCharge);
    }

    // Method to pass the shop itself to other classes.
    static Inventory getShop(){return eliteShop;}
}

