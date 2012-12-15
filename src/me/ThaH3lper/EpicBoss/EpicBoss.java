package me.ThaH3lper.EpicBoss;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.EntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EpicBoss extends JavaPlugin
{
	//** Valuables
	public final Logger logger = Logger.getLogger("Minecraft");
	public String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "EpicBoss" + ChatColor.GRAY + "] ";
	
	public static EpicBoss pl;
	public static API API;
	public static SpawnBoss Spawnboss;
	
	public static FileConfiguration DataConfig = null;
	public static File data = null;
	
	public static FileConfiguration DataConfigo = null;
	public static File datao = null;
	
	public HashMap<String, Integer> map = new HashMap<String, Integer>();
	File filee = new File("plugins/EpicBoss/config.yml");
	File fileO = new File("plugins/EpicBoss/Options.yml");
	
	public boolean getboss = false;
	public boolean getTimer = false;
	public String getTimers;
	public LivingEntity lp;
	public String file = "Data.yml";
	public String fileo = "Options.yml";
	public String bName = null;
	public List<String> skills;
	public Location l;
	//** Valuables
	
	//*** On and Off!
	public EpicBoss()
	{
		@SuppressWarnings("unused")
		API api = new API(this);
	}
	@Override
	public void onDisable() 
	{
		//Save Mobs that is not a savedmob!
		if(getCustomConfig(file).contains("mobs") == true)
		{
		for(String w : getCustomConfig(file).getConfigurationSection("mobs").getKeys(false))
		{
			for(World world : Bukkit.getServer().getWorlds())
			{
				for(LivingEntity e : world.getLivingEntities())
				{
						if(e.getEntityId() == Integer.parseInt(w))
						{						
								getCustomConfig(file).set("savemobs." + e.getEntityId() + ".w", e.getWorld().getName());
								getCustomConfig(file).set("savemobs." + e.getEntityId() + ".x", e.getLocation().getX());
								getCustomConfig(file).set("savemobs." + e.getEntityId() + ".y", e.getLocation().getY());
								getCustomConfig(file).set("savemobs." + e.getEntityId() + ".z", e.getLocation().getZ());
								getCustomConfig(file).set("savemobs." + e.getEntityId() + ".type", getCustomConfig(file).getString("mobs." + w + ".Name"));
								getCustomConfig(file).set("mobs." + w, null);
								saveCustomConfig(file);
								e.remove();
						}
					}
				}
		}
		}
		//set savedmobs for timers that has not been respawned!
		if(getCustomConfig(file).contains("Timers") == true)
		{
			for(String w : getCustomConfig(file).getConfigurationSection("Timers").getKeys(false))
			{
				if(getCustomConfig(file).get("Timers." + w + ".on").equals("on"))
				{
					String s = getCustomConfig(file).getString("Timers." + w + ".id");
					String location = getCustomConfig(file).getString("Timers." + w + ".location");
					getCustomConfig(file).set("savemobs." + s + ".w", getCustomConfig(file).getString("Location." + location + ".w"));
				       getCustomConfig(file).set("savemobs." + s + ".x", getCustomConfig(file).getDouble("Location." + location + ".x"));
				       getCustomConfig(file).set("savemobs." + s + ".y", getCustomConfig(file).getDouble("Location." + location + ".y"));
				       getCustomConfig(file).set("savemobs." + s + ".z", getCustomConfig(file).getDouble("Location." + location + ".z"));
				       getCustomConfig(file).set("savemobs." + s + ".type", getCustomConfig(file).getString("Timers." + w + ".type"));
				       getCustomConfig(file).set("Timers." + w + ".on", "no");
				       saveCustomConfig(file);
				}
			}
		}
		PluginDescriptionFile pdfFile = this.getDescription();	
		this.logger.info("[EpicBoss] " + pdfFile.getVersion() +  " Has Been Disabled!");
		getCustomConfig(file).set("mobs", null);
		saveCustomConfig(file);
	}
	@Override
	public void onEnable() 
	{
		PluginDescriptionFile pdfFile = this.getDescription();	
		this.logger.info("[EpicBoss] " + pdfFile.getVersion() +  " Has Been Enabled!");
		
		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(new SpawnBoss(this), this);
		if(!filee.exists())
		{
			this.getConfig().options().copyDefaults(true);
			this.saveConfig();
		}
		this.getCustomConfig(file).options().copyDefaults(true);
		this.saveCustomConfig(file);
		if(!fileO.exists())
		{
			this.getOptionConfig(fileo).options().copyDefaults(true);
			this.saveResource(fileo, false);
		}
		loop();
		
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
			this.logger.info("[EpicBoss] Metrics Failed");
		}
		
		if(getHeroes())
		{
			this.logger.info("[EpicBoss] Hooked into [Heroes]! :D");
		}
		if(getMobhealth())
		{
			this.logger.info("[EpicBoss] Hooked into [MobHealth]! :D");
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 * ################################################################################
	 * #                                                                              #
	 * #                                COMMANDS                                      #
	 * #                                                                              #
	 * ################################################################################
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args)
	{
		if(commandlabel.equalsIgnoreCase("eb") || commandlabel.equalsIgnoreCase("epicboss"))
		{
			
			if(Message.SendPlayer(sender) == true)
			{
				Player player = (Player) sender;
				if(args.length == 0)
				{
				Error(player);
				}
				if(args.length == 1)
				{
					if(args[0].equals("killall"))
					{
						if(player.hasPermission("epicboss.killall"))
						{
							//save mobs remove!
							if(getCustomConfig(file).contains("savemobs"))
							{
								for(String ssave : getCustomConfig(file).getConfigurationSection("savemobs").getKeys(false))
								{
									//Is the mob in a timer=?!
									if(Timers(ssave) == false)
									{
										getCustomConfig(file).set("savemobs." + ssave, null);
										saveCustomConfig(file);
									}
								}
							}
							//mobs remove!
							for(World world : Bukkit.getServer().getWorlds())
							{
								for(LivingEntity e : world.getLivingEntities())
								{
									if(getCustomConfig(file).contains("mobs"))
									{
										for(String smobs : getCustomConfig(file).getConfigurationSection("mobs").getKeys(false))
										{
											Integer i = Integer.parseInt(smobs);
											if(e.getEntityId() == i)
											{
												if(Timers(smobs) == false)
												{
													getCustomConfig(file).set("mobs." + smobs, null);
													saveCustomConfig(file);
													e.setHealth(0);
												}

											}
										}
									}
								}
							}
							
						}
						else
						{
							noPerm(player);
						}
					}
					else if(args[0].equals("reload"))
					{
						if(player.hasPermission("epicboss.reload"))
						{
							reloadConfig();
							saveConfig();
							reloadOptionConfig(fileo);
							this.saveResource(fileo, false);
							player.sendMessage(prefix + ChatColor.GREEN + "has been reloaded!");
						}
						else
							noPerm(player);			
					}
					else if(args[0].equals("p2"))
					{
						p2(player);
					}
					else if(args[0].equals("timers"))
					{
						if(player.hasPermission("epicboss.timers"))
						{
							Message.sLoc = "";
							if(getCustomConfig(file).contains("Timers"))
							{
							for(String s : getCustomConfig(file).getConfigurationSection("Timers").getKeys(false))
							{
								Message.sLoc = Message.sLoc + ChatColor.DARK_PURPLE + s + ChatColor.GRAY + ", ";
							}
						}
						else
						{
							Message.sLoc = ChatColor.DARK_RED + "There is no timers!";
						}
						player.sendMessage(prefix + ChatColor.GOLD + ChatColor.BOLD + "TIMERS");
						player.sendMessage(prefix + ChatColor.DARK_PURPLE + Message.sLoc);
						}
						else
						{
							noPerm(player);
						}
						
					}
					else
					{
						Error(player);
					}
				}
				if(args.length == 2)
				{
					if(args[0].equals("killtype"))
					{
						if(player.hasPermission("epicboss.killtype"))
						{
							if(getConfig().contains("Bosses." + args[1]))
							{
								//save mobs remove!
								if(getCustomConfig(file).contains("savemobs"))
								{
									for(String ssave : getCustomConfig(file).getConfigurationSection("savemobs").getKeys(false))
									{
										if(getCustomConfig(file).get("savemobs." + ssave + ".type").equals(args[1]))
										{
											//Is the mob in a timer=?!
											if(Timers(ssave) == false)
											{
												getCustomConfig(file).set("savemobs." + ssave, null);
												saveCustomConfig(file);
											}
										}
									}
								}
								//mobs remove!
								for(World world : Bukkit.getServer().getWorlds())
								{
									for(LivingEntity e : world.getLivingEntities())
									{
										if(getCustomConfig(file).contains("mobs"))
										{
											for(String smobs : getCustomConfig(file).getConfigurationSection("mobs").getKeys(false))
											{
												if(getCustomConfig(file).get("mobs." + smobs + ".Name").equals(args[1]))
												{
													Integer i = Integer.parseInt(smobs);
													if(e.getEntityId() == i)
													{
														if(Timers(smobs) == false)
														{
															getCustomConfig(file).set("mobs." + smobs, null);
															saveCustomConfig(file);
															e.setHealth(0);
														}

													}
												}
											}
										}
									}
								}
							}
							else
							{
								noBoss(player);
							}
							
						}
						else
						{
							noPerm(player);
						}
					}
					else if(args[0].equals("setlocation"))
					{
						if(player.hasPermission("epicboss.setlocation"))
						{
							String w = player.getWorld().getName();
							double x = player.getLocation().getX();
							double y = player.getLocation().getY();
							double z = player.getLocation().getZ();
							getCustomConfig(file).set("Location." + args[1] + ".w", w);
							getCustomConfig(file).set("Location." + args[1] + ".x", x);
							getCustomConfig(file).set("Location." + args[1] + ".y", y);
							getCustomConfig(file).set("Location." + args[1] + ".z", z);
							saveCustomConfig(file);
							player.sendMessage(prefix + ChatColor.GREEN + args[1] + " has been set!");
						}
						else
						{
							noPerm(player);
						}
					}
					else if(args[0].equals("removelocation"))
					{
						if(player.hasPermission("epicboss.removelocation"))
						{
							if(getCustomConfig(file).contains("Location." + args[1]))
							{
								getCustomConfig(file).set("Location." + args[1], null);
								saveCustomConfig(file);
								player.sendMessage(prefix + ChatColor.GREEN + args[1] + " has been removed!");
								
							}
							else
							{
								wrongloc(player);
							}
						}
						else
						{
							noPerm(player);
						}
					}
					else if(args[0].equals("timers"))
					{
						if(player.hasPermission("epicboss.timers"))
						{
							if(getCustomConfig(file).contains("Timers"))
							{
								if(getCustomConfig(file).contains("Timers." + args[1]))
								{
									player.sendMessage(prefix + ChatColor.GRAY + "Name: " + ChatColor.DARK_PURPLE + args[1]);
									player.sendMessage(prefix + ChatColor.GRAY + "Type: " + ChatColor.DARK_PURPLE + getCustomConfig(file).getString("Timers." + args[1] + ".type"));
									player.sendMessage(prefix + ChatColor.GRAY + "Location: " + ChatColor.DARK_PURPLE + getCustomConfig(file).getString("Timers." + args[1] + ".location"));
				
									int time = getCustomConfig(file).getInt("Timers." + args[1] + ".ticks");
									player.sendMessage(prefix + ChatColor.GRAY + "RespawnTime: " + ChatColor.DARK_PURPLE + timerInfo(time));
									
									//----
									Iterator<Entry<String, Integer>> iter = map.entrySet().iterator();
									Entry<String, Integer> entry;
									int time2 = 0;
									while(iter.hasNext()) {
									    entry = iter.next();
									    if(entry.getKey().equals(args[1]))
									    {
									    	time2 = entry.getValue();
									    }
									}
									    player.sendMessage(prefix + ChatColor.GRAY + "RespawnIn: " + ChatColor.DARK_PURPLE + timerInfo(time2));
									//----
								}
								else
								{
									player.sendMessage(prefix + ChatColor.RED + "That timer dosen't exict!");
								}
							}
							else
							{
								player.sendMessage(prefix + ChatColor.RED + "There is no timers!");
							}
						}
					}
					else
					{
						Error(player);
					}
				}
				if(args.length == 3)
				{
					if(args[0].equals("spawn"))
					{
						if(player.hasPermission("epicboss.spawn"))
						{
							Boss(player, args[1], args[2]);
						}
						else
						{
							noPerm(player);
						}
					}
					else if(args[0].equals("timer") && args[1].equals("remove"))
					{
						if(player.hasPermission("epicboss.removetimer"))
						{
							if(getCustomConfig(file).contains("Timers"))
							{
								for(String s : getCustomConfig(file).getConfigurationSection("Timers").getKeys(false))
								{
									if(args[2].equals(s))
									{
										getCustomConfig(file).set("Timers." + s, null);
										player.sendMessage(prefix + ChatColor.GREEN + s + " has been removed!");
									}
								}
							}
						}
						else
						{
							noPerm(player);
						}
					}	
					else
					{
						Error(player);
					}
				}
				if(args.length == 5)
				{
					if(args[0].equals("timer"))
					{
						if(player.hasPermission("epicboss.newtimer"))
						{
							String name = args[1];
							if(getConfig().contains("Bosses." + args[2]))
							{
								if(getCustomConfig(file).contains("Location." + args[3]))
								{
									String[] time = args[4].split(":");
									Integer h = parseInteger(time[0]);
									Integer m = parseInteger(time[1]);
									Integer s = parseInteger(time[2]);
									if(!(h == null) && !(m==null) && !(s == null))
									{
										Integer TimeTicks = ((s)+(m*60)+(h*60*60));
										getCustomConfig(file).set("Timers." + name + ".type", args[2]);
										getCustomConfig(file).set("Timers." + name + ".location", args[3]);
										getCustomConfig(file).set("Timers." + name + ".ticks", TimeTicks);
										BossTimer(args[2], args[3]);
										getCustomConfig(file).set("Timers." + name + ".id", lp.getEntityId());
										getCustomConfig(file).set("Timers." + name + ".on", "no");
										saveCustomConfig(file);
										player.sendMessage(prefix + ChatColor.GREEN + ChatColor.DARK_GREEN + ChatColor.ITALIC + name + ChatColor.GREEN + " created at " + ChatColor.DARK_GREEN + ChatColor.ITALIC + args[3] + ChatColor.GREEN + " with boss " + ChatColor.DARK_GREEN + ChatColor.ITALIC + args[2] + ChatColor.GREEN + " and respawns after " + ChatColor.DARK_GREEN + ChatColor.ITALIC + h + "hours, " + m + "mins, " + s + "seconds");
									}
									else
										player.sendMessage(prefix + ChatColor.RED + "Timer respawntime error, you made it wrong :/");
								}
								else
									player.sendMessage(prefix + ChatColor.RED + "There is no location with that name!");
							}
							else
								player.sendMessage(prefix + ChatColor.RED + "There is no boss with that name!");
						}
					}
				}
			}
			else
			{
				if(args.length == 3)
				{
					if(args[0].equals("spawn"))
					{
						Boss(null, args[1], args[2]);
					}
				}
				else
				{
					sender.sendMessage("/eb spawn <boss> <location>");
				}
			}
		}
		return false;

	}
	public String timerInfo(Integer i)
	{
		int m = 0;
		int h = 0;
		int s = 0;
		if(i>=60)
		{
			s = i%60;
			i -= s;
			i = i/60;
		}
		else
		{
			s = i;
			return h + "hour(s), " + m + "minut(s), " + s + "second(s)";
		}
		if(i>=60)
		{
			m = i%60;
			i -= m;
			h = i/60;	
		}
		else
		{
			m = i;
			return h + "hour(s), " + m + "minut(s), " + s + "second(s)";
		}
		return h + "hour(s), " + m + "minut(s), " + s + "second(s)";
	}
	public boolean Timers(String s)
	{
		if(getCustomConfig(file).contains("Timers"))
		{
			for(String stimer : getCustomConfig(file).getConfigurationSection("Timers").getKeys(false))
			{
				if(getCustomConfig(file).getString("Timers." + stimer + ".id").equals(s))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                                Message                                       #
	 * #                                                                              #
	 * ################################################################################
	 */
	public void Error(Player p)
	{
		Message.sbosses = "";
		Message.sLoc = "";
		for(String s : getConfig().getConfigurationSection("Bosses").getKeys(false))
		{
			Message.sbosses = Message.sbosses + ChatColor.DARK_PURPLE + s + ChatColor.GRAY + ", ";
		}
		if(getCustomConfig(file).contains("Location"))
		{
			for(String w : getCustomConfig(file).getConfigurationSection("Location").getKeys(false))
			{
				Message.sLoc = Message.sLoc + ChatColor.DARK_PURPLE + w + ChatColor.GRAY + ", ";
			}
		}
		if(Message.sLoc == "")
		{
			Message.sLoc = ChatColor.RED + "There is no Locations!";
		}
		p.sendMessage(prefix + ChatColor.GOLD + ChatColor.BOLD + "COMMANDS" + ChatColor.GRAY + ChatColor.ITALIC + "   /eb p2" + ChatColor.GRAY + " for next page");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + "/eb spawn <bossname> <Location/here>");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + "/eb setlocation <name>");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + "/eb removelocation <name>");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + "/eb killall");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + "/eb killtype <bossname>");
		p.sendMessage(prefix + ChatColor.GOLD + ChatColor.BOLD + "BOSSES");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + Message.sbosses);
		p.sendMessage(prefix + ChatColor.GOLD + ChatColor.BOLD + "LOCATIONS");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + Message.sLoc);
	}
	public void p2(Player p)
	{
		p.sendMessage(prefix + ChatColor.GOLD + ChatColor.BOLD + "MORE COMMANDS");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + "/eb timers");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + "/eb timer remove <name>");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + "/eb timer <name> <boss> <location> <h:min:sec>");
		p.sendMessage(prefix + ChatColor.DARK_PURPLE + "/eb reload");
	}
	public void noPerm(Player p)
	{
		p.sendMessage(prefix + ChatColor.RED + "You don't have permission to do that!");
	}
	public void noBoss(Player p)
	{
		p.sendMessage(prefix + ChatColor.RED + "That boss dosen't exict!");
	}
	public void wrongloc(Player p)
	{
		p.sendMessage(prefix + ChatColor.RED + "That Location dose not exict!");
	}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                            BOSS START EVENT!                                 #
	 * #                                                                              #
	 * ################################################################################
	 */
	public void Boss(Player p, String s, String d)
	{
		if(getConfig().contains("Bosses." + s))
		{
			if(getCustomConfig(file).contains("Location." + d))
			{
				Integer x = getCustomConfig(file).getInt("Location." + d + ".x");
				Integer y = getCustomConfig(file).getInt("Location." + d + ".y");
				Integer z = getCustomConfig(file).getInt("Location." + d + ".z");
				World w = Bukkit.getWorld(getCustomConfig(file).getString("Location." + d + ".w"));
				l = new Location(w, x, y, z);
				
			}
			else if(d.equals("here"))
			{
				if(p != null)
				{
					l = p.getLocation();
				}
			}
			else
			{
				l = null;
				if(p != null)
				{
				wrongloc(p);
				}
			}
			if(!(l == null))
			{
				int id = (int) (Math.random()*99999) + 100000;
				//fix so it dosent overwrite existing!
				getCustomConfig(file).set("savemobs." + id + ".w", l.getWorld().getName());
				getCustomConfig(file).set("savemobs." + id + ".x", l.getX());
				getCustomConfig(file).set("savemobs." + id + ".y", l.getY());
				getCustomConfig(file).set("savemobs." + id + ".z", l.getZ());
				getCustomConfig(file).set("savemobs." + id + ".type", s);
				if(p != null)
				{
					p.sendMessage(prefix + ChatColor.GREEN + "You spawned " + ChatColor.DARK_GREEN + ChatColor.ITALIC + s);
				}
				BossDespawn();

			}
		}
		else
		{
			if(p != null)
			{
			noBoss(p);
			}
		}
	}
	public void BossTimer(String s, String d)
	{
		if(getConfig().contains("Bosses." + s))
		{
			if(getCustomConfig(file).contains("Location." + d))
			{
				Integer x = getCustomConfig(file).getInt("Location." + d + ".x");
				Integer y = getCustomConfig(file).getInt("Location." + d + ".y");
				Integer z = getCustomConfig(file).getInt("Location." + d + ".z");
				World w = Bukkit.getWorld(getCustomConfig(file).getString("Location." + d + ".w"));
				l = new Location(w, x, y, z);
				
			}
			if(!(l == null))
			{
			getboss = true;
			bName = s;
			if(getConfig().get("Bosses." + s + ".Type").equals("witherskeleton"))
			{
				Entity liver = l.getWorld().spawnEntity(l, EntityType.SKELETON);
				Skeleton ske = (Skeleton) liver;
				ske.setSkeletonType(SkeletonType.WITHER);
			}
			if(getConfig().get("Bosses." + s + ".Type").equals("zombiebaby"))
			{
				Entity liver = l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
				Zombie zm = (Zombie) liver;
				zm.setBaby(true);
			}
			if(getConfig().get("Bosses." + s + ".Type").equals("zombievillager"))
			{
				Entity liver = l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
				Zombie zm = (Zombie) liver;
				zm.setVillager(true);
			}
			
			if(getConfig().get("Bosses." + s + ".Type").equals("bat"))
				l.getWorld().spawnEntity(l, EntityType.BAT);
			if(getConfig().get("Bosses." + s + ".Type").equals("witch"))
				l.getWorld().spawnEntity(l, EntityType.WITCH);
			if(getConfig().get("Bosses." + s + ".Type").equals("mushroomcow"))
				l.getWorld().spawnEntity(l, EntityType.MUSHROOM_COW);
			if(getConfig().get("Bosses." + s + ".Type").equals("mushroomcowbaby"))
			{
				Entity liver = l.getWorld().spawnEntity(l, EntityType.MUSHROOM_COW);
				MushroomCow mu = (MushroomCow) liver;
				mu.setBaby();
			}
			
			if(getConfig().get("Bosses." + s + ".Type").equals("squid"))
				l.getWorld().spawnEntity(l, EntityType.SQUID);
			if(getConfig().get("Bosses." + s + ".Type").equals("skeleton"))
				l.getWorld().spawnEntity(l, EntityType.SKELETON);
			if(getConfig().get("Bosses." + s + ".Type").equals("ghast"))
				l.getWorld().spawnEntity(l, EntityType.GHAST);
			if(getConfig().get("Bosses." + s + ".Type").equals("blaze"))
				l.getWorld().spawnEntity(l, EntityType.BLAZE);
			if(getConfig().get("Bosses." + s + ".Type").equals("zombie"))
				l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
			if(getConfig().get("Bosses." + s + ".Type").equals("slime"))
			{
				Entity liver = l.getWorld().spawnEntity(l, EntityType.SLIME);
				Slime slime = (Slime) liver;
				slime.setSize(5);
			}
			if(getConfig().get("Bosses." + s + ".Type").equals("wolf"))
				l.getWorld().spawnEntity(l, EntityType.WOLF);
			if(getConfig().get("Bosses." + s + ".Type").equals("irongolem"))
				l.getWorld().spawnEntity(l, EntityType.IRON_GOLEM);
			if(getConfig().get("Bosses." + s + ".Type").equals("pig"))
				l.getWorld().spawnEntity(l, EntityType.PIG);
			if(getConfig().get("Bosses." + s + ".Type").equals("sheep"))
				l.getWorld().spawnEntity(l, EntityType.SHEEP);
			if(getConfig().get("Bosses." + s + ".Type").equals("villager"))
				l.getWorld().spawnEntity(l, EntityType.VILLAGER);
			if(getConfig().get("Bosses." + s + ".Type").equals("ocelot"))
				l.getWorld().spawnEntity(l, EntityType.OCELOT);
			if(getConfig().get("Bosses." + s + ".Type").equals("chicken"))
				l.getWorld().spawnEntity(l, EntityType.CHICKEN);
			if(getConfig().get("Bosses." + s + ".Type").equals("chickenbaby"))
			{
				Entity liver = l.getWorld().spawnEntity(l, EntityType.CHICKEN);
				Chicken ch = (Chicken) liver;
				ch.setBaby();	
			}
			if(getConfig().get("Bosses." + s + ".Type").equals("cow"))
				l.getWorld().spawnEntity(l, EntityType.COW);
			if(getConfig().get("Bosses." + s + ".Type").equals("cowbaby"))
			{
				Entity liver = l.getWorld().spawnEntity(l, EntityType.COW);
				Cow ch = (Cow) liver;
				ch.setBaby();
			}
			if(getConfig().get("Bosses." + s + ".Type").equals("spider"))
				l.getWorld().spawnEntity(l, EntityType.SPIDER);
			if(getConfig().get("Bosses." + s + ".Type").equals("enderman"))
				l.getWorld().spawnEntity(l, EntityType.ENDERMAN);
			if(getConfig().get("Bosses." + s + ".Type").equals("cavespider"))
				l.getWorld().spawnEntity(l, EntityType.CAVE_SPIDER);
			if(getConfig().get("Bosses." + s + ".Type").equals("giant"))
				l.getWorld().spawnEntity(l, EntityType.GIANT);
			if(getConfig().get("Bosses." + s + ".Type").equals("silverfish"))
				l.getWorld().spawnEntity(l, EntityType.SILVERFISH);
			if(getConfig().get("Bosses." + s + ".Type").equals("magmacube"))
				l.getWorld().spawnEntity(l, EntityType.MAGMA_CUBE);
			if(getConfig().get("Bosses." + s + ".Type").equals("pigzombie"))
				l.getWorld().spawnEntity(l, EntityType.PIG_ZOMBIE);
			}
		}
	}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                              Despawning help!                                #
	 * #                                                                              #
	 * ################################################################################
	 */
	public void BossQuit(Player p)
	{
		if(getCustomConfig(file).contains("mobs"))
		{
			for(String w : getCustomConfig(file).getConfigurationSection("mobs").getKeys(false))
			{
				for(World world : Bukkit.getServer().getWorlds())
				{
					for(LivingEntity e : world.getLivingEntities())
					{
							if(e.getEntityId() == Integer.parseInt(w))
							{
									if(insidequit(e, p) == true)
									{
										getCustomConfig(file).set("savemobs." + e.getEntityId() + ".w", e.getWorld().getName());
										getCustomConfig(file).set("savemobs." + e.getEntityId() + ".x", e.getLocation().getX());
										getCustomConfig(file).set("savemobs." + e.getEntityId() + ".y", e.getLocation().getY());
										getCustomConfig(file).set("savemobs." + e.getEntityId() + ".z", e.getLocation().getZ());
										getCustomConfig(file).set("savemobs." + e.getEntityId() + ".type", getCustomConfig(file).getString("mobs." + w + ".Name"));
										getCustomConfig(file).set("mobs." + w, null);
										saveCustomConfig(file);
										e.remove();
									}
							}
						}
					}
			}
		}
	}
	//depsawn
	public void BossDespawn()
	{
			if(getCustomConfig(file).contains("mobs"))
			{
				for(String w : getCustomConfig(file).getConfigurationSection("mobs").getKeys(false))
				{
					for(World world : Bukkit.getServer().getWorlds())
					{
						for(LivingEntity e : world.getLivingEntities())
						{
								if(e.getEntityId() == Integer.parseInt(w))
								{
										if(insideradius(e) == true)
										{
											getCustomConfig(file).set("savemobs." + e.getEntityId() + ".w", e.getWorld().getName());
											getCustomConfig(file).set("savemobs." + e.getEntityId() + ".x", e.getLocation().getX());
											getCustomConfig(file).set("savemobs." + e.getEntityId() + ".y", e.getLocation().getY());
											getCustomConfig(file).set("savemobs." + e.getEntityId() + ".z", e.getLocation().getZ());
											getCustomConfig(file).set("savemobs." + e.getEntityId() + ".type", getCustomConfig(file).getString("mobs." + w + ".Name"));
											getCustomConfig(file).set("mobs." + w, null);
											saveCustomConfig(file);
											e.remove();
										}
								}
							}
						}
				}
			}
			for(Player p: Bukkit.getServer().getOnlinePlayers())
			{
			if(getCustomConfig(file).contains("savemobs"))
			{
			for(String w : getCustomConfig(file).getConfigurationSection("savemobs").getKeys(false))
			{
				String t = getCustomConfig(file).getString("savemobs." + w + ".w");
				String s = getCustomConfig(file).getString("savemobs." + w + ".type");
				double x = getCustomConfig(file).getDouble("savemobs." + w + ".x");
				double y = getCustomConfig(file).getDouble("savemobs." + w + ".y");
				double z = getCustomConfig(file).getDouble("savemobs." + w + ".z");
				Location q = new Location(Bukkit.getServer().getWorld(t), x, y, z);
				if(p.getWorld().equals(q.getWorld()) && p.isDead() == false)
				{
				if(p.getLocation().distance(q) <= 30)
				{
					if(getConfig().contains("Bosses." + s))
					{
						l = q;
						if(!(l == null))
						{
						getboss = true;
						bName = s;
						
						if(getCustomConfig(file).contains("Timers"))
						{
							for(String k : getCustomConfig(file).getConfigurationSection("Timers").getKeys(false))
							{
								if(getCustomConfig(file).getString("Timers." + k + ".id").equals(w.toString()))
								{
									getTimers = w.toString();
									getTimer = true;
								}
							}
						}
						if(getConfig().get("Bosses." + s + ".Type").equals("witherskeleton"))
						{
							Entity liver = l.getWorld().spawnEntity(l, EntityType.SKELETON);
							Skeleton ske = (Skeleton) liver;
							ske.setSkeletonType(SkeletonType.WITHER);
						}
						if(getConfig().get("Bosses." + s + ".Type").equals("zombiebaby"))
						{
							Entity liver = l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
							Zombie zm = (Zombie) liver;
							zm.setBaby(true);
						}
						if(getConfig().get("Bosses." + s + ".Type").equals("zombievillager"))
						{
							Entity liver = l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
							Zombie zm = (Zombie) liver;
							zm.setVillager(true);
						}
						
						if(getConfig().get("Bosses." + s + ".Type").equals("bat"))
							l.getWorld().spawnEntity(l, EntityType.BAT);
						if(getConfig().get("Bosses." + s + ".Type").equals("witch"))
							l.getWorld().spawnEntity(l, EntityType.WITCH);
						if(getConfig().get("Bosses." + s + ".Type").equals("mushroomcow"))
							l.getWorld().spawnEntity(l, EntityType.MUSHROOM_COW);
						if(getConfig().get("Bosses." + s + ".Type").equals("mushroomcowbaby"))
						{
							Entity liver = l.getWorld().spawnEntity(l, EntityType.MUSHROOM_COW);
							MushroomCow mu = (MushroomCow) liver;
							mu.setBaby();
						}
						
						if(getConfig().get("Bosses." + s + ".Type").equals("squid"))
							l.getWorld().spawnEntity(l, EntityType.SQUID);
						if(getConfig().get("Bosses." + s + ".Type").equals("skeleton"))
							l.getWorld().spawnEntity(l, EntityType.SKELETON);
						if(getConfig().get("Bosses." + s + ".Type").equals("ghast"))
							l.getWorld().spawnEntity(l, EntityType.GHAST);
						if(getConfig().get("Bosses." + s + ".Type").equals("blaze"))
							l.getWorld().spawnEntity(l, EntityType.BLAZE);
						if(getConfig().get("Bosses." + s + ".Type").equals("zombie"))
							l.getWorld().spawnEntity(l, EntityType.ZOMBIE);
						if(getConfig().get("Bosses." + s + ".Type").equals("slime"))
						{
							Entity liver = l.getWorld().spawnEntity(l, EntityType.SLIME);
							Slime slime = (Slime) liver;
							slime.setSize(5);
						}
						if(getConfig().get("Bosses." + s + ".Type").equals("wolf"))
							l.getWorld().spawnEntity(l, EntityType.WOLF);
						if(getConfig().get("Bosses." + s + ".Type").equals("irongolem"))
							l.getWorld().spawnEntity(l, EntityType.IRON_GOLEM);
						if(getConfig().get("Bosses." + s + ".Type").equals("pig"))
							l.getWorld().spawnEntity(l, EntityType.PIG);
						if(getConfig().get("Bosses." + s + ".Type").equals("sheep"))
							l.getWorld().spawnEntity(l, EntityType.SHEEP);
						if(getConfig().get("Bosses." + s + ".Type").equals("villager"))
							l.getWorld().spawnEntity(l, EntityType.VILLAGER);
						if(getConfig().get("Bosses." + s + ".Type").equals("ocelot"))
							l.getWorld().spawnEntity(l, EntityType.OCELOT);
						if(getConfig().get("Bosses." + s + ".Type").equals("chicken"))
							l.getWorld().spawnEntity(l, EntityType.CHICKEN);
						if(getConfig().get("Bosses." + s + ".Type").equals("chickenbaby"))
						{
							Entity liver = l.getWorld().spawnEntity(l, EntityType.CHICKEN);
							Chicken ch = (Chicken) liver;
							ch.setBaby();	
						}
						if(getConfig().get("Bosses." + s + ".Type").equals("cow"))
							l.getWorld().spawnEntity(l, EntityType.COW);
						if(getConfig().get("Bosses." + s + ".Type").equals("cowbaby"))
						{
							Entity liver = l.getWorld().spawnEntity(l, EntityType.COW);
							Cow ch = (Cow) liver;
							ch.setBaby();
						}
						if(getConfig().get("Bosses." + s + ".Type").equals("spider"))
							l.getWorld().spawnEntity(l, EntityType.SPIDER);
						if(getConfig().get("Bosses." + s + ".Type").equals("enderman"))
							l.getWorld().spawnEntity(l, EntityType.ENDERMAN);
						if(getConfig().get("Bosses." + s + ".Type").equals("cavespider"))
							l.getWorld().spawnEntity(l, EntityType.CAVE_SPIDER);
						if(getConfig().get("Bosses." + s + ".Type").equals("giant"))
							l.getWorld().spawnEntity(l, EntityType.GIANT);
						if(getConfig().get("Bosses." + s + ".Type").equals("silverfish"))
							l.getWorld().spawnEntity(l, EntityType.SILVERFISH);
						if(getConfig().get("Bosses." + s + ".Type").equals("magmacube"))
							l.getWorld().spawnEntity(l, EntityType.MAGMA_CUBE);
						if(getConfig().get("Bosses." + s + ".Type").equals("pigzombie"))
							l.getWorld().spawnEntity(l, EntityType.PIG_ZOMBIE);
						}
						
						getCustomConfig(file).set("savemobs." + w, null);
						
						
						
					}

					
				}
				}
				
			}
			}
		}
	}
	public boolean insideradius(LivingEntity e)
	{
		for(Player p: Bukkit.getServer().getOnlinePlayers())
		{
			if(p.isDead() == false)
			{
				if(p.getWorld().equals(e.getWorld()))
				{
					if(p.getLocation().distance(e.getLocation()) <= 40)
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	public boolean insidequit(LivingEntity e, Player ps)
	{
		for(Player p: Bukkit.getServer().getOnlinePlayers())
		{
			if(p.isDead() == false)
			{
				if(p.getWorld().equals(e.getWorld()))
				{
					if(p.getLocation().distance(e.getLocation()) <= 40)
					{
						if(p!=ps)
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	// TIMER! WHEN ITS DONE!
	public void SpawnAfterZero(String string)
	{
	       String type = getCustomConfig(file).getString("Timers." + string + ".type");
	       String location = getCustomConfig(file).getString("Timers." + string + ".location");
	       String ID = getCustomConfig(file).getString("Timers." + string + ".id");
	       getCustomConfig(file).set("Timers." + string + ".on", "no");
	       getCustomConfig(file).set("savemobs." + ID + ".w", getCustomConfig(file).getString("Location." + location + ".w"));
	       getCustomConfig(file).set("savemobs." + ID + ".x", getCustomConfig(file).getDouble("Location." + location + ".x"));
	       getCustomConfig(file).set("savemobs." + ID + ".y", getCustomConfig(file).getDouble("Location." + location + ".y"));
	       getCustomConfig(file).set("savemobs." + ID + ".z", getCustomConfig(file).getDouble("Location." + location + ".z"));
	       getCustomConfig(file).set("savemobs." + ID + ".type", type);
	       saveCustomConfig(file);
	}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                                  Save-ALL                                    #
	 * #                                                                              #
	 * ################################################################################
	 */
	public void saveall()
	{
		if(getCustomConfig(file).contains("mobs") == true)
		{
		for(String w : getCustomConfig(file).getConfigurationSection("mobs").getKeys(false))
		{
			for(World world : Bukkit.getServer().getWorlds())
			{
				for(LivingEntity e : world.getLivingEntities())
				{
						if(e.getEntityId() == Integer.parseInt(w))
						{						
								getCustomConfig(file).set("savemobs." + e.getEntityId() + ".w", e.getWorld().getName());
								getCustomConfig(file).set("savemobs." + e.getEntityId() + ".x", e.getLocation().getX());
								getCustomConfig(file).set("savemobs." + e.getEntityId() + ".y", e.getLocation().getY());
								getCustomConfig(file).set("savemobs." + e.getEntityId() + ".z", e.getLocation().getZ());
								getCustomConfig(file).set("savemobs." + e.getEntityId() + ".type", getCustomConfig(file).getString("mobs." + w + ".Name"));
								getCustomConfig(file).set("mobs." + w, null);
								saveCustomConfig(file);
								e.remove();
						}
					}
				}
		}
		}
	}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                            CUSTOM FILE SAVE!                                 #
	 * #                                                                              #
	 * ################################################################################
	 */
	public void reloadCustomConfig(String sfile) {
	    if (data == null) {
	    data = new File(getDataFolder(), sfile);
	    }
	    DataConfig = YamlConfiguration.loadConfiguration(data);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource(sfile);
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        DataConfig.setDefaults(defConfig);
	    }
	}

	public FileConfiguration getCustomConfig(String sfile) {
	    if (DataConfig == null) {
	        this.reloadCustomConfig(sfile);
	    }
	    return DataConfig;
	}

	public void saveCustomConfig(String sfile) {
	    if (DataConfig == null || data == null) {
	    return;
	    }
	    try {
	        getCustomConfig(sfile).save(data);
	    } catch (IOException ex) {
	        this.getLogger().log(Level.SEVERE, "Could not save config to " + data, ex);
	    }
	    
	}
	//----------------------------------------------------------------------------------------
	public void reloadOptionConfig(String sfile) {
	    if (datao == null) {
	    datao = new File(getDataFolder(), sfile);
	    }
	    DataConfigo = YamlConfiguration.loadConfiguration(datao);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource(sfile);
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        DataConfigo.setDefaults(defConfig);
	    }
	}

	public FileConfiguration getOptionConfig(String sfile) {
	    if (DataConfigo == null) {
	        this.reloadOptionConfig(sfile);
	    }
	    return DataConfigo;
	}

	public void saveOptionConfig(String sfile) {
	    if (DataConfigo == null || datao == null) {
	    return;
	    }
	    try {
	    	getOptionConfig(sfile).save(datao);
	    } catch (IOException ex) {
	        this.getLogger().log(Level.SEVERE, "Could not save config to " + datao, ex);
	    }
	    
	}
	//------------------------------------------------------------------------------------
	public void Skills(LivingEntity l)
	{
		String s = getCustomConfig(file).getString("mobs." + l.getEntityId() + ".Name");
		skills = getConfig().getStringList("Bosses." + s + ".Skills");
	}
	private static Integer parseInteger(String s) {
        try {
            return Integer.parseInt(s.trim());
        }
        catch (Exception e) {
            return null;
        }
    }
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                             Skill - Effect!                                  #
	 * #                                                                              #
	 * ################################################################################
	 */
	public void effectBoss()
	{
		for(World world : Bukkit.getServer().getWorlds())
		{
			for(LivingEntity e : world.getLivingEntities())
			{
				if(getCustomConfig(file).contains("mobs." + e.getEntityId()))
				{
					Skills(e);
					if(skills.contains("effectfire"))
					{
						e.getWorld().playEffect(e.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);
					}
				}
			}
		}
	}
	public void loop()
	{
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			   public void run() {
				   BossDespawn();
				   effectBoss();
				   FixTimerCrash();
				   if(map != null)
				   {
					   Iterator<Entry<String, Integer>> iter = map.entrySet().iterator();
					   Entry<String, Integer> entry;
					   while(iter.hasNext()) {
						    entry = iter.next();
						    if (entry.getValue() == 0) {
						    	SpawnAfterZero(entry.getKey());
						        iter.remove();
						    }
						    else {
						        entry.setValue(entry.getValue() - 1);
						    }
					   }
			   }
		}
	}, 0L, 20L);
}
	/*
	 * ################################################################################
	 * #                                                                              #
	 * #                                  Heroes/Mobhealth                            #
	 * #                                                                              #
	 * ################################################################################
	 */
	public boolean getMobhealth()
	{
		if (Bukkit.getServer().getPluginManager().getPlugin("MobHealth") != null) 
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean getHeroes()
	{
		if (Bukkit.getServer().getPluginManager().getPlugin("Heroes") != null) 
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean getDiablodrops()
	{
		if (Bukkit.getServer().getPluginManager().getPlugin("DiabloDrops") != null) 
		{
			return true;
		}
		else
		{
			return false;
		}
	}
/*
 * ################################################################################
 * #                                                                              #
 * #                                BUG FIXES                                     #
 * #                                                                              #
 * ################################################################################
 */
	public boolean BossExict(LivingEntity l)
	{
		int id = l.getEntityId();
		for(World world : Bukkit.getServer().getWorlds())
		{
			for(LivingEntity e : world.getLivingEntities())
			{
				if(e.getEntityId() == id)
				{
					return true;
				}
			}	
		}
		if(getCustomConfig(file).contains("savemobs"))
		{
			for(String w : getCustomConfig(file).getConfigurationSection("savemobs").getKeys(false))
			{
				if(Integer.parseInt(w) == id)
				{
					return true;
				}
			}
		}
		if(getCustomConfig(file).contains("mobs"))
		{
			for(String m : getCustomConfig(file).getConfigurationSection("mobs").getKeys(false))
			{
				if(Integer.parseInt(m) == id)
				{
					return true;
				}
			}
		}
		return false;
	}
	public void FixTimerCrash()
	{
		if(getCustomConfig(file).contains("Timers") == true)
		{
			for(String w : getCustomConfig(file).getConfigurationSection("Timers").getKeys(false))
			{
				if(getCustomConfig(file).get("Timers." + w + ".on").equals("no"))
				{
					int id = getCustomConfig(file).getInt("Timers." + w + ".id");
					if(CheakEntity(id) == false)
					{
						SpawnAfterZero(w);
					}
				}
			}
		}
	}
	public boolean CheakEntity(int id)
	{
			for(World world : Bukkit.getServer().getWorlds())
			{
				for(LivingEntity e : world.getLivingEntities())
				{
					if(e.getEntityId() == id)
					{
						return true;	
					}
				}
			}
			return false;
	}
	public static void seteq (LivingEntity mob, ItemStack item, int slot) 
	{
        EntityLiving ent = ((CraftLivingEntity) mob).getHandle();
        net.minecraft.server.ItemStack itemStack = new CraftItemStack(item).getHandle();
        ent.setEquipment(slot, itemStack);
    }
}

