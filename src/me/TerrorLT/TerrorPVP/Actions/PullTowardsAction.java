package me.TerrorLT.TerrorPVP.Actions;

import me.TerrorLT.TerrorPVP.Systems.TagManager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

public class PullTowardsAction extends FishingAction {

	public static double maxDist = 5;
	public static final double maxYVelocity = 0.3;
	
	public void execute(PlayerFishEvent e)
	{
		if (!(e.getCaught() instanceof Player)) return;
		if (TagManager.SPECTATOR_MODE.contains(e.getCaught())) return;
		
		Location to = e.getCaught().getLocation();
		Location from = e.getPlayer().getLocation();
		
		double diag = to.distance(from);
		
		Vector velocity = new Vector();
		
		if(diag < maxDist){
			velocity.setX(from.getX() - to.getX());
			velocity.setZ(from.getZ() - to.getZ());
		}else{
			double sine = (from.getX() - to.getX())/diag;
			double cosine = (from.getZ() - to.getZ())/diag;

			velocity.setX(maxDist*sine);
			velocity.setZ(maxDist*cosine);
		}
		
		double yVel = ((from.getBlockY()-to.getY())*0.05);
		velocity.setY(yVel > maxYVelocity ? maxYVelocity : yVel);
		
		//Cos - in front div by diagonal
		
		e.getCaught().setVelocity(velocity);
		
	}
	
}
