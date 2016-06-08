package me.TerrorLT.TerrorPVP;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configuration {

	public FileConfiguration configuration;
	private File configurationFile;
	
	public Configuration(File folder, String file)
	{
		loadData(folder, file);
	}
	
	public void loadData(File dataDirectory, String fileName)
	{
		if(configurationFile == null){
			configurationFile = new File(dataDirectory, fileName);
		}
		
		configuration =  YamlConfiguration.loadConfiguration(configurationFile);
	}
	
	public void saveConfiguration()
	{
		if(configuration == null || configurationFile == null)
			return;
		
		try {
			configuration.save(configurationFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
