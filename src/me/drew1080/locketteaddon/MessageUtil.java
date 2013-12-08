package me.drew1080.locketteaddon;

import org.bukkit.ChatColor;

public class MessageUtil {
	  public static String colourmessage(String message)
	  { 
			    return ChatColor.translateAlternateColorCodes("&".charAt(0), message);
	  }
}
