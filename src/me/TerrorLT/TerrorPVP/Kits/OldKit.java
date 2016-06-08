package me.TerrorLT.TerrorPVP.Kits;

import java.util.ArrayList;
import java.util.List;

import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.Systems.TagManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OldKit {

	private static ItemStack kitSelectItem = null;
	
	public ItemStack ArmorHead = null;
	public ItemStack ArmorTorso = null;
	public ItemStack ArmorLegs = null;
	public ItemStack ArmorFeet = null;
	
	public ItemStack[] Hotbar = null;
	
	public String KitName = "";
	public Material displayMaterial = null; 
	
	
	public List<PotionEffectType> effects = new ArrayList<PotionEffectType>();
	public List<Integer> effectLevel = new ArrayList<Integer>();
	
	public void setOutfit(Material armorHead, Material armorTorso, Material armorLegs, 
			Material armorFeet, ItemStack[] hotbar)
	{
		ItemStack aHead, aTorso, aLegs, aFeet;
		aHead = aTorso = aLegs = aFeet = null;
		
		if(armorHead != null) aHead = new ItemStack(armorHead, 1);
		if(armorTorso != null) aTorso = new ItemStack(armorTorso, 1);
		if(armorLegs != null) aLegs = new ItemStack(armorLegs, 1);
		if(armorFeet != null) aFeet = new ItemStack(armorFeet, 1);
		
		setOutfit(aHead, aTorso, aLegs, aFeet, hotbar);
	}
	
	public void setOutfit(ItemStack head, ItemStack torso, ItemStack legs, 
			ItemStack feet, ItemStack[] hotbar)
	{
		Hotbar = new ItemStack[9];
		
		for(int i = 0; i < Hotbar.length; i++){
			if(i >= hotbar.length) break;
			Hotbar[i] = hotbar[i];
		}
		ArmorHead = head;
		ArmorTorso = torso;
		ArmorLegs = legs;
		ArmorFeet = feet;
	}
	
	public void addPotionEffect(PotionEffectType effect, int level)
	{
		effects.add(effect);
		effectLevel.add(level);
	}
	
	public void setDisplayName(String kitName)
	{
		KitName = kitName;
	}
	
	public void setDisplayMaterial(Material material)
	{
		displayMaterial = material;
	}
	
	public static ItemStack createDisplayItem(Material material, String name)
	{
		ItemStack item = new ItemStack(material, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);
		
		return item;
	}
	
	public void outfit(Player player)
	{
		TagManager.SELECTED_KIT.apply(player, this);
		TagManager.PREVIOUS_KIT.apply(player, this);
		
		player.getInventory().clear();

		outfitArmor(player);
		for(int i = 0; i < Hotbar.length; i++)
		{
			if(Hotbar[i] == null) continue;
			player.getInventory().setItem(i, Hotbar[i]);
		}
		
		for( int i = 0; i < effects.size(); i++)
		{
			player.addPotionEffect(new PotionEffect(effects.get(i), Integer.MAX_VALUE, effectLevel.get(i)), true);
		}
	}
		
	 public void outfitArmor(Player player)
	 {
			player.getInventory().setArmorContents(
					new ItemStack[] {ArmorFeet, ArmorLegs, ArmorTorso, ArmorHead});
	 }
	
	public static void giveKitSelectItem(Player player)
	{
		if(kitSelectItem == null)
		{
			kitSelectItem = new ItemStack(Material.NETHER_STAR);
			ItemMeta im = kitSelectItem.getItemMeta();
			im.setDisplayName(Globals.GUI_KITSELECT_ITEM);
			kitSelectItem.setItemMeta(im);
		}
		
		player.getInventory().addItem(kitSelectItem);	
	}
	
	public static void openGui(Player player)
	{
		Inventory inventory = Bukkit.createInventory(null, 45, Globals.GUI_KITSELECT);
		
		for(int i = 0; i < Kits.values().length; i++)
		{
			//if(Kits.values()[i].getKit().displayMaterial == null) continue;
			
			if(!player.hasPermission(Kits.values()[i].getNode())
					&& Kits.values()[i].getType() != DataTypes.DEFAULT) continue;
			
			Material dispMaterial = Kits.values()[i].getKit().displayMaterial;
			if(dispMaterial == null) dispMaterial = Material.DIAMOND;
			
			ItemStack item = new ItemStack(dispMaterial, 1);
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + Kits.values()[i].getKit().KitName);
			item.setItemMeta(im);
			inventory.addItem(item);
		}
		
		player.openInventory(inventory);
	}
	
	public static OldKit getKitByName(String kitName)
	{
		Kits kit = Kits.getByItemName(kitName);
		if(kit != null) return kit.getKit();
		else return null;
	}
	
}
