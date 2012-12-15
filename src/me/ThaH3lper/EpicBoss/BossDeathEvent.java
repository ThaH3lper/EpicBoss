package me.ThaH3lper.EpicBoss;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BossDeathEvent extends Event {
    private String player;
    private String boss;
    private EpicBoss plugin;
    private static final HandlerList handlers = new HandlerList();
 
    public BossDeathEvent(Player p, LivingEntity l, EpicBoss plugin) {
    	this.plugin = plugin;
    	player = p.getName();
    	boss = plugin.getCustomConfig(plugin.file).getString("mobs." + plugin.lp.getEntityId() + ".Name");
        
    }
 
    public String getPlayer() {
        return player;
    }
 
    public String getBoss() {
        return boss;
    }    
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}