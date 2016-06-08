package me.TerrorLT.TerrorPVP.Actions;

import java.util.HashSet;
import java.util.Set;

import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Objects.Cooldown;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LightningAction{

	public static final String ability = "Judgement";
	public static final double sqRadius = 9d;
	public static final double blindRadius = 36d;
	public static final double damage = 10d;
	public static final int fireTicks = 80;
	public static final int confusionTicks = 40;
	public static final int blindTicks = 40;
	public static final int blindTicksClose = 80;
	public static final int effectiveDist = 25;
	public static final int cooldown = 60;
	
	public static Set<Material> transparent = new HashSet<Material>();
	
	public LightningAction()
	{
		transparent.add(Material.AIR);
		transparent.add(Material.GLASS);
	}
	
	public void execute(PlayerInteractEvent e)
	{
		Player thor = e.getPlayer();
		
		if(Main.cooldownManager.hasCooldown(thor, ability, true)) return;
		
		Block target = thor.getTargetBlock(transparent, effectiveDist);
		
		if(target == null) return;
		
		if(target.isEmpty()) return;
		
		Location targetLoc = target.getLocation();
		
		if(HandlerFunctions.withinSafezoneBounds(targetLoc)) return;
		
		int hit = 0;
		
		for(Player plr : Main.plugin.getServer().getOnlinePlayers())
		{
			if(plr == thor) continue;
			if(HandlerFunctions.withinSafezoneBounds(plr.getLocation())) continue;
			
			double distSq = plr.getLocation().distanceSquared(targetLoc);
			
			if(distSq > sqRadius && distSq < blindRadius){
				plr.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindTicks, 1));
				continue;
			}
			
			if( distSq > sqRadius) continue;
			
			hit++;
			
			plr.getWorld().strikeLightningEffect(plr.getLocation());
			plr.damage(damage);
			Main.battleTag.Tag(plr, thor, damage);
			plr.setFireTicks(fireTicks);
			plr.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindTicksClose, 1));
			plr.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, confusionTicks, 1));
		}
		
		if(hit == 0)
			thor.getWorld().strikeLightningEffect(targetLoc);
		
		Main.cooldownManager.addCooldown(new Cooldown(thor, cooldown, ability));
	}
	
}
