package me.ThaH3lper.EpicBoss;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.herocraftonline.heroes.Heroes;
import com.modcrafting.diablodrops.DiabloDrops;
import com.modcrafting.diablodrops.drops.DropsAPI;

public class SpawnBoss implements Listener{
	private EpicBoss plugin;
	public String[] items;
	public List<String> ItemList;
	public String rawString; 
	public Player dmger;
	
	public ArrayList<String> prot = new ArrayList<String>();
	public ArrayList<Integer> lvl = new ArrayList<Integer>();
	
	public SpawnBoss(EpicBoss plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void getBoss(CreatureSpawnEvent event)
	{
		if(plugin.getboss == true)
		{
			plugin.getboss = false;
			plugin.lp = event.getEntity();
			plugin.lp.setHealth(plugin.lp.getMaxHealth() - 1);
			if(plugin.getTimer == true)
			{
				for(String k : plugin.getCustomConfig(plugin.file).getConfigurationSection("Timers").getKeys(false))
				{
					if( plugin.getCustomConfig(plugin.file).getString("Timers." + k + ".id").equals(plugin.getTimers))
					{
						plugin.getCustomConfig(plugin.file).set("Timers." + k + ".id", plugin.lp.getEntityId());
						plugin.getTimer = false;
					}
				}
			}
			String s = "mobs." + plugin.lp.getEntityId();
			
			plugin.getCustomConfig(plugin.file).set(s + ".Health", plugin.getConfig().get("Bosses." + plugin.bName + ".Health"));
			plugin.getCustomConfig(plugin.file).set(s + ".Name", plugin.bName);
			plugin.saveCustomConfig(plugin.file);
			
			String name = plugin.getCustomConfig(plugin.file).getString("mobs." + plugin.lp.getEntityId() + ".Name");
			GetStuff("Bosses." + name + ".Drops", plugin.lp);
			
			if(plugin.getHeroes())
			{
				Heroes heroes = (Heroes) Bukkit.getPluginManager().getPlugin("Heroes");
				
				String sk = plugin.getCustomConfig(plugin.file).getString("mobs." + plugin.lp.getEntityId() + ".Name");
				Integer dmg = plugin.getConfig().getInt("Bosses." + sk + ".Damage");
				
				heroes.getCharacterManager().getMonster(plugin.lp).setDamage(dmg);
				heroes.getCharacterManager().getMonster(plugin.lp).setMaxHealth(plugin.getConfig().getInt("Bosses." + sk + ".Health"));
			}
			plugin.Skills(plugin.lp);
			for (String str : plugin.skills)
			{
				if(str.equals("speed_1"))
					plugin.lp.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000, 1));
				if(str.equals("speed_2"))
					plugin.lp.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000, 2));
				if(str.equals("jump"))
					plugin.lp.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, 2));
				if(str.equals("slow"))
					plugin.lp.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000, 2));
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGH)
	  public void Heath(EntityDamageByEntityEvent event)
	{
		Entity damager = event.getDamager();
		Entity hitEntity = event.getEntity();
		Entity damagerMob = event.getDamager();
		if(damager instanceof Arrow)
		{
			Arrow a = (Arrow) event.getDamager();
			damager = a.getShooter();
			if(damager == null && plugin.getCustomConfig(plugin.file).contains("mobs." + hitEntity.getEntityId()))
			{
				event.setCancelled(true);
				return;
			}
			else if(damager == null)
			{
				return;
			}
		}
		if(damager instanceof Fireball)
		{
			Fireball a = (Fireball) event.getDamager();
			damager = a.getShooter();
		}
		if(damager instanceof SmallFireball)
		{
			SmallFireball a = (SmallFireball) event.getDamager();
			damager = a.getShooter();
		}
		if(plugin.getCustomConfig(plugin.file).contains("mobs." + damager.getEntityId()) && hitEntity instanceof HumanEntity)
		{
			String s = plugin.getCustomConfig(plugin.file).getString("mobs." + damager.getEntityId() + ".Name");
			Integer dmg = plugin.getConfig().getInt("Bosses." + s + ".Damage");
			event.setDamage(dmg);
		}
		if(plugin.getCustomConfig(plugin.file).contains("mobs." + hitEntity.getEntityId()))
		{
			int hitamount = event.getDamage();
			if(damager instanceof HumanEntity || damager instanceof Wolf)
			{
			if(damager instanceof HumanEntity && hitEntity instanceof LivingEntity || damager instanceof Wolf)
			{
				if(damager instanceof HumanEntity)
				{
					dmger = (Player) damager;
				}
				plugin.lp = (LivingEntity) hitEntity;
				
				if(!plugin.getHeroes())
				{
					event.setDamage(2);	
				}
				if(!(plugin.lp.getHealth() == plugin.lp.getMaxHealth()))
				{
					int health = plugin.getCustomConfig(plugin.file).getInt("mobs." + hitEntity.getEntityId() + ".Health");
					String hitAmount = null;
					if(plugin.getHeroes())
					{
						Heroes heroes = (Heroes) Bukkit.getPluginManager().getPlugin("Heroes");
						health = heroes.getCharacterManager().getMonster(plugin.lp).getHealth();
						if(damager instanceof HumanEntity)
						{
							HumanEntity p = (HumanEntity) damager;
							int hit = 0;
							if(heroes.getDamageManager().getItemDamage(p.getInventory().getItemInHand().getType(), p) != null)
							{
								hit = heroes.getDamageManager().getItemDamage(p.getInventory().getItemInHand().getType(), p);
							}
							else
							{
								hit = event.getDamage();
							}
							health = health - hit;
							hitAmount = hit + "";
						}
						else if(damager instanceof Wolf)
						{
							int hit2 = heroes.getCharacterManager().getMonster((LivingEntity) damager).getDamage();
							health = health - hit2;
							hitAmount = hit2 + "";
						}
						
					}
					else
					{
						health = health - hitamount;
						hitAmount = hitamount + "";
					}
					plugin.getCustomConfig(plugin.file).set("mobs." + hitEntity.getEntityId() + ".Health", health);
					plugin.saveCustomConfig(plugin.file);
					Integer hp = plugin.lp.getMaxHealth();
					plugin.lp.setHealth(hp);
					if(health > 0 && damager instanceof HumanEntity)
					{
						if(plugin.getOptionConfig(plugin.fileo).getString("ShowHealth").equals("none"))
						{
						
						}
						else if(plugin.getOptionConfig(plugin.fileo).getString("ShowHealth").equals("percent"))
						{
							String s = plugin.getCustomConfig(plugin.file).getString("mobs." + hitEntity.getEntityId() + ".Name");
							String k = s.replace("_", " ");
							
							String pref = plugin.getOptionConfig(plugin.fileo).getString("BossTitle");
							pref = ChatColor.translateAlternateColorCodes('&', pref);
							
							int maxhp = plugin.getConfig().getInt("Bosses." + s + ".Health" );
							int per = (health*10)/maxhp + 1;
							int oldper = 100;
							if(plugin.getCustomConfig(plugin.file).contains("mobs." + hitEntity.getEntityId() + ".per"))
							{
								oldper = plugin.getCustomConfig(plugin.file).getInt("mobs." + hitEntity.getEntityId() + ".per");
							}
							else
							{
								plugin.getCustomConfig(plugin.file).set("mobs." + hitEntity.getEntityId() + ".per", 100);
							}
							
							
							if(oldper != per)
							{
								for (Player player: Bukkit.getServer().getOnlinePlayers())
								{
									if(plugin.lp.getWorld() == player.getWorld())
									{
										if(plugin.lp.getLocation().distance(player.getLocation()) <= 20)
										{
											player.sendMessage(pref + ChatColor.DARK_PURPLE + k + " " + ChatColor.GRAY + "[" + ChatColor.RED + per + "0%" + ChatColor.GRAY + "]");
										}
									}
								}
								plugin.getCustomConfig(plugin.file).set("mobs." + hitEntity.getEntityId() + ".per", per);
							}
						}
						else
						{
						String s = plugin.getCustomConfig(plugin.file).getString("mobs." + hitEntity.getEntityId() + ".Name");
						String k = s.replace("_", " ");
						
						String pref = plugin.getOptionConfig(plugin.fileo).getString("BossTitle");
						pref = ChatColor.translateAlternateColorCodes('&', pref);
						
						dmger.sendMessage(pref + ChatColor.DARK_PURPLE + k + " " + ChatColor.GRAY + "[" + ChatColor.RED + health + ChatColor.GRAY + "/" + ChatColor.RED + plugin.getConfig().getInt("Bosses." + s + ".Health" ) + ChatColor.GRAY + "]");
						}
						
						skillexec(plugin.lp, dmger);
						/*if(plugin.getMobhealth())
						{
							MobHealth pMobHealth = (MobHealth) plugin.getServer().getPluginManager().getPlugin("MobHealth");
							MobHealthAPI mobHealthAPI = pMobHealth.getAPI(pMobHealth);
							String mobtype = plugin.getCustomConfig(plugin.file).getString("mobs." + hitEntity.getEntityId() + ".Name");
							int mobsMaxHealth = plugin.getConfig().getInt("Bosses." + mobtype + ".Health");
							mobHealthAPI.showNotification((Player)damager, hitAmount, mobtype, health, mobsMaxHealth, damagerMob);
						}*/
						
					}
					else if(health > 0 && damager instanceof Wolf)
					{
						int rand = (int) (Math.random()*5);
						if(rand == 0)
						{
							damager.setVelocity(new org.bukkit.util.Vector(0, 1.4, 0));
						}
						if(rand == 1)
						{
							damager.setFireTicks(100);
						}
					}
					else
					{
						String s = plugin.getCustomConfig(plugin.file).getString("mobs." + hitEntity.getEntityId() + ".Name");
						GetItems("Bosses." + s + ".Drops", plugin.lp);
						DeathMsg(plugin.lp, dmger);
						// Call the event
						BossDeathEvent eve = new BossDeathEvent(dmger, plugin.lp, plugin);
						Bukkit.getServer().getPluginManager().callEvent(eve);
						plugin.getCustomConfig(plugin.file).set("mobs." + hitEntity.getEntityId(), null);
						plugin.saveCustomConfig(plugin.file);
						plugin.lp.remove();
						if(plugin.getCustomConfig(plugin.file).contains("Timers"))
						{
							for(final String string : plugin.getCustomConfig(plugin.file).getConfigurationSection("Timers").getKeys(false))
							{
								if(plugin.getCustomConfig(plugin.file).get("Timers." + string + ".id").equals(plugin.lp.getEntityId()))
								{
									Integer wait = (Integer) plugin.getCustomConfig(plugin.file).get("Timers." + string + ".ticks");
									plugin.getCustomConfig(plugin.file).set("Timers." + string + ".on", "on");
									plugin.map.put(string, wait);
								}
							}
						}
					}
				}
				}
			}
			else
			{
				event.setCancelled(true);
			}
		}
	}
	/*@EventHandler(priority=EventPriority.NORMAL)
	public void skillHeroes(SkillDamageEvent e)
	{
		EntityDamageByEntityEvent eve = new EntityDamageByEntityEvent(e.getDamager().getEntity(), e.getEntity(), DamageCause.CUSTOM, e.getDamage());
		Bukkit.getServer().getPluginManager().callEvent(eve);
	}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                        LAST PLAYER LEAVE BUG FIX!                            #
	 * #                                                                              #
	 * ################################################################################
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Leaver(PlayerQuitEvent event)
	{
		plugin.BossQuit(event.getPlayer());
	}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                                  ITEMS!                                      #
	 * #                                                                              #
	 * ################################################################################
	 */
	public void GetItems(String s, LivingEntity f)
	{
		items = null;
		ItemList = plugin.getConfig().getStringList(s);
			for(String str : ItemList)
			{
				String[] enchant = str.split(" ");
				//first part
				if(enchant[0].equals("diablodrops"))
				{
					if(plugin.getDiablodrops())
					{
						DiabloDrops dd = (DiabloDrops) Bukkit.getPluginManager().getPlugin("DiabloDrops");
						if(enchant[2].equals("random"))
						{
							drop(dd.dropsAPI.getItem(), f, Integer.parseInt(enchant[1]));
						}
						else
						{
							String it = enchant[2].replace("_", " ");
							CraftItemStack stacks = dd.dropsAPI.getCustomItem(it);
							drop(stacks, f, Integer.parseInt(enchant[1]));
						}
					}
				}
				else
				{
				String fir = enchant[0];
				
				String[] incItems = fir.split(":");
				Integer id = parseInteger(incItems[0]);
				Integer data = parseInteger(incItems[1]);
				Integer amount = parseInteger(incItems[2]);
				int data2 = (int) data;
				
				//second part
				String sec = enchant[1];
				if(enchant.length == 3)
				{
					//both
					Integer chance = parseInteger(sec);
					
					String third = enchant[2];
					String[] Enchant = third.split(",");
					for(String enc : Enchant)
					{
						String[] last = enc.split(":");
						prot.add(last[0]);
						lvl.add(parseInteger(last[1]));
					}
					if(!(id == null|| data == null || amount == null))
					{
						CraftItemStack addEnchant = new CraftItemStack(Material.getMaterial(id), amount, (short) data2);
						int level = -1;
						for(String st : prot)
						{
							level++;
							addEnchant.addUnsafeEnchantment(Enchantment.getByName(st), lvl.get(level));
						}
						drop(addEnchant, f, chance);
						prot.clear();
						lvl.clear();
					}
				}
				else
				{
					// only chance
					Integer chance = parseInteger(sec);
					if(!(id == null|| data == null || amount == null))
					{
						CraftItemStack addEnchant = new CraftItemStack(Material.getMaterial(id), amount, (short) data2);
						drop(addEnchant, f, chance);
					}
				}
				}
				
			}
	}
	//get armor
	public void GetStuff(String s, LivingEntity f)
	{
		items = null;
		ItemList = plugin.getConfig().getStringList(s);
			for(String str : ItemList)
			{
				String[] enchant = str.split(" ");
				//first part
				String fir = enchant[0];
				
				String[] incItems = fir.split(":");
				if(incItems.length == 4)
				{
				Integer id = parseInteger(incItems[0]);
				Integer data = parseInteger(incItems[1]);
				Integer amount = parseInteger(incItems[2]);
				Integer slot = parseInteger(incItems[3]);
				int data2 = (int) data;
				
				//second part
				String sec = enchant[1];
				if(enchant.length == 3)
				{
					//both	
					String third = enchant[2];
					String[] Enchant = third.split(",");
					for(String enc : Enchant)
					{
						String[] last = enc.split(":");
						prot.add(last[0]);
						lvl.add(parseInteger(last[1]));
					}
					if(!(id == null|| data == null || amount == null))
					{
						ItemStack addEnchant = new ItemStack(Material.getMaterial(id), amount, (short) data2);
						int level = -1;
						for(String st : prot)
						{
							level++;
							addEnchant.addUnsafeEnchantment(Enchantment.getByName(st), lvl.get(level));
						}
						EpicBoss.seteq(f, addEnchant, slot);
						prot.clear();
						lvl.clear();
					}
				}
				else
				{
					// only chance
					if(!(id == null|| data == null || amount == null))
					{
						ItemStack addEnchant = new ItemStack(Material.getMaterial(id), amount, (short) data2);
						EpicBoss.seteq(f, addEnchant, slot);
					}
				}
				}
			}
				
	}
	public void drop(CraftItemStack f, LivingEntity p, Integer cha)
	{
		int x = f.getAmount();
		f.setAmount(1);
		int y = 0;
		Random r = new Random();
		int random = r.nextInt(plugin.getOptionConfig(plugin.fileo).getInt("MaxPercent"));
		if(random <= cha)
		{
			while(x > y)
			{
				y++;
				p.getWorld().dropItemNaturally(p.getLocation(), f);
			}
		}
		f.setAmount(x);
	}
	private static Integer parseInteger(String s) {
        try {
            return Integer.parseInt(s.trim());
        }
        catch (Exception e) {
            return null;
        }
    }
	@EventHandler(priority=EventPriority.HIGH)
	  public void BossNoLose(EntityDamageEvent event)
	{
		if(plugin.getCustomConfig(plugin.file).contains("mobs." + event.getEntity().getEntityId())){
			if(!(event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.PROJECTILE))
			{
				event.setCancelled(true);
			}
		}
	}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                              Message Random                                  #
	 * #                                                                              #
	 * ################################################################################
	 */
	public void PlayerDeath(Entity damager, Player p)
	{
		if(plugin.getOptionConfig(plugin.fileo).contains("Message") == true)
		{
			if(plugin.getCustomConfig(plugin.file).contains("mobs." + damager.getEntityId()))
			{
				String bossname = plugin.getCustomConfig(plugin.file).getString("mobs." + damager.getEntityId() + ".Name");
				String playername = p.getName();
				if(plugin.getOptionConfig(plugin.fileo).contains("Message." + bossname + ".PlayerDeath") == true)
				{
					String s = plugin.getOptionConfig(plugin.fileo).getString("Message." + bossname + ".PlayerDeath");
					s = ChatColor.translateAlternateColorCodes('&', s);
					bossname = bossname.replace("_", " ");
					s = s.replace("%boss", bossname);
					s = s.replace("%player", playername);
					Bukkit.broadcastMessage(s);
				}
			}
		}
	}
	public void SkillMsg(LivingEntity l, Player p)
	{
		if(plugin.getOptionConfig(plugin.fileo).contains("Message") == true)
		{
			if(plugin.getCustomConfig(plugin.file).contains("mobs." + l.getEntityId()))
			{
				String bossname = plugin.getCustomConfig(plugin.file).getString("mobs." + l.getEntityId() + ".Name");
				if(plugin.getOptionConfig(plugin.fileo).contains("Message." + bossname + ".SkillMsg") == true)
				{
					List<String> Msgs = plugin.getOptionConfig(plugin.fileo).getStringList("Message." + bossname + ".SkillMsg");
					if(!Msgs.isEmpty())
					{
						int length = Msgs.size();
						int selected = (int) (Math.random()*length);
						String s = Msgs.get(selected);
						s = ChatColor.translateAlternateColorCodes('&', s);			
						for (Player player: Bukkit.getServer().getOnlinePlayers())
						{
							if(l.getWorld() == player.getWorld())
							{
								if(l.getLocation().distance(player.getLocation()) <= 20)
								{
									String t = plugin.getCustomConfig(plugin.file).getString("mobs." + l.getEntityId() + ".Name");
									t = t.replace("_", " ");
									player.sendMessage(ChatColor.DARK_PURPLE + t + " " + s);
								}
							}
						}
					}
				}
			}
		}
	}
	public void DeathMsg(LivingEntity l, Player p)
	{
		if(plugin.getOptionConfig(plugin.fileo).contains("Message") == true)
		{
			if(plugin.getCustomConfig(plugin.file).contains("mobs." + l.getEntityId()))
			{
				String bossname = plugin.getCustomConfig(plugin.file).getString("mobs." + l.getEntityId() + ".Name");
				String playername = p.getName();
				if(plugin.getOptionConfig(plugin.fileo).contains("Message." + bossname + ".DeathMsg") == true)
				{
					String dmsg = plugin.getOptionConfig(plugin.fileo).getString("Message." + bossname + ".DeathMsg");
					dmsg = ChatColor.translateAlternateColorCodes('&', dmsg);
					bossname = bossname.replace("_", " ");
					dmsg = dmsg.replace("%boss", bossname);
					dmsg = dmsg.replace("%player", playername);
					Bukkit.broadcastMessage(dmsg);
				}
				if(plugin.getOptionConfig(plugin.fileo).contains("Message." + bossname + ".CmdMsg") == true)
				{
					List<String> Msgs = plugin.getOptionConfig(plugin.fileo).getStringList("Message." + bossname + ".CmdMsg");
					if(!Msgs.isEmpty())
					{
						for(String s : Msgs)
						{
							s = ChatColor.translateAlternateColorCodes('&', s);
							bossname = bossname.replace("_", " ");
							s = s.replace("%boss", bossname);
							s = s.replace("%player", playername);
							if(s.contains(":"))
							{
								String[] parts = s.split(":");
								int chance = Integer.parseInt(parts[0]);
								Random r = new Random();
								int random = r.nextInt(plugin.getOptionConfig(plugin.fileo).getInt("MaxPercent"));
								String say;
								if(parts.length==3)
								{
									say = parts[1] + ":" + parts[2];
								}
								else if(parts.length==4)
								{
									say = parts[1] + ":" + parts[2] + ":" + parts[3];
								}
								else
								{
									say = parts[1];
								}
								if(random <= chance)
								{
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), say);
								}	
							}
							else
							{
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
							}
						}
					}
				}
			}
		}
	}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                              Turn Off taming                                 #
	 * #                                                                              #
	 * ################################################################################
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Taming(EntityTameEvent event)
	{
		int id = event.getEntity().getEntityId();
		if(plugin.getCustomConfig(plugin.file).contains("mobs"))
		{
			if(plugin.getCustomConfig(plugin.file).contains("mobs." + id))
			{
				event.setCancelled(true);
			}
			else
			{
				if(plugin.getCustomConfig(plugin.file).contains("savemobs"))
				{
					if(plugin.getCustomConfig(plugin.file).contains("savemobs." + id))
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                                 SKILLS!                                      #
	 * #                                                                              #
	 * ################################################################################
	 */
	public void skillexec(LivingEntity l, Player p)
	{
		plugin.Skills(l);
		String s = plugin.getCustomConfig(plugin.file).getString("mobs." + l.getEntityId() + ".Name");
		int random = plugin.getConfig().getInt("Bosses." + s + ".SkillsInterval");
		if((int) (Math.random()*random) == 0)
		{
			int cskills = plugin.skills.size();
			int selected = (int) (Math.random()*cskills);
			int number = 0;
			for(String str: plugin.skills)
			{
				if(selected == number)
				{
					SkillMsg(l, p);
					if(str.equals("teleport"))
						teleport(l,p);
					if(str.equals("swap"))
						swap(l,p);
					if(str.equals("drag_in"))
						dragin(l);
					if(str.equals("lightning"))
						lightning(l);
					
					if(str.equals("poison_short"))
						poisons(l,PotionEffectType.POISON,140);
					if(str.equals("poison_long"))						
						poisons(l,PotionEffectType.POISON,500);
					
					if(str.equals("blindness_short"))
						poisons(l,PotionEffectType.BLINDNESS,140);
					if(str.equals("blindness_long"))		
						poisons(l,PotionEffectType.BLINDNESS,500);
					
					if(str.equals("weakness_short"))
						poisons(l,PotionEffectType.WEAKNESS,140);
					if(str.equals("weakness_long"))		
						poisons(l,PotionEffectType.WEAKNESS,500);
					
					if(str.equals("slow_short"))
						poisons(l,PotionEffectType.SLOW,140);
					if(str.equals("slow_long"))		
						poisons(l,PotionEffectType.SLOW,500);
					
					if(str.equals("hunger_short"))
						poisons(l,PotionEffectType.HUNGER,140);
					if(str.equals("hunger_long"))		
						poisons(l,PotionEffectType.HUNGER,500);
					
					if(str.equals("confusion_short"))
						poisons(l,PotionEffectType.CONFUSION,140);
					if(str.equals("confusion_long"))
						poisons(l,PotionEffectType.CONFUSION,500);
					
					if(str.equals("zombie_swarm_small"))
						spawns(l, EntityType.ZOMBIE);
					if(str.equals("zombie_swarm_big"))
						spawnb(l, EntityType.ZOMBIE);
					
					if(str.equals("skeleton_swarm_small"))
						spawns(l, EntityType.SKELETON);
					if(str.equals("skeleton_swarm_big"))
						spawnb(l, EntityType.SKELETON);
					number++;
					
				}
				else
				{
					number++;
				}
			}
		}
	}
	public void teleport(LivingEntity l, Player p)
	{
		l.teleport(p.getLocation());
	}
	public void dragin(LivingEntity l)
	{
		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if(l.getWorld() == player.getWorld())
			{
				if(l.getLocation().distance(player.getLocation()) <= 20)
				{
					player.teleport(l.getLocation());
				}
			}
		}
	}
	public void swap(LivingEntity l, Player p)
	{
		Location lo = l.getLocation();
		l.teleport(p.getLocation());
		p.teleport(lo);
	}
	public void lightning(LivingEntity l)
	{
		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if(l.getWorld() == player.getWorld())
			{
				if(l.getLocation().distance(player.getLocation()) <= 20)
				{
					player.getWorld().strikeLightning(player.getLocation());
				}
			}
		}
	}
	public void fires(LivingEntity l)
	{
		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if(l.getWorld() == player.getWorld())
			{
				if(l.getLocation().distance(player.getLocation()) <= 20)
				{
					player.setFireTicks(140);
				}
			}
		}
	}
	public void firel(LivingEntity l)
	{
		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if(l.getWorld() == player.getWorld())
			{
				if(l.getLocation().distance(player.getLocation()) <= 20)
				{
					player.setFireTicks(500);
				}
			}
		}
	}
	public void poisons(LivingEntity l, PotionEffectType p, int q)
	{
		for (Player player: Bukkit.getServer().getOnlinePlayers())
		{
			if(l.getWorld() == player.getWorld())
			{
				if(l.getLocation().distance(player.getLocation()) <= 20)
				{
					player.addPotionEffect(new PotionEffect(p, q, 2));
				}
			}
		}
	}
	public void spawns(LivingEntity l, EntityType t)
	{
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
	}
	public void spawnb(LivingEntity l, EntityType t)
	{
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
		l.getWorld().spawnEntity(l.getLocation(), t);
	}
}
