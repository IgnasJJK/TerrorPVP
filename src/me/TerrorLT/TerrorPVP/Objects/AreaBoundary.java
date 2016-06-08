package me.TerrorLT.TerrorPVP.Objects;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AreaBoundary extends Configurable {

	private int MIN_X;
	private int MAX_X;
	private int MIN_Y;
	private int MAX_Y;
	private int MIN_Z;
	private int MAX_Z;
	
	public AreaBoundary()
	{
		
	}
	
	public AreaBoundary(int minX, int maxX, int minY, int maxY, int minZ, int maxZ)
	{
		MIN_X = minX;
		MAX_X = maxX;
		MIN_Z = minZ;
		MAX_Z = maxZ;
		MIN_Y = minY;
		MAX_Y = maxY;
	}
	
	public boolean within(Player player)
	{
		return within(player.getLocation());
	}
	
	public boolean withinWalls(Location location)
	{
		if(location.getBlockX() < MIN_X) return false;
		if(location.getBlockX() > MAX_X) return false;
		if(location.getBlockZ() < MIN_Z) return false;
		if(location.getBlockZ() > MAX_Z) return false;
		
		return true;
	}
	
	public boolean within(Location location)
	{
		if(location.getBlockX() < MIN_X) return false;
		if(location.getBlockX() > MAX_X) return false;
		if(location.getBlockZ() < MIN_Z) return false;
		if(location.getBlockZ() > MAX_Z) return false;
		if(location.getBlockY() < MIN_Y) return false;
		if(location.getBlockY() > MAX_Y) return false;
			
		return true;
	}
	
	public boolean xBorder(Location location)
	{
		return (location.getBlockX() == MIN_X || location.getBlockX() == MAX_X);
	}
	
	public boolean zBorder(Location location)
	{
		return (location.getBlockZ() == MIN_Z || location.getBlockZ() == MAX_Z);
	}
	
	public void put(FileConfiguration config, String node)
	{
		config.set(node+".MinX", MIN_X);
		config.set(node+".MinY", MIN_Y);
		config.set(node+".MinZ", MIN_Z);
		config.set(node+".MaxX", MAX_X);
		config.set(node+".MaxY", MAX_Y);
		config.set(node+".MaxZ", MAX_Z);
	}
	
	public Configurable retrieve(FileConfiguration config, String node)
	{
		if(!config.isSet(node+".MinX")) return null;
		if(!config.isSet(node+".MinY")) return null;
		if(!config.isSet(node+".MinZ")) return null;
		if(!config.isSet(node+".MaxX")) return null;
		if(!config.isSet(node+".MaxY")) return null;
		if(!config.isSet(node+".MaxZ")) return null;
		
		MIN_X = config.getInt(node+".MinX");
		MIN_Y = config.getInt(node+".MinY");
		MIN_Z = config.getInt(node+".MinZ");
		MAX_X = config.getInt(node+".MaxX");
		MAX_Y = config.getInt(node+".MaxY");
		MAX_Z = config.getInt(node+".MaxZ");
		
		return this;
		
	}
	
}
