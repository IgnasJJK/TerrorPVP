package me.TerrorLT.TerrorPVP.Objects;

import me.TerrorLT.TerrorPVP.Globals;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigLocation extends Configurable{

	Location location;
	
	public ConfigLocation(Location location)
	{
		this.location =  location;
	}
	public ConfigLocation(){
		
	}
	
	public Location getLocation() { return location;}
	
	@Override
	public Configurable retrieve(FileConfiguration config, String node) {
		
		if(!config.contains(node + ".X")) return null;
		if(!config.contains(node + ".Y")) return null;
		if(!config.contains(node + ".Z")) return null;
		
		location = new Location(Bukkit.getWorld(Globals.worldName),
				config.getInt(node + ".X"),
				config.getInt(node + ".Y"),
				config.getInt(node + ".Z"));
		
		return this;
	}

	@Override
	public void put(FileConfiguration config, String node) {
		config.set(node+".X", location.getBlockX());
		config.set(node+".Y", location.getBlockY());
		config.set(node+".Z", location.getBlockZ());
	}

}
