package me.TerrorLT.TerrorPVP.Objects;

import me.TerrorLT.TerrorPVP.Globals;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

public class HighscoreSign extends Configurable{

	Location location;
	int place;
	boolean timed = false;
	
	public HighscoreSign()
	{
		
	}
	
	public HighscoreSign(Location loc, int place, boolean timed)
	{
		this.timed = timed;
		this.place = place;
		location = loc;
	}

	public Location getLocation() { return location;}
	public int getPlace() {return place;}
	public boolean getTimed() {return timed;}
	
	@Override
	public Configurable retrieve(FileConfiguration config, String node) {
		if(!config.contains(node)) return null;
		if(!config.contains(node + ".Place")) return null;
		if(!config.contains(node + ".Location.X")) return null;
		if(!config.contains(node + ".Location.Y")) return null;
		if(!config.contains(node + ".Location.Z")) return null;
		if(!config.contains(node + ".Timed")) timed = false;
		else timed = config.getBoolean(node + ".Timed");
		
		place = config.getInt(node + ".Place");
		location = new Location(Bukkit.getWorld(Globals.worldName),
				config.getInt(node + ".Location.X"),
				config.getInt(node + ".Location.Y"),
				config.getInt(node + ".Location.Z"));
		
		return this;
	}
	
	public boolean isValid(){
		if(location == null || place <= 0) return false;
		
		try{
		Block b = location.getWorld().getBlockAt(location);
		
		if(b == null) return false;
		if(!(b.getState() instanceof Sign)) return false;
		return true;
		}catch(NullPointerException e){
			return false;
		}
	}

	@Override
	public void put(FileConfiguration config, String node) {
		if(!isValid()) return;
		String locId = generateId(location); 
		config.set(node + "." + locId + ".Place", place);
		config.set(node + "." + locId + ".Timed", timed);
		config.set(node + "." + locId + ".Location.X", location.getBlockX());
		config.set(node + "." + locId + ".Location.Y", location.getBlockY());
		config.set(node + "." + locId + ".Location.Z", location.getBlockZ());
	}
	
	
	public static String generateId(Location location)
	{
		return location.getBlockX() +""+ location.getBlockY() +""+ location.getBlockZ();
	}
	
}
