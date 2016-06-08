package me.TerrorLT.TerrorPVP.Actions;

import org.bukkit.inventory.ItemStack;

public class Action {

	//Whether any item can be interacted with to use the skill
	private boolean anyItem = false;
	
	//Used to identify the item to which an ability is bound
	private ItemStack triggerItem = null;
	
	public void setTrigger(ItemStack item)
	{
		triggerItem = item;
	}
	
	public ItemStack getTriggerItem()
	{
		return triggerItem;
	}
	
	public boolean canAnyItemBeUsed()
	{
		return anyItem;
	}
	
	//To be used for actions that don't require a reference to anything.
	
	public void execute(){
		
	}
	
}
