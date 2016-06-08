package me.TerrorLT.TerrorPVP;

import me.TerrorLT.TerrorPVP.Objects.AreaBoundary;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Globals {

	//Configuration
	public static String worldName = "world";
	public static String hubWorldName = "world";
	
	public static double firstHitBenefit = 1d;
	public static double minimumVelocity = 0.05d;
	public static int    claimTimeout = 10;
	public static String pointSuffix = "C";
	public static String pointPrefix = "";

	public static AreaBoundary safeZone;
	public static AreaBoundary worldZone;
	
	//Settings
	public static boolean particlesActive = false;
	public static boolean bounceForward = true;
	public static boolean usageNeedsPerm = false;
	public static boolean suicideIsDeath = false;
	public static boolean noPlayerDeathIsDeath = false;
	
	//Nodes
	private static final String CNode_World = "main.world";
	private static final String CNode_hubWorld = "main.hubworld";
	private static final String CNode_SafeZone = "main.safezone";
	public static final String CNode_HighScores = "main.highscores";
	public static final String CNode_LootChest = "main.lootchest";
	private static final String CNode_WorldZone = "main.worldzone";
	
	//SQL tables
	public static final String PLAYERS = "players";
	public static final String BATTLE_TAGS = "battletags";
	public static final String PLAYER_KEYS = "plrKeys";
	
	
	//Inventory names
	public static final String GUI_KITSELECT = ChatColor.BLUE + "Select a kit";
	public static final String GUI_KITSELECT_ITEM = ChatColor.BLUE + "Kits " + ChatColor.GRAY + "(Right-click)";
	public static final String GUI_ITEMSHOP_MAIN = ChatColor.YELLOW + "Select a category";
	public static final String GUI_ITEMSHOP_TITLES = ChatColor.YELLOW + "Title shop";
	public static final String GUI_ITEMSHOP_KITS = ChatColor.YELLOW + "Kit shop";
	public static final String GUI_ITEMSHOP_ITEM = ChatColor.YELLOW + "Coin Shop " + ChatColor.GRAY + "(Right-click)";
	public static final String GUI_ITEMSHOP_ITEM_TITLES = ChatColor.YELLOW + "Titles";
	public static final String GUI_ITEMSHOP_ITEM_KITS = ChatColor.YELLOW + "Kits";
	
	public static void ReadConfiguration()
	{
		if(Main.main == null) return;
		
		if(Main.main.configuration.contains(CNode_World))
			worldName = Main.main.configuration.getString(CNode_World);
		if(Main.main.configuration.contains(CNode_hubWorld))
			hubWorldName = Main.main.configuration.getString(CNode_hubWorld);
		
		if(Main.main.configuration.isSet(CNode_SafeZone))
			safeZone = (AreaBoundary)
			(new AreaBoundary()).retrieve(Main.main.configuration, CNode_SafeZone);
		
		if(Main.main.configuration.isSet(CNode_WorldZone))
			worldZone = (AreaBoundary)
			(new AreaBoundary()).retrieve(Main.main.configuration, CNode_WorldZone);
		
	}
	
	public static void setSafezone(Location P1, Location P2)
	{
		safeZone = fromLocations(P1, P2);
		safeZone.put(Main.main.configuration, CNode_SafeZone);
		Main.main.saveConfiguration();
	}
	
	public static void removeSafezone()
	{
		worldZone = null;
		Main.main.configuration.set(CNode_WorldZone, null);
		Main.main.saveConfiguration();
	}
	
	public static void removeWorldzone()
	{
		safeZone = null;
		Main.main.configuration.set(CNode_SafeZone, null);
		Main.main.saveConfiguration();
	}
	
	public static void setWorldZone(Location P1, Location P2)
	{
		worldZone = fromLocations(P1, P2);
		worldZone.put(Main.main.configuration, CNode_WorldZone);
		Main.main.saveConfiguration();
	}
	
	public static AreaBoundary fromLocations(Location P1, Location P2)
	{
		if(P1.getWorld() != P2.getWorld()) return null;
		
		int topX, topY, topZ, minX, minY, minZ;
		
		if(P1.getBlockX() > P2.getBlockX()){
			topX = P1.getBlockX();
			minX = P2.getBlockX();
		}else{
			topX = P2.getBlockX();
			minX = P1.getBlockX();
		}
		
		if(P1.getBlockZ() > P2.getBlockZ()){
			topZ = P1.getBlockZ();
			minZ = P2.getBlockZ();
		}else{
			topZ = P2.getBlockZ();
			minZ = P1.getBlockZ();
		}
		
		if(P1.getBlockY() > P2.getBlockY()){
			topY = P1.getBlockY();
			minY = P2.getBlockY();
		}else{
			topY = P2.getBlockY();
			minY = P1.getBlockY();
		}
		
		return new AreaBoundary(minX, topX, minY, topY, minZ, topZ);
	}
	
	public static Location getKitPvpSpawnLocation()
	{
		if(Bukkit.getServer().getWorld(worldName) != null)
			return Bukkit.getServer().getWorld(worldName).getSpawnLocation();
		return null;
	}
	
	public static boolean isSafezoneSet()
	{
		return safeZone != null;
	}
	
	public static boolean isWorldzoneSet()
	{
		return worldZone != null;
	}
	
	public static AreaBoundary getWorldzone()
	{
		return worldZone;
	}
	
	public static void removeNode(String node)
	{
		Main.main.configuration.set(node, null);
		Main.main.saveConfiguration();
	}
}
