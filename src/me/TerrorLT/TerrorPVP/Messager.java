package me.TerrorLT.TerrorPVP;

import me.TerrorLT.TerrorPVP.Objects.Kill;
import me.TerrorLT.TerrorPVP.SQLObjects.SQLPlayer;
import me.TerrorLT.TerrorPVP.Systems.LootCrate;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.connorlinfoot.titleapi.TitleAPI;

public class Messager {

	//Misc
	public static String KDCounterDelim = " : ";
	
	//Safezone
	public static String Zone_SetP1 = ChatColor.GREEN + "Click a corner block of the zone.";
	public static String Zone_SetP2 = ChatColor.GREEN + "Click the opposite corner block of the zone.";
	public static String Zone_SetSuccess = ChatColor.GREEN + "Zone successfully set!";
	public static String Zone_SetCancel = ChatColor.RED + "Zone setting cancelled.";
	
	public static String Safezone_NoEntry = ChatColor.RED + "You cannot return to the safe zone.";
	public static String Safezone_OnLeave = ChatColor.RED + "You have left the safe zone.";
	
	//Flaming chest
	public static String LootChest_Set = ChatColor.GREEN + "Select a chest to register as a "+LootCrate.name;
	public static String LootChest_SetSuccess = LootCrate.name + "" +ChatColor.GREEN + " successfully set!";
	public static String LootChest_SetCancel = LootCrate.name + "" + ChatColor.RED + " setting cancelled.";
	public static String LootChest_SetNotChest = ChatColor.RED + "You have to select a chest!";
	
	public static String LootChest_NoKeys = ChatColor.RED + "You don't have any keys to open this crate with!";
	public static String LootChest_KeyAds = ChatColor.GREEN + "You can purchase keys at " + ChatColor.YELLOW + ChatColor.UNDERLINE + "www.lonksmc.com/kitpvp";
	public static String LootChest_GivenKeys = ChatColor.GREEN + 
			"You have been given "+ChatColor.YELLOW+"{amount}"+ChatColor.GREEN+" keys!";
	public static String LootChest_YouCanOnly = ChatColor.GREEN + "You can only get this from the " + LootCrate.name;
	
	//Misc
	public static String Cannot_Command_Console = ChatColor.RED + "Cannot use this command from the console!";
	public static String DB_RESET_SUCCESS = ChatColor.GREEN + "SQLite Databases have been successfully reset.";
	
	//Deaths
	public static String Death_Suicide = ChatColor.RED + "You have killed yourself.";
	public static String DeathT_Suicide = ChatColor.RED + "YOU KILLED YOURSELF.";
	
	public static String DeathT_Simple = ChatColor.RED + "YOU DIED.";
	
	//Kills
	public static String DeathS_KilledBy = ChatColor.RED+"You were killed by {name}";
	public static String DeathS_ClaimedBy = ChatColor.RED+"Your death was claimed by {name}";
	public static String KillAB_StolenBy = ChatColor.RED + "Your kill has been stolen by {name}";
	
	//Shop
	public static String NOT_ENOUGH_MONEY = ChatColor.RED + "You do not have enough coins to buy that!";
	public static String BOUGHT = ChatColor.GREEN + "You have bought: ";
	public static String TITLE_SET_TO = ChatColor.GREEN + "Your title has been set to ";
	public static String TITLE_REMOVED = ChatColor.GREEN + "Your title has been removed.";
	
	//TODO: ^ Make all of these configurable
	
	public static void Deliver(Player player, String message)
	{
		player.sendMessage(message);
	}
	
	public static void Deliver(CommandSender sender, String message)
	{
		sender.sendMessage(message);
	}
	
	public static String formatPoints(int points)
	{
		return Globals.pointPrefix + points + Globals.pointSuffix;
	}
	
	public static void setTabKDP(Player player, SQLPlayer data)
	{
		if(player == null || data == null) return;
		
		TitleAPI.sendTabTitle(player, 
				formatKD(data.getKills(), data.getDeaths(), data.getStolenFrom()), 
				ChatColor.GOLD + formatPoints(data.getPoints()));
	}
	
	public static void resetTabKDP(Player player)
	{
		if(player == null) return;
		
		TitleAPI.sendTabTitle(player, "", "");
	}
	
	public static String formatKD(int kills, int deaths, int stolenfrom)
	{
		return ChatColor.GREEN +""+ kills +ChatColor.WHITE + KDCounterDelim +
				ChatColor.RED + "" + deaths + ChatColor.WHITE + KDCounterDelim+
				ChatColor.BLUE + "" + stolenfrom;
	}
	
	public static String formatColors(String s)
	{
		return s.replaceAll("&0", ChatColor.BLACK+"")
				.replaceAll("&1", ChatColor.DARK_BLUE +"")
				.replaceAll("&2", ChatColor.DARK_GREEN+"")
				.replaceAll("&3", ChatColor.DARK_AQUA+"")
				.replaceAll("&4", ChatColor.DARK_RED+"")
				.replaceAll("&5", ChatColor.DARK_PURPLE+"")
				.replaceAll("&6", ChatColor.GOLD+"")
				.replaceAll("&7", ChatColor.GRAY+"")
				.replaceAll("&8", ChatColor.DARK_GRAY+"")
				.replaceAll("&9", ChatColor.BLUE + "")
				.replaceAll("&a", ChatColor.GREEN + "")
				.replaceAll("&b", ChatColor.AQUA +"")
				.replaceAll("&c", ChatColor.RED + "")
				.replaceAll("&d", ChatColor.LIGHT_PURPLE + "")
				.replaceAll("&e", ChatColor.YELLOW + "")
				.replaceAll("&f", ChatColor.WHITE + "")
				.replaceAll("&r", ChatColor.RESET + "")
				.replaceAll("&l", ChatColor.BOLD + "")
				.replaceAll("&o", ChatColor.ITALIC + "")
				.replaceAll("&n", ChatColor.UNDERLINE + "")
				.replaceAll("&m", ChatColor.STRIKETHROUGH + "")
				.replaceAll("&k", ChatColor.MAGIC+"");
	}
	
	public static String formatPlayerString(Player player, String str)
	{
		if(str == null || player == null) return null;
		
		return str.replaceAll("\\{name\\}", player.getName())
				.replaceAll("\\{dname\\}", player.getDisplayName());
	}
	
	public static void DeliverKillMessage(Player player, Kill kill)
	{
		ActionBarAPI.sendActionBar(player, ChatColor.AQUA + kill.GetKillMessage(), 50);
		DeliverPointMessage(player, kill.getPoints());
	}
	
	public static void DeliverStolenKillMessage(Player player, Player thief, int points)
	{
		ActionBarAPI.sendActionBar(player, formatPlayerString(thief, KillAB_StolenBy), 50);
		if(points > 0) DeliverPointMessage(player, points);
	}
	
	public static void DeliverPointMessage(Player player, int points)
	{
		TitleAPI.sendTitle(player, 2, 30, 2, "", 
				ChatColor.AQUA + "+" + Messager.formatPoints(points));
	}
	
	public static String formatDouble(double num)
	{
		int doub = (int)(num*100);
		return (doub/100)+"."+((doub%100)/10)+""+((doub%10));
	}
	
	public static String formatGivenKeys(int amount)
	{
		return LootChest_GivenKeys.replaceAll("\\{amount\\}", amount+"");
	}
	
}
