package me.TerrorLT.TerrorPVP.Objects;

import org.bukkit.configuration.file.FileConfiguration;

public abstract class Configurable {

	public abstract Configurable retrieve(FileConfiguration config, String node);
	public abstract void put(FileConfiguration config, String node);
	
}
