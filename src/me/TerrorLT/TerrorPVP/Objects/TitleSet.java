package me.TerrorLT.TerrorPVP.Objects;

import java.util.ArrayList;
import java.util.List;

import me.TerrorLT.TerrorPVP.Kits.DataTypes;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class TitleSet extends Configurable {

	List<PlayerTitle> titles = null;
	
	public List<PlayerTitle> getTitles()
	{
		return titles;
	}
	
	@Override
	public Configurable retrieve(FileConfiguration config, String node) {
		titles = new ArrayList<PlayerTitle>();
		//node = Titles
		if(!config.contains(node)) return null;
		
		ConfigurationSection section = config.getConfigurationSection(node);
		
		for(String s : section.getKeys(false))
		{
			DataTypes type = null;
			String title = null;
			title = section.getString(s+".title");
			
			if(section.contains(s+".type")){
				type = DataTypes.getTypeByName(section.getString(s+".type"));
			}
			
			if(type == null) continue;
			
			switch(type)
			{
				case DEFAULT:{
					titles.add(new PlayerTitle(type, title, 0, 0));
				}break;
				case SHOP:{
					if(!section.contains(s+".price")) continue;
					int price = section.getInt(s+".price");
					titles.add(new PlayerTitle(type, title, price, 0));
				}break;
				
				case KEYCRATE:{
					if(!section.contains(s+".droprate")) continue;
					int droprate = section.getInt(s+".droprate");
					titles.add(new PlayerTitle(type, title, 0, droprate));
				}break;
				default: continue;
			}
		}
		
		return this;
	}

	@Override
	public void put(FileConfiguration config, String node) {
		
	}
	
	public PlayerTitle getByItemName(String itemName)
	{
		for(PlayerTitle title : titles)
		{
			if(title.getTitle().equals(itemName))
			{
				return title;
			}
		}
		return null;
	}

}
