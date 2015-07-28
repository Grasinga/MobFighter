package net.grasinga.MobFighter;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEco {
	
	private static Economy econ = null;
    private static boolean vaultLoaded = false;
    
    public static Economy getEconomy(){
        if(!vaultLoaded){
            vaultLoaded = true;
            Server theServer = Bukkit.getServer();
            if (theServer.getPluginManager().getPlugin("Vault") != null){
                RegisteredServiceProvider<Economy> rsp = theServer.getServicesManager().getRegistration(Economy.class);
                if(rsp!=null){
                   econ = rsp.getProvider();
                }
            }
        }
               
        return econ;
    }
}