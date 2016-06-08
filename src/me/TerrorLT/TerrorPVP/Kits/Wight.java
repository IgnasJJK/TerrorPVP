package me.TerrorLT.TerrorPVP.Kits;

import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class Wight extends OldKit{

	public static final int invisibilityTicks = 200;
	
	public Wight()
	{
		this.setDisplayMaterial(Material.STRING);
		this.setDisplayName("Wight");
		
		ItemStack head, torso, legs, feet;
		
		Color color = Color.fromRGB(255, 255, 255);
		
		head = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta lam = (LeatherArmorMeta)head.getItemMeta();
		lam.setColor(color);
		head.setItemMeta(lam);
		torso = new ItemStack(Material.LEATHER_CHESTPLATE);
		lam = (LeatherArmorMeta)torso.getItemMeta();
		lam.setColor(color);
		torso.setItemMeta(lam);
		legs = new ItemStack(Material.LEATHER_LEGGINGS);
		lam = (LeatherArmorMeta)legs.getItemMeta();
		lam.setColor(color);
		legs.setItemMeta(lam);
		feet = new ItemStack(Material.LEATHER_BOOTS);
		lam = (LeatherArmorMeta)feet.getItemMeta();
		lam.setColor(color);
		feet.setItemMeta(lam);
		
		this.setOutfit(head, torso, legs, feet,
				new ItemStack[]{new ItemStack(Material.IRON_SWORD)});
	}
	
	public static void Vanish(final Player wight)
	{
		//if(CooldownManager.hasCooldown(wight, "Vanish", false)) return;
		
		//CooldownManager.addCooldown(new Cooldown(wight, 30, "Vanish"));
		wight.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, invisibilityTicks, 1));
		ActionBarAPI.sendActionBar(wight, ChatColor.BLUE+"You have vanished.", 60);
		HandlerFunctions.RemoveArmor(wight);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
			@Override
			public void run()
			{
				Kits.WIGHT.getKit().outfitArmor(wight);
			}
		}, (invisibilityTicks-1));
	}
	
	public static void Reappear(Player wight)
	{
		if(wight.hasPotionEffect(PotionEffectType.INVISIBILITY)){
			wight.removePotionEffect(PotionEffectType.INVISIBILITY);
			Kits.WIGHT.getKit().outfitArmor(wight);
		}
	}
	
}
