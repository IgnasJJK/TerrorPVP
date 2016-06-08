package me.TerrorLT.TerrorPVP.Actions;

import org.bukkit.inventory.ItemStack;

public class ActionLink {

	String identifier = "";
	ItemStack trigger_ItemStack = null;
	int trigger_Int = -1;
	
	public ActionLink(String identifier)
	{
		this.identifier = identifier;
	}
	
	public void setTriggerInt(int i)
	{
		trigger_Int = i;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
	
	public int getTriggerInt()
	{
		return trigger_Int;
	}
	
	public boolean hasTriggerInt()
	{
		return trigger_Int != -1;
	}
	
	public void setTriggerItemStack(ItemStack item)
	{
		trigger_ItemStack = item.clone();
	}
	
	public boolean hasTriggerItemStack()
	{
		return trigger_ItemStack != null;
	}
	
	public ItemStack getTriggerItemStack()
	{
		return trigger_ItemStack;
	}
}
