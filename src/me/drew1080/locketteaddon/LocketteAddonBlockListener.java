package me.drew1080.locketteaddon;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.yi.acru.bukkit.Lockette.Lockette;

public class LocketteAddonBlockListener implements Listener {

	private LocketteAddon plugin;
	
	Sign sign;

	public LocketteAddonBlockListener(LocketteAddon instance)
	{
		this.plugin = instance;
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void OnSignChange(SignChangeEvent event) {
		
		Player player = event.getPlayer();
		if(event.getLine(0).equalsIgnoreCase("[Private]") && event.getLine(1).equalsIgnoreCase(event.getPlayer().getName())){
			if(this.plugin.getConfig().getBoolean("Use_Global_Price") == true && this.plugin.getConfig().getBoolean("Use_Group_Price") == true){
				event.getPlayer().sendMessage(ChatColor.GOLD+"["+ChatColor.RED+"LocketteAddon"+ChatColor.GOLD+"]"+ChatColor.RED + " Error: You cant use a global price and have per-group price, please try disabling one in the config");
				LocketteAddon.log.warning("[LocketteAddon] You cant use a global price and have per-group price enabled at the same time");
				LocketteAddon.log.warning("[LocketteAddon] Please try disabling one option in the config");
				event.setCancelled(true);
				event.getBlock().breakNaturally();
			}

			if(event.getLine(2).equalsIgnoreCase(this.plugin.getConfig().getString("Text_for_selling_containers"))){
				if(event.getLine(3) != null){
					player.sendMessage(MessageUtil.colourmessage(plugin.getCustomConfig().getString("success_sellable_chest")));
				}else{
					player.sendMessage(MessageUtil.colourmessage(plugin.getCustomConfig().getString("no_price_found")));
					event.setCancelled(true);
					event.getBlock().breakNaturally();
				}
			}else
				if(plugin.getConfig().getBoolean("Use_Global_Price") == true){
					double price = this.plugin.getConfig().getDouble("GlobalPrice");
					if(LocketteAddon.economy.getBalance(player.getName()) < price){
						String notenoughmoney = MessageUtil.colourmessage(plugin.getCustomConfig().getString("not_enough_money"));
						notenoughmoney = notenoughmoney.replaceAll("%container_cost%", plugin.getConfig().getString("GlobalPrice"));
						notenoughmoney = notenoughmoney.replaceAll("%player_balance%", String.valueOf(LocketteAddon.economy.getBalance(player.getName())));
						notenoughmoney = notenoughmoney.replaceAll("Dollar", "");
						player.sendMessage(notenoughmoney);
						event.setCancelled(true);
						event.getBlock().breakNaturally();
					}else{
						LocketteAddon.economy.withdrawPlayer(event.getPlayer().getName(), price);
						String successprotectcontainer = MessageUtil.colourmessage(plugin.getCustomConfig().getString("success_protect_container"));
						successprotectcontainer = successprotectcontainer.replaceAll("%container_cost%", plugin.getConfig().getString("GlobalPrice"));
						player.sendMessage(successprotectcontainer);
					}
				}else

					if(plugin.getConfig().getBoolean("Use_Group_Price") == true){
						String group = LocketteAddon.permission.getPrimaryGroup(player);
						double groupprice = this.plugin.getConfig().getDouble("Group_Pricing."+group);
						if(LocketteAddon.economy.getBalance(player.getName()) < groupprice){
							String notenoughmoney = MessageUtil.colourmessage(plugin.getCustomConfig().getString("not_enough_money"));
							notenoughmoney = notenoughmoney.replaceAll("%container_cost%", plugin.getConfig().getString("Group_Pricing."+group));
							notenoughmoney = notenoughmoney.replaceAll("%player_balance%", String.valueOf(LocketteAddon.economy.getBalance(player.getName())));
							notenoughmoney = notenoughmoney.replaceAll("Dollar", "");
							player.sendMessage(notenoughmoney);
							event.setCancelled(true);
							event.getBlock().breakNaturally();
						}else{
							LocketteAddon.economy.withdrawPlayer(player.getName(), groupprice);
							String successprotectcontainer = MessageUtil.colourmessage(plugin.getCustomConfig().getString("success_protect_container"));
							successprotectcontainer = successprotectcontainer.replaceAll("%container_cost%", plugin.getConfig().getString("Group_Pricing."+group));
							player.sendMessage(successprotectcontainer);
						}
					}

		}


	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void OnBlockPlace(BlockPlaceEvent event)
	{

		Player player = event.getPlayer();
		Block block = event.getBlockPlaced();
		int type = block.getTypeId();

		if (type == Material.WALL_SIGN.getId()) {
			Block checkBlock = Lockette.getSignAttachedBlock(block);

			if (checkBlock == null) return;

			type = checkBlock.getTypeId();

			if ((type == Material.CHEST.getId()) || (type == Material.DISPENSER.getId()) || 
					(type == Material.FURNACE.getId()) || (type == Material.BURNING_FURNACE.getId()) || 
					(type == Material.BREWING_STAND.getId()) || (type == Material.WOOD_DOOR.getId()) || 
					(type == Material.IRON_DOOR.getId()) || (type == Material.TRAP_DOOR.getId()) ||
					(type == Material.ENCHANTMENT_TABLE.getId()))
			{
				int length = player.getName().length();

				if (length > 15) length = 15;

				if (Lockette.isProtected(checkBlock))
				{
					if (Lockette.isOwner(checkBlock, player.getName()))
					{
						if(this.plugin.getConfig().getBoolean("Use_Global_Price") == true && this.plugin.getConfig().getBoolean("Use_Group_Price") == true){
							event.getPlayer().sendMessage(ChatColor.GOLD+"["+ChatColor.RED+"LocketteAddon"+ChatColor.GOLD+"]"+ChatColor.RED + " Error: You cant use a global price and have per-group price, please try disabling one in the config");
							LocketteAddon.log.warning("[LocketteAddon] You cant use a global price and have per-group price enabled at the same time");
							LocketteAddon.log.warning("[LocketteAddon] Please try disabling one option in the config");
							event.setCancelled(true);
							event.getBlock().breakNaturally();
						}

						if(plugin.getConfig().getBoolean("Use_Global_Price") == true){
							double price = this.plugin.getConfig().getDouble("GlobalPrice");
							if(LocketteAddon.economy.getBalance(player.getName()) < price){
								String notenoughmoney = MessageUtil.colourmessage(plugin.getCustomConfig().getString("not_enough_money"));
								notenoughmoney = notenoughmoney.replaceAll("%container_cost%", plugin.getConfig().getString("GlobalPrice"));
								notenoughmoney = notenoughmoney.replaceAll("%player_balance%", String.valueOf(LocketteAddon.economy.getBalance(player.getName())));
								notenoughmoney = notenoughmoney.replaceAll("Dollar", "");
								player.sendMessage(notenoughmoney);
								event.setCancelled(true);
								event.getBlock().breakNaturally();
							}else{
								LocketteAddon.economy.withdrawPlayer(event.getPlayer().getName(), price);
								String successprotectcontainer = MessageUtil.colourmessage(plugin.getCustomConfig().getString("success_protect_container"));
								successprotectcontainer = successprotectcontainer.replaceAll("%container_cost%", plugin.getConfig().getString("GlobalPrice"));
								player.sendMessage(successprotectcontainer);
							}
						}else
							
							if(plugin.getConfig().getBoolean("Use_Group_Price") == true){
								String group = LocketteAddon.permission.getPrimaryGroup(player);
								double groupprice = this.plugin.getConfig().getDouble("Group_Pricing."+group);
								if(LocketteAddon.economy.getBalance(player.getName()) < groupprice){
									String notenoughmoney = MessageUtil.colourmessage(plugin.getCustomConfig().getString("not_enough_money").trim());
									notenoughmoney = notenoughmoney.replace("%container_cost%", plugin.getConfig().getString("Group_Pricing."+group));
									notenoughmoney = notenoughmoney.replaceAll("%player_balance%", String.valueOf(LocketteAddon.economy.getBalance(player.getName())));
									player.sendMessage(notenoughmoney);
									event.setCancelled(true);
									event.getBlock().breakNaturally();
								}else{
									LocketteAddon.economy.withdrawPlayer(player.getName(), groupprice);
									String successprotectcontainer = MessageUtil.colourmessage(plugin.getCustomConfig().getString("success_protect_container"));
									successprotectcontainer = successprotectcontainer.replaceAll("%container_cost%", plugin.getConfig().getString("Group_Pricing."+group));
									player.sendMessage(successprotectcontainer);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if ((event.getClickedBlock().getState() instanceof Sign)) {
				this.sign = ((Sign)event.getClickedBlock().getState());

				if(this.sign.getLine(0).equalsIgnoreCase("[Private]") && this.sign.getLine(2).equalsIgnoreCase(this.plugin.getConfig().getString("Text_for_selling_containers")))	
				{
					if(this.sign.getLine(1).equalsIgnoreCase(event.getPlayer().getName())){
						event.getPlayer().sendMessage(ChatColor.GOLD+"["+ChatColor.RED+"LocketteAddon"+ChatColor.GOLD+"]"+ChatColor.RED + " This is your container");
						event.getPlayer().sendMessage(MessageUtil.colourmessage(plugin.getCustomConfig().getString("your_container")));

					}
					else{

					double price = Double.parseDouble(this.sign.getLine(3));
					if(LocketteAddon.economy.getBalance(event.getPlayer().getName()) < price){
						String not_enough_to_buy_container = MessageUtil.colourmessage(plugin.getCustomConfig().getString("not_enough_money_to_buy_container"));
						not_enough_to_buy_container = not_enough_to_buy_container.replaceAll("%container_cost%", sign.getLine(3));
						not_enough_to_buy_container = not_enough_to_buy_container.replaceAll("%player_balance%", String.valueOf(LocketteAddon.economy.getBalance(event.getPlayer().getName())));
						event.getPlayer().sendMessage(not_enough_to_buy_container);
					}
					LocketteAddon.economy.withdrawPlayer(event.getPlayer().getName(), price);
					String success_buying_container = MessageUtil.colourmessage(plugin.getCustomConfig().getString("success_buy_container"));
					success_buying_container = success_buying_container.replaceAll("%container_cost%", sign.getLine(3));
					success_buying_container = success_buying_container.replaceAll("%player_balance%",String.valueOf(LocketteAddon.economy.getBalance(event.getPlayer().getName())));
					success_buying_container = success_buying_container.replaceAll("Dollar", "");
					event.getPlayer().sendMessage(success_buying_container);
					String originalowner = this.sign.getLine(1);
					LocketteAddon.economy.depositPlayer(originalowner, price);
					this.sign.setLine(1, event.getPlayer().getName());
					this.sign.setLine(2, ChatColor.RED + "");
					this.sign.setLine(3, "");
					this.sign.update();
					}
				}
			}
		}
	}
}