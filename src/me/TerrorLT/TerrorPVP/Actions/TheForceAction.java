package me.TerrorLT.TerrorPVP.Actions;

import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Objects.Cooldown;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class TheForceAction {
	
	public static final double maxYVelocity = 0.3;
	
	public void execute(PlayerInteractEvent e)
	{
		if(Main.cooldownManager.hasCooldown(e.getPlayer(), "The Force", true)) return;
		
		Location targetLoc = e.getPlayer().getLocation();
		
		if(HandlerFunctions.withinSafezoneBounds(targetLoc)) return;
		
		for(Player plr : Main.plugin.getServer().getOnlinePlayers())
		{
			if(plr == e.getPlayer()) continue;
			if(plr.getWorld() != e.getPlayer().getWorld())
			if(HandlerFunctions.withinSafezoneBounds(plr.getLocation())) continue;
			
			double distSq = plr.getLocation().distanceSquared(targetLoc);
			
			if( distSq > 36d) continue;
			
			//hit++;
			
			Location to = plr.getLocation();
			Location from = e.getPlayer().getLocation();
			
			double diag = to.distance(from);
			
			Vector velocity = new Vector();
			
			double sine = (from.getX() - to.getX())/diag;
			double cosine = (from.getZ() - to.getZ())/diag;

			velocity.setX(-5*sine);
			velocity.setZ(-5*cosine);
			
			double yVel = ((from.getBlockY()-to.getY())*0.05);
			velocity.setY(yVel > maxYVelocity ? maxYVelocity : yVel);
			
			plr.damage(1d);
			
		}
		
		//if(hit == 0)
			//thor.getWorld().strikeLightningEffect(targetLoc);
		
		Main.cooldownManager.addCooldown(new Cooldown(e.getPlayer(), 30, "The Force"));
	}
	
}
