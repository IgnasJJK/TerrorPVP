package me.TerrorLT.TerrorPVP.Actions;

import java.util.ArrayList;
import java.util.List;

import me.TerrorLT.TerrorPVP.Kits.Kit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ActionProfile {
	
	List<ActionLink> links = new ArrayList<ActionLink>();
	
	public void setupTriggers(Kit kit)
	{
		for(int i = 0; i < links.size(); i++)
		{
			if(links.get(i).hasTriggerInt())
				links.get(i).setTriggerItemStack(kit.getItem(links.get(i).getTriggerInt()));
		}
	}
	
	public boolean contains(String identifier)
	{
		for(ActionLink l : links)
		{
			if(l.getIdentifier().equalsIgnoreCase(identifier))
				return true;
		}
		
		return false;
	}
	
	public ActionLink getLink(String identifier)
	{
		for(ActionLink l : links)
		{
			if(l.getIdentifier().equalsIgnoreCase(identifier))
				return l;
		}
		return null;
	}
	
	public void addLink(ActionLink link)
	{
		links.add(link);
	}
	
	public static ActionProfile readProfile(ConfigurationSection section)
	{
		if(!section.contains("abilities")) return null;
		if(!section.contains("abilities.active")) return null;
		
		ActionProfile ap = new ActionProfile();
	
		List<String> linkNames = section.getStringList("abilities.active");
		
		for(String s : linkNames)
		{
			ActionLink al = new ActionLink(s);
			if(section.contains("abilities."+s))
				al.setTriggerInt(section.getInt("abilities."+s));
			
			ap.addLink(al);
		}
		
		return ap;
	}
	
	/*public Action onRightClick = null;
	public Action onFish = null;
	
	public static ActionProfile readProfile(ConfigurationSection section)
	{
		ActionProfile profile = new ActionProfile();

		Action action = null;
		
		if(section.contains("onRightClick")){
			action = ActionManager.getActionById(section.getString("onRightClick"));
			if(action != null)
				profile.onRightClick = action;
		}
		
		if(section.contains("onFish")){
			action = ActionManager.getActionById(section.getString("onFish"));
			if(action != null)
				profile.onFish = action;
		}
		
		return profile;
	}
	
	public void setTrigger(ItemStack item)
	{
		if(onRightClick != null) onRightClick.setTrigger(item);
		if(onFish != null) onFish.setTrigger(item);
	}*/
}
