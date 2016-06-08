package me.TerrorLT.TerrorPVP.Kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

public class Monkey extends OldKit{

	public Monkey()
	{
		this.setDisplayMaterial(Material.GOLD_SWORD);
		this.setDisplayName("Monkey");
		
		ItemStack banana = new ItemStack(Material.GOLD_SWORD);
		ItemMeta im = banana.getItemMeta();
		im.setDisplayName(ChatColor.YELLOW+"Banana");
		banana.setItemMeta(im);
		banana.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		
		this.setOutfit(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, 
				Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, new ItemStack[]{banana});
		
		this.addPotionEffect(PotionEffectType.SPEED, 0);
		this.addPotionEffect(PotionEffectType.JUMP, 1);
	}
	
}
