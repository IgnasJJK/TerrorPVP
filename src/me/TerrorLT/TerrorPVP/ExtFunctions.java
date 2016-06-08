package me.TerrorLT.TerrorPVP;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExtFunctions {

	public static double[] sinCache = new double[90];
	public static double[] cosCache = new double[90];
	
	//Returns damage reduction percentage based on player's armor
	public static double getDamageReduced(Player player)
    {
        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        ItemStack boots = inv.getBoots();
        ItemStack helmet = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack pants = inv.getLeggings();
        double red = 0.0;
        if(helmet != null)
        if(helmet.getType() == Material.LEATHER_HELMET)red = red + 0.04;
        else if(helmet.getType() == Material.GOLD_HELMET)red = red + 0.08;
        else if(helmet.getType() == Material.CHAINMAIL_HELMET)red = red + 0.08;
        else if(helmet.getType() == Material.IRON_HELMET)red = red + 0.08;
        else if(helmet.getType() == Material.DIAMOND_HELMET)red = red + 0.12;
        //
        if(boots != null)
        if(boots.getType() == Material.LEATHER_BOOTS)red = red + 0.04;
        else if(boots.getType() == Material.GOLD_BOOTS)red = red + 0.04;
        else if(boots.getType() == Material.CHAINMAIL_BOOTS)red = red + 0.04;
        else if(boots.getType() == Material.IRON_BOOTS)red = red + 0.08;
        else if(boots.getType() == Material.DIAMOND_BOOTS)red = red + 0.12;
        //
        if(pants != null)
        if(pants.getType() == Material.LEATHER_LEGGINGS)red = red + 0.08;
        else if(pants.getType() == Material.GOLD_LEGGINGS)red = red + 0.12;
        else if(pants.getType() == Material.CHAINMAIL_LEGGINGS)red = red + 0.16;
        else if(pants.getType() == Material.IRON_LEGGINGS)red = red + 0.20;
        else if(pants.getType() == Material.DIAMOND_LEGGINGS)red = red + 0.24;
        //
        if(chest != null)
        if(chest.getType() == Material.LEATHER_CHESTPLATE)red = red + 0.12;
        else if(chest.getType() == Material.GOLD_CHESTPLATE)red = red + 0.20;
        else if(chest.getType() == Material.CHAINMAIL_CHESTPLATE)red = red + 0.20;
        else if(chest.getType() == Material.IRON_CHESTPLATE)red = red + 0.24;
        else if(chest.getType() == Material.DIAMOND_CHESTPLATE)red = red + 0.32;
        return red;
    }
	
	
	public static double getDistanceBetween(Location a, Location b)
	{
		double xDiff = a.getX() - b.getX();
		double zDiff = a.getZ() - b.getZ();
		
		return Math.sqrt(Math.pow(xDiff, 2) + Math.pow(zDiff, 2));
	}
	
	public static int getTimeStamp()
	{
		return (int)(System.currentTimeMillis()/1000L);
	}
	
	public static void generateSinCosCaches()
	{
		for(int i = 0; i < 90; i++)
		{
			sinCache[i] = Math.sin(i);
			cosCache[i] = Math.cos(i);
		}
	}
	
	public static double sin(int x)
	{
		x = x % 360;
		if(x >= 0 && x < 90)
			return sinCache[x];
		if(x >= 90 && x < 180)
			return 1-sinCache[x%90];
		if(x >= 180 && x < 270)
			return -sinCache[x%90];
		if(x >= 270 && x < 360)
			return -(1-sinCache[x%90]);
		return 0;
	}
	
	public static double cos(int x)
	{
		x = x % 360;
		if(x >= 0 && x < 90)
			return cosCache[x];
		if(x >= 90 && x < 180)
			return 1-cosCache[x%90];
		if(x >= 180 && x < 270)
			return -cosCache[x%90];
		if(x >= 270 && x < 360)
			return -(1-cosCache[x%90]);
		return 0;
	}
			
	public static void logToFile(String message)
    {
        try
        {
            File dataFolder = Main.plugin.getDataFolder();
            if(!dataFolder.exists())
            {
                dataFolder.mkdir();
            }
 
            File saveTo = new File(dataFolder, ""+(getTimeStamp()/86400)+".log");
            if (!saveTo.exists())
            {
                saveTo.createNewFile();
            }
 
            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("["+(new Date()).toString()+"] "+message);
            pw.flush();
            pw.close();
 
        } catch (IOException e)
        {
            e.printStackTrace();
        }
 
    }
	
	
}
