package me.TerrorLT.TerrorPVP.Kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Archer extends OldKit{

	public Archer()
	{
		setOutfit(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS,
				Material.LEATHER_BOOTS, new ItemStack[]{new ItemStack(Material.STONE_SWORD, 1), 
				new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 64)});
		setDisplayMaterial(Material.BOW);
		setDisplayName("Archer");
		
	}
	
}
