package me.drew1080.locketteaddon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class LocketteAddon extends JavaPlugin {

	public static Logger log = Logger.getLogger("Mineraft");
	public static Permission permission = null;
	public static Economy economy = null;
	private FileConfiguration customConfig;
	private File customConfigFile;

	public void onEnable(){
		setupPermissions();
		setupEconomy();
		
		getServer().getPluginManager().registerEvents(new LocketteAddonBlockListener(this), this);
		if (!new File(getDataFolder(), "config.yml").exists()){
			SetupConfig();
		}
		if (!new File(getDataFolder(), "messages.yml").exists()){
			SetupMessagesConfig();
		}
		checkconfigcontents();
		log.info("[LocketteAddon] LocketteAddon has been enabled");
	}

	public void onDisbale(){
		log.info("[LocketteAddon] LocketteAddon has been disabled");
	}
	private boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
		if (permissionProvider != null) {
			permission = (Permission)permissionProvider.getProvider();
			log.info("[LocketteAddon] Found permissions plugin " + permission.getName());
		}
		return permission != null;
	}

	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = (Economy)economyProvider.getProvider();
		}
		return economy != null;
	}
	public void SetupConfig(){
		this.getConfig().set("Use_Global_Price", Boolean.valueOf(true));
		this.getConfig().set("Use_Group_Price", Boolean.valueOf(false));
		this.getConfig().set("GlobalPrice", Integer.valueOf(50));
		this.saveConfig();
		this.reloadConfig();
		String[] groups = LocketteAddon.permission.getGroups();
		List<String> list = Arrays.asList(groups);
		for(int i = 0; i < list.size(); i++){
			String groupname = list.get(i);
			this.getConfig().set("Group_Pricing."+groupname, Integer.valueOf(0));
			this.saveConfig();
			this.reloadConfig();
		}
		this.getConfig().set("Text_for_selling_containers", String.valueOf("[LocketteAddon]"));
		this.getConfig().options().header("LocketteAddon's Main config file, For more information on how to setup the config visit the bukkit-dev page");
		this.getConfig().options().copyHeader(true);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.reloadConfig();


	}

	public void SetupMessagesConfig() {
		saveResource("messages.yml", true);
	}

	public void checkconfigcontents(){
		if(this.getConfig().getBoolean("Use_Global_Price") == true && this.getConfig().getBoolean("Use_Group_Price") == true){
			log.warning("[LocketteAddon] You cant use a global price and have per-group price enabled at the same time");
			log.warning("[LocketteAddon] Please try disabling one option in the config");
			log.info("[LocketteAddon] LocketteAddon disabling");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
	}

	public boolean permissionCheck(Player p, String node)
	{
		return p.hasPermission(node);
	}

	public void reloadCustomConfig() {
		if (this.customConfigFile == null) {
			this.customConfigFile = new File(getDataFolder(), "messages.yml");
		}
		this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);

		InputStream defConfigStream = getResource("messages.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = 
					YamlConfiguration.loadConfiguration(defConfigStream);
			this.customConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getCustomConfig() {
		if (this.customConfig == null) {
			reloadCustomConfig();
		}
		return this.customConfig;
	}

	public void saveCustomConfig() {
		if ((this.customConfig == null) || (this.customConfigFile == null))
			return;
		try
		{
			getCustomConfig().save(this.customConfigFile);
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, 
					"Could not save config to " + this.customConfigFile, ex);
		}
	}

	public boolean onCommand(CommandSender s, Command c, String l, String[] args){
		Player player = (Player)s;
		if(c.getName().equalsIgnoreCase("locketteaddon")){
			if(args.length < 1) {
				s.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
				s.sendMessage(ChatColor.DARK_AQUA + "LocketteAddon Command Help:");
				s.sendMessage(ChatColor.AQUA + "/locketteaddon help" + ChatColor.RED + " - " + ChatColor.GREEN + "Lists all available commands");
				s.sendMessage(ChatColor.AQUA + "/locketteaddon about" + ChatColor.RED + " - " + ChatColor.GREEN + "Shows info about the plugin");
				s.sendMessage(ChatColor.AQUA + "/locketteaddon reload" + ChatColor.RED + " - " + ChatColor.GREEN + "Reloads the config");
				s.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
				return true;
			}
			if(args[0].equalsIgnoreCase("help") && this.permissionCheck(player, "LocketteAddon.help")) {
				s.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
				s.sendMessage(ChatColor.DARK_AQUA + "LocketteAddon Command Help:");
				s.sendMessage(ChatColor.AQUA + "/locketteaddon help" + ChatColor.RED + " - " + ChatColor.GREEN + "Lists all available commands");
				s.sendMessage(ChatColor.AQUA + "/locketteaddon about" + ChatColor.RED + " - " + ChatColor.GREEN + "Shows info about the plugin");
				s.sendMessage(ChatColor.AQUA + "/locketteaddon reload" + ChatColor.RED + " - " + ChatColor.GREEN + "Reloads the config");
				s.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
				return true;
			}
			if(args[0].equalsIgnoreCase("reload") && this.permissionCheck(player, "LocketteAddon.reload")){
				player.sendMessage(ChatColor.GOLD+"["+ChatColor.RED+"LocketteAddon"+ChatColor.GOLD+"]"+ChatColor.GREEN + " Reloading config...");
				this.saveConfig();
				this.reloadConfig();
				player.sendMessage(ChatColor.GOLD+"["+ChatColor.RED+"LocketteAddon"+ChatColor.GOLD+"]"+ChatColor.GREEN + " ... Reload Done");
				return true;
			}
			if(args[0].equalsIgnoreCase("about") && this.permissionCheck(player, "LocketteAddon.about")){
				player.sendMessage(ChatColor.GOLD+"--------["+ChatColor.RED+"LocketteAddon v2.4.2"+ChatColor.GOLD+"]---------");
				player.sendMessage(ChatColor.GREEN + "LocketteAddon made by: " + ChatColor.AQUA+"Drew1080");
				player.sendMessage(ChatColor.GOLD+"----------------------------------");
				return true;
			}
		}
		return false;

	}
}
