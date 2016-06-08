package me.TerrorLT.TerrorPVP;

import me.TerrorLT.TerrorPVP.Kits.Kits;

import org.bukkit.entity.Player;

public class Permissions {

	
	private static String USAGE_PERM = "terrorpvp.use";
	private static String DB_RESET_PERM = "terrorpvp.db.reset";
	
	//Returns T/F if player can play kitpvp
	public static boolean canUse(Player player)
	{
		if(player == null) return false;
		if(Globals.usageNeedsPerm) return player.hasPermission(USAGE_PERM);
		return false;
	}
	
	//Returns T/F if player can use the specified kit.
	public static boolean canUseKit(Player player, Kits kit)
	{
		if(kit.getNode() == null || kit.getNode().equals("")) return canUse(player);
		if(player.hasPermission(USAGE_PERM+"."+kit.getNode())) return true;
		return false;
	}
	
	public static boolean canResetDb(Player player)
	{
		if(player == null) return false;
		return player.hasPermission(DB_RESET_PERM);
	}
	
}
