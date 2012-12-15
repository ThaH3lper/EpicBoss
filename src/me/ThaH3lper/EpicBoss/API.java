package me.ThaH3lper.EpicBoss;

import org.bukkit.entity.LivingEntity;

public class API {
	private EpicBoss plugin;
	
	public API(EpicBoss plugin) {
		this.plugin = plugin;
	}
	// is boss?
	public boolean entityBoss(LivingEntity e)
	{
		int id = e.getEntityId();
		if(plugin.getCustomConfig(plugin.file).contains("mobs"))
		{
			if(plugin.getCustomConfig(plugin.file).contains("mobs." + id))
			{
				return true;
			}
		}
		return false;
	}
	public String GetBossName(LivingEntity e)
	{
		int id = e.getEntityId();
		if(plugin.getCustomConfig(plugin.file).contains("mobs"))
		{
			if(plugin.getCustomConfig(plugin.file).contains("mobs." + id))
			{
				String s = plugin.getCustomConfig(plugin.file).getString("mobs." + id + ".Name");
				return s;
			}
		}
		return "";
	}
	public int GetMaxHealth(LivingEntity e)
	{
		int id = e.getEntityId();
		if(plugin.getCustomConfig(plugin.file).contains("mobs"))
		{
			if(plugin.getCustomConfig(plugin.file).contains("mobs." + id))
			{
				String s = plugin.getCustomConfig(plugin.file).getString("mobs." + id + ".Name");
				int health = plugin.getConfig().getInt("Bosses." + s + ".Health");
				return health;
			}
		}
		return 0;
	}
	public int GetHealth(LivingEntity e)
	{
		int id = e.getEntityId();
		if(plugin.getCustomConfig(plugin.file).contains("mobs"))
		{
			if(plugin.getCustomConfig(plugin.file).contains("mobs." + id))
			{
				int s = plugin.getCustomConfig(plugin.file).getInt("mobs." + id + ".Health");
				return s;
			}
		}
		return 0;
	}
}
