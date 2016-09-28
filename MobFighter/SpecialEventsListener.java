package net.grasinga.MobFighter;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

class SpecialEventsListener implements Listener{

    // Important variables to be used in other classes.
    static ItemStack taintedSoul = new ItemStack(Material.EMERALD);
    static ItemStack undeadHeart = new ItemStack(Material.COAL);
    private MobFighter mobfighter;

    SpecialEventsListener(MobFighter mf){
        mobfighter = mf;
        ItemMeta meta = taintedSoul.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Tainted Soul");
        taintedSoul.setItemMeta(meta);
        ItemMeta meta2 = undeadHeart.getItemMeta();
        meta2.setDisplayName(ChatColor.LIGHT_PURPLE + "Undead Heart");
        undeadHeart.setItemMeta(meta2);
    }// End of constructor

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void playerClick(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
            if(player.getItemInHand().getType().equals(Material.TNT)){

                // Ignore if player is in Creative Immunity.
                for(int i=0;i<mobfighter.getConfig().getList("Creative Immunity").size();i++)
                    if(player.getDisplayName().equalsIgnoreCase(mobfighter.getConfig().getList("Creative Immunity").get(i).toString()))
                        return;

                // Takes one TNT from player.
                int tntAmount = player.getItemInHand().getAmount();
                int index = player.getInventory().getHeldItemSlot();
                ItemStack updated = new ItemStack(Material.TNT,(tntAmount-1));
                player.getInventory().setItem(index, updated);
                player.updateInventory();
            }
    }// End of playerClick()

    // Handles lighting TNT
    @EventHandler(priority = EventPriority.HIGH)
    public void onTNTPlace(BlockPlaceEvent event){
        Block block = event.getBlock();
        if(block.getType().equals(Material.TNT)){
            Location loc = block.getLocation();
            block.setType(Material.AIR);
            Entity tnt = block.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
            ((TNTPrimed)tnt).setFuseTicks(30);
        }
    }

    // Handles events that deal with picking up items. (Lightning, Explosive, and Field of Flowers)
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPickUp(PlayerPickupItemEvent event){
        final Player player = event.getPlayer();
        World world = player.getWorld();

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
            int x = (player.getLocation().getBlockX() + ((int)(Math.random()*20)));
            int y = player.getLocation().getBlockY();
            int z = (player.getLocation().getBlockZ() + ((int)(Math.random()*20)));
            Location strike = new Location(world, x, y, z);
            Entity tnt = world.spawnEntity(strike, EntityType.PRIMED_TNT);
            ((TNTPrimed)tnt).setFuseTicks(0);
        }
    }

    // Handles passing on the potato to another player in the hot potato special event.
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void hotPotatoTransfer(PlayerInteractEntityEvent event)
    {
        if(SpecialEvents.potato)
        {
            final Player player = event.getPlayer();
            final Player targetPlayer = (Player) event.getRightClicked();
            if(targetPlayer != null)
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
        if(SpecialEvents.potato){
            ItemStack potato = new ItemStack(Material.POISONOUS_POTATO);
            if(event.getItemDrop().getItemStack().equals(potato)){
                final Player player = event.getPlayer();
                Location loc = player.getLocation();
                player.getWorld().strikeLightning(loc);
                event.setCancelled(true);
                player.updateInventory();
                Bukkit.broadcastMessage(player.getDisplayName() + " you shall not throw the potato.");
                player.setHealth(0);
            }// End of check if potato.
        }// End of potato event check.
    }// End of playerItemDrop().

    // Deals with monster spawns for special events. (Corrupted Wolf Pack)
    @EventHandler(priority = EventPriority.NORMAL)
    public void spawningMobs(CreatureSpawnEvent event)
    {
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
                    z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
            }
    }


    // Handles special event deaths.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent event){

        if(SpecialEvents.explosive){
            if(event.getEntity().getLastDamageCause().getCause().toString().equalsIgnoreCase("ENTITY_EXPLOSION")){
                event.getDrops().clear();
                ItemStack tnt = new ItemStack(Material.TNT);
                event.getDrops().add(tnt);
            }
        }

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
            bm.setPages(Collections.singletonList("The Lords and Other powerful Dwellers of the Underworld are up to giving you more power if you can collect enough Undead Hearts."));
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
                        if(entities.get(i).getType().equals(EntityType.PLAYER))
                            other.setTarget((LivingEntity) entities.get(i));
                        break;
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
}
