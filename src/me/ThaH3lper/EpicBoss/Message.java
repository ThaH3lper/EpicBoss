package me.ThaH3lper.EpicBoss;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message 
{
	public static String sbosses;
	public static String sLoc;
	public static boolean SendPlayer(CommandSender sender)
	{
		if(sender instanceof Player)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
