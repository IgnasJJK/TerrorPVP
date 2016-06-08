package me.TerrorLT.TerrorPVP.Systems;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TerrorLT.TerrorPVP.Messager;
import net.md_5.bungee.api.ChatColor;

public class Title {

	public String title = null;
	public String internalName = null;
	
	public boolean Default = false;
	
	public Title(String internal)
	{
		internalName = internal;
	}
	
	public boolean isDefault()
	{
		return Default;
	}
	
	public void setDefault(boolean bool)
	{
		Default = bool;
	}
	
	public String getInternalName(){
		return internalName;
	}
	
	public ItemStack getItem()
	{
		if(title == null) return null;
		
		ItemStack item = new ItemStack(Material.NAME_TAG);
		
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(Messager.formatColors(title));
		item.setItemMeta(im);
		
		return item;
	}
	
	public String getPerm()
	{
		return "terrorpvp.title." + ChatColor.stripColor(internalName);
	}
	
	public String getTitle(){
		return title;
	}
	
	public boolean hasTitle(Player player)
	{
		if(isDefault()) return true;
		return player.hasPermission(getPerm());
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}

}
