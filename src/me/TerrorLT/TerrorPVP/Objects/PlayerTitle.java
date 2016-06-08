package me.TerrorLT.TerrorPVP.Objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.Kits.DataTypes;
import me.TerrorLT.TerrorPVP.Systems.LootCrate;

public class PlayerTitle {

	DataTypes type = null;
	String title = null;
	int price = 0;
	int droprate = 0;
	String perm = null;
	
	public PlayerTitle(DataTypes type, String title, int price, int droprate)
	{
		this.type = type;
		this.title = Messager.formatColors(title);
		this.price = price;
		this.droprate = droprate;
		perm = ChatColor.stripColor(this.title).toLowerCase();
	}
	
	public String getNode()
	{
		return "terrorpvp.titles."+perm;
	}
	
	public ItemStack createOwnedTitle()
	{
		ItemStack item = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(title);
		List<String> strs = new ArrayList<String>();
		strs.add(ChatColor.GREEN+"Owned");
		im.setLore(strs);
		item.setItemMeta(im);
		
		return item;
	}
	
	public ItemStack createShopTitle()
	{
		ItemStack item = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(title);
		List<String> strs = new ArrayList<String>();
		strs.add(ChatColor.GREEN+"Price: "+ChatColor.YELLOW+Messager.formatPoints(price));
		im.setLore(strs);
		item.setItemMeta(im);
		
		return item;
	}
	
	public ItemStack createCrateTitle()
	{
		ItemStack item = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(title);
		List<String> strs = new ArrayList<String>();
		strs.add(ChatColor.GREEN+"You can only get this title from the "+LootCrate.name);
		im.setLore(strs);
		item.setItemMeta(im);
		
		return item;
	}
	
	public DataTypes getType()
	{
		return type;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public int getPrice()
	{
		return price;
	}
	
}
