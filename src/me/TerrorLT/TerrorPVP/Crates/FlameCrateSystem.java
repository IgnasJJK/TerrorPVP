package me.TerrorLT.TerrorPVP.Crates;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FlameCrateSystem {

	private FileConfiguration data = null;
	private File dataFile = null;
	
	//private List<CrateItem> availableItems = new ArrayList<CrateItem>();
	
	public FlameCrateSystem(File folder)
	{
		loadData(folder);
	}
	
	public void loadData(File folder)
	{
		if(dataFile == null){
			dataFile = new File(folder, "crates.yml");
		}
		data =  YamlConfiguration.loadConfiguration(dataFile);
	}
	
	public void loadCrateItems()
	{
		if(!data.contains("flamecrate")) return;
	}
	
}
